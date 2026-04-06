package dev.winclip.vetclinic.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.user.dto.UserMeResponse;
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
}
