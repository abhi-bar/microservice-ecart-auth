package org.springboot.udemy.initial.category.repository;

import com.embarkx.ecommerce.model.Carts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepo extends JpaRepository<Carts, Integer> {
    @Query("SELECT c FROM Carts c WHERE c.user.email = ?1")
    Carts findCartByEmail(String email);

    @Query("SELECT c FROM Carts c WHERE c.user.email = ?1 AND c.cartId = ?2")
    Carts findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("SELECT c FROM Carts c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.productId = ?1")
    List<Carts> findCartsByProductId(Long productId);
}
