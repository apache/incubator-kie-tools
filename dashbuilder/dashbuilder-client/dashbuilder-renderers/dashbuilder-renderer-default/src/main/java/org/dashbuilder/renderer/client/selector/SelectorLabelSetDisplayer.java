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
public class SelectorLabelSetDisplayer extends AbstractErraiDisplayer<SelectorLabelSetDisplayer.View> {

    public interface View extends AbstractErraiDisplayer.View<SelectorLabelSetDisplayer> {

        void showTitle(String title);

        void setWidth(int width);

        void margins(int top, int bottom, int left, int right);

        void clearItems();

        void addItem(SelectorLabelItem item);

        String getGroupsTitle();

        String getColumnsTitle();

        void noData();
    }

    protected View view;
    protected boolean filterOn = false;
    protected boolean multipleSelections = false;
    protected SyncBeanManager beanManager;
    protected Set<SelectorLabelItem> itemCollection = new HashSet<>();

    @Inject
    public SelectorLabelSetDisplayer(View view, SyncBeanManager beanManager) {
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
        for (SelectorLabelItem item : itemCollection) {
            beanManager.destroyBean(item);
        }
        itemCollection.clear();
    }

    protected void resetItems() {
        for (SelectorLabelItem item : itemCollection) {
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
                .excludeAttribute(DisplayerAttributeDef.SELECTOR_SHOW_INPUTS)
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
        view.margins(displayerSettings.getChartMarginTop(),
                displayerSettings.getChartMarginBottom(),
                displayerSettings.getChartMarginLeft(),
                displayerSettings.getChartMarginRight());

        multipleSelections = displayerSettings.isSelectorMultiple();
        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        clearItems();
        if (dataSet.getRowCount() == 0) {
            view.noData();
        }
        else {
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
                        ColumnSettings columnSettings = displayerSettings.getColumnSettings(extraColumn);
                        String extraColumnName = columnSettings.getColumnName();
                        Object extraValue = dataSet.getValueAt(i, j);
                        if (extraValue != null) {
                            title.append(j > 1 ? " " : "");
                            String formattedValue = super.formatValue(i, j);
                            title.append(extraColumnName).append("=").append(formattedValue);
                        }
                    }
                }
                final SelectorLabelItem item = beanManager.lookupBean(SelectorLabelItem.class).newInstance();
                item.init(i, value, title.toString());
                item.setOnSelectCommand(() -> onItemSelected(item));
                item.setOnResetCommand(() -> onItemReset(item));
                view.addItem(item);
                itemCollection.add(item);
            }

            // Set both the global and each item width
            if (displayerSettings.getSelectorWidth() > 0) {
                view.setWidth(displayerSettings.getSelectorWidth() + 100);
                int itemWidth = 85 / itemCollection.size();
                for (SelectorLabelItem labelItem : itemCollection) {
                    labelItem.setWidth(itemWidth);
                }
            }
        }
    }

    public String getFirstColumnId() {
        DataColumn firstColumn = dataSet.getColumnByIndex(0);
        return firstColumn.getId();
    }

    void onItemSelected(SelectorLabelItem item) {
        if (displayerSettings.isFilterEnabled()) {

            String firstColumnId = getFirstColumnId();

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
        }
    }

    void onItemReset(SelectorLabelItem item) {
        if (displayerSettings.isFilterEnabled()) {

            String firstColumnId = getFirstColumnId();
            filterUpdate(firstColumnId, item.getId());
        }
    }
}
