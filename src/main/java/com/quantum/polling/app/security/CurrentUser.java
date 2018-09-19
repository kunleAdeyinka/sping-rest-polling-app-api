package com.quantum.polling.app.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * Spring security provides an annotation called @AuthenticationPrincipal to access the currently authenticated user in the controllers.
 * CurrentUser annotation is a wrapper around @AuthenticationPrincipal annotation so that we don’t get too much tied up of with Spring Security related annotations 
 * everywhere in our project
 * @author Kunle_Adeyinka
 *
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {

}
