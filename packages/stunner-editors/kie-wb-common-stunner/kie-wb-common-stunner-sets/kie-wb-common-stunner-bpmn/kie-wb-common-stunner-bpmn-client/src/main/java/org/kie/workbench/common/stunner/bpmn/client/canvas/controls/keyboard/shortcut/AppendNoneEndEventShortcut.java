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


package org.kie.workbench.common.stunner.bpmn.client.canvas.controls.keyboard.shortcut;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.AbstractAppendNodeShortcut;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.E;
import static org.kie.workbench.common.stunner.core.util.DefinitionUtils.getElementDefinition;

@BPMN
@Dependent
public class AppendNoneEndEventShortcut extends AbstractAppendNodeShortcut {

    @Inject
    public AppendNoneEndEventShortcut(final ToolboxDomainLookups toolboxDomainLookups,
                                      final DefinitionsCacheRegistry definitionsCacheRegistry,
                                      final @BPMN GeneralCreateNodeAction generalCreateNodeAction) {
        super(toolboxDomainLookups, definitionsCacheRegistry, generalCreateNodeAction);
    }

    @Override
    public boolean matchesPressedKeys(final KeyboardEvent.Key... pressedKeys) {
        return KeysMatcher.doKeysMatch(pressedKeys, getKeyCombination());
    }

    @Override
    public boolean matchesSelectedElement(final Element selectedElement) {
        return selectedElement != null && !selectedElementIsEndEvent(selectedElement);
    }

    private boolean selectedElementIsEndEvent(final Element selectedElement) {
        return getElementDefinition(selectedElement) instanceof BaseEndEvent;
    }

    @Override
    public boolean canAppendNodeOfDefinition(final Object definition) {
        return definition instanceof EndNoneEvent;
    }

    @Override
    public KeyboardEvent.Key[] getKeyCombination() {
        return new KeyboardEvent.Key[]{E};
    }

    @Override
    public String getLabel() {
        return "Append None End Event";
    }
}
