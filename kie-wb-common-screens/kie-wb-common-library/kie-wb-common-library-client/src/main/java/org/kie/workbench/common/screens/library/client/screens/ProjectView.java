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

import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetItemWidget;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetsActionsWidget;
import org.kie.workbench.common.screens.library.client.widgets.project.ProjectActionsWidget;
import org.uberfire.mvp.Command;

@Templated
public class ProjectView
        implements ProjectScreen.View,
                   IsElement {

    @Inject
    @DataField("project-toolbar")
    Div projectToolbar;

    @Inject
    @DataField("assets-toolbar")
    Div assetsToolbar;

    @Inject
    @DataField("details-container")
    Div detailsContainer;

    @Inject
    @DataField("filter-text")
    Input filterText;

    @Inject
    @DataField("project-name")
    Div projectNameContainer;

    @Inject
    @DataField
    Div assetListContainer;

    private ProjectScreen presenter;

    @Inject
    private ProjectsDetailScreen projectsDetailScreen;

    @Inject
    private ManagedInstance<AssetItemWidget> itemWidgetsInstances;

    @Inject
    private TranslationService ts;

    @Inject
    private ProjectActionsWidget projectActionsWidget;

    @Inject
    private AssetsActionsWidget assetsActionsWidget;

    private AssetList assetList;

    public ProjectView() {
    }

    @Inject
    public ProjectView(final AssetList assetList) {
        this.assetList = assetList;
    }

    @Override
    public void init(ProjectScreen presenter) {
        this.presenter = presenter;
        assetsActionsWidget.init();
        projectActionsWidget.init(presenter::goToSettings);
        filterText.setAttribute("placeholder",
                                ts.getTranslation(LibraryConstants.FilterByName));
        detailsContainer.appendChild(projectsDetailScreen.getView().getElement());
        assetsToolbar.appendChild(assetsActionsWidget.getView().getElement());
        projectToolbar.appendChild(projectActionsWidget.getView().getElement());
        assetListContainer.appendChild(assetList.getElement());
        assetList.addChangeHandler(new Command() {
            @Override
            public void execute() {
                presenter.onReload();
            }
        });
    }

    @Override
    public void setProjectName(final String projectName) {
        projectNameContainer.setTextContent(projectName);
    }

    @Override
    public void clearAssets() {
        assetList.clear();
    }

    @Override
    public void resetPageRangeIndicator() {
        assetList.resetPageRangeIndicator();
    }

    @Override
    public void addAsset(final String assetName,
                         final String assetPath,
                         final String assetType,
                         final IsWidget assetIcon,
                         final String lastModifiedDate,
                         final String createdDate,
                         final Command details,
                         final Command select) {
        final AssetItemWidget assetItemWidget = itemWidgetsInstances.get();
        assetItemWidget.init(assetName,
                             assetPath,
                             assetType,
                             assetIcon,
                             lastModifiedDate,
                             createdDate,
                             details,
                             select);
        assetList.add(assetItemWidget.getElement());
    }

    @Override
    public void showIndexingIncomplete() {
        assetList.showEmptyState(ts.getTranslation(LibraryConstants.IndexingHasNotFinished),
                                 ts.getTranslation(LibraryConstants.PleaseWaitWhileTheProjectContentIsBeingIndexed));
    }

    @Override
    public void showSearchHitNothing() {
        assetList.showEmptyState(ts.getTranslation(LibraryConstants.EmptySearch),
                                 ts.getTranslation(LibraryConstants.NoFilesWhereFoundWithTheGivenSearchCriteria));
    }

    @Override
    public void showNoMoreAssets() {
        assetList.showEmptyState(ts.getTranslation(LibraryConstants.EndOfFileList),
                                 ts.getTranslation(LibraryConstants.NoMoreFilesPleasePressPrevious));
    }

    @Override
    public int getFirstIndex() {
        return assetList.getFirstIndex();
    }

    @Override
    public Integer getStep() {
        return assetList.getStep();
    }

    @Override
    public String getFilterValue() {
        return filterText.getValue();
    }

    @Override
    public void setFilterName(String name) {
        this.filterText.setValue(name);
    }

    @SinkNative(Event.ONKEYUP)
    @EventHandler("filter-text")
    public void onFilterTextChange(Event e) {
        presenter.onFilterChange();
    }
}
