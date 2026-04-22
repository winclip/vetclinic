package dev.winclip.vetclinic.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.winclip.vetclinic.error.ErrorResponse;
import dev.winclip.vetclinic.support.AbstractIntegrationTest;

class AuthFlowIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private TestRestTemplate http;

	@Test
	void registerThenLoginReturnsAccessToken() {
		RegisterRequest register = new RegisterRequest(
				"john_wick",
				"top-secret-1",
				"john.wick@continental.example",
				"John Wick");

		ResponseEntity<RegisterResponse> registered = http.postForEntity(
				"/api/auth/register", register, RegisterResponse.class);

		assertThat(registered.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(registered.getBody()).isNotNull();
		assertThat(registered.getBody().username()).isEqualTo("john_wick");

		LoginRequest login = new LoginRequest("john_wick", "top-secret-1");
		ResponseEntity<LoginResponse> loggedIn = http.postForEntity(
				"/api/auth/login", login, LoginResponse.class);

		assertThat(loggedIn.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(loggedIn.getBody()).isNotNull();
		assertThat(loggedIn.getBody().accessToken()).isNotBlank();
		assertThat(loggedIn.getBody().tokenType()).isEqualTo("Bearer");
	}

	@Test
	void loginWithWrongPasswordReturnsUnauthorized() {
		RegisterRequest register = new RegisterRequest(
				"winston_scott",
				"top-secret-2",
				"winston@continental.example",
				"Winston Scott");
		http.postForEntity("/api/auth/register", register, RegisterResponse.class);

		LoginRequest badLogin = new LoginRequest("winston_scott", "wrong-password");
		ResponseEntity<ErrorResponse> response = http.postForEntity(
				"/api/auth/login", badLogin, ErrorResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().code()).isEqualTo("INVALID_CREDENTIALS");
	}
}
