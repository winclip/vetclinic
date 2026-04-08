package dev.winclip.vetclinic.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.error.ErrorResponse;
import dev.winclip.vetclinic.auth.RegisterResponse;
import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "admin", description = "Admin endpoints")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Create user", description = "Creates a new user with the requested role (ADMIN only).")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden (ADMIN only)",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Duplicate username/email",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
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
