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

package org.uberfire.backend.server.cdi.model;

import org.uberfire.backend.cdi.workspace.Workspace;

public class WorkspaceImpl implements Workspace {

    private final String name;

    public WorkspaceImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof WorkspaceImpl) {
            return this.getName() == ((WorkspaceImpl) obj).getName() ||
                    this.getName().equals(((WorkspaceImpl) obj).getName());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        return result;
    }
}
