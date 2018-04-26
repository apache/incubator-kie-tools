/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.close;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.util.URIUtil;

import static org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants.ProjectScreenSettings;
import static org.kie.workbench.common.screens.library.client.util.LibraryPlaces.PROJECT_SCREEN;

public class CloseUnsavedProjectAssetsPopUpPresenter {

    public interface View extends UberElemental<CloseUnsavedProjectAssetsPopUpPresenter> {

        void addPlace(final CloseUnsavedProjectAssetsPopUpListItemPresenter.View placeListItem);

        void show(String projectName);

        void hide();

        void clearPlaces();
    }

    private final CloseUnsavedProjectAssetsPopUpPresenter.View view;
    private final ManagedInstance<CloseUnsavedProjectAssetsPopUpListItemPresenter> closeUnsavedProjectAssetsPopUpListItemPresenters;
    private final TranslationService translationService;

    Optional<Command> proceedCallback;

    Optional<Command> cancelCallback;

    @Inject
    public CloseUnsavedProjectAssetsPopUpPresenter(final CloseUnsavedProjectAssetsPopUpPresenter.View view,
                                                   final ManagedInstance<CloseUnsavedProjectAssetsPopUpListItemPresenter> closeUnsavedProjectAssetsPopUpListItemPresenters,
                                                   final TranslationService translationService) {
        this.view = view;
        this.closeUnsavedProjectAssetsPopUpListItemPresenters = closeUnsavedProjectAssetsPopUpListItemPresenters;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final WorkspaceProject project,
                     final List<PlaceRequest> uncloseablePlaces,
                     final Optional<Command> proceedCallback,
                     final Optional<Command> cancelCallback) {
        this.proceedCallback = proceedCallback;
        this.cancelCallback = cancelCallback;

        view.clearPlaces();
        uncloseablePlaces.forEach(place -> {

            final CloseUnsavedProjectAssetsPopUpListItemPresenter placeItem = closeUnsavedProjectAssetsPopUpListItemPresenters.get();
            placeItem.setup(getLabel(project, place));
            view.addPlace(placeItem.getView());
        });
        view.show(project.getName());
    }

    public void proceed() {
        view.hide();
        proceedCallback.ifPresent(Command::execute);
    }

    public void cancel() {
        view.hide();
        cancelCallback.ifPresent(Command::execute);
    }

    private String getLabel(final WorkspaceProject project, final PlaceRequest place) {

        if (place instanceof PathPlaceRequest) {
            return getAssetPath(project, ((PathPlaceRequest) place).getPath().toURI());
        }
        if (place.getIdentifier().equals(PROJECT_SCREEN)) {
            return translationService.format(ProjectScreenSettings, project.getName());
        }

        return place.getFullIdentifier();
    }

    private String getAssetPath(final WorkspaceProject project,
                                final String fullPath) {
        final String projectRootPath = project.getRootPath().toURI();
        final String relativeAssetPath = fullPath.substring(projectRootPath.length());
        final String decodedRelativeAssetPath = URIUtil.decode(relativeAssetPath);

        return decodedRelativeAssetPath;
    }
}
