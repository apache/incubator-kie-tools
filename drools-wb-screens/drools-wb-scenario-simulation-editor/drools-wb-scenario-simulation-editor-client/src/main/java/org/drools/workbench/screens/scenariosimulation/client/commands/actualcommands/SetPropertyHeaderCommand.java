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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import javax.enterprise.context.Dependent;

import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;

/**
 * <code>Command</code> to to set the <i>property</i> level header for a given column
 */
@Dependent
public class SetPropertyHeaderCommand extends AbstractSelectedColumnCommand {

    public SetPropertyHeaderCommand(GridWidget gridWidget, FactMappingValueType factMappingValueType) {
        super(gridWidget, factMappingValueType);
    }

    private SetPropertyHeaderCommand() {
        // CDI
    }

    @Override
    protected void executeIfSelectedColumn(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn) {
        setPropertyHeader(context,
                          selectedColumn,
                          context.getStatus().getPropertyNameElements(),
                          context.getStatus().getValueClassName());
    }

}