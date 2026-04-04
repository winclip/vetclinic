package dev.winclip.vetclinic.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.auth.RegisterResponse;
import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RegisterResponse createUser(@Valid @RequestBody AdminUserCreateRequest request) {
		User user = userService.register(
				request.username(),
				request.password(),
				request.role(),
				request.email(),
				request.fullName());
		return new RegisterResponse(
				user.getUsername(),
				user.getRole().name(),
				user.getEmail(),
				user.getFullName());
	}
}
