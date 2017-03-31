/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.widgets;

import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class ProjectActionsWidget {

    public interface View extends UberElement<ProjectActionsWidget>,
                                  BuildExecutor.View {

    }

    private View view;

    private BuildExecutor buildExecutor;

    private LibraryPlaces libraryPlaces;

    private Command showSettingsCommand;

    @Inject
    public ProjectActionsWidget(final View view,
                                final BuildExecutor buildExecutor,
                                final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.buildExecutor = buildExecutor;
        this.libraryPlaces = libraryPlaces;
    }

    public void init(final Command showSettingsCommand) {
        this.showSettingsCommand = showSettingsCommand;
        view.init(this);
        buildExecutor.init(view);
    }

    public void goToProjectSettings() {
        showSettingsCommand.execute();
    }

    public void compileProject() {
        buildExecutor.triggerBuild();
    }

    public void buildAndDeployProject() {
        buildExecutor.triggerBuildAndDeploy();
    }

    public void goToMessages() {
        libraryPlaces.goToMessages();
    }

    public View getView() {
        return view;
    }
}
