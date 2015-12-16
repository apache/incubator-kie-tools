/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.testscenario.client;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.model.menu.MenuItem;

public interface ScenarioEditorView
        extends IsWidget,
                KieEditorView {

    interface Presenter {

        void onRunScenario();

        void onRedraw();

        void onRunAllScenarios();

    }

    void setPresenter(Presenter presenter);

    MenuItem getRunScenarioMenuItem();

    MenuItem getRunAllScenariosMenuItem();

    void initKSessionSelector(final ObservablePath path,
                              final Scenario scenario);

    void showAuditView(Set<String> log);

    void showResults();

    void renderFixtures(Path path, AsyncPackageDataModelOracle oracle, Scenario scenario);

}
