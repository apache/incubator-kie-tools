/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectionContext;

class ConnectionContextImpl implements ConnectionContext {

    private final String connectorRole;
    private final Optional<Set<String>> sourceRoles;
    private final Optional<Set<String>> targetRoles;

    ConnectionContextImpl(final String connectorRole,
                          final Optional<Set<String>> sourceRoles,
                          final Optional<Set<String>> targetRoles) {
        this.connectorRole = connectorRole;
        this.sourceRoles = sourceRoles;
        this.targetRoles = targetRoles;
    }

    @Override
    public String getConnectorRole() {
        return connectorRole;
    }

    @Override
    public Optional<Set<String>> getSourceRoles() {
        return sourceRoles;
    }

    @Override
    public Optional<Set<String>> getTargetRoles() {
        return targetRoles;
    }

    @Override
    public String getName() {
        return "Connection";
    }

    @Override
    public boolean isDefaultDeny() {
        return true;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return ConnectionContext.class;
    }
}
