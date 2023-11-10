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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to update a DomainObject's property.
 */
public final class UpdateDomainObjectPropertyValueCommand extends AbstractGraphCommand {

    private final DomainObject domainObject;
    private final String field;
    private final Object value;

    private Object oldValue;

    public UpdateDomainObjectPropertyValueCommand(final DomainObject domainObject,
                                                  final String field,
                                                  final Object value) {
        this.domainObject = checkNotNull("domainObject", domainObject);
        this.field = checkNotNull("field", field);
        this.value = value;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final DefinitionManager definitionManager = context.getDefinitionManager();
        final Object p = definitionManager.adapters().forDefinition().getProperty(domainObject, field).get();
        final AdapterManager adapterManager = definitionManager.adapters();
        final AdapterRegistry adapterRegistry = adapterManager.registry();
        final PropertyAdapter<Object, Object> adapter = (PropertyAdapter<Object, Object>) adapterRegistry.getPropertyAdapter(p.getClass());
        oldValue = adapter.getValue(p);
        adapter.setValue(p,
                         value);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final UpdateDomainObjectPropertyValueCommand undoCommand = new UpdateDomainObjectPropertyValueCommand(domainObject,
                                                                                                              field,
                                                                                                              oldValue);
        return undoCommand.execute(context);
    }

    @Override
    public String toString() {
        return "UpdateDomainObjectPropertyValueCommand [domainObject=" + domainObject + ", field=" + field + ", value=" + value + "]";
    }
}
