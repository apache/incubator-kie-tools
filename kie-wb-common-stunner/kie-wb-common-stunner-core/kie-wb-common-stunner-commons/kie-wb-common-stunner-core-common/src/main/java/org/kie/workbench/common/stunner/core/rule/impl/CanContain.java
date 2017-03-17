/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.impl;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public final class CanContain extends AbstractRule {

    private final String parentId;
    private final Set<String> allowedRoles;

    public CanContain(final @MapsTo("name") String name,
                      final @MapsTo("parentId") String parentId,
                      final @MapsTo("allowedRoles") Set<String> allowedRoles) {
        super(name);
        this.parentId = parentId;
        this.allowedRoles = allowedRoles;
    }

    public String getParentId() {
        return parentId;
    }

    public Set<String> getAllowedRoles() {
        return allowedRoles;
    }
}
