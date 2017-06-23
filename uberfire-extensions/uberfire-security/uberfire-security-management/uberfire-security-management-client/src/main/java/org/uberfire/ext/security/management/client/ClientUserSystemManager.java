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

package org.uberfire.ext.security.management.client;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.GroupManagerSettings;
import org.uberfire.ext.security.management.api.RoleManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.UserManagerSettings;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.ext.security.management.api.validation.EntityValidator;
import org.uberfire.ext.security.management.client.validation.ClientGroupValidator;
import org.uberfire.ext.security.management.client.validation.ClientRoleValidator;
import org.uberfire.ext.security.management.client.validation.ClientUserValidator;
import org.uberfire.ext.security.management.impl.UserAttributeImpl;
import org.uberfire.mvp.Command;

/**
 * <p>The main client side manager for the user management stuff.</p>
 * <p>
 * @since 0.8.0
 */
@ApplicationScoped
public class ClientUserSystemManager implements UserSystemManager {

    // User management provider settings are immutable and can be cached to avoid some backend service calls.
    UserManagerSettings userManagerSettings;
    // Group management provider settings are immutable and can be cached to avoid some backend service calls.
    GroupManagerSettings groupManagerSettings;
    // The error presenter.
    ErrorPopupPresenter errorPopupPresenter;
    /**
     * The service caller for the Users Manager.
     */
    private final Caller<UserManagerService> usersManagerService;
    /**
     * The service caller for the Groups Manager.
     */
    private final Caller<GroupManagerService> groupsManagerService;
    /**
     * The service caller for the Users Manager.
     */
    private final Caller<RoleManagerService> rolesManagerService;
    private final ClientSecurityExceptionMessageResolver exceptionMessageResolver;
    private boolean isActive;
    protected final ErrorCallback<Message> loadErrorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message,
                             final Throwable throwable) {
            showError(throwable);
            ClientUserSystemManager.this.isActive = false;
            return false;
        }
    };

    @Inject
    public ClientUserSystemManager(final Caller<UserManagerService> usersManagerService,
                                   final Caller<GroupManagerService> groupsManagerService,
                                   final Caller<RoleManagerService> rolesManagerService,
                                   final ClientSecurityExceptionMessageResolver exceptionMessageResolver,
                                   final ErrorPopupPresenter errorPopupPresenter) {
        this.usersManagerService = usersManagerService;
        this.groupsManagerService = groupsManagerService;
        this.rolesManagerService = rolesManagerService;
        this.exceptionMessageResolver = exceptionMessageResolver;
        this.errorPopupPresenter = errorPopupPresenter;
        this.isActive = false;
    }

    @PostConstruct
    public void initCache() {
        initializeCache(() -> ClientUserSystemManager.this.isActive = true,
                        loadErrorCallback);
    }

    public UserManager users(RemoteCallback<?> remoteCallback,
                             ErrorCallback errorCallback) {
        return usersManagerService.call(remoteCallback,
                                        errorCallback);
    }

    public GroupManager groups(RemoteCallback<?> remoteCallback,
                               ErrorCallback errorCallback) {
        return groupsManagerService.call(remoteCallback,
                                         errorCallback);
    }

    public RoleManager roles(RemoteCallback<?> remoteCallback,
                             ErrorCallback errorCallback) {
        return rolesManagerService.call(remoteCallback,
                                        errorCallback);
    }

    @Override
    public UserManager users() {
        return usersManagerService.call();
    }

    @Override
    public GroupManager groups() {
        return groupsManagerService.call();
    }

    @Override
    public RoleManager roles() {
        return rolesManagerService.call();
    }

    public boolean isUserCapabilityEnabled(final Capability capability) {
        if (userManagerSettings != null) {
            return isCapabilityEnabled(userManagerSettings.getCapabilities(),
                                       capability);
        }
        return false;
    }

    public Collection<UserManager.UserAttribute> getUserSupportedAttributes() {
        return userManagerSettings.getSupportedAttributes();
    }

    public UserManager.UserAttribute getUserSupportedAttribute(final String attributeName) {
        if (attributeName != null && userManagerSettings != null &&
                userManagerSettings.getSupportedAttributes() != null) {
            for (final UserManager.UserAttribute attribute : userManagerSettings.getSupportedAttributes()) {
                if (attributeName.equals(attribute.getName())) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public boolean isGroupCapabilityEnabled(final Capability capability) {
        if (groupManagerSettings != null) {
            return isCapabilityEnabled(groupManagerSettings.getCapabilities(),
                                       capability);
        }
        return false;
    }

    public boolean isCapabilityEnabled(final Map<Capability, CapabilityStatus> capabilities,
                                       final Capability capability) {
        if (capabilities != null) {
            final CapabilityStatus status = capabilities.get(capability);
            return status != null && CapabilityStatus.ENABLED.equals(status);
        }
        return false;
    }

    public Collection<String> getConstrainedGroups() {
        return groupManagerSettings != null ? groupManagerSettings.getConstrainedGroups() : null;
    }

    public User createUser(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return new UserImpl(identifier);
    }

    public UserManager.UserAttribute createUserAttribute(final String name,
                                                         final boolean isMandatory,
                                                         boolean isEditable,
                                                         final String defaultValue) {
        if (name == null) {
            return null;
        }
        return new UserAttributeImpl(name,
                                     isMandatory,
                                     isEditable,
                                     defaultValue);
    }

    public Group createGroup(final String name) {
        if (name == null) {
            return null;
        }
        return new GroupImpl(name);
    }

    public Role createRole(final String name) {
        if (name == null) {
            return null;
        }
        return new RoleImpl(name);
    }

    @Override
    public EntityValidator<User> usersValidator() {
        return new ClientUserValidator();
    }

    @Override
    public EntityValidator<Group> groupsValidator() {
        return new ClientGroupValidator();
    }

    @Override
    public EntityValidator<Role> rolesValidator() {
        return new ClientRoleValidator();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    /**
     * Executes the command argument when services are initialized, you can.check the <code>isActive()</code> method after the command execution to check it services are up.
     * The command argument is ensured to be executed whether services are up or when the initialization has failed.
     * @param command The command executed when the initialization has finished.
     */
    public void waitForInitialization(final Command command) {
        if (null != command) {
            initializeCache(command,
                            (message, throwable) -> {
                                command.execute();
                                return false;
                            });
        }
    }

    public UserManagerSettings getUserManagerSettings() {
        return userManagerSettings;
    }

    public GroupManagerSettings getGroupManagerSettings() {
        return groupManagerSettings;
    }

    private void initializeCache(final Command command,
                                 final ErrorCallback<Message> errorCallback) {
        // Load user & group management providers' settings.
        loadUserSettings(() -> loadGroupSettings(new Command() {
                                                     @Override
                                                     public void execute() {
                                                         command.execute();
                                                     }
                                                 },
                                                 errorCallback),
                         errorCallback);
    }

    /**
     * Loads the user management provider's settings into cache.
     * @param callback Load finished callback.
     */
    private void loadUserSettings(final Command callback,
                                  final ErrorCallback<Message> errorCallback) {
        if (null == userManagerSettings) {
            usersManagerService.call(new RemoteCallback<UserManagerSettings>() {
                                         @Override
                                         public void callback(final UserManagerSettings userManagerSettings) {
                                             ClientUserSystemManager.this.userManagerSettings = userManagerSettings;
                                             if (null != callback) {
                                                 callback.execute();
                                             }
                                         }
                                     },
                                     errorCallback).getSettings();
        } else {
            callback.execute();
        }
    }

    /**
     * Loads the group management provider's settings into cache.
     * @param callback Load finished callback.
     */
    private void loadGroupSettings(final Command callback,
                                   final ErrorCallback<Message> errorCallback) {
        if (null == groupManagerSettings) {
            groupsManagerService.call(new RemoteCallback<GroupManagerSettings>() {
                                          @Override
                                          public void callback(final GroupManagerSettings groupManagerSettings) {
                                              ClientUserSystemManager.this.groupManagerSettings = groupManagerSettings;
                                              if (null != callback) {
                                                  callback.execute();
                                              }
                                          }
                                      },
                                      errorCallback).getSettings();
        } else {
            callback.execute();
        }
    }

    void showError(final Throwable throwable) {
        exceptionMessageResolver.consumeExceptionMessage(throwable,
                                                         errorPopupPresenter::showMessage);
    }
}
