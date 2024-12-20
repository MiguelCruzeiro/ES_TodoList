package com.es.todolist.config;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockMyUserSecurityContextFactory.class)
public @interface WithMockMyUser {
  String username() default "username";
  String userSub() default "sub";
  String email() default "email";
}
