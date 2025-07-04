package com.CafeSystem.cafe.security;

import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HandleException("Sorry, Email Not Found"));

//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getEmail())
//                .password(user.getPassword())
//                .roles((user.getRole()).name())
//                .build();

        return new CustomUserDetails(user);

    }

}
