package com.example.demo.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;


    @Service
public class CustomUserDetailsService implements UserDetailsService { // hna kanimplementiw UserDetailsService li hiya interface mli kanbghiw n3rfou details dyal user li kaydir login

    @Autowired
    private UserRepository userRepository;  // bach naccediw ldata dyal user mn lbdd

    @Override
    public UserDetails loadUserByUsername(String email) 
            throws UsernameNotFoundException { // hadi method li katji mlli user kaydir login u katreceviw email dyalou
 
        
                    User user = userRepository.findByEmail(email); // kan9lbou 3la user b email dyalou
                           // ila ma l9inach user b hadak email kanrj3o exception
                        if(user == null) {
                            throw new UsernameNotFoundException("User not found with email: " + email);
                        }

                    // spring security katst3ml wahed l'objet UserDetails bach tkhlliha t3rf details dyal user li kaydir login
                    return org.springframework.security.core.userdetails.User 
                            .withUsername(user.getEmail())        
                            .password(user.getPassword())
                             .authorities(new ArrayList<>()) // aucune autorité              
                            .build();
    }
}


