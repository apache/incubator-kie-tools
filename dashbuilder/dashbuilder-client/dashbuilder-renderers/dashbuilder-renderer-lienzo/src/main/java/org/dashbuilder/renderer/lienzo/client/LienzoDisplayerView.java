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
package org.dashbuilder.renderer.lienzo.client;

import java.util.Date;

import com.ait.lienzo.charts.client.core.model.DataTable;
import com.ait.lienzo.charts.client.core.model.DataTableColumn;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.lienzo.client.resources.i18n.LienzoDisplayerConstants;
import org.gwtbootstrap3.client.ui.Label;

public abstract class LienzoDisplayerView<P extends LienzoDisplayer>
        extends AbstractGwtDisplayerView<P>
        implements LienzoDisplayer.View<P> {

    private Panel container = new FlowPanel();
    private Panel filterPanel = new HorizontalPanel();
    private Panel displayerPanel = new FlowPanel();
    private HTML titleHtml = new HTML();
    private DataTable dataTable = new DataTable();

    protected int width = 500;
    protected int height = 300;
    protected int marginTop = 10;
    protected int marginBottom = 10;
    protected int marginRight = 10;
    protected int marginLeft = 10;
    protected DisplayerSubType subType = null;
    protected boolean filterEnabled = false;
    protected boolean resizeEnabled = false;
    protected String fontFamily = "Verdana";
    protected String fontStyle = "bold";
    protected int fontSize = 8;

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

    @Override
    public void showTitle(String title) {
        titleHtml.setText(title);
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    @Override
    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Override
    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    @Override
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Override
    public void setSubType(DisplayerSubType subType) {
        this.subType = subType;
    }

    @Override
    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    @Override
    public void setResizeEnabled(boolean resizeEnabled) {
        this.resizeEnabled = resizeEnabled;
    }

    @Override
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    @Override
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public String getGroupsTitle() {
        return LienzoDisplayerConstants.INSTANCE.categories();
    }

    @Override
    public String getColumnsTitle() {
        return LienzoDisplayerConstants.INSTANCE.series();
    }

    @Override
    public void dataClear() {
        dataTable = new DataTable();
    }

    @Override
    public void dataAddColumn(String columnId, String columnName, ColumnType columnType) {
        dataTable.addColumn(columnId, getColumnType(columnType));
    }

    @Override
    public void dataAddValue(String columnId, Date value) {
        dataTable.addValue(columnId, value);
    }

    @Override
    public void dataAddValue(String columnId, Number value) {
        dataTable.addValue(columnId, value.doubleValue());
    }

    @Override
    public void dataAddValue(String columnId, String value) {
        dataTable.addValue(columnId, value);
    }

    @Override
    public void clearFilterStatus() {
        if (filterPanel != null) {
            filterPanel.clear();
        }
    }

    @Override
    public void addFilterValue(String value) {
        filterPanel.add(new Label(value));
    }

    @Override
    public void addFilterReset() {
        Anchor anchor = new Anchor(LienzoDisplayerConstants.INSTANCE.resetAnchor());
        filterPanel.add(anchor);
        anchor.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                getPresenter().onFilterResetClicked();
            }
        });
    }

    @Override
    public void nodata() {
        showDisplayer(new Label(LienzoDisplayerConstants.INSTANCE.noData()));
    }

    protected DataTableColumn.DataTableColumnType getColumnType(ColumnType type) {
        if (ColumnType.LABEL.equals(type)) {
            return DataTableColumn.DataTableColumnType.STRING;
        }
        if (ColumnType.TEXT.equals(type)) {
            return DataTableColumn.DataTableColumnType.STRING;
        }
        if (ColumnType.NUMBER.equals(type)) {
            return DataTableColumn.DataTableColumnType.NUMBER;
        }
        if (ColumnType.DATE.equals(type)) {
            return DataTableColumn.DataTableColumnType.DATE;
        }
        return DataTableColumn.DataTableColumnType.STRING;
    }
}
