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
package org.kie.workbench.common.screens.social.hp.security;


import org.jboss.errai.security.shared.api.identity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;

@ApplicationScoped
public class UserCDIContextHelper {

    @Inject
    private User identity;


    public UserCDIContextHelper() {
    }

    public boolean thereIsALoggedUserInScope() {
        try {
            if ( identity == null ) {
                return false;
            }
            checkIfThereIsAValidUserOnRequestScope();
        } catch ( ContextNotActiveException c ) {
            return false;
        }
        return true;
    }

    private void checkIfThereIsAValidUserOnRequestScope() {
        identity.getIdentifier();
    }

    public User getUser() {
        return identity;
    }
}
