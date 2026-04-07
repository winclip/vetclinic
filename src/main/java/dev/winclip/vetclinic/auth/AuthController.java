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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserService userService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
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
