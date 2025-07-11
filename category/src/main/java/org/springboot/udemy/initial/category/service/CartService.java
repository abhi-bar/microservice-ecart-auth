package org.springboot.udemy.initial.category.service;

import com.embarkx.ecommerce.payload.CartDTO;
import com.embarkx.ecommerce.payload.CartItemDTO;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quality);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);

    String createOrUpdateCartWithItems(List<CartItemDTO> cartItems);
}
