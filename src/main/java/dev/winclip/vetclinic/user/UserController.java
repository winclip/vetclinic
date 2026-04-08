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

import dev.winclip.vetclinic.user.dto.UserMeResponse;
import dev.winclip.vetclinic.user.dto.UserMeUpdateRequest;
import dev.winclip.vetclinic.user.dto.UserPasswordChangeRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	public UserMeResponse me(@AuthenticationPrincipal String username) {
		return userService.getCurrentUserProfile(username);
	}

	@PatchMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	public UserMeResponse patchMe(
			@AuthenticationPrincipal String username,
			@Valid @RequestBody UserMeUpdateRequest request) {
		return userService.updateCurrentUserProfile(username, request);
	}

	@PutMapping("/me/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	public void changePassword(
			@AuthenticationPrincipal String username,
			@Valid @RequestBody UserPasswordChangeRequest request) {
		userService.changePassword(username, request);
	}
}
