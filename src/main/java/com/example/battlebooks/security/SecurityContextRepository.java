package com.example.battlebooks.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	private static final String TOKEN_PREFIX = "Bearer ";
    final Logger logger = LogManager.getLogger(SecurityContextRepository.class.getSimpleName());

    public SecurityContextRepository() {
    }
	@Override
	public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange swe) {
		
		ServerHttpRequest request = swe.getRequest();
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String authToken = null;
		if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
			authToken = authHeader.replace(TOKEN_PREFIX, "");
		}
		
		if (authToken != null) {
			Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
			return this.authenticationManager.authenticate(auth).map((authentication) -> new SecurityContextImpl(authentication));
		} else {
			return Mono.empty();
		}
	}
}