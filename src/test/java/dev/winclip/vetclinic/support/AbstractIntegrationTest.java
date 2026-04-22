package dev.winclip.vetclinic.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import dev.winclip.vetclinic.auth.LoginRequest;
import dev.winclip.vetclinic.auth.LoginResponse;
import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserRepository;
import dev.winclip.vetclinic.user.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

	@ServiceConnection
	static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

	static {
		POSTGRES.start();
	}

	@Autowired
	protected TestRestTemplate http;

	@Autowired
	protected UserRepository users;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	protected String createAdmin(String username, String rawPassword) {
		User admin = new User();
		admin.setUsername(username);
		admin.setEmail(username + "@continental.example");
		admin.setFullName(username);
		admin.setPasswordHash(passwordEncoder.encode(rawPassword));
		admin.setRole(UserRole.ADMIN);
		users.save(admin);
		return loginAndGetBearer(username, rawPassword);
	}

	protected String loginAndGetBearer(String username, String rawPassword) {
		LoginRequest login = new LoginRequest(username, rawPassword);
		ResponseEntity<LoginResponse> response = http.postForEntity(
				"/api/auth/login", login, LoginResponse.class);
		if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
			throw new IllegalStateException("Login failed for " + username + ": " + response.getStatusCode());
		}
		return "Bearer " + response.getBody().accessToken();
	}

	protected HttpHeaders authHeaders(String bearer) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, bearer);
		return headers;
	}

	protected <T, R> ResponseEntity<R> exchange(HttpMethod method, String path, String bearer, T body, Class<R> responseType) {
		HttpEntity<T> entity = new HttpEntity<>(body, authHeaders(bearer));
		return http.exchange(path, method, entity, responseType);
	}
}
