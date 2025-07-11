package org.springboot.udemy.initial.category.repository;

import com.embarkx.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo extends JpaRepository<Address, Long> {
}
