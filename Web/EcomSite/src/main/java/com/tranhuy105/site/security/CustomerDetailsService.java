package com.tranhuy105.site.security;

import com.tranhuy105.site.security.CustomerDetails;
import com.tranhuy105.site.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomerDetails(repository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Couldn't find user with email "+username)
        ));
    }
}
