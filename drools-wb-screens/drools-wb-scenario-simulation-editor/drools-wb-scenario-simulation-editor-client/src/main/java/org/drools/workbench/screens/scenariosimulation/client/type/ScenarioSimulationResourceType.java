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

package org.drools.workbench.screens.scenariosimulation.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.client.resources.ScenarioSimulationEditorResources;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.type.ScenarioSimulationResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class ScenarioSimulationResourceType
        extends ScenarioSimulationResourceTypeDefinition
        implements ClientResourceType {

    //GwtMockito barfs when this is static... so keep it as an instance variable
    private Image IMAGE = new Image(ScenarioSimulationEditorResources.INSTANCE.images().typeScenarioSimulation());

    public ScenarioSimulationResourceType() {
    }

    @Inject
    public ScenarioSimulationResourceType(final Decision category) {
        super(category);
    }

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = ScenarioSimulationEditorConstants.INSTANCE.scenarioSimulationResourceTypeDescription();
        if (desc == null || desc.isEmpty()) {
            return super.getDescription();
        }
        return desc;
    }
}
