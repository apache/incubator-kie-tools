/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.backend.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.FailedAuthenticationException;
import org.jboss.errai.security.shared.service.AuthenticationService;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class BasicAuthSecurityFilter implements Filter {

    public static final String REALM_NAME_PARAM = "realmName";
    public static final String INVALIDATE_PARAM = "invalidate";
    public static final String EXCEPTION_PATHS = "excludedPaths";
    public static final String ALLOW_OPTIONS = "allowOptions";

    private static final String OPTIONS = "OPTIONS";

    @Inject
    AuthenticationService authenticationService;

    private String realmName = "UberFire Security Extension Default Realm";
    private Boolean invalidate = true;
    private Boolean allowOptions = true;
    private Set<String> excludedPaths = new HashSet<>();

    @Override
    public void init(final FilterConfig filterConfig) {
        final String realmName = filterConfig.getInitParameter(REALM_NAME_PARAM);
        if (realmName != null) {
            this.realmName = realmName;
        }
        final String invalidate = filterConfig.getInitParameter(INVALIDATE_PARAM);
        if (invalidate != null) {
            this.invalidate = Boolean.valueOf(invalidate);
        }
        final String excludedPaths = filterConfig.getInitParameter(EXCEPTION_PATHS);
        if (excludedPaths != null) {
            this.excludedPaths = Arrays.stream(excludedPaths.split(",")).filter(s -> !isBlank(s)).collect(toSet());
        }
        final String allowOptions = filterConfig.getInitParameter(ALLOW_OPTIONS);
        if (allowOptions != null) {
            this.allowOptions = Boolean.parseBoolean(allowOptions);
        }
    }

    @Override
    public void destroy() {}

    @Override
    public void doFilter(final ServletRequest _request,
                         final ServletResponse _response,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) _request;
        final HttpServletResponse response = (HttpServletResponse) _response;
        
        if (isExceptionPath(request) || checkOptions(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        final User user = authenticationService.getUser();
        try {
            if (user == null || isAnonymousUser(user)) {
                if (authenticate(request)) {
                    chain.doFilter(request,
                                   response);
                    if (response.isCommitted()) {
                        authenticationService.logout();
                    }
                } else {
                    challengeClient(request,
                                    response);
                }
            } else {
                chain.doFilter(request,
                               response);
            }
        } finally {
            // invalidate session only when it did not exists before this request,
            // it was created as part of this request and filter is configured to invalidate.
            if (session == null && invalidate) {
                session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
            }
        }
    }

    private boolean checkOptions(HttpServletRequest request) {
        return OPTIONS.equalsIgnoreCase(request.getMethod()) && allowOptions;
    }

    private boolean isExceptionPath(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        while (requestURI != null && requestURI.endsWith("/")) {
            requestURI = requestURI.substring(0, requestURI.length() - 1);
        }

        if (requestURI != null && requestURI.startsWith(request.getContextPath())) {
            requestURI = requestURI.replaceFirst(request.getContextPath(), "");
        }

        return excludedPaths.contains(requestURI);
    }

    public void challengeClient(final HttpServletRequest request,
                                final HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate",
                           "Basic realm=\"" + this.realmName + "\"");

        // this usually means we have a failing authentication request from an ajax client. so we return SC_FORBIDDEN instead.
        if (isAjaxRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean authenticate(final HttpServletRequest req) {
        final String authHead = req.getHeader("Authorization");

        if (authHead != null) {
            final int index = authHead.indexOf(' ');
            final String[] credentials = new String(Base64.decodeBase64(authHead.substring(index)),
                                                    Charsets.UTF_8).split(":",
                                                                          -1);

            try {
                authenticationService.login(credentials[0],
                                            credentials[1]);
                return true;
            } catch (final FailedAuthenticationException e) {
                return false;
            }
        }

        return false;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    private boolean isAnonymousUser(User user) {
        if (user.getRoles().size() != 1) {
            return false;
        }
        String roleName = user.getRoles().iterator().next().getName();
        return User.ANONYMOUS.getIdentifier().equals(user.getIdentifier()) &&
               Role.NOBODY.getName().equals(roleName);
    }
}