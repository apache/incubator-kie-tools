/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.backend;

import javax.enterprise.context.Dependent;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;

@Dependent
public class DummyAuthenticationService implements AuthenticationService {

    @Override
    public void logout() {
    }

    @Override
    public User login(String username, String password) {
        return new UserImpl("admin");
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public User getUser() {
        return new UserImpl("admin");
    }
}