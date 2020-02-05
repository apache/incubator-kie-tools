/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.canvas.controls.keyboard.shortcut;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.AbstractAppendNodeShortcut;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.KeyboardShortcut;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

import static org.kie.workbench.common.stunner.core.util.DefinitionUtils.getElementDefinition;

@DMNEditor
@Dependent
public class AppendDecisionShortcut extends AbstractAppendNodeShortcut implements KeyboardShortcut<AbstractCanvasHandler> {

    @Inject
    public AppendDecisionShortcut(final ToolboxDomainLookups toolboxDomainLookups,
                                  final DefinitionsCacheRegistry definitionsCacheRegistry,
                                  final @Default GeneralCreateNodeAction generalCreateNodeAction) {
        super(toolboxDomainLookups, definitionsCacheRegistry, generalCreateNodeAction);
    }

    @Override
    public boolean matchesPressedKeys(final KeyboardEvent.Key... pressedKeys) {
        return KeysMatcher.doKeysMatch(pressedKeys, KeyboardEvent.Key.D);
    }

    @Override
    public boolean matchesSelectedElement(final Element selectedElement) {
        return selectedNodeIsDecision(selectedElement) || selectedNodeIsInput(selectedElement);
    }

    private boolean selectedNodeIsDecision(final Element selectedElement) {
        return getElementDefinition(selectedElement) instanceof Decision;
    }

    private boolean selectedNodeIsInput(final Element selectedElement) {
        return getElementDefinition(selectedElement) instanceof InputData;
    }

    @Override
    public boolean canAppendNodeOfDefinition(final Object definition) {
        return definition instanceof Decision;
    }
}
