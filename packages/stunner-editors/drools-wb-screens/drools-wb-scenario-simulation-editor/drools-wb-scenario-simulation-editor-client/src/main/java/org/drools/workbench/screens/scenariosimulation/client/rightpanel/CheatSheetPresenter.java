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


package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;

@ApplicationScoped
@Named(CheatSheetPresenter.IDENTIFIER)
public class CheatSheetPresenter extends AbstractSubDockPresenter<CheatSheetView> implements CheatSheetView.Presenter {

    public static final String IDENTIFIER = "org.drools.scenariosimulation.CheatSheet";

    public CheatSheetPresenter() {
        //Zero argument constructor for CDI
        title = ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet();
    }

    @Inject
    public CheatSheetPresenter(CheatSheetView view) {
        super(view);
        title = ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void reset() {
        view.reset();
    }

    public void initCheatSheet(ScenarioSimulationModel.Type type) {
        if (type.equals(ScenarioSimulationModel.Type.RULE)) {
            view.setRuleCheatSheetContent();
        }
        if (type.equals(ScenarioSimulationModel.Type.DMN)) {
            view.setDMNCheatSheetContent();
        }
    }
}
