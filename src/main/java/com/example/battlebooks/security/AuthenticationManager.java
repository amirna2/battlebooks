package com.example.battlebooks.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

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
		
	    
		if (username != null && !tokenProvider.isTokenExpired(authToken)) {
			Claims claims = tokenProvider.getAllClaimsFromToken(authToken);
	         logger.info("Authenticating claims:{} ", claims.toString());

		    logger.info("Authenticating user:{} with authentication:{}", username, authentication.toString());

		    // NOTE: For now this assumes single roles only. For multiple roles we need to split the roles string
		    // and add them all to the set.
			String roles = claims.get("scopes", String.class);
		    Set<GrantedAuthority> authorities = new HashSet<> ();
		    authorities.add (new SimpleGrantedAuthority (roles));
		    
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				username,
				null,
				authorities);
			return Mono.just(auth);
		} else {
			return Mono.empty();
		}
	}
}