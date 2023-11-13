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

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to update an element's property.
 */
public final class UpdateElementPropertyValueCommand extends AbstractGraphCommand {

    private final String elementUUID;
    private final String field;
    private final Object value;
    private Object oldValue;
    private transient Element<?> element;

    public UpdateElementPropertyValueCommand(final String elementUUID,
                                             final String field,
                                             final Object value) {
        this.elementUUID = checkNotNull("elementUUID", elementUUID);
        this.field = checkNotNull("field", field);
        this.value = value;
        this.element = null;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public UpdateElementPropertyValueCommand(final Element<?> element,
                                             final String propertyId,
                                             final Object value) {
        this(element.getUUID(),
             propertyId,
             value);
        this.element = element;
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        getNullSafeElement(context);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final Element<Definition<?>> element = getNullSafeElement(context);
        final Object p = context.getDefinitionManager().adapters().forDefinition().getProperty(element.getContent().getDefinition(),
                                                                                               field).get();
        final PropertyAdapter<Object, Object> adapter = (PropertyAdapter<Object, Object>) context.getDefinitionManager().adapters().registry().getPropertyAdapter(p.getClass());
        oldValue = adapter.getValue(p);
        adapter.setValue(p,
                         value);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final UpdateElementPropertyValueCommand undoCommand = new UpdateElementPropertyValueCommand(getNullSafeElement(context),
                                                                                                    field,
                                                                                                    oldValue);
        return undoCommand.execute(context);
    }

    public Object getOldValue() {
        return oldValue;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public Element<?> getElement() {
        return element;
    }

    private Element getNullSafeElement(final GraphCommandExecutionContext context) {
        if (null == element) {
            return super.getElementNotNull(context,
                                           elementUUID);
        }
        return element;
    }

    @Override
    public String toString() {
        return "UpdateElementPropertyValueCommand [element=" + elementUUID + ", field=" + field + ", value=" + value + "]";
    }
}
