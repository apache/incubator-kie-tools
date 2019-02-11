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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.Routed;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

public class AssetsScreen {

    private final AssetsScreen.View view;
    private final LibraryPlaces libraryPlaces;
    private final EmptyAssetsScreen emptyAssetsScreen;
    private final PopulatedAssetsScreen populatedAssetsScreen;
    private final BusyIndicatorView busyIndicatorView;
    private InvalidProjectScreen invalidProjectScreen;
    private final TranslationService ts;
    private final Caller<LibraryService> libraryService;
    private WorkspaceProject workspaceProject;
    private boolean empty = true;

    public interface View extends UberElemental<AssetsScreen> {

        void setContent(HTMLElement element);
    }

    @Inject
    public AssetsScreen(final AssetsScreen.View view,
                        final LibraryPlaces libraryPlaces,
                        final EmptyAssetsScreen emptyAssetsScreen,
                        final PopulatedAssetsScreen populatedAssetsScreen,
                        final InvalidProjectScreen invalidProjectScreen,
                        final TranslationService ts,
                        final BusyIndicatorView busyIndicatorView,
                        final Caller<LibraryService> libraryService) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.emptyAssetsScreen = emptyAssetsScreen;
        this.populatedAssetsScreen = populatedAssetsScreen;
        this.invalidProjectScreen = invalidProjectScreen;
        this.ts = ts;
        this.busyIndicatorView = busyIndicatorView;
        this.libraryService = libraryService;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);
        this.workspaceProject = libraryPlaces.getActiveWorkspace();
        this.showAssets();
    }

    public void observeAddAsset(@Observes NewResourceSuccessEvent event) {
        if (isEmpty()) {
            this.showAssets();
        }
    }

    protected boolean isEmpty() {
        return this.empty;
    }

    public void onAssetListUpdated(@Observes @Routed ProjectAssetListUpdated event) {
        if (event.getProject().getRepository().getIdentifier().equals(workspaceProject.getRepository().getIdentifier()) && isEmpty()) {
            this.showAssets();
        }
    }

    protected void showAssets() {

        if (workspaceProject.getMainModule() == null) {
            ensureContentSet(invalidProjectScreen.getView().getElement());
        } else {

            busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.LoadingAssets));

            libraryService.call((Boolean hasAssets) -> {
                                    final HTMLElement element =
                                            (hasAssets) ? populatedAssetsScreen.getView().getElement() : emptyAssetsScreen.getView().getElement();
                                    this.empty = !hasAssets;
                                    ensureContentSet(element);
                                    busyIndicatorView.hideBusyIndicator();
                                },
                                new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).hasAssets(this.workspaceProject);
        }
    }

    private void ensureContentSet(final HTMLElement element) {
        if (element.parentNode == null) {
            this.view.setContent(element);
        }
    }

    public View getView() {
        return view;
    }
}
