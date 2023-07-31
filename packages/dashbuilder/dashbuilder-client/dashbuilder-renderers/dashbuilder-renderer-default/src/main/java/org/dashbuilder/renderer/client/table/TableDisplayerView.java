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
package org.dashbuilder.renderer.client.table;

import java.util.List;

import javax.inject.Inject;

import elemental2.dom.CSSProperties.HeightUnionType;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.displayer.client.AbstractErraiDisplayerView;
import org.dashbuilder.patternfly.table.Table;
import org.dashbuilder.renderer.client.resources.i18n.TableConstants;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class TableDisplayerView extends AbstractErraiDisplayerView<TableDisplayer> implements TableDisplayer.View {

    @Inject
    protected Table table;

    @Inject
    @DataField
    HTMLDivElement rootContainer;

    @Inject
    @DataField
    HTMLDivElement filterLabelContainer;

    @Inject
    @DataField
    HTMLDivElement tableContainer;

    @Inject
    Elemental2DomUtil elemental2DomUtil;

    @Override
    public void init(TableDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization(Js.cast(rootContainer));

        tableContainer.append(table.getElement());
        filterLabelContainer.append(presenter.getFilterLabelSet().getElement());

        table.setOnCellSelectedListener(presenter::selectCell);
    }

    @Override
    public void showTitle(String title) {
        table.setTitle(title);
    }

    @Override
    public String getGroupsTitle() {
        return TableConstants.INSTANCE.tableDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return TableConstants.INSTANCE.tableDisplayer_columnsTitle();
    }

    @Override
    public void redrawTable(List<String> columnsNames, String[][] data, int pageSize) {
        table.buildTable(columnsNames, data, pageSize);
    }

    @Override
    public void setWidth(int width) {
        table.getElement().style.width = WidthUnionType.of(width + "px");
    }

    @Override
    public void setHeight(int chartHeight) {
        table.getElement().style.height = HeightUnionType.of(chartHeight + "px");
    }

    @Override
    public void fullWidth() {
        table.getElement().style.width = WidthUnionType.of("100%");
    }

    @Override
    public void setSortEnabled(boolean enabled) {
        //table.setSortEnabled(enabled);
    }

    @Override
    public void setColumnPickerEnabled(boolean enabled) {
        //table.setColumnPickerButtonVisible(enabled);
    }

    @Override
    public void addColumn(ColumnType columnType,
                          String columnId,
                          String columnName,
                          int index,
                          boolean selectEnabled,
                          boolean sortEnabled) {
        // no-op

    }

    @Override
    public void gotoFirstPage() {
        table.showPage(1);
    }

    @Override
    public void setSelectable(boolean selectable) {
        table.setSelectable(selectable);
    }

}
