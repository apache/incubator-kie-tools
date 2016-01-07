/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.OperationFailedException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.security.management.impl.UserAttributeImpl;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;

public abstract class BaseKeyCloakManager {
    private static final Logger LOG = LoggerFactory.getLogger(BaseKeyCloakManager.class);
    private static final String DEFAULT_AUTH_SERVER = "http://localhost:8080/auth";
    private static final String DEFAULT_REALM = "example";
    private static final String DEFAULT_USER = "examples-admin-client";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_CLIENT_ID = "examples-admin-client";
    private static final String DEFAULT_CLIENT_SECRET = "password";
    
    protected static final String ATTRIBUTE_USER_ID = "user.id";
    protected static final String ATTRIBUTE_USER_FIRST_NAME = "user.firstName";
    protected static final String ATTRIBUTE_USER_LAST_NAME = "user.lastName";
    protected static final String ATTRIBUTE_USER_ENABLED = "user.enabled";
    protected static final String ATTRIBUTE_USER_EMAIL = "user.email";
    protected static final String ATTRIBUTE_USER_EMAIL_VERIFIED = "user.isEmailVerified";

    protected static final UserManager.UserAttribute USER_ID = new UserAttributeImpl(ATTRIBUTE_USER_ID, true, false, null);
    protected static final UserManager.UserAttribute USER_FIST_NAME = new UserAttributeImpl(ATTRIBUTE_USER_FIRST_NAME, true, true, "First name");
    protected static final UserManager.UserAttribute USER_LAST_NAME= new UserAttributeImpl(ATTRIBUTE_USER_LAST_NAME, true, true, "Last name");
    protected static final UserManager.UserAttribute USER_ENABLED  = new UserAttributeImpl(ATTRIBUTE_USER_ENABLED, true, true, "true");
    protected static final UserManager.UserAttribute USER_EMAIL = new UserAttributeImpl(ATTRIBUTE_USER_EMAIL, false, true, "");
    protected static final UserManager.UserAttribute USER_EMAIL_VERIFIED = new UserAttributeImpl(ATTRIBUTE_USER_EMAIL_VERIFIED, false, true, "false");
    protected static final Collection<UserManager.UserAttribute> USER_ATTRIBUTES = 
            Arrays.asList(USER_ID, USER_FIST_NAME, USER_LAST_NAME, USER_ENABLED, USER_EMAIL, USER_EMAIL_VERIFIED);
    
    protected Keycloak keycloak;
    protected String authServer;
    protected String realm;
    protected String user;
    protected String password;
    protected String clientId;
    protected String clientPassword;

    protected void loadConfig( final ConfigProperties config ) {
        LOG.debug("Configuring KeyCloak provider from properties.");

        final ConfigProperties.ConfigProperty authServer = config.get("org.uberfire.ext.security.management.keycloak.authServer", DEFAULT_AUTH_SERVER);
        final ConfigProperties.ConfigProperty realm = config.get("org.uberfire.ext.security.management.keycloak.realm", DEFAULT_REALM);
        final ConfigProperties.ConfigProperty user = config.get("org.uberfire.ext.security.management.keycloak.user", DEFAULT_USER);
        final ConfigProperties.ConfigProperty password = config.get("org.uberfire.ext.security.management.keycloak.password", DEFAULT_PASSWORD);
        final ConfigProperties.ConfigProperty clientId = config.get("org.uberfire.ext.security.management.keycloak.clientId", DEFAULT_CLIENT_ID);
        final ConfigProperties.ConfigProperty clientSecret = config.get("org.uberfire.ext.security.management.keycloak.clientSecret", DEFAULT_CLIENT_SECRET);

        // Check mandatory properties.
        if (!isConfigPropertySet(authServer)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.keycloak.authServer' is mandatory and not set.");
        if (!isConfigPropertySet(realm)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.keycloak.realm' is mandatory and not set.");
        if (!isConfigPropertySet(user)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.keycloak.user' is mandatory and not set.");
        if (!isConfigPropertySet(password)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.keycloak.password' is mandatory and not set.");
        if (!isConfigPropertySet(clientId)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.keycloak.clientId' is mandatory and not set.");
        if (!isConfigPropertySet(clientSecret)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.keycloak.clientSecret' is mandatory and not set.");

        this.authServer = authServer.getValue();
        this.realm = realm.getValue();
        this.user = user.getValue();
        this.password = password.getValue();
        this.clientId = clientId.getValue();
        this.clientPassword = clientSecret.getValue();

        LOG.debug("Configuration of KeyCloak provider finished.");
    }

    protected synchronized Keycloak getKeyCloakInstance() {
        if (this.keycloak == null) {
            this.keycloak = Keycloak.getInstance(authServer, realm, user, password, clientId, clientPassword);
        }
        return keycloak;
    }
    
    protected RealmResource getRealmResource() {
        return getKeyCloakInstance().realm(realm);
    }
    
    protected AbstractEntityManager.SearchRequest getSearchRequest(final AbstractEntityManager.SearchRequest request) {
        return request != null ? request : new SearchRequestImpl();
    }

    protected User createUser(UserRepresentation userRepresentation) {
        return createUser(userRepresentation, null, null);
    }

    protected User createUser(UserRepresentation userRepresentation, Set<Group> groups, Set<Role> roles) {
        if (userRepresentation != null) {
            String username = userRepresentation.getUsername();
            final User user = SecurityManagementUtils.createUser(username, groups, roles);
            fillUserAttributes(user, userRepresentation);
            return user;
        }
        return null;
    }
    
    protected Group createGroup(RoleRepresentation roleRepresentation) {
        if (roleRepresentation != null) {
            String name = roleRepresentation.getName();;
            final Group group = createGroup(name);
            return group;
        }
        return null;
    }

    protected Group createGroup(String name) {
        if (name != null) {
            final Group group = SecurityManagementUtils.createGroup(name);
            return group;
        }
        return null;
    }

    protected Set[] getUserGroupsAndRoles(final RoleMappingResource roleMappingResource) {
        if (roleMappingResource != null) {
            List<RoleRepresentation> roles = roleMappingResource.realmLevel().listEffective();
            if (roles != null && !roles.isEmpty()) {
                final Set<Group> _groups = new HashSet<Group>();
                final Set<Role> _roles = new HashSet<Role>();
                final Set<String> registeredRoles = SecurityManagementUtils.getRegisteredRoleNames();
                for (RoleRepresentation roleRepresentation : roles) {
                    if (roleRepresentation != null) {
                        String name = roleRepresentation.getName();
                        SecurityManagementUtils.populateGroupOrRoles(name, registeredRoles, _groups, _roles);
                    }
                }
                return new Set[] { _groups, _roles };
            }
        }
        return null;
    }

    protected void fillUserAttributes(final User user, final UserRepresentation userRepresentation) {
        final String userId = userRepresentation.getId();
        final String firstName = userRepresentation.getFirstName();
        final String lastName = userRepresentation.getLastName();
        final String email = userRepresentation.getEmail();
        final boolean isEmailVerified = userRepresentation.isEmailVerified();
        final boolean isEnabled = userRepresentation.isEnabled();
        user.setProperty(ATTRIBUTE_USER_ID, userId);
        user.setProperty(ATTRIBUTE_USER_FIRST_NAME, firstName);
        user.setProperty(ATTRIBUTE_USER_LAST_NAME, lastName);
        user.setProperty(ATTRIBUTE_USER_EMAIL, email);
        user.setProperty(ATTRIBUTE_USER_EMAIL_VERIFIED, Boolean.toString(isEmailVerified));
        user.setProperty(ATTRIBUTE_USER_ENABLED, Boolean.toString(isEnabled));
        final Map<String, Object> attrs = userRepresentation.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            for (final Map.Entry<String, Object> entry : attrs.entrySet()) {
                final String v = entry.getValue() != null ? entry.getValue().toString() : null;
                user.setProperty(entry.getKey(), v);
            }
        }
    }

    protected void fillUserRepresentationAttributes(final User user, final UserRepresentation userRepresentation) {
        String username = user.getIdentifier();
        userRepresentation.setUsername(username);
        Map<String, String> props = user.getProperties();
        if (props != null && !props.isEmpty()) {
            for (Map.Entry<String, String> entry : props.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                if (ATTRIBUTE_USER_ID.equals(key)) {
                    userRepresentation.setId(value);
                } else if (ATTRIBUTE_USER_FIRST_NAME.equals(key)) {
                    userRepresentation.setFirstName(value);
                } else if (ATTRIBUTE_USER_LAST_NAME.equals(key)) {
                    userRepresentation.setLastName(value);
                } else if (ATTRIBUTE_USER_EMAIL.equals(key)) {
                    userRepresentation.setEmail(value);
                } else if (ATTRIBUTE_USER_EMAIL_VERIFIED.equals(key)) {
                    userRepresentation.setEmailVerified(Boolean.valueOf(value));
                } else if (ATTRIBUTE_USER_ENABLED.equals(key)) {
                    userRepresentation.setEnabled(Boolean.valueOf(value));
                } else {
                    userRepresentation.singleAttribute(key, value);
                }
            }
        }
        
    }

    protected UserResource getUserResource(UsersResource usersResource, String username) {
        List<UserRepresentation> userRepresentations = usersResource.search(username, null, null, null, 0, 1);
        if (userRepresentations == null || userRepresentations.isEmpty()) throw new UserNotFoundException(username);
        String id = userRepresentations.get(0).getId();
        return usersResource.get(id);
    }

    protected RoleRepresentation getRoleRepresentation(String name, RoleResource roleResource) {
        if (roleResource != null) {
            try {
                return roleResource.toRepresentation();
            } catch (NotFoundException e) {
                throw new GroupNotFoundException(name);
            } catch (Exception e) {
                throw new SecurityManagementException(e);
            }
        }
        throw new GroupNotFoundException(name);
    }
    
    protected void handleResponse(Response response) {
        if (response == null) throw new NullPointerException();
        if (response.getStatus() >= 400) throw new OperationFailedException(response.getStatus(), "Operation failed. See server log messages.");
        response.close();
    }

    protected static boolean isConfigPropertySet(ConfigProperties.ConfigProperty property) {
        if (property == null) return false;
        String value = property.getValue();
        return !isEmpty(value);
    }
    
    protected static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

}
