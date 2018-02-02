/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.project.cli;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;

@ApplicationScoped
public class CommandLineAuthService implements AuthenticationService {

    public static final User PLACEHOLDER = new UserImpl("cli-user");

    @Override
    public User login(String username, String password) {
        throw new UnsupportedOperationException("Cannot login with CLI auth service.");
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException("Cannot logout with CLI auth service.");
    }

    @Override
    public User getUser() {
        return PLACEHOLDER;
    }

}
