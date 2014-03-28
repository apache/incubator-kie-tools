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

package org.uberfire.security.auth;

import java.io.Serializable;

import org.uberfire.security.Subject;

/**
 * Lightweight interim placeholder for a user who has been authenticated but for whom we are still in the process of
 * building up a {@link Subject}. This type is only of interest to those who are extending UberFire security with
 * additional Authorization, Authentication, Role, or Property providers.
 */
public interface Principal extends Serializable {

    /**
     * Returns the name of the user who has been authenticated. Usually, this will be the same as the username
     * credential that was used in the successful login attempt.
     */
    String getName();
}
