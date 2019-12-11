package com.example.battlebooks.security.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;

import com.example.battlebooks.security.AuthenticationManager;
import com.example.battlebooks.security.SecurityContextRepository;

import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    final Logger logger = LogManager.getLogger(WebSecurityConfig.class.getSimpleName());

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private SecurityContextRepository securityContextRepository;

	@Bean
    public ServerCsrfTokenRepository csrfTokenRepository() {
        WebSessionServerCsrfTokenRepository repository =
            new WebSessionServerCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TK");

        return repository;
    }
	
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		String[] permittedPaths = new String[] {"/api/auth/signin", "/api/auth/signup"};
	    
		return http
			.csrf().disable()
			.authenticationManager(authenticationManager)
			.securityContextRepository(securityContextRepository)
	        .authorizeExchange()
			.pathMatchers(HttpMethod.OPTIONS).permitAll()
	        .pathMatchers(permittedPaths).permitAll()
	        .anyExchange().authenticated() // any exchange requires an authenticated user except for the permitted paths
	        .and()
	        .build();
		
	}
}
