package com.github.helendigger.jwtsecurity.configuration;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.*;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Common public endpoints request matcher
     * @return set of public endpoints request matchers
     */
    @Bean
    public RequestMatcher publicEndpointMatcher() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/api/v1/public/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/swagger-ui/index.html"),
                new AntPathRequestMatcher("/error")
        );
    }

    /**
     * Default password encoder
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Default security filter chain
     * @param manager authentication manager
     * @param converter jwt authentication converter
     * @param httpSecurity http security
     * @return configure security chain
     * @throws Exception exception
     */
    @Bean
    public SecurityFilterChain secureFilterChain(AuthenticationManager manager, AuthenticationConverter converter,
                                                 HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.securityMatcher("/**")
                .authorizeHttpRequests(auth ->
                        auth.dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD,
                                        DispatcherType.REQUEST, DispatcherType.ASYNC).permitAll()
                                .requestMatchers(publicEndpointMatcher()).permitAll()
                                .anyRequest().authenticated())
                .authenticationManager(manager)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(tokenFilter(manager, converter), BasicAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    /**
     * Filter that converts bearer authorization to spring authentication
     * @param manager authentication manager in use
     * @param converter converter that converts bearer header to authentication
     * @return filter that processes all request except public endpoints and tries to create authentication
     */
    public AuthenticationFilter tokenFilter(AuthenticationManager manager, AuthenticationConverter converter) {
        var filter = new AuthenticationFilter(manager, converter);
        filter.setRequestMatcher(
                new AndRequestMatcher(
                        AnyRequestMatcher.INSTANCE,
                        new NegatedRequestMatcher(publicEndpointMatcher())
                )
        );
        filter.setSuccessHandler((request, response, auth) -> {});
        return filter;
    }

    /**
     * Default authentication manager as providers managers
     * @param providerList authentication providers registered inside application
     * @return authentication manager
     */
    @Bean
    public AuthenticationManager defaultManager(List<AuthenticationProvider> providerList) {
        return new ProviderManager(providerList);
    }
}
