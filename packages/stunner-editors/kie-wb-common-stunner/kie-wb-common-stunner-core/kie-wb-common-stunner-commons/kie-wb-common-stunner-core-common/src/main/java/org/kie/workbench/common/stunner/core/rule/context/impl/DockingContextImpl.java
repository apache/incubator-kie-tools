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

import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.DockingContext;

class DockingContextImpl implements DockingContext {

    private final Set<String> roles;
    private final Set<String> allowedRoles;

    DockingContextImpl(final Set<String> roles,
                       final Set<String> allowedRoles) {
        this.roles = roles;
        this.allowedRoles = allowedRoles;
    }

    @Override
    public Set<String> getParentRoles() {
        return roles;
    }

    @Override
    public Set<String> getCandidateRoles() {
        return allowedRoles;
    }

    @Override
    public String getName() {
        return "Docking";
    }

    @Override
    public boolean isDefaultDeny() {
        return true;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return DockingContext.class;
    }
}
