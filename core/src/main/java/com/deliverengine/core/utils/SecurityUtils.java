package com.deliverengine.core.utils;

import com.deliverengine.core.enumeration.AuthoritiesConstants;
import com.deliverengine.core.enumeration.RolesConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


public class SecurityUtils {

    private SecurityUtils() {

    }

    public static RolesConstants currentRole() {
        var role = (String) getSecurityContextDetails().orElse(Collections.EMPTY_MAP).get("role");
        return role == null ? null : RolesConstants.valueOf(role);
    }

    public static Boolean isCurrentUserInPermission(AuthoritiesConstants authority) {
        String authorities = (String) Optional.ofNullable(getSecurityContextDetails().orElseThrow().get("authorities")).orElseThrow();
        return Arrays.stream(authorities.split(",")).map(AuthoritiesConstants::valueOf).anyMatch(authority::equals);
    }

    public static String currentUserLoginOrException() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                }
            )
            .orElseThrow();
    }

    private static Optional<Map<String, Object>> getSecurityContextDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) return Optional.empty();
        return Optional.of((Map<String, Object>) authentication.getDetails());
    }

}
