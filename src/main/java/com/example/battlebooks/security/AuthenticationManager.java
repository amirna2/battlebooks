package com.example.battlebooks.security;

import io.jsonwebtoken.Claims;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

	@Autowired
	private TokenProvider tokenProvider;
	
    final Logger logger = LogManager.getLogger(AuthenticationManager.class.getSimpleName());

    public AuthenticationManager() {
    	logger.info("AuthenticationManager created");
    }
    
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		
		String username;
		try {
			username = tokenProvider.getUsernameFromToken(authToken);
		} catch (Exception e) {
			username = null;
		}
		if (username != null && tokenProvider.isTokenExpired(authToken)) {
			Claims claims = tokenProvider.getAllClaimsFromToken(authToken);
			@SuppressWarnings("unchecked")
			List<String> rolesMap = claims.get("role", List.class);
			List<Role> roles = new ArrayList<>();
			rolesMap.forEach(role -> roles.add(Role.valueOf(role)));

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				username,
				null,
				roles.stream().map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList())
			);
			return Mono.just(auth);
		} else {
			return Mono.empty();
		}
	}
}