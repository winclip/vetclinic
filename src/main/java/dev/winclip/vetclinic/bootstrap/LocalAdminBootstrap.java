package dev.winclip.vetclinic.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import dev.winclip.vetclinic.user.UserRepository;
import dev.winclip.vetclinic.user.UserRole;
import dev.winclip.vetclinic.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalAdminBootstrap implements ApplicationRunner {

	private final UserRepository userRepository;
	private final UserService userService;

	@Value("${vetclinic.bootstrap.enabled:true}")
	private boolean enabled;

	@Value("${vetclinic.bootstrap.admin-username:admin}")
	private String adminUsername;

	@Value("${vetclinic.bootstrap.admin-password:local-dev}")
	private String adminPassword;

	@Override
	public void run(ApplicationArguments args) {
		if (!enabled) {
			log.info("vetclinic bootstrap: skipped, disabled in config");
			return;
		}
		long userCount = userRepository.count();
		if (userCount > 0) {
			log.info("vetclinic bootstrap: skipped, already {} user(s) in db", userCount);
			return;
		}
		if (adminPassword.isBlank()) {
			log.warn("vetclinic bootstrap: admin password empty, not creating user");
			return;
		}
		userService.register(adminUsername, adminPassword, UserRole.ADMIN);
		log.info("vetclinic bootstrap: ok, seeded {}", adminUsername);
	}
}
