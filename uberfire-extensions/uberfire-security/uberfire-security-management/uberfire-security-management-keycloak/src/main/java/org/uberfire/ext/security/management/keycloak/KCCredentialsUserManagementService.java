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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.UberfireRoleManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.service.AbstractUserManagementService;

/**
 * <p>The KeyCloak management service beans used if Credential authentication settings are provided.</p>
 * @since 0.9.0
 */
@Dependent
@Named(value = "KCCredentialsUserManagementService")
public class KCCredentialsUserManagementService extends AbstractUserManagementService {

    KeyCloakUserManager userManager;
    KeyCloakGroupManager groupManager;
    CredentialsClientFactory clientFactory;

    @Inject
    public KCCredentialsUserManagementService(final KeyCloakUserManager userManager,
                                              final KeyCloakGroupManager groupManager,
                                              final CredentialsClientFactory clientFactory,
                                              final @Named("uberfireRoleManager") UberfireRoleManager roleManager) {
        super(roleManager);
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.clientFactory = clientFactory;
    }

    @PostConstruct
    public void init() {
        clientFactory.init(new ConfigProperties(System.getProperties()));
        this.userManager.init(clientFactory);
        this.groupManager.init(clientFactory);
    }

    @Override
    public UserManager users() {
        return userManager;
    }

    @Override
    public GroupManager groups() {
        return groupManager;
    }
}
