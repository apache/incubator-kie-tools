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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(final List<String> messages) {
        super(fromListToString(messages));
    }

    public AuthenticationException(final String message) {
        super(message);
    }

    public AuthenticationException(final Exception e) {
        super(e);
    }

    public AuthenticationException(final String message, final Exception ex) {
        super(message, ex);
    }

    private static String fromListToString(final List<String> messages) {
        final StringBuilder sb = new StringBuilder();
        if (messages != null && !messages.isEmpty()) {
            for (final String message : messages) {
                sb.append(message).append('\n');
            }
        }
        return sb.toString();
    }
}