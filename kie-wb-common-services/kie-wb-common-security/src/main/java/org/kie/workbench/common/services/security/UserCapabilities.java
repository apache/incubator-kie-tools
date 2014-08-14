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

package org.kie.workbench.common.services.security;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;

/**
 * This is used to turn off GUI functionality. The server decides what should be visible based on roles
 * and permissions granted. This is essentially a security and permissions function (however the Capabilities
 * do not enforce actions on the server - these are more for GUI convenience so elements are not displayed
 * that are not relevant to a given users role).
 */
public final class UserCapabilities {

    private static UserImpl identity = null;

    public static boolean canCreateNewAsset() {
        if ( identity == null ) {
            init();
        }
        return identity.hasAnyRoles( AppRoles.ADMIN.getName(), AppRoles.REPOSITORY_EDITOR.getName() );
    }

    public static boolean canSeeModulesTree() {
        if ( identity == null ) {
            init();
        }
        return identity.hasAnyRoles( AppRoles.ADMIN.getName(), AppRoles.REPOSITORY_EDITOR.getName(), AppRoles.REPOSITORY_VIEWER.getName() );
    }

    public static boolean canSeeStatuses() {
        if ( identity == null ) {
            init();
        }
        return identity.hasAnyRoles( AppRoles.ADMIN.getName(), AppRoles.REPOSITORY_EDITOR.getName() );
    }

    public static boolean canSeeQA() {
        if ( identity == null ) {
            init();
        }
        return identity.hasAnyRoles( AppRoles.ADMIN.getName(), AppRoles.REPOSITORY_EDITOR.getName() );
    }

    public static boolean canSeeDeploymentTree() {
        if ( identity == null ) {
            init();
        }
        return identity.hasAnyRoles( AppRoles.ADMIN.getName(), AppRoles.REPOSITORY_EDITOR.getName() );
    }

    private static void init() {
        if ( identity == null ) {
            final User _identity = IOC.getBeanManager().lookupBean( User.class ).getInstance();
            if ( _identity instanceof UserImpl ) {
                identity = (UserImpl) _identity;
            } else {
                identity = new UserImpl( _identity.getIdentifier(), _identity.getRoles(), _identity.getGroups(), _identity.getProperties() );
            }
        }
    }
}
