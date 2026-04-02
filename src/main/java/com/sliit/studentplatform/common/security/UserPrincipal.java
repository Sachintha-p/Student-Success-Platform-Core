package com.sliit.studentplatform.common.security;

import com.sliit.studentplatform.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter between the domain {@link User} entity and Spring Security's
 * {@link UserDetails} contract.
 *
 * <p>
 * Injected via {@code @AuthenticationPrincipal UserPrincipal currentUser}
 * in controller method parameters.
 */
@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

  private final Long id;
  private final String email;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean enabled;

  /**
   * Factory method — creates a {@link UserPrincipal} from a domain {@link User}.
   *
   * @param user the authenticated domain user
   * @return populated {@link UserPrincipal}
   */
  public static UserPrincipal create(User user) {
    // CRITICAL RBAC LOGIC:
    // This takes the database role (e.g., "ADMIN") and adds "ROLE_" to it (e.g., "ROLE_ADMIN").
    // This perfectly matches the .hasRole("ADMIN") rule we put in your SecurityConfig!
    List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

    return UserPrincipal.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .enabled(user.isEnabled())
            .build();
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}