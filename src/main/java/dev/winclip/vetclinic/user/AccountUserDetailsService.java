package dev.winclip.vetclinic.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User account = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("no such user: " + username));
		return org.springframework.security.core.userdetails.User.builder()
				.username(account.getUsername())
				.password(account.getPasswordHash())
				.roles(account.getRole().name())
				.build();
	}
}
