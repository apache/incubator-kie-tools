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

package org.kie.workbench.common.screens.library.client.screens;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import org.ext.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.util.URIUtil;

public class WorkspaceProjectListAssetsPresenter {

    public interface View
            extends IsElement {

        void init(WorkspaceProjectListAssetsPresenter workspaceProjectListAssetsPresenter);

        void setProjectName(final String projectName);

        void setProjectDetails(final IsElement element);

        void clearAssets();

        void addAsset(final String assetName,
                      final String assetPath,
                      final String assetType,
                      final IsWidget assetIcon,
                      final String lastModifiedTime,
                      final String createdTime,
                      final Command details,
                      final Command select);

        String getFilterValue();

        void setFilterName(final String name);

        Integer getStep();

        void showIndexingIncomplete();

        void showSearchHitNothing();

        void showNoMoreAssets();

        int getFirstIndex();

        void resetPageRangeIndicator();

        void setupAssetsActions();
    }

    private View view;
    private LibraryPlaces libraryPlaces;
    private ProjectsDetailScreen projectsDetailScreen;
    private TranslationService ts;
    private Caller<LibraryService> libraryService;
    private Classifier assetClassifier;
    private Event<AssetDetailEvent> assetDetailEvent;

    private BusyIndicatorView busyIndicatorView;
    private ProjectController projectController;
    private List<AssetInfo> assets;
    private Reloader reloader = new Reloader();
    private Timer projectLoadTimer;
    private boolean isProjectLoadPending = false;
    private boolean isProjectLoadInProgress = false;
    private WorkspaceProject project;

    @Inject
    public WorkspaceProjectListAssetsPresenter(final View view,
                                               final LibraryPlaces libraryPlaces,
                                               final ProjectsDetailScreen projectsDetailScreen,
                                               final TranslationService ts,
                                               final Caller<LibraryService> libraryService,
                                               final Classifier assetClassifier,
                                               final Event<AssetDetailEvent> assetDetailEvent,
                                               final BusyIndicatorView busyIndicatorView,
                                               final ProjectController projectController) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.projectsDetailScreen = projectsDetailScreen;
        this.ts = ts;
        this.libraryService = libraryService;
        this.assetClassifier = assetClassifier;
        this.assetDetailEvent = assetDetailEvent;
        this.busyIndicatorView = busyIndicatorView;
        this.projectController = projectController;

        view.init(this);
    }

    public void show(final WorkspaceProject project) {
        this.project = PortablePreconditions.checkNotNull("ProjectListPresenter.project",
                                                          project);

        loadProjectInfo();
        view.setProjectName(project.getName());
        view.setProjectDetails(projectsDetailScreen.getView());

        if (projectController.canUpdateProject(project)) {
            view.setupAssetsActions();
        }
    }

    protected Timer createTimer() {
        return new Timer() {
            @Override
            public void run() {
                onTimerAction();
            }
        };
    }

    protected void onTimerAction() {
        view.resetPageRangeIndicator();
        loadProjectInfo();
    }

    private void cancel() {
        if (projectLoadTimer != null) {
            projectLoadTimer.cancel();
            projectLoadTimer = null;
        }
    }

    public void goToSettings() {
        assetDetailEvent.fire(new AssetDetailEvent(project,
                                                   null));
    }

    String getLastModifiedTime(final AssetInfo asset) {
        return ts.format(LibraryConstants.LastModified) + " " + SocialDateFormatter.format(asset.getLastModifiedTime());
    }

    String getCreatedTime(final AssetInfo asset) {
        return ts.format(LibraryConstants.Created) + " " + SocialDateFormatter.format(asset.getCreatedTime());
    }

    Command selectCommand(final Path assetPath) {
        return () -> libraryPlaces.goToAsset(assetPath);
    }

    Command detailsCommand(final Path assetPath) {
        return selectCommand(assetPath);
    }

    void loadProjectInfo() {
        if (isProjectLoadInProgress) {
            isProjectLoadPending = true;
            return;
        }

        isProjectLoadInProgress = true;

        libraryService.call(new RemoteCallback<List<AssetInfo>>() {
            @Override
            public void callback(List<AssetInfo> assetsList) {

                assets = assetsList;

                setupAssets(assets);

                isProjectLoadInProgress = false;

                if (isProjectLoadPending) {
                    isProjectLoadPending = false;
                    loadProjectInfo();
                } else {
                    reloader.check(assetsList);
                }

                busyIndicatorView.hideBusyIndicator();
            }
        }).getProjectAssets(new ProjectAssetsQuery(project,
                                                   view.getFilterValue(),
                                                   view.getFirstIndex(),
                                                   view.getStep()));
    }

    private void setupAssets(final List<AssetInfo> assets) {
        view.clearAssets();

        if (assets.isEmpty()) {
            if (isFilterEmpty()) {
                if (view.getFirstIndex() == 0) {
                    view.showIndexingIncomplete();
                } else {
                    view.showNoMoreAssets();
                }
            } else {
                if (view.getFirstIndex() == 0) {
                    view.showSearchHitNothing();
                } else {
                    view.showNoMoreAssets();
                }
            }
        } else {
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
    }

    // TODO: FIX FILTER
//    public void filterUpdate(@Observes final FilterUpdateEvent event) {
//        view.setFilterName(event.getName());
//        onFilterChange();
//    }

    private boolean isFilterEmpty() {
        return view.getFilterValue().isEmpty();
    }

    private String getAssetPath(final AssetInfo asset) {
        final String fullPath = ((Path) asset.getFolderItem().getItem()).toURI();

        if (!project.getRepository().getDefaultBranch().isPresent()) {
            throw new IllegalStateException("Project repository should have at least one branch");
        }

        final String projectRootPath = project.getRepository().getDefaultBranch().get().getPath().toURI();
        final String relativeAssetPath = fullPath.substring(projectRootPath.length());
        final String decodedRelativeAssetPath = URIUtil.decode(relativeAssetPath);

        return decodedRelativeAssetPath;
    }

    public IsElement getView() {
        return view;
    }

    public void onReload() {
        loadProjectInfo();
    }

    protected void reload() {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                                               @Override
                                               public boolean execute() {
                                                   loadProjectInfo();
                                                   return false;
                                               }
                                           },
                                           2000);
    }

    public void onFilterChange() {
        cancel();
        projectLoadTimer = createTimer();
        projectLoadTimer.schedule(250);
    }

    /**
     * This class is needed in situations where you open the project screen, but the indexing has not yet finished.
     * <p/>
     * It keeps reloading the file list from the backend server until either the page is full or
     * when the indexing runs out of files and stops.
     */
    private class Reloader {

        private boolean active = false;
        private int previousAmount = -1;

        public void check(final List<AssetInfo> assetsList) {

            if (assets != null && assetsList.size() <= previousAmount && !assets.isEmpty()) {
                active = false;
            }

            if (assetsList.isEmpty() && view.getFirstIndex() == 0 && filterIsNotSet()) {
                active = true;
            }

            if (active && view.getFirstIndex() != 0) {
                active = false;
            }

            if (assetsList.size() == view.getStep()) {
                active = false;
            }

            previousAmount = assetsList.size();

            if (active) {
                reload();
            }
        }

        private boolean filterIsNotSet() {
            return view.getFilterValue() == null || view.getFilterValue().trim().isEmpty();
        }
    }
}
