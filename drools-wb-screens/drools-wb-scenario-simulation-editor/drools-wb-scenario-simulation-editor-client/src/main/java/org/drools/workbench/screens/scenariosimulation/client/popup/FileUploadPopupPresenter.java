/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.uberfire.mvp.Command;

@Dependent
public class FileUploadPopupPresenter implements FileUploadPopup.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    protected FileUploadPopup fileUploadPopup;

    @Override
    public void show(final String mainTitleText,
                     final String okButtonText,
                     final Command okCommand) {
        show(Collections.emptyList(), mainTitleText, okButtonText, okCommand);
    }

    @Override
    public void show(final List<String> acceptedExtension,
                     final String mainTitleText,
                     final String okButtonText,
                     final Command okCommand) {
        fileUploadPopup = viewsProvider.getFileUploadPopup();
        fileUploadPopup.setAcceptedExtension(acceptedExtension);
        fileUploadPopup.show(ScenarioSimulationEditorConstants.INSTANCE.selectImportFile(), ScenarioSimulationEditorConstants.INSTANCE.importLabel(), okCommand);

    }

    @Override
    public void hide() {

    }

    @Override
    public String getFileContents() {
        return fileUploadPopup.getFileContents();
    }
}
