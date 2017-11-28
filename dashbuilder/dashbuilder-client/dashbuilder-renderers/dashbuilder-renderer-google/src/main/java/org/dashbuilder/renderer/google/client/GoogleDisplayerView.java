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
package org.dashbuilder.renderer.google.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.format.DateFormat;
import com.googlecode.gwt.charts.client.format.DateFormatOptions;
import com.googlecode.gwt.charts.client.format.NumberFormat;
import com.googlecode.gwt.charts.client.format.NumberFormatOptions;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public abstract class GoogleDisplayerView<P extends GoogleDisplayer>
        extends AbstractGwtDisplayerView<P>
        implements GoogleDisplayer.View<P> {

    private Panel container = new FlowPanel();
    private Panel filterPanel = new FlowPanel();
    private Panel displayerPanel = new FlowPanel();
    private HTML titleHtml = new HTML();
    private DataTable dataTable ;
    private GoogleRenderer googleRenderer;

    public void setRenderer(GoogleRenderer googleRenderer) {
        this.googleRenderer = googleRenderer;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void showDisplayer(Widget w) {
        displayerPanel.clear();
        displayerPanel.add(w);
    }

    @Override
    public void init(P presenter) {
        super.setPresenter(presenter);
        super.setVisualization(container);

        container.add(titleHtml);
        container.add(filterPanel);
        container.add(displayerPanel);

        filterPanel.getElement().setAttribute("cellpadding", "2");
    }

    /**
     * GCharts drawing is performed asynchronously
     */
    @Override
    public void draw() {
        if (googleRenderer == null)  {
            getPresenter().showError(new ClientRuntimeError("Google renderer not set"));
        }
        else if (!getPresenter().isDrawn())  {
            List<Displayer> displayerList = new ArrayList<Displayer>();
            displayerList.add(getPresenter());
            googleRenderer.draw(displayerList);
        }
    }

    @Override
    public void dataClear() {
        dataTable = DataTable.create();
    }

    @Override
    public void dataRowCount(int rowCount) {
        dataTable.addRows(rowCount);
    }

    @Override
    public void dataAddColumn(ColumnType type, String id, String name) {
        dataTable.addColumn(getColumnType(type), name, id);
    }

    @Override
    public void dataSetValue(int row, int column, Date value) {
        dataTable.setValue(row, column, value);
    }

    @Override
    public void dataSetValue(int row, int column, double value) {
        dataTable.setValue(row, column, value);
    }

    @Override
    public void dataSetValue(int row, int column, String value) {
        dataTable.setValue(row, column, value);
    }

    @Override
    public void dataFormatDateColumn(String pattern, int column) {
        DateFormatOptions dateFormatOptions = DateFormatOptions.create();
        dateFormatOptions.setPattern(pattern);
        DateFormat dateFormat = DateFormat.create(dateFormatOptions);
        dateFormat.format(dataTable, column);
    }

    @Override
    public void dataFormatNumberColumn(String pattern, int column) {
        NumberFormatOptions numberFormatOptions = NumberFormatOptions.create();
        numberFormatOptions.setPattern(pattern);
        NumberFormat numberFormat = NumberFormat.create(numberFormatOptions);
        numberFormat.format(dataTable, column);
    }

    @Override
    public String getGroupsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Categories();
    }

    @Override
    public String getColumnsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Series();
    }

    @Override
    public void showTitle(String title) {
        titleHtml.setText(title);
    }

    @Override
    public void setFilterLabelSet(FilterLabelSet widget) {
        HTMLElement element = widget.getElement();
        element.getStyle().setProperty("position", "absolute");
        element.getStyle().setProperty("z-index", "10");
        filterPanel.clear();
        filterPanel.add(ElementWrapperWidget.getWidget(element));
    }

    public com.googlecode.gwt.charts.client.ColumnType getColumnType(ColumnType type) {
        if (ColumnType.LABEL.equals(type)) {
            return com.googlecode.gwt.charts.client.ColumnType.STRING;
        }
        if (ColumnType.TEXT.equals(type)) {
            return com.googlecode.gwt.charts.client.ColumnType.STRING;
        }
        if (ColumnType.NUMBER.equals(type)) {
            return com.googlecode.gwt.charts.client.ColumnType.NUMBER;
        }
        if (ColumnType.DATE.equals(type)) {
            return com.googlecode.gwt.charts.client.ColumnType.DATE;
        }
        return com.googlecode.gwt.charts.client.ColumnType.STRING;
    }
}
