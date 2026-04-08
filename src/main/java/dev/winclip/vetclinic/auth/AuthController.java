package dev.winclip.vetclinic.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserRole;
import dev.winclip.vetclinic.user.UserService;
import dev.winclip.vetclinic.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth", description = "Registration and login")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserService userService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register user", description = "Creates a new USER account and returns basic profile info.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"VALIDATION_FAILED","message":"One field has an invalid value (see the fields map for details)","fields":{"username":"size must be between 4 and 64"}}
									"""))),
			@ApiResponse(responseCode = "409", description = "Duplicate username or email",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"DUPLICATE_EMAIL","message":"This email is already registered"}
									""")))
	})
	public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
		User user = userService.register(
				request.username(),
				request.password(),
				UserRole.USER,
				request.email(),
				request.fullName());
		return new RegisterResponse(
				user.getUsername(),
				user.getRole().name(),
				user.getEmail(),
				user.getFullName());
	}

	@PostMapping("/login")
	@Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT access token.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"VALIDATION_FAILED","message":"One field has an invalid value (see the fields map for details)","fields":{"username":"must not be blank"}}
									"""))),
			@ApiResponse(responseCode = "401", description = "Invalid credentials",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"INVALID_CREDENTIALS","message":"Invalid username or password"}
									""")))
	})
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		UserDetails user = (UserDetails) auth.getPrincipal();
		String role = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(a -> a.startsWith("ROLE_"))
				.map(a -> a.substring("ROLE_".length()))
				.findFirst()
				.orElse("USER");
		String accessToken = jwtService.generateToken(user.getUsername(), role);
		return new LoginResponse(accessToken, "Bearer");
	}
}
