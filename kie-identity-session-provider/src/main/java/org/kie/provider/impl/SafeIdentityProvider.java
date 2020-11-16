/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.provider.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class SafeIdentityProvider implements IdentityProvider, Serializable {

    private static final Logger logger = LoggerFactory.getLogger( SafeIdentityProvider.class );

    /** generated serial version UID */
    private static final long serialVersionUID = 7709094889603436905L;

    @Inject
    private Instance<User> identityInstance;
    
    @Inject
    private Instance<HttpServletRequest> request;
    
    @Override
    public String getName() {
        if( identityInstance.isUnsatisfied() ) { 
            return getIdentityFromRequest();
        }
          
        // default
        try {
            return identityInstance.get().getIdentifier();
        } catch (Exception e) {
            logger.debug( "Error on getting identity from User bean: " + e.getMessage(), e );
            return getIdentityFromRequest();
        }
    }

    private String getIdentityFromRequest() { 
        if (!request.isUnsatisfied() && request.get().getUserPrincipal() != null) {
            return request.get().getUserPrincipal().getName();
        }
        return UNKNOWN_USER_IDENTITY;
    }
    
    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        if( identityInstance.isUnsatisfied() ) { 
            // TODO: retrieve roles via info in servlet request and JAAS?
            return roles;
        }
        
        // default
        User identity = identityInstance.get();
        final Set<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }

        final Set<Group> ufGroups = identity.getGroups();
        for (Group group : ufGroups) {
            roles.add(group.getName());
        }

        return roles;
    }

    @Override
    public boolean hasRole(String role) {
        if (request.isUnsatisfied()) {
            return request.get().isUserInRole(role);
        }
        return getRoles().contains(role);
    }

}
