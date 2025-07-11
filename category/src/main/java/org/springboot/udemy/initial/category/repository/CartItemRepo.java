package org.springboot.udemy.initial.category.repository;

import com.embarkx.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepo extends JpaRepository<CartItem, Integer> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.productId= ?2")
    CartItem findCartItemByProductAndCart_CartId(Long cartId, Long productId);

    @Modifying
    @Query("delete from CartItem ci where ci.cart.cartId = ?1 AND ci.product.productId= ?2 ")
    void deleteCartItemsByCartAndProduct(Long cartId, Long productId);

    @Modifying
    void deleteCartItemsByCart_CartId(Long cartCartId);
}
