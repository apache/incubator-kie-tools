/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.security.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.Identity;
import org.picketlink.authentication.web.HTTPAuthenticationScheme;
import org.picketlink.credential.DefaultLoginCredentials;

/**
 * A modified version of the built-in PicketLink security filter which allows the flexibility of plugging in 3rd-party
 * and user-defined authentication strategies.
 */
public class PicketLinkAuthenticationFilter implements Filter {
    public static final String AUTH_TYPE_INIT_PARAM = "authType";
    public static final String UNPROTECTED_METHODS_INIT_PARAM = "unprotectedMethods";
    public static final String FORCE_REAUTHENTICATION_INIT_PARAM = "forceReAuthentication";
    private final Set<String> unprotectedMethods = new HashSet<String>();
    private boolean forceReAuthentication;

    @Inject
    private Instance<Identity> identityInstance;

    @Inject
    private Instance<DefaultLoginCredentials> credentialsInstance;

    private HTTPAuthenticationScheme authenticationScheme;

    @Override
    public void init(FilterConfig config) throws ServletException {
        initAuthenticationScheme(config);

        String unprotectedMethodsInitParam = config.getInitParameter(UNPROTECTED_METHODS_INIT_PARAM);

        if (unprotectedMethodsInitParam != null) {
            if (unprotectedMethodsInitParam.contains(",")) {
                for (String method : unprotectedMethodsInitParam.split(",")) {
                    this.unprotectedMethods.add(method.trim().toUpperCase());
                }
            } else {
                this.unprotectedMethods.add(unprotectedMethodsInitParam.trim().toUpperCase());
            }
        }

        this.forceReAuthentication = Boolean.valueOf(config.getInitParameter(FORCE_REAUTHENTICATION_INIT_PARAM));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
    ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        Identity identity = getIdentity();

        DefaultLoginCredentials creds = extractCredentials(request);

        if (creds.getCredential() != null && this.forceReAuthentication) {
            identity.logout();
            creds = extractCredentials(request);
        }

        if (isProtected(request) && !identity.isLoggedIn()) {
            // Force session creation
            request.getSession();

            identity.login();

            if (identity.isLoggedIn()) {
                if (this.authenticationScheme.postAuthentication(request, response)) {
                    chain.doFilter(servletRequest, servletResponse);
                }
            } else {
                this.authenticationScheme.challengeClient(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private void initAuthenticationScheme(FilterConfig config) {
        String authTypeName = config.getInitParameter(AUTH_TYPE_INIT_PARAM);

        if (authTypeName == null) {
            throw new IllegalArgumentException(
                    "You must specify an implementation of " + HTTPAuthenticationScheme.class.getName()
                    + " to use. Do this in web.xml, in the init parameter <" + AUTH_TYPE_INIT_PARAM + "> of "
                    + getClass().getName() );
        }

        try {
            Class<?> authenticationScheme = Class.forName(authTypeName);
            Constructor<?> constructor = authenticationScheme.getConstructor(FilterConfig.class);
            this.authenticationScheme = (HTTPAuthenticationScheme) constructor.newInstance(config);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not create authentication scheme instance [" + authTypeName + "]."
                            + " Ensure this is a class that implements " + HTTPAuthenticationScheme.class.getName()
                            + " and has a public constructor that takes one argument of type " + FilterConfig.class.getName()
                            + "." , e);
        }
    }

    private DefaultLoginCredentials extractCredentials(HttpServletRequest request) {
        DefaultLoginCredentials creds = getCredentials();

        this.authenticationScheme.extractCredential(request, creds);

        return creds;
    }

    private DefaultLoginCredentials getCredentials() {
        if (this.credentialsInstance.isUnsatisfied()) {
            throw new IllegalStateException(
                    "DefaultLoginCredentials not found - please ensure that the DefaultLoginCredentials component is created on startup.");
        } else if (this.credentialsInstance.isAmbiguous()) {
            throw new IllegalStateException(
                    "DefaultLoginCredentials is ambiguous. Make sure you have a single @RequestScoped instance.");
        }

        try {
            return credentialsInstance.get();
        } catch (Exception e) {
            throw new IllegalStateException("Could not retrieve credentials.", e);
        }
    }

    private Identity getIdentity() throws ServletException {
        if (this.identityInstance.isUnsatisfied()) {
            throw new IllegalStateException(
                    "Identity not found.");
        } else if (this.identityInstance.isAmbiguous()) {
            throw new IllegalStateException(
                    "Identity is ambiguous.");
        }

        try {
            return identityInstance.get();
        } catch (Exception e) {
            throw new IllegalStateException("Could not retrieve Identity.", e);
        }
    }

    private boolean isProtected(HttpServletRequest request) {
        return !this.unprotectedMethods.contains(request.getMethod().toUpperCase());
    }
}
