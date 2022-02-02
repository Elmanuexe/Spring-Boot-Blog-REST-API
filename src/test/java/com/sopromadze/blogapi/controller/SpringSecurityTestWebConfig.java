package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CustomUserDetailsService;
import com.sopromadze.blogapi.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@TestConfiguration
public class SpringSecurityTestWebConfig {

    @Bean("CustomUserDetailsServiceImpl")
    @Primary
    public UserDetailsService userDetailsService() {

        UserPrincipal user = new UserPrincipal(1L,"user", "user","user","user@gmail.com", "user", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        UserPrincipal admin = new UserPrincipal(1L,"admin", "admin","admin","admin@gmail.com", "admin", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        return new InMemoryUserDetailsManager(List.of(admin,user));
    }
}
