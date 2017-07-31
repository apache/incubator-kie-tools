/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak.client.auth.adapter;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.uberfire.ext.security.management.keycloak.client.auth.TokenManager;

/**
 * Token manager that uses current session's access token (provided by the KC client adapter)
 * @since 0.9.0.
 */
public class KCAdapterContextTokenManager implements TokenManager {

    private final HttpServletRequest request;

    public KCAdapterContextTokenManager(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void grantToken() {
        // Nothing to do here. Token in session is granted and refresh by the KC client adapter.
    }

    @Override
    public String getAccessTokenString() {
        return getKCSessionContext().getTokenString();
    }

    @Override
    public String getRealm() {
        return getKCSessionContext().getRealm();
    }

    protected KeycloakSecurityContext getKCSessionContext() {
        KeycloakSecurityContext context = null;
    	
    	context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    	if (context == null) {
    		context = (KeycloakSecurityContext) request.getSession().getAttribute(KeycloakSecurityContext.class.getName());
    	}
    	return context;
    }
}
