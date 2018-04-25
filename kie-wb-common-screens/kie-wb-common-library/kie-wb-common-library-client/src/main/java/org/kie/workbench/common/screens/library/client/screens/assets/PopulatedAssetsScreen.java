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

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.api.Routed;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.assets.events.UpdatedAssetsEvent;
import org.kie.workbench.common.screens.library.client.util.CategoryUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetItemWidget;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class PopulatedAssetsScreen {

    private View view;
    private final CategoriesManagerCache categoriesManagerCache;
    private final ResourceTypeManagerCache resourceTypeManagerCache;
    private BusyIndicatorView busyIndicatorView;
    private LibraryPlaces libraryPlaces;
    private TranslationService ts;
    private Classifier assetClassifier;
    private ManagedInstance<AssetItemWidget> assetItemWidget;
    private NewFileUploader newFileUploader;
    private NewResourcePresenter newResourcePresenter;
    private ProjectController projectController;
    private Event<UpdatedAssetsEvent> updatedAssetsEventEvent;
    private EmptyState emptyState;
    private CategoryUtils categoryUtils;
    private WorkspaceProject workspaceProject;
    private int currentPage;
    private int pageSize;
    private String filter;
    private int totalPages;
    private String filterType;
    private final Event<WorkspaceProjectContextChangeEvent> contextChangeEvent;
    private AssetQueryService assetQueryService;

    public interface View extends UberElemental<PopulatedAssetsScreen> {

        void addAssetItem(AssetItemWidget item);

        void setCurrentPage(int currentPage);

        void setPageIndicator(int from,
                              int to,
                              int total);

        void setTotalPages(int totalPages);

        void clear();

        void disablePreviousButton();

        void enablePreviousButton();

        void enableNextButton();

        void disableNextButton();

        void setCategories(List<SelectOption> categories);

        void showEmptyState(EmptyState emptyState);

        void hideEmptyState(EmptyState emptyState);
    }

    @Inject
    public PopulatedAssetsScreen(final PopulatedAssetsScreen.View view,
                                 final CategoriesManagerCache categoriesManagerCache,
                                 final ResourceTypeManagerCache resourceTypeManagerCache,
                                 final BusyIndicatorView busyIndicatorView,
                                 final LibraryPlaces libraryPlaces,
                                 final TranslationService ts,
                                 final Classifier assetClassifier,
                                 final ManagedInstance<AssetItemWidget> assetItemWidget,
                                 final NewFileUploader newFileUploader,
                                 final NewResourcePresenter newResourcePresenter,
                                 final ProjectController projectController,
                                 final Event<UpdatedAssetsEvent> updatedAssetsEventEvent,
                                 final EmptyState emptyState,
                                 final CategoryUtils categoryUtils,
                                 final AssetQueryService assetQueryService,
                                 final Event<WorkspaceProjectContextChangeEvent> contextChangeEvent) {
        this.view = view;
        this.categoriesManagerCache = categoriesManagerCache;
        this.resourceTypeManagerCache = resourceTypeManagerCache;
        this.busyIndicatorView = busyIndicatorView;
        this.libraryPlaces = libraryPlaces;
        this.ts = ts;
        this.assetClassifier = assetClassifier;
        this.assetItemWidget = assetItemWidget;
        this.newFileUploader = newFileUploader;
        this.newResourcePresenter = newResourcePresenter;
        this.projectController = projectController;
        this.updatedAssetsEventEvent = updatedAssetsEventEvent;
        this.emptyState = emptyState;
        this.categoryUtils = categoryUtils;
        this.assetQueryService = assetQueryService;
        this.contextChangeEvent = contextChangeEvent;
    }

    @PostConstruct
    public void init() {
        this.workspaceProject = libraryPlaces.getActiveWorkspaceContext();
        this.view.init(this);
        this.filterType = "ALL";
        this.view.setCategories(this.categoryUtils.createCategories());
        this.filter = "";
        this.currentPage = 1;
        this.pageSize = 15;
    }

    public void onAssetListUpdated(@Observes @Routed ProjectAssetListUpdated event) {
        if (event.getProject().getRepository().getIdentifier().equals(workspaceProject.getRepository().getIdentifier())) {
            update();
        }
    }

    private void addAssetsToView(AssetQueryResult result) {
        switch (result.getResultType()) {
            case Normal: {
                List<AssetInfo> assetInfos = result.getAssetInfos().get();
                if (assetInfos.isEmpty()) {
                    this.showSearchHitNothing();
                } else {
                    this.hideEmptyState();
                    assetInfos.forEach(asset -> {
                        if (!asset.getFolderItem().getType().equals(FolderItemType.FOLDER)) {
                            AssetItemWidget item = assetItemWidget.get();
                            final ClientResourceType assetResourceType = getResourceType(asset);
                            final String assetName = getAssetName(asset,
                                                                  assetResourceType);

                            item.init(assetName,
                                      getAssetPath(asset),
                                      assetResourceType.getDescription(),
                                      assetResourceType.getIcon(),
                                      getLastModifiedTime(asset),
                                      getCreatedTime(asset),
                                      detailsCommand((Path) asset.getFolderItem().getItem()),
                                      selectCommand((Path) asset.getFolderItem().getItem()));
                            this.view.addAssetItem(item);
                        }
                    });
                }

                this.updatedAssetsEventEvent.fire(new UpdatedAssetsEvent(assetInfos));
                busyIndicatorView.hideBusyIndicator();
            }
            break;
            case Unindexed:
                showIndexingNotFinished();
                break;
            case DoesNotExist:
                contextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(workspaceProject.getOrganizationalUnit()));
                break;
            default:
                throw new UnsupportedOperationException("No case for " + result.getResultType());
        }
    }

    public void importAsset() {
        if (canUpdateProject()) {
            this.newFileUploader.getCommand(this.newResourcePresenter).execute();
        }
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
        this.update();
    }

    protected boolean canUpdateProject() {
        return this.projectController.canUpdateProject(this.workspaceProject);
    }

    protected void showIndexingNotFinished() {
        this.showEmptyState(ts.getTranslation(LibraryConstants.IndexingHasNotFinished),
                            ts.getTranslation(LibraryConstants.PleaseWaitWhileTheProjectContentIsBeingIndexed));
    }

    public void showSearchHitNothing() {
        this.showEmptyState(ts.getTranslation(LibraryConstants.EmptySearch),
                            ts.getTranslation(LibraryConstants.NoFilesWhereFoundWithTheGivenSearchCriteria));
    }

    public void showEmptyState(String title,
                               String message) {
        this.emptyState.clear();
        this.emptyState.setMessage(title,
                                   message);
        this.view.showEmptyState(emptyState);
    }

    public void hideEmptyState() {
        this.emptyState.clear();
        this.view.hideEmptyState(emptyState);
    }

    public void addAsset() {
        if (canUpdateProject()) {
            this.libraryPlaces.goToAddAsset();
        }
    }

    public void nextPage() {
        int totalPages = this.getTotalPages();
        if (this.currentPage + 1 <= totalPages) {
            this.currentPage++;
            this.update();
        }
    }

    public void prevPage() {
        if (this.currentPage - 1 >= 1) {
            this.currentPage--;
            this.update();
        }
    }

    private int getTotalPages() {
        return this.totalPages;
    }

    protected void setTotalPages(int numberOfAssets,
                                 int pageSize) {
        this.totalPages = totalPages(numberOfAssets,
                                     pageSize);
    }

    protected int totalPages(int elements,
                             int size) {
        return Integer.valueOf((int) Math.ceil(elements / size)) + 1;
    }

    protected void update() {
        this.view.clear();
        this.hideEmptyState();

        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.LoadingAssets));
        this.resolveAssetsCount();
        this.getAssets(this.filter,
                       this.filterType,
                       getOffset(),
                       this.currentPage * this.pageSize,
                       this::addAssetsToView);

        this.view.setCurrentPage(this.currentPage);
        this.checkPaginationButtons();
    }

    protected void checkPaginationButtons() {
        if (this.currentPage - 1 < 1) {
            this.view.disablePreviousButton();
        } else {
            this.view.enablePreviousButton();
        }
        if (this.currentPage + 1 > this.getTotalPages()) {
            this.view.disableNextButton();
        } else {
            this.view.enableNextButton();
        }
    }

    protected int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        if (currentPage <= this.getTotalPages() && currentPage > 0) {
            this.currentPage = currentPage;
            update();
        } else {
            this.view.setCurrentPage(this.currentPage);
        }
    }

    private int getOffset() {
        return this.buildOffset(this.currentPage,
                                this.pageSize);
    }

    protected int buildOffset(int page,
                              int size) {
        return (page - 1) * size;
    }

    private ClientResourceType getResourceType(AssetInfo asset) {
        return assetClassifier.findResourceType(asset.getFolderItem());
    }

    private String getAssetName(AssetInfo asset,
                                ClientResourceType assetResourceType) {
        return Utils.getBaseFileName(asset.getFolderItem().getFileName(),
                                     assetResourceType.getSuffix());
    }

    private String getAssetPath(final AssetInfo asset) {
        final String fullPath = ((Path) asset.getFolderItem().getItem()).toURI();
        final String projectRootPath = workspaceProject.getRootPath().toURI();
        final String relativeAssetPath = fullPath.substring(projectRootPath.length());
        final String decodedRelativeAssetPath = URIUtil.decode(relativeAssetPath);

        return decodedRelativeAssetPath;
    }

    private void getAssets(String filter,
                           String filterType,
                           int startIndex,
                           int amount,
                           RemoteCallback<AssetQueryResult> callback) {

        if (!isProjectNull()) {
            ProjectAssetsQuery query = this.createProjectQuery(filter,
                                                               filterType,
                                                               startIndex,
                                                               amount);

            assetQueryService.getAssets(query)
                    .call(callback, new DefaultErrorCallback());
        } else {
            busyIndicatorView.hideBusyIndicator();
        }
    }

    protected ProjectAssetsQuery createProjectQuery(String filter,
                                                    String filterType,
                                                    int startIndex,
                                                    int amount) {

        Category category = categoriesManagerCache.getCategory(filterType);
        List<String> suffixes = this.getSuffixes(category);

        return new ProjectAssetsQuery(libraryPlaces.getActiveWorkspaceContext(),
                                      filter,
                                      startIndex,
                                      amount,
                                      suffixes);
    }

    protected List<String> getSuffixes(Category category) {
        return this.resourceTypeManagerCache.getResourceTypeDefinitionsByCategory(category)
                .stream()
                .map(ResourceTypeDefinition::getSuffix)
                .collect(Collectors.toList());
    }

    protected void onAssetsUpdated(@Observes UpdatedAssetsEvent event) {
        resolveAssetsCount();
    }

    protected void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        final PlaceRequest place = selectPlaceEvent.getPlace();
        if (workspaceProject != null && workspaceProject.getMainModule() != null && place.getIdentifier().equals(LibraryPlaces.PROJECT_SCREEN)) {
            this.update();
        }
    }

    private String getLastModifiedTime(final AssetInfo asset) {
        return ts.format(LibraryConstants.LastModified) + " " + SocialDateFormatter.format(asset.getLastModifiedTime());
    }

    public void search(String filterText) {
        this.filter = filterText;
        this.update();
    }

    private String getCreatedTime(final AssetInfo asset) {
        return ts.format(LibraryConstants.Created) + " " + SocialDateFormatter.format(asset.getCreatedTime());
    }

    protected Command selectCommand(final Path assetPath) {
        return () -> libraryPlaces.goToAsset(assetPath);
    }

    protected Command detailsCommand(final Path assetPath) {
        return selectCommand(assetPath);
    }

    public View getView() {
        return view;
    }

    protected int getAssetsCount(int numberOfAssets,
                                 int otherCounter) {
        if (numberOfAssets < otherCounter || otherCounter == 0) {
            return numberOfAssets;
        } else {
            return otherCounter;
        }
    }

    private void resolveAssetsCount() {
        if (!isProjectNull()) {
            ProjectAssetsQuery query = this.createProjectQuery(filter,
                                                               filterType,
                                                               0,
                                                               0);

            assetQueryService.getNumberOfAssets(query)
                    .call((Integer numberOfAssets) -> {
                              int offset = getOffset();
                              this.view.setPageIndicator(offset + 1,
                                                         this.getAssetsCount(numberOfAssets,
                                                                             offset + this.pageSize),
                                                         this.getAssetsCount(numberOfAssets,
                                                                             0));
                              this.setTotalPages(numberOfAssets,
                                                 this.pageSize);
                              this.view.setTotalPages(this.getTotalPages());
                              this.checkPaginationButtons();
                          },
                          new DefaultErrorCallback());
        }
    }

    private boolean isProjectNull() {
        return this.libraryPlaces.getActiveWorkspaceContext() == null || this.libraryPlaces.getActiveWorkspaceContext().getMainModule() == null;
    }
}
