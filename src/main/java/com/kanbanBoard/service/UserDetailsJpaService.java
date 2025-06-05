package com.kanbanBoard.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kanbanBoard.repository.UserRepository;

@Service
public class UserDetailsJpaService implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsJpaService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.kanbanBoard.entity.User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		String role = user.getRole().replace("ROLE_", "");

		return User.builder().username(user.getUsername()).password(user.getPassword()).roles(role).build();
	}
}
