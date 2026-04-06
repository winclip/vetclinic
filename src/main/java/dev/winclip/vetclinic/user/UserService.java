package dev.winclip.vetclinic.user;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.user.dto.UserMeResponse;
import dev.winclip.vetclinic.user.dto.UserMeUpdateRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User register(String username, String rawPassword, UserRole role, String email, String fullName) {
		String normalizedUsername = username.strip();
		String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
		String normalizedFullName = fullName.strip();
		if (normalizedUsername.length() < 4 || normalizedUsername.length() > 64) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Username must be between 4 and 64 characters");
		}
		if (userRepository.existsByUsername(normalizedUsername)) {
			throw new DuplicateUserException("DUPLICATE_USERNAME", "This username is already taken");
		}
		if (userRepository.existsByEmail(normalizedEmail)) {
			throw new DuplicateUserException("DUPLICATE_EMAIL", "This email is already registered");
		}
		User user = new User();
		user.setUsername(normalizedUsername);
		user.setPasswordHash(passwordEncoder.encode(rawPassword));
		user.setRole(role);
		user.setEmail(normalizedEmail);
		user.setFullName(normalizedFullName);
		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public UserMeResponse getCurrentUserProfile(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return new UserMeResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getFullName(),
				user.getRole().name());
	}

	@Transactional
	public UserMeResponse updateCurrentUserProfile(String username, UserMeUpdateRequest request) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		if (request.email() != null) {
			String normalized = request.email().strip().toLowerCase(Locale.ROOT);
			if (normalized.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must not be blank when provided");
			}
			String current = user.getEmail();
			boolean unchanged = current != null && normalized.equals(current);
			if (!unchanged && userRepository.existsByEmail(normalized)) {
				throw new DuplicateUserException("DUPLICATE_EMAIL", "This email is already registered");
			}
			user.setEmail(normalized);
		}
		if (request.fullName() != null) {
			String name = request.fullName().strip();
			if (name.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName must not be blank when provided");
			}
			user.setFullName(name);
		}
		userRepository.save(user);
		return getCurrentUserProfile(username);
	}
}
