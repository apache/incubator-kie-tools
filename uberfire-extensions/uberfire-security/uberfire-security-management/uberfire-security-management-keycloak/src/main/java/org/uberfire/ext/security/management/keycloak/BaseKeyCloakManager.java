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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.spi.NotFoundException;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.OperationFailedException;
import org.uberfire.ext.security.management.api.exception.RealmManagementNotAuthorizedException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.security.management.impl.UserAttributeImpl;
import org.uberfire.ext.security.management.keycloak.client.ClientFactory;
import org.uberfire.ext.security.management.keycloak.client.Keycloak;
import org.uberfire.ext.security.management.keycloak.client.resource.RealmResource;
import org.uberfire.ext.security.management.keycloak.client.resource.RoleMappingResource;
import org.uberfire.ext.security.management.keycloak.client.resource.RoleResource;
import org.uberfire.ext.security.management.keycloak.client.resource.UserResource;
import org.uberfire.ext.security.management.keycloak.client.resource.UsersResource;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

public abstract class BaseKeyCloakManager {

    static final int STATUS_NOT_AUTHORIZED = 403;

    protected static final String ATTRIBUTE_USER_ID = "user.id";
    protected static final String ATTRIBUTE_USER_FIRST_NAME = "user.firstName";
    protected static final String ATTRIBUTE_USER_LAST_NAME = "user.lastName";
    protected static final String ATTRIBUTE_USER_ENABLED = "user.enabled";
    protected static final String ATTRIBUTE_USER_EMAIL = "user.email";
    protected static final String ATTRIBUTE_USER_EMAIL_VERIFIED = "user.isEmailVerified";
    protected static final UserManager.UserAttribute USER_ID = new UserAttributeImpl(ATTRIBUTE_USER_ID,
                                                                                     true,
                                                                                     false,
                                                                                     null);
    protected static final UserManager.UserAttribute USER_FIST_NAME = new UserAttributeImpl(ATTRIBUTE_USER_FIRST_NAME,
                                                                                            true,
                                                                                            true,
                                                                                            "First name");
    protected static final UserManager.UserAttribute USER_LAST_NAME = new UserAttributeImpl(ATTRIBUTE_USER_LAST_NAME,
                                                                                            true,
                                                                                            true,
                                                                                            "Last name");
    protected static final UserManager.UserAttribute USER_ENABLED = new UserAttributeImpl(ATTRIBUTE_USER_ENABLED,
                                                                                          true,
                                                                                          true,
                                                                                          "true");
    protected static final UserManager.UserAttribute USER_EMAIL = new UserAttributeImpl(ATTRIBUTE_USER_EMAIL,
                                                                                        false,
                                                                                        true,
                                                                                        "");
    protected static final UserManager.UserAttribute USER_EMAIL_VERIFIED = new UserAttributeImpl(ATTRIBUTE_USER_EMAIL_VERIFIED,
                                                                                                 false,
                                                                                                 true,
                                                                                                 "false");
    protected static final Collection<UserManager.UserAttribute> USER_ATTRIBUTES =
            Arrays.asList(USER_ID,
                          USER_FIST_NAME,
                          USER_LAST_NAME,
                          USER_ENABLED,
                          USER_EMAIL,
                          USER_EMAIL_VERIFIED);
    private static final Logger LOG = LoggerFactory.getLogger(BaseKeyCloakManager.class);
    protected ClientFactory factory;

    protected void init(ClientFactory factory) {
        this.factory = factory;
    }

    protected synchronized Keycloak getKeyCloakInstance() {
        return factory.get();
    }

    protected void consumeRealm(final Consumer<RealmResource> consumer) {
        try {
            consumer.accept(getRealmResource());
        } catch (ClientResponseFailure e) {
            if (STATUS_NOT_AUTHORIZED == e.getResponse().getResponseStatus().getStatusCode()) {
                throw new RealmManagementNotAuthorizedException(getKeyCloakInstance().getRealm());
            } else {
                throw new SecurityManagementException(e);
            }
        }
    }

    private RealmResource getRealmResource() {
        return getKeyCloakInstance().realm();
    }

    protected AbstractEntityManager.SearchRequest getSearchRequest(final AbstractEntityManager.SearchRequest request) {
        return request != null ? request : new SearchRequestImpl();
    }

    protected User createUser(UserRepresentation userRepresentation) {
        return createUser(userRepresentation,
                          null,
                          null);
    }

    protected User createUser(UserRepresentation userRepresentation,
                              Set<Group> groups,
                              Set<Role> roles) {
        if (userRepresentation != null) {
            String username = userRepresentation.getUsername();
            final User user = SecurityManagementUtils.createUser(username,
                                                                 groups,
                                                                 roles);
            fillUserAttributes(user,
                               userRepresentation);
            return user;
        }
        return null;
    }

    protected Group createGroup(RoleRepresentation roleRepresentation) {
        if (roleRepresentation != null) {
            String name = roleRepresentation.getName();
            ;
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
                        SecurityManagementUtils.populateGroupOrRoles(name,
                                                                     registeredRoles,
                                                                     _groups,
                                                                     _roles);
                    }
                }
                return new Set[]{_groups, _roles};
            }
        }
        return null;
    }

    protected void fillUserAttributes(final User user,
                                      final UserRepresentation userRepresentation) {
        final String userId = userRepresentation.getId();
        final String firstName = userRepresentation.getFirstName();
        final String lastName = userRepresentation.getLastName();
        final String email = userRepresentation.getEmail();
        final boolean isEmailVerified = userRepresentation.isEmailVerified();
        final boolean isEnabled = userRepresentation.isEnabled();
        user.setProperty(ATTRIBUTE_USER_ID,
                         userId);
        user.setProperty(ATTRIBUTE_USER_FIRST_NAME,
                         firstName);
        user.setProperty(ATTRIBUTE_USER_LAST_NAME,
                         lastName);
        user.setProperty(ATTRIBUTE_USER_EMAIL,
                         email);
        user.setProperty(ATTRIBUTE_USER_EMAIL_VERIFIED,
                         Boolean.toString(isEmailVerified));
        user.setProperty(ATTRIBUTE_USER_ENABLED,
                         Boolean.toString(isEnabled));
        final Map<String, List<String>> attrs = userRepresentation.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            for (final Map.Entry<String, List<String>> entry : attrs.entrySet()) {
                final String v = entry.getValue() != null ? String.join(", ",entry.getValue()) : null;
                user.setProperty(entry.getKey(), v);
            }
        }
    }

    protected void fillUserRepresentationAttributes(final User user,
                                                    final UserRepresentation userRepresentation) {
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
                    userRepresentation.singleAttribute(key,
                                                       value);
                }
            }
        }
    }

    protected UserResource getUserResource(UsersResource usersResource,
                                           String username) {
        List<UserRepresentation> userRepresentations = usersResource.search(username,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            0,
                                                                            1);
        if (userRepresentations == null || userRepresentations.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        String id = userRepresentations.get(0).getId();
        return usersResource.get(id);
    }

    protected RoleRepresentation getRoleRepresentation(String name,
                                                       RoleResource roleResource) {
        if (roleResource != null) {
            try {
                return roleResource.toRepresentation();
            } catch (NotFoundException e) {
                throw new GroupNotFoundException(name);
            } catch (ClientResponseFailure clientResponseFailure) {
                int status = clientResponseFailure.getResponse().getResponseStatus().getStatusCode();
                if (404 == status) {
                    throw new GroupNotFoundException(name);
                }
            } catch (Exception e) {
                throw new SecurityManagementException(e);
            }
        }
        throw new GroupNotFoundException(name);
    }

    protected void handleResponse(ClientResponse response) {
        if (response != null) {
            int status = response.getStatus();
            response.releaseConnection();

            if (status >= 400) {
                throw new OperationFailedException(status,
                                                   "Operation failed. See server log messages.");
            }
        }
    }
}
