package org.springboot.udemy.initial.authhandler.repository;

import org.springboot.udemy.initial.authhandler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String username);

    Boolean existsUserByUserName(String userName);

    Boolean existsUserByEmail(String email);
}
