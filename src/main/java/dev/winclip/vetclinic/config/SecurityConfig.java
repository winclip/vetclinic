package dev.winclip.vetclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dev.winclip.vetclinic.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/doctors", "/api/doctors/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/doctors/*/working-hours").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
						.requestMatchers(HttpMethod.PATCH, "/api/users/me").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/users/me/password").authenticated()
						.requestMatchers("/api/pets/me/**").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/appointments/me").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/appointments").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/appointments/*/cancel").authenticated()
						.requestMatchers(HttpMethod.PATCH, "/api/appointments/*").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/admin/pets").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/admin/pets/*/restore").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/doctors").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/doctors/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/doctors/*/working-hours").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/doctors/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/admin/users").hasRole("ADMIN")
						.requestMatchers("/error").permitAll()
						.anyRequest().denyAll());
		return http.build();
	}
}
