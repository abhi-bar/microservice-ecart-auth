package org.springboot.udemy.initial.category.repository;

import com.embarkx.ecommerce.model.Categories;
import com.embarkx.ecommerce.model.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {
    Page<Products> findByCategoryOrderByPriceAsc(Categories category, Pageable pageDetails);

    Page<Products> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}
