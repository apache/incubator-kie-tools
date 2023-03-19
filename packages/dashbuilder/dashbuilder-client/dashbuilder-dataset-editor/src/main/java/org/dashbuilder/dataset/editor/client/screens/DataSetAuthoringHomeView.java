/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.editor.client.screens;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.editor.client.resources.i18n.DataSetAuthoringConstants;

@Dependent
public class DataSetAuthoringHomeView extends Composite implements DataSetAuthoringHomePresenter.View {

    interface DataSetEditorViewBinder extends UiBinder<Widget, DataSetAuthoringHomeView> {}
    private static DataSetEditorViewBinder uiBinder = GWT.create(DataSetEditorViewBinder.class);

    @UiField
    HTMLPanel initialViewPanel;

    @UiField
    Hyperlink newDataSetLink;

    @UiField
    HTML dataSetCountText;

    DataSetAuthoringHomePresenter presenter;

    public DataSetAuthoringHomeView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(DataSetAuthoringHomePresenter presenter) {
        this.presenter = presenter;
    }

    public void setDataSetCount(int n) {
        dataSetCountText.setText(DataSetAuthoringConstants.INSTANCE.dataSetCount(n));
    }

    @UiHandler("newDataSetLink")
    public void onNewDataSetClicked(ClickEvent event) {
        presenter.newDataSet();
    }
}
