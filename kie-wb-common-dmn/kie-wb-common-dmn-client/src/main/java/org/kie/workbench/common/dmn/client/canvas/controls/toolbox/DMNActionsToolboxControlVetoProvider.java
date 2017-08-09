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
package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ActionsToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;

/**
 * A DMN-specific implementation of {@see ActionsToolboxControlProvider} that
 * prevents the default behaviour applying for DMN DefinitionSets.
 */
@Dependent
@Specializes
public class DMNActionsToolboxControlVetoProvider extends ActionsToolboxControlProvider {

    private final DefinitionManager definitionManager;
    private final Set<String> dmnDefinitionIds = new HashSet<>();

    protected DMNActionsToolboxControlVetoProvider() {
        this(null,
             null,
             null);
    }

    @Inject
    public DMNActionsToolboxControlVetoProvider(final ToolboxFactory toolboxFactory,
                                                final ToolboxCommandFactory toolboxCommandFactory,
                                                final DefinitionManager definitionManager) {
        super(toolboxFactory,
              toolboxCommandFactory);
        this.definitionManager = definitionManager;
        final DMNDefinitionSet definitionSet = (DMNDefinitionSet) definitionManager.definitionSets().getDefinitionSetByType(DMNDefinitionSet.class);
        this.dmnDefinitionIds.addAll(definitionManager.adapters().forDefinitionSet().getDefinitions(definitionSet));
    }

    @Override
    public boolean supports(final Object definition) {
        final String definitionId = definitionManager.adapters().forDefinition().getId(definition);
        return !dmnDefinitionIds.contains(definitionId);
    }
}
