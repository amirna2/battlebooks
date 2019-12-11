package com.example.battlebooks.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;

import com.example.battlebooks.security.AuthenticationManager;
import com.example.battlebooks.security.SecurityContextRepository;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

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
        .and()
        .logout()
        .and().build();
		
		/*
		return http
				.exceptionHandling()
					.authenticationEntryPoint((serverWebExchange, exception) -> {
						return Mono.fromRunnable(() -> {
							serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
						});
					}).accessDeniedHandler((serverWebExchange, exception) -> {
						return Mono.fromRunnable(() -> {
							serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
						});
					})
				.and()
				
				.csrf().accessDeniedHandler((serverWebExchange, exception) -> {
					return Mono.fromRunnable(() -> {
						serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
					});
				}).and()
				
				.formLogin().disable()
				.httpBasic().disable()
				.authenticationManager(authenticationManager)
				.securityContextRepository(securityContextRepository)
				.authorizeExchange()
				.pathMatchers(HttpMethod.OPTIONS).permitAll()
				.pathMatchers(pathPatterns).permitAll()
				.anyExchange().authenticated()
				.and().build();
		*/
	}
}
