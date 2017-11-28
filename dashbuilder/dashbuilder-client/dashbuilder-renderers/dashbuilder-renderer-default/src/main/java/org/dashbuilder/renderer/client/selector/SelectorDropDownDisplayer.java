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
package org.dashbuilder.renderer.client.selector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.AbstractErraiDisplayer;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@Dependent
public class SelectorDropDownDisplayer extends AbstractErraiDisplayer<SelectorDropDownDisplayer.View> {

    public interface View extends AbstractErraiDisplayer.View<SelectorDropDownDisplayer> {

        void showTitle(String title);

        void margins(int top, int bottom, int left, int right);

        void setWidth(int width);

        void showSelectHint(String column, boolean multiple);

        void showResetHint(String column, boolean multiple);

        void showCurrentSelection(String text, String hint);

        void clearItems();

        void addItem(SelectorDropDownItem item);

        String getGroupsTitle();

        String getColumnsTitle();
    }

    protected View view;
    protected boolean filterOn = false;
    protected boolean multipleSelections = false;
    protected SyncBeanManager beanManager;
    protected Set<SelectorDropDownItem> itemCollection = new HashSet<>();

    @Inject
    public SelectorDropDownDisplayer(View view, SyncBeanManager beanManager) {
        this.beanManager = beanManager;
        this.view = view;
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void close() {
        super.close();
        clearItems();
    }

    protected void clearItems() {
        view.clearItems();
        for (SelectorDropDownItem item : itemCollection) {
            beanManager.destroyBean(item);
        }
        itemCollection.clear();
    }

    protected void resetItems() {
        for (SelectorDropDownItem item : itemCollection) {
            item.reset();
        }
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxColumns(-1)
                .setMinColumns(1)
                .setExtraColumnsAllowed(true)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle());

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.SUBTYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeDef.TITLE)
                .supportsAttribute(DisplayerAttributeDef.TITLE_VISIBLE)
                .supportsAttribute(DisplayerAttributeGroupDef.SELECTOR_GROUP)
                .excludeAttribute(DisplayerAttributeGroupDef.SELECTOR_SHOW_INPUTS)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP);
    }

    @Override
    protected void beforeDataSetLookup() {
        // Make sure the drop down entries are sorted
        DataSetGroup group = dataSetHandler.getCurrentDataSetLookup().getLastGroupOp();
        if (dataSetHandler.getCurrentDataSetLookup().getOperationList(DataSetSort.class).isEmpty() && group != null) {
            ColumnGroup column = group.getColumnGroup();
            if (!GroupStrategy.FIXED.equals(column.getStrategy())) {
                dataSetHandler.sort(column.getSourceId(), SortOrder.ASCENDING);
            }
        }
    }

    @Override
    protected void createVisualization() {
        if (displayerSettings.isTitleVisible()) {
            view.showTitle(displayerSettings.getTitle());
        }
        if (displayerSettings.getSelectorWidth() > 0) {
            view.setWidth(displayerSettings.getSelectorWidth());
        }
        view.margins(displayerSettings.getChartMarginTop(),
                displayerSettings.getChartMarginBottom(),
                displayerSettings.getChartMarginLeft(),
                displayerSettings.getChartMarginRight());

        multipleSelections = displayerSettings.isSelectorMultiple();
        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        DataColumn firstColumn = dataSet.getColumnByIndex(0);
        ColumnSettings columnSettings = displayerSettings.getColumnSettings(firstColumn);
        String firstColumnName = columnSettings.getColumnName();
        List<Integer> currentFilter = super.filterIndexes(firstColumn.getId());

        clearItems();

        // Generate the list entries from the current data set
        for (int i = 0; i < dataSet.getRowCount(); i++) {

            Object obj = dataSet.getValueAt(i, 0);
            if (obj == null) {
                continue;
            }

            String value = super.formatValue(i, 0);
            StringBuilder title = new StringBuilder();

            int ncolumns = dataSet.getColumns().size();
            if (ncolumns > 1) {
                for (int j = 1; j < ncolumns; j++) {
                    DataColumn extraColumn = dataSet.getColumnByIndex(j);
                    columnSettings = displayerSettings.getColumnSettings(extraColumn);
                    String extraColumnName = columnSettings.getColumnName();
                    Object extraValue = dataSet.getValueAt(i, j);
                    if (extraValue != null) {
                        title.append(j > 1 ? " " : "");
                        String formattedValue = super.formatValue(i, j);
                        title.append(extraColumnName).append("=").append(formattedValue);
                    }
                }
            }
            final SelectorDropDownItem item = beanManager.lookupBean(SelectorDropDownItem.class).newInstance();
            item.init(i, value, title.toString());
            item.setSelectionIconVisible(multipleSelections);
            item.setOnSelectCommand(() -> onItemSelected(item));
            item.setOnResetCommand(() -> onItemReset(item));
            if (currentFilter.contains(i)) {
                item.select();
            } else {
                item.reset();
            }
            view.addItem(item);
            itemCollection.add(item);
        }

        // Add a selector hint according to the filter status
        if (currentFilter.isEmpty()) {
            view.showSelectHint(firstColumnName, multipleSelections);
        } else {
            view.showResetHint(firstColumnName, multipleSelections);
        }
    }

    public String getFirstColumnId() {
        DataColumn firstColumn = dataSet.getColumnByIndex(0);
        return firstColumn.getId();
    }

    public String getFirstColumnName() {
        DataColumn firstColumn = dataSet.getColumnByIndex(0);
        ColumnSettings columnSettings = displayerSettings.getColumnSettings(firstColumn);
        return columnSettings.getColumnName();
    }

    protected void onItemSelected(SelectorDropDownItem item) {
        if (displayerSettings.isFilterEnabled()) {

            String firstColumnId = getFirstColumnId();
            String firstColumnName = getFirstColumnName();

            // Reset current selection (if any) in single selection mode
            if (!multipleSelections) {
                List<Integer> currentFilter = filterIndexes(firstColumnId);
                if (currentFilter != null && !currentFilter.isEmpty()) {
                    resetItems();
                    super.filterReset();
                    item.select();
                }
            }
            // Filter by the selected entry
            filterUpdate(firstColumnId, item.getId());
            List<Integer> currentFilter = super.filterIndexes(firstColumnId);
            showSelectedItems(currentFilter);
            view.showResetHint(firstColumnName, multipleSelections);
        }
    }

    protected void onItemReset(SelectorDropDownItem item) {
        if (displayerSettings.isFilterEnabled()) {

            String firstColumnId = getFirstColumnId();
            String firstColumnName = getFirstColumnName();

            filterUpdate(firstColumnId, item.getId());
            List<Integer> currentFilter = super.filterIndexes(firstColumnId);
            if (currentFilter.isEmpty()) {
                view.showSelectHint(firstColumnName, multipleSelections);
            } else {
                showSelectedItems(currentFilter);
            }
        }
    }

    protected void showSelectedItems(List<Integer> currentFilter) {
        List<String> itemList = new ArrayList<>();
        for (Integer idx : currentFilter) {
            String value = super.formatValue(idx, 0);
            itemList.add(value);
        }
        int width = displayerSettings.getSelectorWidth();
        String hint = formatItemList(itemList);
        String text = width > 0 ? formatItemList(itemList, width) : hint;
        view.showCurrentSelection(text, hint);
    }

    public String formatItemList(List<String> itemList) {
        StringBuffer out = new StringBuffer();
        for (String item : itemList) {
            if (out.length() > 0) {
                out.append(", ");
            }
            out.append(item);
        }
        return out.toString() + " ";
    }
    public String formatItemList(List<String> itemList, int maxWidth) {
        StringBuffer out = new StringBuffer();
        int charLength = 9;
        int availableChars = maxWidth / charLength;
        for (String item : itemList) {
            if (availableChars < 0) {
                out.append(" ...");
                return out.toString();
            }
            if (out.length() > 0) {
                availableChars -= 2;
                if (availableChars <= 0) {
                    out.append(" ...");
                    return out.toString();
                } else {
                    out.append(", ");
                }
            }
            if (item.length() > availableChars) {
                out.append(item.substring(0, availableChars)).append("...");
                return out.toString();
            }
            else {
                availableChars -= item.length();
                out.append(item);
            }
        }
        return out.toString() + " ";
    }

    void onResetSelections() {
        filterReset();
        updateVisualization();
    }
}
