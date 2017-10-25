/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.config;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.rpc.SessionInfo;

public class SafeSessionInfo
        implements SessionInfo {

    private SessionInfo delegate;

    public SafeSessionInfo(SessionInfo delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        try {
            return delegate.getId();
        } catch (Exception e) {
            return "--";
        }
    }

    @Override
    public User getIdentity() {
        try {
            return delegate.getIdentity();
        } catch (Exception e) {
            return new UserImpl("Anonymous");
        }
    }
}
