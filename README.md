# E-Commerce Platform (Still In Development)

## Overview

This E-Commerce Platform is a comprehensive online shopping solution designed for individuals and small distributors to sell their products efficiently with fully COD support. With the premise of design for small distributors, utilizing MVC architecture, the system is easy to deploy within a single JAR file, offering cost-effective management and scalability. 

The platform integrates with Giao Hàng Nhanh (GHN), Vietnam's leading domestic shipping service, and supports various payment methods, including PayPal and VNPay (domestic).

## System Architecture

- **MVC Architecture**: 
  - **Model**: Manages data and business logic.
  - **View**: Handles the presentation layer for both customers and admins.
  - **Controller**: Manages user interactions and updates the model and view.

- **Frontend**:
  - **Bootstrap**: Provides a responsive and modern user interface with its powerful grid system. Ensure to work smoothly even on phone.
  - **jQuery**: Enhances user experience with AJAX for dynamic content updates without full page reload.

- **Backend**:
  - **Java and Spring Framework**: Handles core business logic, data access, and external service integrations.
  - **APIs**: Integrates with GHN for shipping and PayPal/VNPay for payments.

- **Database**:
  - **MySQL**: Stores product, order, and customer information.

#### Database Schema

The database schema for the platform is designed to efficiently handle product management, order processing, and customer data. You can check the migration details (still updating) in the Flyway migration folder:

- **Flyway Migrations**: [Flyway Migration Scripts](./Common/src/main/resources/db/migration)

#### Demo Video

To get a better understanding of how the platform works, watch the following demo video: **update soon**




## Key Features

## Shipping Integration with GHN

- **GHN (Giao Hàng Nhanh)** is Vietnam's leading domestic shipping service. GHN provides comprehensive shipping solutions, including fast and reliable delivery services. The platform integrates with GHN to offer seamless shipping management for orders.

- **Order and Shipping Workflow**:
  - **Order Creation**: Admins initiate shipping orders through GHN’s API directly from the admin interface.
  - **Label Printing**: Shipping labels are generated and printed in A5 format. The system provides all necessary details, including tracking codes and shipment information.
  - **Applying Labels**: The printed labels must be affixed to the packages. Salers then have to bring the labeled packages to a GHN post office.
  - **Real-Time Updates** (currently in development): Integration with GHN’s webhook system  provide real-time updates and notifications for order shipping status changes, enhancing the shipping and delivery process.

## Moderator Features Behind The Scene To Manage The System

### Order & Shipping Workflow Management

1. **Order Management**
   - **Order Confirmation**:
     
     - Admins can review and confirm incoming orders from the dashboard.
     - Update the order status to `CONFIRMED`.
   - **Order Preparation**:
     
     - **Prepare for Shipping**: Generate shipping requests to GHN.
     - **Update Shipping Information**: Input shipping details including ward code and district ID.
   - **Order Shipping**:
     
     - **Ship Orders**: Update the status to `IN_TRANSIT` and record GHN shipping codes.
     - **Generate Shipping Labels**: Print A5 labels with shipping details and barcodes.
   - **Order Delivery**:
     
     - **Mark as Delivered**: Update status to `DELIVERED` and set payment status to `PAID`.

2. **Shipping Integration**
   - **GHN API Integration**:
     
     - **Create Shipping Orders**: Interface with GHN’s API to create and manage shipping orders.
     - **Generate Shipping Labels**: Print labels with essential delivery information using GHN’s API.
     - **Track Shipments**: Monitor shipping progress and update order status.
   - **Domestic Address Management**:
     
     - **Fetch Districts and Wards**: Retrieve data from GHN’s API for accurate shipping.
     - **Manage Shipping Details**: Update shipping details as needed.

3. **Sales Management** (In development)
   - **Sales Promotions**:
     
     - **Create and Manage Sales**: Set up and manage discounts and special offers.
     - **Apply Promotions**: Apply sales promotions to products or categories.
   - **Track Sales Performance**:
     
     - **Review Sales Data**: Access and analyze sales reports.
     - **Analyze Trends**: Adjust marketing strategies based on sales trends.

4. **User Management**
   - **Role Management**:
     
     - **Create and Manage User Accounts**: Admins can create and manage user accounts.
     - **Assign Roles**: Manage access and permissions by assigning roles.
   - **Access Control**:
     
     - **Control Access**: Securely manage access to various parts of the admin interface.

5. **Order and Shipping Label Printing**
   - **Print Shipping Labels**:
     
     - **Generate Labels**: Generate shipping labels with GHN integration.
     - **Print Directly**: Print labels in A5 format from the admin interface.
   - **Manage Labels**:
     
     - **Track Label Status**: Monitor the status of printed labels.
    
### Product And Inventory Management

1. **Product Updates**
   - **Add New Products**:
     - **Product Information**: Admins can add new products by entering detailed information including product name, description, price, and images.
     - **SKU Management**: Define unique SKU codes for each product to ensure accurate tracking and inventory management. (SHIRT-XL-BLUE, SHIRT-XXL-WHITE, etc..) Each sku should has its own price and stock quantity.
     - **Categories and Brands**: Assign products to appropriate categories and brands for better organization and searchability.

   - **Edit Existing Products**:
     - **Update Product Details**:
       - **Modify Information**: Admins can update product information such as name, description, price, and other attributes.
       - **Change Images**: Upload or replace product images as needed to reflect the latest visual representation of the product.
       - **Adjust SKUs**: Update SKU codes if necessary, ensuring that all changes are reflected in the inventory system.

   - **Manage Stock Levels**:
     - **Update Stock Quantities**: Adjust the stock levels for products based on inventory changes, such as new stock arrivals or sales.
     - **Track Inventory**: Monitor and manage inventory levels to ensure accurate stock information and avoid over-selling or stockouts.

   - **Manage Categories and Brands**:
     - **Update Categories**: Add, edit, or remove product categories to keep the catalog organized and relevant.
     - **Manage Brands**: Update brand information and associate products with appropriate brands to enhance brand visibility and searchability.

   - **Product Visibility and Availability**:
     - **Set Product Status**: Enable or disable products as needed to control their visibility on the front-end, such as marking products as out of stock or discontinued.
     - **Manage Product Variants**: Handle different product variants, such as sizes or colors, by updating their availability and pricing.


### Customer And Viewer Features In The Main Site

1. **Product Search and Filtering**
   - **Search**: Customers can search for products by name with instant results.
   - **Filtering**:
     
     - **Brand**: Filter products by brand.
     - **Category**: Browse by product categories.
     - **Price Range**: Set a price range to find products within budget.

2. **Product Listings and Order Creation**
   - **Product Listings**:
     
     - View products with details including name, price, and description.
     - Access detailed product pages with high-quality images and full descriptions.
   - **Order Creation**:
     
     - **Add to Cart**: Easily add products to the shopping cart.
     - **View Cart**: Review and modify cart contents.
     - **Checkout**: Seamless checkout with options for shipping and payment.
     - **Order Confirmation**: Receive immediate order confirmation via email.
    

## API Integration

1. **GHN API**:
   - **Create Shipping Orders**: Create and manage shipping orders through GHN’s API.
   - **Generate Shipping Labels**: Generate and print labels for shipments.
   - **Fetch District and Ward Information**: Retrieve district and ward data.

2. **Payment Gateways**:
   - **PayPal**: Securely process international payments.
   - **VNPay**: Handle local transactions efficiently.

3. **Email Notifications**:
   - **SMTP Integration**: Send confirmation emails for account creation and order updates as well as sale promotion via Google Gmail SMTP.
