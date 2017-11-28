/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.PanelGroup;

import javax.enterprise.context.Dependent;

/**
 * <p>Default view for DataSetPanel presenter.</p>
 * 
 * @since 0.3.0
 */
@Dependent
public class DataSetExplorerView extends Composite implements DataSetExplorer.View {

    interface DataSetExplorerViewBinder extends UiBinder<Widget, DataSetExplorerView> {}
    private static DataSetExplorerViewBinder uiBinder = GWT.create(DataSetExplorerViewBinder.class);

    interface DataSetExplorerViewStyle extends CssResource {
    }

    @UiField
    DataSetExplorerViewStyle style;

    @UiField
    com.google.gwt.user.client.ui.Label emptyLabel;

    @UiField
    PanelGroup dataSetsPanelGroup;

    private DataSetExplorer presenter;

    public DataSetExplorerView() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @Override
    public void init(final DataSetExplorer presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public DataSetExplorer.View addPanel(final DataSetPanel.View panelView) {
        dataSetsPanelGroup.add(panelView);
        emptyLabel.setVisible(false);
        dataSetsPanelGroup.setVisible(true);
        return this;
    }

    @Override
    public DataSetExplorer.View clear() {
        dataSetsPanelGroup.clear();
        dataSetsPanelGroup.setVisible(false);
        emptyLabel.setVisible(true);
        return this;
    }
}
