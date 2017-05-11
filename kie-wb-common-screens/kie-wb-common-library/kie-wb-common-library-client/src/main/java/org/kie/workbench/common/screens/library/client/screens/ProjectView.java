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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.AssetItemWidget;
import org.kie.workbench.common.screens.library.client.widgets.AssetsActionsWidget;
import org.kie.workbench.common.screens.library.client.widgets.ProjectActionsWidget;
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
    @DataField("asset-list")
    Div assetList;
    @Inject
    @DataField("filter-text")
    Input filterText;
    @Inject
    @DataField
    Input pageNumber;
    @Inject
    @DataField("project-name")
    Div projectNameContainer;
    @Inject
    @DataField
    Button previousPageLink;
    @Inject
    @DataField
    Button nextPageLink;
    @Inject
    @DataField
    Button toFirstPage;
    @Inject
    @DataField
    Select howManyOnOnePage;
    @Inject
    @DataField
    Span fromToRange;
    @Inject
    @DataField("indexing-info")
    Div indexingInfo;
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

    private EmptyState emptyState;

    public ProjectView() {
    }

    @Inject
    public ProjectView(EmptyState emptyState) {
        this.emptyState = emptyState;
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

        howManyOnOnePage.setValue("15");
    }

    @Override
    public void setProjectName(final String projectName) {
        projectNameContainer.setTextContent(projectName);
    }

    @Override
    public void clearAssets() {
        DOMUtil.removeAllChildren(assetList);
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
        assetList.appendChild(assetItemWidget.getElement());
    }

    @Override
    public void showIndexingIncomplete() {
        emptyState.setMessage(ts.getTranslation(LibraryConstants.IndexingHasNotFinished),
                              ts.getTranslation(LibraryConstants.PleaseWaitWhileTheProjectContentIsBeingIndexed));
        showEmptyState();
    }

    private void showEmptyState() {

        indexingInfo.setClassName("blank-slate-pf");
        indexingInfo.getStyle()
                .setProperty("height",
                             "100%");
        indexingInfo.getStyle()
                .setProperty("width",
                             "100%");
        indexingInfo.getStyle()
                .setProperty("visibility",
                             "visible");
        indexingInfo.setInnerHTML(emptyState.getElement().getOuterHTML());
    }

    @Override
    public void hideEmptyState() {

        emptyState.clear();

        indexingInfo.setClassName("");
        indexingInfo.getStyle()
                .setProperty("visibility",
                             "hidden");
        indexingInfo.getStyle()
                .setProperty("height",
                             "0px");
        indexingInfo.getStyle()
                .setProperty("width",
                             "0px");
        indexingInfo.setInnerHTML("");
    }

    @Override
    public void showSearchHitNothing() {
        emptyState.setMessage(ts.getTranslation(LibraryConstants.EmptySearch),
                              ts.getTranslation(LibraryConstants.NoFilesWhereFoundWithTheGivenSearchCriteria));
        showEmptyState();
    }

    @Override
    public void showNoMoreAssets() {
        emptyState.setMessage(ts.getTranslation(LibraryConstants.EndOfFileList),
                              ts.getTranslation(LibraryConstants.NoMoreFilesPleasePressPrevious));
        showEmptyState();
    }

    @Override
    public void setForwardDisabled(final boolean disabled) {
        nextPageLink.setDisabled(disabled);
    }

    @Override
    public void setBackwardDisabled(final boolean disabled) {
        toFirstPage.setDisabled(disabled);
        previousPageLink.setDisabled(disabled);
    }

    @Override
    public Integer getPageNumber() {
        return Integer.valueOf(pageNumber.getValue());
    }

    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber.setValue(Integer.toString(pageNumber));
    }

    @Override
    public Integer getStep() {
        return Integer.valueOf(howManyOnOnePage.getValue());
    }

    @Override
    public void range(int from,
                      int to) {
        fromToRange.setInnerHTML(from + " - " + to);
    }

    @Override
    public String getFilterValue() {
        return filterText.getValue();
    }

    @Override
    public void setFilterName(String name) {
        this.filterText.setValue(name);
    }

    @SinkNative(Event.ONBLUR | Event.ONKEYDOWN)
    @EventHandler("pageNumber")
    public void onPageChange(Event e) {
        if (e.getKeyCode() < 0 || e.getKeyCode() == KeyCodes.KEY_ENTER) {
            presenter.onUpdateAssets();
        }
    }

    @SinkNative(Event.ONCHANGE)
    @EventHandler("howManyOnOnePage")
    public void onStepChange(Event e) {
        presenter.onUpdateAssets();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("toFirstPage")
    public void toFirstPage(Event e) {
        presenter.onToFirstPage();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("nextPageLink")
    public void onNextPage(Event e) {
        presenter.onToNextPage();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("previousPageLink")
    public void onPreviousNextPage(Event e) {
        presenter.onToPrevious();
    }

    @SinkNative(Event.ONKEYUP)
    @EventHandler("filter-text")
    public void onFilterTextChange(Event e) {
        presenter.onUpdateAssets();
    }
}
