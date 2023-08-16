/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.explorer.navigator.view;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorView;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

@Dependent
public class BootstrapNavigatorView
        extends Composite
        implements NavigatorView<NavigatorItem<?>> {

    interface ViewBinder extends UiBinder<Widget, BootstrapNavigatorView> {

    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    ResizeFlowPanel mainPanel;

    @UiField
    FlowPanel loadingPanel;

    @UiField
    Container container;

    private Row currentRow;
    private int itemCounter;

    public BootstrapNavigatorView() {
        initWidget(uiBinder.createAndBindUi(this));
        this.currentRow = null;
        this.itemCounter = 0;
        setLoading(false);
    }

    @Override
    public BootstrapNavigatorView add(final NavigatorItemView<NavigatorItem<?>> view) {
        addItem(view.asWidget());
        return this;
    }

    @Override
    public BootstrapNavigatorView clear() {
        container.clear();
        resetRow();
        return this;
    }

    @Override
    public NavigatorView<NavigatorItem<?>> setLoading(final boolean loading) {
        container.setVisible(!loading);
        loadingPanel.setVisible(loading);
        return this;
    }

    private void addItem(final IsWidget widget) {
        if (null == currentRow || (itemCounter == 4)) {
            resetRow();
        }
        final Column column = new Column(ColumnSize.MD_3);
        column.add(widget);
        currentRow.add(column);
        itemCounter++;
    }

    private void resetRow() {
        currentRow = new Row();
        container.add(currentRow);
        itemCounter = 0;
    }
}
