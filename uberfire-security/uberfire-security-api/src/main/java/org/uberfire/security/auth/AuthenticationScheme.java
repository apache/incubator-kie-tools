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

package org.uberfire.security.auth;

import org.uberfire.security.SecurityContext;

/**
 * An authentication is responsible for extracting credentials provided by the
 * client in an HTTP request. Authentication schemes plug into an
 * {@link AuthenticationManager} and determine how clients can present their
 * authentication information. One AuthenticationManager has one or more
 * AuthenticationSchemes.
 */
public interface AuthenticationScheme {

    /**
     * Determines if the given request object contains an authentication (login)
     * request according to this authentication scheme.
     *
     * @param context
     *         information about the current request and pending response. Never null.
     * @return true if and only if this authentication scheme can build a
     *         {@link Credential} object from information contained in the given
     *         request.
     */
    boolean isAuthenticationRequest(final SecurityContext context);

    /**
     * Modifies the response to indicate to the client that authentication is
     * required (but was not provided) for the current request. For example, this
     * could send an HTTP 401 Unauthorized response, a redirect to a page with a
     * login form, or whatever is appropriate to this authentication scheme.
     *
     * @param context
     *          information about the current request and pending response. Never
     *          null.
     */
    void challengeClient(final SecurityContext context);

    /**
     * Extracts the user-provided login credential from the given request. This
     * method should only be called after verifying the request contains a
     * credential this authentication scheme recognizes. See
     * {@link #isAuthenticationRequest(SecurityContext)}.
     *
     * @param context
     *          information about the current request and pending response. Never
     *          null.
     * @return a Credential built from information supplied by the client in the
     *         request.
     * @throws AuthenticationException
     *           if the request does not contain a recognized credential.
     */
    Credential buildCredential(final SecurityContext context) throws AuthenticationException;
}
