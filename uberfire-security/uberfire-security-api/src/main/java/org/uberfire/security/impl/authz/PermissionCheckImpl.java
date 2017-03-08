/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.security.impl.authz;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationCheck;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCheck;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingStrategy;

/**
 * A check executed over a {@link Permission} instance.
 */
public class PermissionCheckImpl implements PermissionCheck {

    protected PermissionManager permissionManager;
    protected String permission;
    protected User user;
    protected VotingStrategy votingStrategy;
    protected Boolean result = null;

    public PermissionCheckImpl(PermissionManager permissionManager,
                               String permission,
                               User user,
                               VotingStrategy votingStrategy) {
        this.permissionManager = permissionManager;
        this.permission = permission;
        this.user = user;
        this.votingStrategy = votingStrategy;
    }

    protected void check() {
        Permission p = permissionManager.createPermission(permission,
                                                          true);
        AuthorizationResult authz = permissionManager.checkPermission(p,
                                                                      user,
                                                                      votingStrategy);
        result = !AuthorizationResult.ACCESS_DENIED.equals(authz);
    }

    @Override
    public AuthorizationCheck granted(Command onGranted) {
        if (result()) {
            onGranted.execute();
        }
        return this;
    }

    @Override
    public AuthorizationCheck denied(Command onDenied) {
        if (!result()) {
            onDenied.execute();
        }
        return this;
    }

    @Override
    public boolean result() {
        if (result == null) {
            check();
        }
        return result;
    }
}