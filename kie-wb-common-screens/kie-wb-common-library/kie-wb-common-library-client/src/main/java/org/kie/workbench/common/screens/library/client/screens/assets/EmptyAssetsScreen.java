/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.assets;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;

public class EmptyAssetsScreen {

    public interface View extends UberElemental<EmptyAssetsScreen> {

        void enableImportButton(boolean enable);

        void enableAddAssetButton(boolean enable);
    }

    private final EmptyAssetsScreen.View view;
    private final NewFileUploader newFileUploader;
    private final NewResourcePresenter newResourcePresenter;
    private final ProjectController projectController;
    private final LibraryPlaces libraryPlaces;
    private final Promises promises;

    @Inject
    public EmptyAssetsScreen(final EmptyAssetsScreen.View view,
                             final NewFileUploader newFileUploader,
                             final NewResourcePresenter newResourcePresenter,
                             final ProjectController projectController,
                             final LibraryPlaces libraryPlaces,
                             final Promises promises) {
        this.view = view;
        this.newFileUploader = newFileUploader;
        this.newResourcePresenter = newResourcePresenter;
        this.projectController = projectController;
        this.libraryPlaces = libraryPlaces;
        this.promises = promises;
    }

    @PostConstruct
    public void initialize() {
        this.view.init(this);

        projectController.canUpdateProject(this.libraryPlaces.getActiveWorkspace()).then(userCanUpdateProject -> {
            this.enableButtons(userCanUpdateProject);

            newFileUploader.acceptContext(new Callback<Boolean, Void>() {
                @Override
                public void onFailure(Void reason) {
                    view.enableImportButton(false);
                }

                @Override
                public void onSuccess(Boolean result) {
                    view.enableImportButton(result && userCanUpdateProject);
                }
            });

            return promises.resolve();
        });
    }

    private void enableButtons(boolean enable) {
        this.view.enableImportButton(enable);
        this.view.enableAddAssetButton(enable);
    }

    public void importAsset() {
        projectController.canUpdateProject(this.libraryPlaces.getActiveWorkspace()).then(userCanUpdateProject -> {
            if (userCanUpdateProject) {
                newFileUploader.getCommand(newResourcePresenter).execute();
            }

            return promises.resolve();
        });
    }

    public void addAsset() {
        projectController.canUpdateProject(this.libraryPlaces.getActiveWorkspace()).then(userCanUpdateProject -> {
            if (userCanUpdateProject) {
                this.libraryPlaces.goToAddAsset();
            }

            return promises.resolve();
        });
    }

    public EmptyAssetsScreen.View getView() {
        return view;
    }
}
