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

package org.guvnor.ala.source.git;

import org.guvnor.ala.security.Credentials;

public class GitCredentials implements Credentials {

    private final String user;
    private final String passw;

    public GitCredentials() {
        this(null,
             null);
    }

    public GitCredentials(final String user,
                          final String passw) {
        this.user = user;
        this.passw = passw;
    }

    public String getUser() {
        return user;
    }

    public String getPassw() {
        return passw;
    }
}
