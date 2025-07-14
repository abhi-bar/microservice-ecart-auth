package org.springboot.udemy.initial.authhandler.repository;

import org.springboot.udemy.initial.authhandler.enums.AppRoleCategory;
import org.springboot.udemy.initial.authhandler.model.Role;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findRoleByAppRoleCategory(AppRoleCategory appRoleCategory);
}
