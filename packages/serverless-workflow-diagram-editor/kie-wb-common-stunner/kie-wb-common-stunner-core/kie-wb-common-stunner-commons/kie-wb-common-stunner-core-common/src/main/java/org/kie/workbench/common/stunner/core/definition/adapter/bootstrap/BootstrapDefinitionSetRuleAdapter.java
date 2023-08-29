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


package org.kie.workbench.common.stunner.core.definition.adapter.bootstrap;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleSet;

class BootstrapDefinitionSetRuleAdapter implements DefinitionSetRuleAdapter<Object> {

    private final AdapterRegistry adapterRegistry;

    BootstrapDefinitionSetRuleAdapter(final AdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    public RuleSet getRuleSet(final Object pojo) {
        return getWrapped(pojo).getRuleSet(pojo);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return null != getWrapped(type);
    }

    private DefinitionSetRuleAdapter<Object> getWrapped(final Object pojo) {
        return getWrapped(pojo.getClass());
    }

    private DefinitionSetRuleAdapter<Object> getWrapped(final Class<?> type) {
        return adapterRegistry.getDefinitionSetRuleAdapter(type);
    }
}
