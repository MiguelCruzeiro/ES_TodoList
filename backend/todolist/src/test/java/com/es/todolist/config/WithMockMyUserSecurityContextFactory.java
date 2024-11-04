package com.es.todolist.config;

import com.es.todolist.configuration.CustomUserDetails;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class WithMockMyUserSecurityContextFactory implements WithSecurityContextFactory<WithMockMyUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockMyUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    CustomUserDetails principal = new CustomUserDetails(customUser.userSub(), customUser.username(), customUser.email());
    UsernamePasswordAuthenticationToken auth =
      new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());

    context.setAuthentication(auth);
    return context;
  }
}
