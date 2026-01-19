package com.iknow.iflowtracksysproxy.config;

import com.iknow.iflowtracksysproxy.repository.UserRepository;
import com.iknow.iflowtracksysproxy.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtUtil.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userOptional = userRepository.findByEmail(userEmail);

                if (userOptional.isPresent() && jwtUtil.validateToken(jwt, userEmail)) {
                    var user = userOptional.get();
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail,
                            null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token, continue without authentication
        }

        filterChain.doFilter(request, response);
    }
}
