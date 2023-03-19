/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.violations;

import java.util.Optional;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ConnectionRuleViolation extends AbstractRuleViolation {

    private String role;
    private Set<String> allowedConnections;

    public ConnectionRuleViolation(final @MapsTo("role") String role,
                                   final @MapsTo("allowedConnections") Set<String> allowedConnections) {
        this.role = role;
        this.allowedConnections = allowedConnections;
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(role, allowedConnections);
    }

    @Override
    public String getMessage() {
        return "Edge does not emanate from a GraphNode with a permitted Role nor terminate " +
                "at GraphNode with a permitted Role. [Role=" + role + "] [Permitted Connections are: " + allowedConnections.toString() + "]";
    }
}
