/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.ext.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.util.URIUtil;

@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class ProjectScreen {

    public interface View extends UberElement<ProjectScreen> {

        void setProjectName(String projectName);

        void clearAssets();

        void addAsset(String assetName,
                      String assetPath,
                      String assetType,
                      IsWidget assetIcon,
                      String lastModifiedTime,
                      String createdTime,
                      Command details,
                      Command select);
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private TranslationService ts;

    private Caller<LibraryService> libraryService;

    private Classifier assetClassifier;

    private Event<AssetDetailEvent> assetDetailEvent;

    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    private BusyIndicatorView busyIndicatorView;

    private ProjectInfo projectInfo;

    private List<AssetInfo> assets;

    @Inject
    public ProjectScreen(final View view,
                         final LibraryPlaces libraryPlaces,
                         final TranslationService ts,
                         final Caller<LibraryService> libraryService,
                         final Classifier assetClassifier,
                         final Event<AssetDetailEvent> assetDetailEvent,
                         final Event<ProjectContextChangeEvent> projectContextChangeEvent,
                         final BusyIndicatorView busyIndicatorView) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.ts = ts;
        this.libraryService = libraryService;
        this.assetClassifier = assetClassifier;
        this.assetDetailEvent = assetDetailEvent;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.busyIndicatorView = busyIndicatorView;
    }

    public void onStartup(@Observes final ProjectDetailEvent projectDetailEvent) {
        this.projectInfo = projectDetailEvent.getProjectInfo();
        loadProjectInfo();
        view.setProjectName(projectInfo.getProject().getProjectName());
    }

    public void refreshOnFocus(@Observes final PlaceGainFocusEvent placeGainFocusEvent) {
        final PlaceRequest place = placeGainFocusEvent.getPlace();
        if (projectInfo != null && place.getIdentifier().equals(LibraryPlaces.PROJECT_SCREEN)) {
            loadProjectInfo();
        }
    }

    public void updateAssetsBy(final String filter) {
        if (assets != null) {
            List<AssetInfo> filteredAssets = filterAssets(assets,
                                                          filter);
            setupAssets(filteredAssets);
        }
    }

    public void goToSettings() {
        assetDetailEvent.fire(new AssetDetailEvent(projectInfo,
                                                   null));
    }

    public String getProjectName() {
        return projectInfo.getProject().getProjectName();
    }

    List<AssetInfo> filterAssets(final List<AssetInfo> assets,
                                 final String filter) {
        return assets.stream()
                .filter(a -> a.getFolderItem().getFileName().toUpperCase().startsWith(filter.toUpperCase()))
                .collect(Collectors.toList());
    }

    String getLastModifiedTime(final AssetInfo asset) {
        return ts.format(LibraryConstants.LastModified) + " " + SocialDateFormatter.format(asset.getLastModifiedTime());
    }

    String getCreatedTime(final AssetInfo asset) {
        return ts.format(LibraryConstants.Created) + " " + SocialDateFormatter.format(asset.getCreatedTime());
    }

    Command selectCommand(final Path assetPath) {
        return () -> libraryPlaces.goToAsset(projectInfo,
                                             assetPath);
    }

    Command detailsCommand(final Path assetPath) {
        return selectCommand(assetPath);
    }

    private void loadProjectInfo() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.LoadingAssets));
        libraryService.call(new RemoteCallback<List<AssetInfo>>() {
            @Override
            public void callback(List<AssetInfo> assetsList) {
                assets = assetsList;
                loadProject(assets);
                busyIndicatorView.hideBusyIndicator();
            }
        }).getProjectAssets(projectInfo.getProject());
    }

    private void loadProject(List<AssetInfo> assets) {
        setupAssets(assets);
    }

    private void setupAssets(List<AssetInfo> assets) {
        view.clearAssets();

        assets.stream().forEach(asset -> {
            if (!asset.getFolderItem().getType().equals(FolderItemType.FOLDER)) {
                final ClientResourceType assetResourceType = assetClassifier.findResourceType(asset.getFolderItem());
                final String assetName = Utils.getBaseFileName(asset.getFolderItem().getFileName(),
                                                               assetResourceType.getSuffix());

                view.addAsset(assetName,
                              getAssetPath(asset),
                              assetResourceType.getDescription(),
                              assetResourceType.getIcon(),
                              getLastModifiedTime(asset),
                              getCreatedTime(asset),
                              detailsCommand((Path) asset.getFolderItem().getItem()),
                              selectCommand((Path) asset.getFolderItem().getItem()));
            }
        });
    }

    private String getAssetPath(final AssetInfo asset) {
        final String fullPath = ((Path) asset.getFolderItem().getItem()).toURI();
        final String projectRootPath = projectInfo.getProject().getRootPath().toURI();
        final String relativeAssetPath = fullPath.substring(projectRootPath.length() + 1);
        final String decodedRelativeAssetPath = URIUtil.decode(relativeAssetPath);

        return decodedRelativeAssetPath;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Project Screen";
    }

    @WorkbenchPartView
    public UberElement<ProjectScreen> getView() {
        return view;
    }
}
