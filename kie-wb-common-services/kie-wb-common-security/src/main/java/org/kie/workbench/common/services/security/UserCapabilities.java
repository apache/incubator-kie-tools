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
import org.uberfire.security.Identity;

/**
 * This is used to turn off GUI functionality. The server decides what should be visible based on roles
 * and permissions granted. This is essentially a security and permissions function (however the Capabilities
 * do not enforce actions on the server - these are more for GUI convenience so elements are not displayed
 * that are not relevant to a given users role).
 */
public final class UserCapabilities {

    private static Identity identity = null;

    public static boolean canCreateNewAsset() {
        if ( identity == null ) {
            init();
        }
        return identity.hasRole( AppRoles.ADMIN )
                || identity.hasRole( AppRoles.REPOSITORY_EDITOR );
    }

    public static boolean canSeeModulesTree() {
        if ( identity == null ) {
            init();
        }
        return identity.hasRole( AppRoles.ADMIN )
                || identity.hasRole( AppRoles.REPOSITORY_EDITOR )
                || identity.hasRole( AppRoles.REPOSITORY_VIEWER );
    }

    public static boolean canSeeStatuses() {
        if ( identity == null ) {
            init();
        }
        return identity.hasRole( AppRoles.ADMIN )
                || identity.hasRole( AppRoles.REPOSITORY_EDITOR );
    }

    public static boolean canSeeQA() {
        if ( identity == null ) {
            init();
        }
        return identity.hasRole( AppRoles.ADMIN )
                || identity.hasRole( AppRoles.REPOSITORY_EDITOR );
    }

    public static boolean canSeeDeploymentTree() {
        if ( identity == null ) {
            init();
        }
        return identity.hasRole( AppRoles.ADMIN )
                || identity.hasRole( AppRoles.REPOSITORY_EDITOR );
    }

    private static void init() {
        if ( identity == null ) {
            identity = IOC.getBeanManager().lookupBean( Identity.class ).getInstance();
        }
    }
}
