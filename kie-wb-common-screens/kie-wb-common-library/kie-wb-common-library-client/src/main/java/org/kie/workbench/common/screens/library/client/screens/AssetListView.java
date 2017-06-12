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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetItemWidget;

@Templated
public class AssetListView
        implements AssetList.View,
                   IsElement {

    @Inject
    @DataField
    Anchor previousPageLink;
    @Inject
    @DataField
    Anchor nextPageLink;
    @Inject
    @DataField
    Anchor toFirstPage;
    @Inject
    @DataField
    Select howManyOnOnePage;
    @Inject
    @DataField
    Span fromToRange;
    @Inject
    @DataField
    ListItem toFirstPageListItem;
    @Inject
    @DataField
    ListItem previousPageLinkListItem;
    @Inject
    @DataField
    ListItem nextPageLinkListItem;
    @Inject
    @DataField("indexing-info")
    Div indexingInfo;
    @Inject
    @DataField("asset-list")
    Div assetList;

    @Inject
    @DataField
    Input pageNumber;

    @Inject
    private TranslationService ts;
    private EmptyState emptyState;

    @Inject
    private ManagedInstance<AssetItemWidget> itemWidgetsInstances;
    private AssetList presenter;

    public AssetListView() {
    }

    @Inject
    public AssetListView(EmptyState emptyState) {
        this.emptyState = emptyState;
    }

    @Override
    public void init(final AssetList presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setStep(int step) {
        howManyOnOnePage.setValue(Integer.toString(step));
    }

    @Override
    public void setForwardDisabled(final boolean disabled) {
        nextPageLinkListItem.setClassName(getDisabledClassName(disabled));
    }

    @Override
    public void setBackwardDisabled(final boolean disabled) {
        toFirstPageListItem.setClassName(getDisabledClassName(disabled));
        previousPageLinkListItem.setClassName(getDisabledClassName(disabled));
    }

    @Override
    public void range(int from,
                      int to) {
        fromToRange.setInnerHTML(from + " - " + to);
    }

    private String getDisabledClassName(final boolean disabled) {
        if (disabled) {
            return "disabled";
        } else {
            return "";
        }
    }

    @Override
    public void clearAssets() {
        DOMUtil.removeAllChildren(assetList);
    }

    @Override
    public void add(HTMLElement element) {
        assetList.appendChild(element);
    }

    @Override
    public void showEmptyStateMessage(String topic,
                                      String message) {
        emptyState.setMessage(topic,
                              message);
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
    public void setPageNumber(int pageNumber) {
        this.pageNumber.setValue(Integer.toString(pageNumber));
    }

    @SinkNative(Event.ONBLUR | Event.ONKEYDOWN)
    @EventHandler("pageNumber")
    public void onPageChange(final Event e) {
        if (e.getKeyCode() < 0 || e.getKeyCode() == KeyCodes.KEY_ENTER) {
            presenter.onPageNumberChange(Integer.valueOf(pageNumber.getValue()));
        }
    }

    @SinkNative(Event.ONCHANGE)
    @EventHandler("howManyOnOnePage")
    public void onStepChange(Event e) {
        presenter.onChangeAmountOfItemsShown(Integer.valueOf(howManyOnOnePage.getValue()));
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
}
