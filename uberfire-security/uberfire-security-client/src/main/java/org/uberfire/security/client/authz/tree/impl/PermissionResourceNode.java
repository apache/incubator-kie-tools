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
package org.uberfire.security.client.authz.tree.impl;

import org.uberfire.security.client.authz.tree.HasResources;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;

/**
 * A resource node allows for adding or removing children nodes (each one represents a set of permissions
 * over a single resource instance) at runtime.
 */
public class PermissionResourceNode extends AbstractPermissionNode implements HasResources {

    private String resourceName = null;

    public PermissionResourceNode() {
        super();
    }

    public PermissionResourceNode(String resourceName,
                                  PermissionTreeProvider provider) {
        this();
        super.setPermissionTreeProvider(provider);
        this.resourceName = resourceName;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
