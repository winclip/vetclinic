package dev.winclip.vetclinic.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User register(String username, String rawPassword, UserRole role) {
		if (userRepository.existsByUsername(username)) {
			throw new DuplicateUserException("DUPLICATE_USERNAME", "This username is already taken");
		}
		User user = new User();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(rawPassword));
		user.setRole(role);
		return userRepository.save(user);
	}
}
