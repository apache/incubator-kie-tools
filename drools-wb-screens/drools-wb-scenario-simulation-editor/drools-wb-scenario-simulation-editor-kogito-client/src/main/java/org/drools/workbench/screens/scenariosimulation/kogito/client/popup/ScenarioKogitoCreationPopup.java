/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.popup;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.uberfire.mvp.Command;

public interface ScenarioKogitoCreationPopup {

    /**
     * Makes the <code>ScenarioKogitoCreationPopup</code> visible with OK/CANCEL.
     * @param mainTitleText
     * @param okCommand
     */
    void show(final String mainTitleText, Command okCommand);

    /**
     * Makes this popup container(and the main content along with it) invisible. Has no effect if the popup is not
     * already showing.
     */
    void hide();

    ScenarioSimulationModel.Type getSelectedType();

    String getSelectedPath();

    interface Presenter {

        /**
         * Makes the <code>NewScesimPopup</code> visible with OK/CANCEL.
         * @param mainTitleText
         * @param okCommand
         */
        void show(final String mainTitleText, Command okCommand);


        ScenarioSimulationModel.Type getSelectedType();

        /**
         * Makes this popup container(and the main content along with it) invisible. Has no effect if the popup is not
         * already showing.
         */
        void hide();

        String getSelectedPath();
    }
}
