/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.kogito.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.mvp.PlaceRequest;

/**
 * This is a trimmed down {@code org.kie.workbench.common.widgets.metadata.client.KieEditor} for Kogito.
 * @param <CONTENT> The domain model of the editor
 */
public abstract class MultiPageEditorContainerPresenter<CONTENT>
        extends BaseKogitoEditor<CONTENT>
        implements MultiPageEditorContainerView.Presenter {

    private MultiPageEditorContainerView multiPageEditorContainerView;

    protected MultiPageEditorContainerPresenter() {
        //CDI proxy
    }

    protected MultiPageEditorContainerPresenter(final BaseEditorView baseEditorView,
                                                final PlaceManager placeManager,
                                                final MultiPageEditorContainerView multiPageEditorContainerView) {
        super(baseEditorView,
              placeManager);
        this.multiPageEditorContainerView = multiPageEditorContainerView;
    }

    @Override
    protected void init(final PlaceRequest place) {
        multiPageEditorContainerView.init(this);
        super.init(place);
    }

    protected void addPage(final Page page) {
        multiPageEditorContainerView.addPage(page);
    }

    protected void resetEditorPages() {
        multiPageEditorContainerView.clear();
        multiPageEditorContainerView.setEditorWidget(getBaseEditorView());
    }

    protected void OnClose() {
        multiPageEditorContainerView.clear();
    }

    protected void selectEditorTab() {
        multiPageEditorContainerView.selectEditorTab();
    }

    protected boolean isEditorTabSelected() {
        return multiPageEditorContainerView.isEditorTabSelected();
    }

    public void setSelectedTab(final int index) {
        multiPageEditorContainerView.setSelectedTab(index);
    }

    protected int getSelectedTabIndex() {
        return multiPageEditorContainerView.getSelectedTabIndex();
    }

    public IsWidget asWidget() {
        return getWidget().asWidget();
    }

    protected MultiPageEditorContainerView getWidget() {
        return multiPageEditorContainerView;
    }

    @Override
    public void onEditTabSelected() {
    }

    @Override
    public void onEditTabUnselected() {
    }
}
