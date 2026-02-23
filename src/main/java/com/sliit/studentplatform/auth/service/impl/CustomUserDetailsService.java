package com.sliit.studentplatform.auth.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security {@link UserDetailsService} implementation.
 *
 * <p>
 * Loads a user by their database ID (stored as the JWT subject) or email.
 * Used by the authentication framework and the JWT filter.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads a user by their ID (numeric string) or email.
   *
   * <p>
   * When loading from the JWT filter, {@code username} is the user's ID.
   * When loading during form-based authentication, it may be an email.
   *
   * @param username user ID (as string) or email
   * @return Spring Security UserDetails
   * @throws UsernameNotFoundException if no matching user is found
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user;

    try {
      Long userId = Long.parseLong(username);
      user = userRepository.findById(userId)
          .orElseThrow(() -> new UsernameNotFoundException(
              "User not found with id: " + userId));
    } catch (NumberFormatException ex) {
      // Fall back to email lookup (e.g., during form login)
      user = userRepository.findByEmail(username)
          .orElseThrow(() -> new UsernameNotFoundException(
              "User not found with email: " + username));
    }

    return UserPrincipal.create(user);
  }
}
