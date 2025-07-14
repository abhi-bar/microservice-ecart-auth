package org.springboot.udemy.initial.authhandler.springUser.service;

import org.springboot.udemy.initial.authhandler.model.User;
import org.springboot.udemy.initial.authhandler.repository.UserRepository;
import org.springboot.udemy.initial.authhandler.springUser.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("User " + username + " not found."));

        return UserDetailsImpl.build(user);
    }

}
