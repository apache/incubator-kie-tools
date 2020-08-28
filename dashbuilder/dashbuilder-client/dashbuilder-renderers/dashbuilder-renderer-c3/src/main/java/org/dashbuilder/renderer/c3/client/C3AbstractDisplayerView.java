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
package org.dashbuilder.renderer.c3.client;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.c3.client.resources.i18n.C3DisplayerConstants;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

public abstract class C3AbstractDisplayerView<P extends C3AbstractDisplayer>
                                             extends AbstractGwtDisplayerView<P>
                                             implements C3AbstractDisplayer.View<P> {

    protected Panel container = GWT.create(FlowPanel.class);
    protected Panel displayerPanel = GWT.create(FlowPanel.class);
    private Panel filterPanel = GWT.create(FlowPanel.class);
    private Panel dataPanel = GWT.create(FlowPanel.class);
    private HTML titleHtml = GWT.create(HTML.class);

    FlexTable dataTable = GWT.create(FlexTable.class);

    protected int width;
    protected int height;

    @Override
    public void init(P presenter) {
        super.setPresenter(presenter);
        super.setVisualization(container);
        dataPanel.setVisible(false);
        dataPanel.add(dataTable);
        container.add(titleHtml);
        container.add(filterPanel);
        container.add(displayerPanel);
        container.add(dataPanel);
        filterPanel.getElement().setAttribute("cellpadding", "2");
    }

    @Override
    public void showTitle(String title) {
        titleHtml.setText(title);
    }

    @Override
    public void setFilterLabelSet(FilterLabelSet widget) {
        HTMLElement element = widget.getElement();
        element.getStyle().setProperty("position", "absolute");
        element.getStyle().setProperty("z-index", "20");
        filterPanel.clear();
        filterPanel.add(ElementWrapperWidget.getWidget(element));
    }

    @Override
    public void noData() {
        FlowPanel noDataPanel = GWT.create(FlowPanel.class);
        noDataPanel.setWidth(width + "px");
        noDataPanel.setHeight(height + "px");
        Label lblNoData = GWT.create(Label.class);
        lblNoData.setText(C3DisplayerConstants.INSTANCE.common_noData());
        noDataPanel.add(lblNoData);
        displayerPanel.clear();
        displayerPanel.add(noDataPanel);
        dataTable.clear();
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setTableData(String[][] data) {
        dataTable.clear();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                dataTable.setWidget(j, i, new Text(data[i][j]));
            }
        }
    }
}