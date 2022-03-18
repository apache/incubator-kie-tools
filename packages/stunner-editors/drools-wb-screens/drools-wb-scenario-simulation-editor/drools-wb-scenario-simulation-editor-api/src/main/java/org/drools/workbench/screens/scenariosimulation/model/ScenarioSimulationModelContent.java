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

package org.drools.workbench.screens.scenariosimulation.model;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.widgets.client.datamodel.copied.PackageDataModelOracleBaselinePayload;

@Portable
public class ScenarioSimulationModelContent {

    private ScenarioSimulationModel model;

    private PackageDataModelOracleBaselinePayload dataModel;

    public ScenarioSimulationModelContent() {
    }

    public ScenarioSimulationModelContent(final ScenarioSimulationModel model,
                                          final PackageDataModelOracleBaselinePayload dataModel) {
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.dataModel = PortablePreconditions.checkNotNull("dataModel",
                                                            dataModel);
    }

    public ScenarioSimulationModel getModel() {
        return model;
    }

    public PackageDataModelOracleBaselinePayload getDataModel() {
        return dataModel;
    }
}
