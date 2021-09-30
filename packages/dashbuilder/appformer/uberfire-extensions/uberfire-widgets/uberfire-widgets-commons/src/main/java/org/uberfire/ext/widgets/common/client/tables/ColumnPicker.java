/*
 * Copyright 2019 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.ext.widgets.table.client.UberfireColumnPicker;

public class ColumnPicker<T> extends UberfireColumnPicker<T> {

    public static int DETAULT_COLUMN_WIDTH = 120;
    private GridPreferencesStore gridPreferences;
    private Optional<String> columnZeroWidth = Optional.empty();
    private int dataGridMinWidth = 0;
    private int defaultColumnWidthSize = DETAULT_COLUMN_WIDTH;

    public ColumnPicker(DataGrid<T> dataGrid,
                        GridPreferencesStore gridPreferences) {
        super(dataGrid);
        this.gridPreferences = gridPreferences;
    }

    public ColumnPicker(DataGrid<T> dataGrid) {
        super(dataGrid);
    }

    protected void sortAndAddColumns(List<ColumnMeta<T>> columnMetas) {
        updateColumnsMeta(columnMetas);
        super.sortAndAddColumns(columnMetas);
    }

    private void updateColumnsMeta(List<ColumnMeta<T>> columnMetas) {
        for (ColumnMeta meta : columnMetas) {
            checkColumnMeta(meta);
        }
    }

    protected void checkColumnMeta(ColumnMeta<T> columnMeta) {
        if (gridPreferences != null) {
            List<GridColumnPreference> columnPreferences = gridPreferences.getColumnPreferences();
            if (!columnPreferences.isEmpty()) {
                boolean found = false;
                for (int i = 0; i < gridPreferences.getColumnPreferences().size() && !found; i++) {
                    GridColumnPreference gcp = gridPreferences.getColumnPreferences().get(i);
                    if (gcp.getName().equals(getColumnStoreName(columnMeta))) {
                        columnMeta.setVisible(true);
                        if (gcp.getWidth() != null) {
                            dataGrid.setColumnWidth(columnMeta.getColumn(),
                                                    gcp.getWidth());
                        } else {
                            dataGrid.setColumnWidth(columnMeta.getColumn(),
                                                    100,
                                                    Style.Unit.PCT);
                        }
                        columnMeta.setPosition(gcp.getPosition());
                        found = true;
                    }
                }
                if (!found) {
                    columnMeta.setPosition(-1);
                    columnMeta.setVisible(false);
                }
            } else if (gridPreferences.getGlobalPreferences() != null) {
                int position = gridPreferences.getGlobalPreferences().getInitialColumns()
                        .indexOf(getColumnStoreName(columnMeta));
                if (position != -1) {
                    columnMeta.setVisible(true);
                    columnMeta.setPosition(position);
                } else {
                    columnMeta.setPosition(-1);
                    columnMeta.setVisible(false);
                }
            }
        }
    }

    public void setGridPreferencesStore(GridPreferencesStore gridPreferences) {
        this.gridPreferences = gridPreferences;
    }

    protected void addResetButtom(final int left,
                                  final int top,
                                  VerticalPanel popupContent) {
        if (gridPreferences != null) {
            Button resetButton = GWT.create(Button.class);
            resetButton.setText(CommonConstants.INSTANCE.Reset());
            resetButton.setSize(ButtonSize.EXTRA_SMALL);
            resetButton.addClickHandler(event -> resetTableColumns(left,
                                                                   top));

            popupContent.add(resetButton);
        }
    }

    protected boolean addThisColumnToPopup(ColumnMeta<T> columnMeta) {
        return gridPreferences == null || !gridPreferences.getGlobalPreferences()
                .getBannedColumns().contains(getColumnStoreName(columnMeta));
    }

    protected void loadGlobalGridPreferences() {
        gridPreferences.resetGridColumnPreferences();
        for (final ColumnMeta<T> columnMeta : columnMetaList) {
            int position = gridPreferences.getGlobalPreferences().getInitialColumns()
                    .indexOf(getColumnStoreName(columnMeta));
            columnMeta.setPosition(position);
            columnMeta.setVisible(position > -1);
        }
    }

    public List<GridColumnPreference> getColumnsState() {
        List<GridColumnPreference> state = new ArrayList<GridColumnPreference>();
        for (final ColumnMeta<T> cm : columnMetaList) {
            if (cm.isVisible()) {
                state.add(new GridColumnPreference(getColumnStoreName(cm),
                                                   dataGrid.getColumnIndex(cm.getColumn()),
                                                   dataGrid.getColumnWidth(cm.getColumn())));
            }
        }
        return state;
    }

    public void adjustColumnWidths() {
        super.adjustColumnWidths();
        List<GridColumnPreference> preferences = getColumnsState();

        if (preferences.isEmpty()) {
            return;
        }

        // If there's only 1 column then set it's width to 100%. However store the original 'requested' preferences
        // in case a 2nd or 3rd etc column is added and we then need to revert to the 'requested' width
        if (preferences.size() == 1) {
            columnZeroWidth = Optional.of(preferences.get(0).getWidth());
            dataGrid.setColumnWidth(dataGrid.getColumn(0),
                                    100,
                                    Style.Unit.PCT);
            dataGridMinWidth = defaultColumnWidthSize;
            return;
        }

        //So.. more than one column; we best restore column zero's original 'requested' width
        columnZeroWidth.ifPresent(w -> {
            preferences.get(0).setWidth(w);
            columnZeroWidth = Optional.empty();
        });

        int fixedColumnsWidth = 0;
        Map<String, String> fixedWidths = new HashMap<String, String>();
        List<String> columnsToCalculate = new ArrayList<String>();

        for (GridColumnPreference preference : preferences) {
            if (preference.getWidth() != null && preference.getWidth().endsWith(Style.Unit.PX.getType())) {
                String width = preference.getWidth().substring(0,
                                                               preference.getWidth().indexOf(Style.Unit.PX.getType()));
                if (width.contains(".")) {
                    width = width.substring(0,
                                            width.indexOf("."));
                }

                fixedWidths.put(preference.getName(),
                                width + Style.Unit.PX.getType());

                fixedColumnsWidth += Integer.decode(width);
            } else {
                columnsToCalculate.add(preference.getName());
            }
        }

        if (columnsToCalculate.size() > 0) {

            double columnPCT = (100 / columnsToCalculate.size());

            if (dataGrid.getOffsetWidth() != 0) {
                int availableColumnSpace = dataGrid.getOffsetWidth() - fixedColumnsWidth;
                double availablePCT = availableColumnSpace * 100 / dataGrid.getOffsetWidth();
                columnPCT = columnPCT * availablePCT / 100;
            }

            for (ColumnMeta<T> cm : columnMetaList) {
                if (cm.isVisible()) {
                    if (columnsToCalculate.contains(getColumnStoreName(cm))) {
                        dataGrid.setColumnWidth(cm.getColumn(),
                                                columnPCT,
                                                Style.Unit.PCT);
                    } else {
                        dataGrid.setColumnWidth(cm.getColumn(),
                                                fixedWidths.get(getColumnStoreName(cm)));
                    }
                }
            }
        }
        dataGridMinWidth = fixedColumnsWidth + columnsToCalculate.size() * defaultColumnWidthSize;
    }

    public int getDataGridMinWidth() {
        return this.dataGridMinWidth;
    }

    public void setDefaultColumnWidthSize(int defaultColumnSize) {
        this.defaultColumnWidthSize = defaultColumnSize;
    }

}
