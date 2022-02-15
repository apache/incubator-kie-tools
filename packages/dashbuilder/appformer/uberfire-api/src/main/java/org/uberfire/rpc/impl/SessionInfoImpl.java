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

package org.uberfire.rpc.impl;

import javax.enterprise.inject.Alternative;

import org.uberfire.rpc.SessionInfo;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

@Alternative
public class SessionInfoImpl implements SessionInfo {

    private String id;

    public SessionInfoImpl() {
    }

    public SessionInfoImpl(final String id) {
        this.id = checkNotEmpty("id",
                                id);
    }


    @Override
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionInfo)) {
            return false;
        }

        SessionInfo that = (SessionInfo) o;

        if (!getId().equals(that.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SessionInfoImpl [id=" + id + "]";
    }
}
