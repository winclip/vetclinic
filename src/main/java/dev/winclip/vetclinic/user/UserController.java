package dev.winclip.vetclinic.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.user.dto.UserMeResponse;
import dev.winclip.vetclinic.user.dto.UserMeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public UserMeResponse me(@AuthenticationPrincipal String username) {
		return userService.getCurrentUserProfile(username);
	}

	@PatchMapping("/me")
	public UserMeResponse patchMe(
			@AuthenticationPrincipal String username,
			@Valid @RequestBody UserMeUpdateRequest request) {
		return userService.updateCurrentUserProfile(username, request);
	}
}
