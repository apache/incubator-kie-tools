/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.security.authz;

import org.uberfire.mvp.Command;

/**
 * A security check executed over a resource or permission.
 * <p>
 * <p>(See the {@link AuthorizationManager} {@code check} methods)</p>
 */
public interface AuthorizationCheck<C extends AuthorizationCheck> {

    /**
     * Specifies the command instance to execute in case the check result is granted.
     * @param onGranted The command to execute
     */
    C granted(Command onGranted);

    /**
     * Specifies the command instance to execute in case the check result is denied.
     * @param onDenied The command to execute
     */
    C denied(Command onDenied);

    /**
     * Get the check result value
     * @return true if granted, false otherwise
     */
    boolean result();
}