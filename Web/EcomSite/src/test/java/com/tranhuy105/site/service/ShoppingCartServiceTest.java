package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.CartItem;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.ShoppingCart;
import com.tranhuy105.common.entity.Sku;
import com.tranhuy105.site.repository.CartItemRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import com.tranhuy105.site.repository.ShoppingCartRepository;
import com.tranhuy105.site.repository.SkuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SkuRepository skuRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private Customer customer;
    private Sku sku;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId(1);
        customer.setEmail("customer@example.com");

        sku = new Sku();
        sku.setId(1);
        sku.setSkuCode("Sku A");

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1);
        shoppingCart.setCustomer(customer);
        shoppingCart.setCartItems(new HashSet<>());
        customer.setShoppingCart(shoppingCart);
    }

    @Test
    void testAddItemToCart_CreateNewCart() {
        Customer newCustomer = new Customer();
        newCustomer.setId(2);
        newCustomer.setShoppingCart(null);

        Sku newSku = new Sku();
        newSku.setId(2);

        ShoppingCart newCart = new ShoppingCart();
        newCart.setCustomer(newCustomer);
        newCart.setCartItems(new HashSet<>());

        when(customerRepository.findByIdWithShoppingCart(newCustomer.getId())).thenReturn(Optional.of(newCustomer));
        when(skuRepository.findById(newSku.getId())).thenReturn(Optional.of(newSku));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart cart = invocation.getArgument(0);
            cart.setId(2); // simulate save operation with a new ID
            return cart;
        });

        shoppingCartService.addItemToCart(newCustomer.getId(), newSku.getId(), 1);

        ArgumentCaptor<ShoppingCart> cartCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository, times(1)).save(cartCaptor.capture());

        ShoppingCart savedCart = cartCaptor.getValue();
        assertNotNull(savedCart);
        assertEquals(newCustomer, savedCart.getCustomer());

        assertEquals(1, savedCart.getCartItems().size());
        CartItem cartItem = savedCart.getCartItems().iterator().next();
        assertEquals(1, cartItem.getQuantity()); // Default quantity is 1
        assertEquals(newSku, cartItem.getSku());
        assertEquals(savedCart, cartItem.getShoppingCart());
    }

    @Test
    void testAddItemToCart_UpdateExistingItem() {
        CartItem existingItem = new CartItem();
        existingItem.setSku(sku);
        existingItem.setQuantity(1);
        existingItem.setShoppingCart(shoppingCart);

        shoppingCart.getCartItems().add(existingItem);

        when(customerRepository.findByIdWithShoppingCart(customer.getId())).thenReturn(Optional.of(customer));
        when(skuRepository.findById(sku.getId())).thenReturn(Optional.of(sku));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        shoppingCartService.addItemToCart(customer.getId(), sku.getId(), 1);

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        assertEquals(1, shoppingCart.getCartItems().size());
        CartItem updatedItem = shoppingCart.getCartItems().iterator().next();
        assertEquals(2, updatedItem.getQuantity()); // Previous quantity 1 + added quantity 1
    }

    @Test
    void testUpdateCartItemQuantity() {
        CartItem existingItem = new CartItem();
        existingItem.setSku(sku);
        existingItem.setQuantity(2);
        existingItem.setShoppingCart(shoppingCart);

        shoppingCart.getCartItems().add(existingItem);

        when(customerRepository.findByIdWithShoppingCart(customer.getId())).thenReturn(Optional.of(customer));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        shoppingCartService.updateCartItemQuantity(customer.getId(), sku.getId(), 5);

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        assertEquals(1, shoppingCart.getCartItems().size());
        CartItem updatedItem = shoppingCart.getCartItems().iterator().next();
        assertEquals(5, updatedItem.getQuantity());
    }

    @Test
    void testRemoveCartItem() {
        CartItem existingItem = new CartItem();
        existingItem.setSku(sku);
        existingItem.setQuantity(5);
        existingItem.setShoppingCart(shoppingCart);

        shoppingCart.getCartItems().add(existingItem);

        when(customerRepository.findByIdWithShoppingCart(customer.getId())).thenReturn(Optional.of(customer));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        shoppingCartService.removeCartItem(customer.getId(), sku.getId());

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void testGetOrCreateCartForCustomer_CreateNewCart() {
        Customer newCustomer = new Customer();
        newCustomer.setId(2);
        newCustomer.setShoppingCart(null);

        ShoppingCart newCart = new ShoppingCart();
        newCart.setCustomer(newCustomer);
        newCart.setCartItems(new HashSet<>());

        when(customerRepository.findByIdWithShoppingCartItems(newCustomer.getId())).thenReturn(Optional.of(newCustomer));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(newCart);

        ShoppingCart result = shoppingCartService.getOrCreateCartForCustomer(newCustomer.getId());

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        assertNotNull(result);
        assertEquals(newCustomer, result.getCustomer());
    }

    @Test
    void testGetOrCreateCartForCustomer_ExistingCart() {
        when(customerRepository.findByIdWithShoppingCartItems(customer.getId())).thenReturn(Optional.of(customer));

        ShoppingCart result = shoppingCartService.getOrCreateCartForCustomer(customer.getId());

        verify(shoppingCartRepository, times(0)).save(any(ShoppingCart.class));
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
    }
}
