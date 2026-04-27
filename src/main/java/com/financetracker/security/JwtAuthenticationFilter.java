package com.financetracker.security;

import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Read the authorization header
        String authHeader = request.getHeader("Authorization");

        // Check if it exists and start with Bearer
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token
        String token = authHeader.substring(7);

        // Validate the token
        if(!jwtService.isTokenValid(token)){
            log.warn("Invalid or expired JWT token received");
            filterChain.doFilter(request, response);
            return;
        }
        // Extract user identity from the token
        Long userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        // Load the user from the database
        // It verifies if the user exists in the database

        User user = userRepository.findById(userId).orElse(null);
        if(user == null || !user.isActive()){
            log.warn("Token valid but the user not found or inactive: useId={}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        // Create authentication object: it tells spring security who's this user
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + role ))
                );

        // Add request details to authentication
        // Store Ip address and session details
        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        // Store authentication in securityContext
        // This thread now know who the user is
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        log.debug("Authentication set for userid: {}",userId);

        filterChain.doFilter(request, response);

    }
}
