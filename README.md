# E-Commerce Platform

### Overview

This E-Commerce Platform is a comprehensive online shopping solution designed for individuals and small distributors to sell their products efficiently with fully COD support. With the premise of design for small distributors, utilizing MVC architecture, the system is easy to deploy within a single application, offering cost-effective management and scalability. 

---

## 1. Tech Stack

- **Backend**: Java (Spring Boot MVC)
- **Frontend**: Thymeleaf, HTML, CSS, Bootstrap
- **Database**: MySQL (hosted on AWS RDS)
- **Third-Party Integrations**: 
  - VNPay for online payments
  - GHN for shipping
- **Containerization**: Docker
- **Hosting**: AWS (EC2, S3, RDS)
- **Infrastructure Provisioning**: Terraform

---



## 2. Architecture

The system comprises two separate applications: **Public Site** and **Admin Panel**.

- The **Public Site** allows users to browse products, place orders, and complete payments.
- The **Admin Panel** handles order confirmation (COD), shipment initiation, and order status management via GHN.

Both apps connect to a shared **MySQL database**.



---

## 3. Robust Payment Flow

The payment process in this project is integrated with VNPay, with options for COD (Cash on Delivery) or online payment. Here’s the detailed flow:

### Payment Flow Breakdown

1. **User Browsing Public Site**:
   - Users browse the site, adding products to their cart and proceeding to checkout.

2. **Place Order & Save to Database**:
   - When the order is placed, order details are saved in the database, and an `OrderCreatedEvent` is fired.

3. **Order Created Listener**:
   - **For COD**:
     - The admin manually confirms the order via the admin panel.
     - The order status remains pending until admin confirmation.
   - **For Online Payment**:
     - The payment service initiates the VNPay payment.
     - The system sets a 15-minute expiration time for the payment.
     - The user is redirected to VNPay's payment gateway to complete the payment.

4. **VNPay Interaction**:
   - The user interacts with VNPay to complete the payment.
   - VNPay processes the payment and returns a status (success, failure, or pending).

5. **Post Payment**:
   - **If Payment is Successful**:
     - The order status is updated to confirmed without admin intervention.
     - Payment and order expiration schedules are canceled.
     - The order moves to the shipping lifecycle.
   - **If Payment Fails or Expires**:
     - The user can retry the payment, which restarts the VNPay redirection flow.

6. **Shipping Lifecycle**:
   - For confirmed orders, shipping is delegated to the **GHN API** via the admin panel.
   - The admin initiates the shipping process.
   - The order status is updated based on GHN webhook callbacks that handle delivery updates.

### Diagram

![Payment Flow](https://github.com/user-attachments/assets/da786a97-74c8-4b94-ae4a-e2b27f723e06)

### Payment Retry Logic

- If a payment fails or expires, the user is provided with an option to retry. 
- This creates a new VNPay transaction and redirects the user back to the VNPay payment gateway for another attempt.
  
### Refund Mechanism

- Refunds are initiated when a user cancels a paid order.
- A refund request is sent to VNPay through the refund API, and VNPay processes and returns a refund status that updates the order in the database.

### Security Considerations

- **Checksum Validation**: All requests to and from VNPay are validated using a secure checksum.
- **Secure Hashing**: HMAC-SHA512 is used to generate secure hash values for all VNPay communications.
- **Sensitive Data Handling**: Sensitive data, such as transaction details, are securely handled and stored according to best practices.

---

# 4. Deployment

This section outlines the deployment process for the public site using Terraform for infrastructure provisioning, Docker for containerization, and AWS for hosting. This setup is designed for small distributors and individuals, providing a reliable, scalable, and cost-effective solution.

## 4.1 Infrastructure Provisioning with Terraform

Terraform is used to define and provision the cloud infrastructure. Key components include:

- **Networking**: A VPC with public and private subnets is configured for the application and database.
- **Compute**: EC2 instances are provisioned to host the public site and admin panel.
- **Database**: An RDS MySQL instance is set up with a dedicated DB subnet group.
- **Storage**: An S3 bucket is created for storing static assets with versioning and encryption enabled.
- **Security**: Security groups control inbound and outbound traffic for EC2 and RDS.

## 4.2 Cloud Services

The project utilizes the following AWS services:

- **EC2**: Hosts the public and admin sites inside Docker containers.
- **RDS (MySQL)**: Stores order data, payments, and user information.
- **S3**: Handles storage for static assets such as product images.

## 4.3 Docker Containerization

The public and admin sites are containerized using Docker, ensuring consistency across development and production environments. Docker images include all necessary components, such as dependencies and application code.

## 4.4 Deployment Workflow

1. **Infrastructure Setup**:
   - Use Terraform to provision the necessary AWS resources, including VPC, subnets, EC2 instances, RDS database, and S3 bucket.
   - Run `terraform init`, `terraform plan`, and `terraform apply` to create or update the infrastructure.

2. **Application Deployment**:
   - Build Docker images for the public and admin sites.
   - SSH into the provisioned EC2 instances and deploy the Docker containers by pulling the images from the container registry.

3. **Static Asset Management**:
   - Upload static assets such as product images to the S3 bucket using the AWS CLI or an SDK.

4. **Database Configuration**:
   - Configure the RDS MySQL instance with the necessary database schema and credentials.

---

![AWS (2019) horizontal framework](https://github.com/user-attachments/assets/feb0063c-bb55-43be-8685-62f44758d0e4)


---

## 5. Business Logic Overview

This section provides an overview of how the application handles key business functionalities, enabling both users and admins to interact with the system seamlessly.

### 5.1 User Interactions

#### Product Browsing & Detail View
- Users can browse products through various categories and use search filters to find specific items.
- Each product page provides detailed information, including descriptions, pricing, discounts, and customer reviews.

<div align="center">
  <img src="https://github.com/user-attachments/assets/40228ad3-ff10-4bd9-8e33-32dfb07fa46f" alt="Product Browsing and Detail View"/>
</div>

#### Cart & Checkout
- Users can add products to their cart, update quantities, and proceed to checkout.

![image](https://github.com/user-attachments/assets/f2307e50-8ac8-4fb7-99e2-4d8031fdaa52)

#### Order Tracking
- After placing an order, users can track their order status in real-time, whether it's in **processing**, **shipping**, or **delivered** stages.

<div align="center">
  <img src="https://github.com/user-attachments/assets/790b66a6-f1c4-4516-a2e1-17ac744eb725" alt="Order Tracking"/>
</div>

### 5.2 Admin Interactions

#### SKU Management
- Admins have full control over managing **product SKUs**, including creating, updating, or disabling products.
- SKUs are linked with inventory management, ensuring that stock levels are updated as orders are processed.

<div align="center">
  <img src="https://github.com/user-attachments/assets/e75b9e05-5e12-4174-a9a5-a35df33332f6" alt="SKU mangement"/>
</div>

#### Order Management
- Admins can view and manage all orders, with functionality to confirm COD orders and initiate shipping.

#### Shipping & Integration with GHN
- Admins handle shipping orders through GHN. The admin panel is integrated with GHN's API for automated shipping label generation and tracking updates.
- Shipping status updates from GHN are automatically reflected in the system via webhook callbacks.
  
<div align="center">
  <img src="https://github.com/user-attachments/assets/b944770c-4bd7-42cd-a2fc-205e691f7210" alt="Shipping & Integration with GHN"/>
</div>




