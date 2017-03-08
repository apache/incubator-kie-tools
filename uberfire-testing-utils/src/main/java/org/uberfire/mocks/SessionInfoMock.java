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

package org.uberfire.mocks;

import java.util.Map;
import java.util.Set;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.rpc.SessionInfo;

public class SessionInfoMock implements SessionInfo {

    private String id;

    private String identity;

    public SessionInfoMock() {
        this("admin");
    }

    public SessionInfoMock(final String identity) {
        this.identity = identity;
    }

    public SessionInfoMock(final String id,
                           final String identity) {
        this.id = id;
        this.identity = identity;
    }

    @Override
    public String getId() {
        return id != null ? id : "session_id";
    }

    @Override
    public User getIdentity() {
        return new User() {
            @Override
            public String getIdentifier() {
                return identity;
            }

            @Override
            public Set<Role> getRoles() {
                return null;
            }

            @Override
            public Set<Group> getGroups() {
                return null;
            }

            @Override
            public Map<String, String> getProperties() {
                return null;
            }

            @Override
            public void setProperty(final String s,
                                    final String s1) {

            }

            @Override
            public void removeProperty(final String s) {

            }

            @Override
            public String getProperty(final String s) {
                return null;
            }
        };
    }
}
