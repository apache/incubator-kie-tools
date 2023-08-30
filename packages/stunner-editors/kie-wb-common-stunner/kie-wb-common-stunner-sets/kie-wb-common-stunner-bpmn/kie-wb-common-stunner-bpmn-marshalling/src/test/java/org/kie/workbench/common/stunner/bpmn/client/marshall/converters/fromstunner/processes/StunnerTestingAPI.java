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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.processes;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.MockitoAnnotations;

public abstract class StunnerTestingAPI {

    public final RuleViolations DEFAULT_RULE_VIOLATIONS = new DefaultRuleViolations();

    public DefinitionManager definitionManager;
    public RuleManager ruleManager;
    public DefinitionUtils definitionUtils;
    public TypeDefinitionSetRegistry definitionSetRegistry;
    public AdapterManager adapterManager;
    public AdapterRegistry adapterRegistry;
    public DefinitionSetAdapter definitionSetAdapter;
    public DefinitionAdapter definitionAdapter;
    public PropertyAdapter propertyAdapter;
    public DefinitionSetRuleAdapter ruleAdapter;
    public FactoryManager factoryManager;
    public FactoryRegistry factoryRegistry;

    public StunnerTestingAPI() {
        MockitoAnnotations.initMocks(this);
        init();
    }

    protected abstract void init();
}