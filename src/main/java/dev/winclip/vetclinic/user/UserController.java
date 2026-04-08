package dev.winclip.vetclinic.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.error.ErrorResponse;
import dev.winclip.vetclinic.user.dto.UserMeResponse;
import dev.winclip.vetclinic.user.dto.UserMeUpdateRequest;
import dev.winclip.vetclinic.user.dto.UserPasswordChangeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@Tag(name = "users", description = "Current user profile")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get my profile", description = "Returns the current authenticated user's profile.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"UNAUTHORIZED","message":"Full authentication is required to access this resource"}
									""")))
	})
	public UserMeResponse me(@AuthenticationPrincipal String username) {
		return userService.getCurrentUserProfile(username);
	}

	@PatchMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Update my profile", description = "Updates email and/or fullName for the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"VALIDATION_FAILED","message":"One field has an invalid value (see the fields map for details)","fields":{"email":"must be a well-formed email address"}}
									"""))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Email already registered",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"DUPLICATE_EMAIL","message":"This email is already registered"}
									""")))
	})
	public UserMeResponse patchMe(
			@AuthenticationPrincipal String username,
			@Valid @RequestBody UserMeUpdateRequest request) {
		return userService.updateCurrentUserProfile(username, request);
	}

	@PutMapping("/me/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Change my password", description = "Changes password for the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Password changed"),
			@ApiResponse(responseCode = "400", description = "Validation failed / new password invalid",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT / current password incorrect",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"UNAUTHORIZED","message":"Current password is incorrect"}
									""")))
	})
	public void changePassword(
			@AuthenticationPrincipal String username,
			@Valid @RequestBody UserPasswordChangeRequest request) {
		userService.changePassword(username, request);
	}
}
