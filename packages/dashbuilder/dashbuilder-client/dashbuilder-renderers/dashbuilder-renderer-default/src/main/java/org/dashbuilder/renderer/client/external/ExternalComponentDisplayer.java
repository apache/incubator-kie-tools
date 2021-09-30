/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.client.external;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.AbstractErraiDisplayer;
import org.dashbuilder.displayer.client.widgets.ExternalComponentPresenter;
import org.dashbuilder.displayer.external.ExternalColumn;
import org.dashbuilder.displayer.external.ExternalColumnSettings;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
import org.dashbuilder.displayer.external.ExternalDataSet;
import org.dashbuilder.displayer.external.ExternalFilterRequest;

@Dependent
public class ExternalComponentDisplayer extends AbstractErraiDisplayer<ExternalComponentDisplayer.View> {

    public interface View extends AbstractErraiDisplayer.View<ExternalComponentDisplayer> {

        void setSize(int chartWidth, int chartHeight);

        void setMargin(int chartMarginTop, int chartMarginRight, int chartMarginBottom, int chartMarginLeft);

        void setFilterLabelSet(FilterLabelSet widget);

    }

    @Inject
    View view;

    @Inject
    ExternalComponentPresenter externalComponentPresenter;

    @Inject
    FilterLabelSet filterLabelSet;

    @Inject
    ExternalComponentMessageHelper messageHelper;

    private String componentId;

    @Override
    public View getView() {
        return view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setFilterLabelSet(filterLabelSet);
        this.filterLabelSet.setOnClearAllCommand(this::onFilterClearAll);
        externalComponentPresenter.setFilterConsumer(this::receiveFilterRequest);
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints().setGroupAllowed(true)
                                                                                   .setGroupRequired(false)
                                                                                   .setExtraColumnsAllowed(true)
                                                                                   .setGroupsTitle("Groups")
                                                                                   .setColumnsTitle("Columns");

        return new DisplayerConstraints(lookupConstraints).supportsAttribute(DisplayerAttributeDef.TYPE)
                                                          .supportsAttribute(DisplayerAttributeDef.EXTERNAL_COMPONENT_ID)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_WIDTH)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_HEIGHT)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
    }

    @Override
    protected void createVisualization() {
        ExternalComponentMessage init = initMessage();
        externalComponentPresenter.sendMessage(init);
        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        String currentComponentId = displayerSettings.getComponentId();
        if (currentComponentId != null && !currentComponentId.equals(componentId)) {
            componentId = currentComponentId;
            String partitionId = displayerSettings.getComponentPartition();
            if (partitionId != null) {
                externalComponentPresenter.withComponent(componentId, partitionId);
            } else {
                externalComponentPresenter.withComponent(componentId);
            }
        }

        ExternalComponentMessage message = dataSetMessage();
        externalComponentPresenter.sendMessage(message);

        view.setSize(displayerSettings.getChartWidth(), displayerSettings.getChartHeight());

        view.setMargin(displayerSettings.getChartMarginTop(),
                       displayerSettings.getChartMarginRight(),
                       displayerSettings.getChartMarginBottom(),
                       displayerSettings.getChartMarginLeft());
        updateFilterStatus();
    }

    private ExternalComponentMessage dataSetMessage() {
        Map<String, Object> componentProperties = new HashMap<>(displayerSettings.getComponentProperties());
        ExternalDataSet ds = ExternalDataSet.of(buildColumns(),
                                                buildData(dataSet));
        return messageHelper.newDataSetMessage(ds, componentProperties);
    }

    private ExternalComponentMessage initMessage() {
        Map<String, Object> componentProperties = new HashMap<>(displayerSettings.getComponentProperties());
        return messageHelper.newInitMessage(componentProperties);
    }

    private ExternalColumn[] buildColumns() {
        return dataSet.getColumns()
                      .stream()
                      .map(this::buildExternalColumn)
                      .toArray(ExternalColumn[]::new);
    }

    public ExternalComponentPresenter getExternalComponentPresenter() {
        return externalComponentPresenter;
    }

    public String[][] buildData(DataSet ds) {
        List<DataColumn> columns = ds.getColumns();
        int rows = columns.get(0).getValues().size();
        int cols = columns.size();
        String[][] result = new String[rows][];
        for (int i = 0; i < rows; i++) {
            String[] line = new String[cols];
            for (int j = 0; j < cols; j++) {
                line[j] = getEvaluatedValue(ds, i, j);
            }
            result[i] = line;
        }
        return result;
    }

    @Override
    protected void afterClose() {
        super.afterClose();
        externalComponentPresenter.destroy();
    }

    protected String columnValueToString(Object mightBeNull) {
        return mightBeNull == null ? "" : mightBeNull.toString();
    }

    protected ExternalColumn buildExternalColumn(DataColumn cl) {
        ColumnSettings clSettings = displayerSettings.getColumnSettings(cl);
        ExternalColumnSettings settings = ExternalColumnSettings.of(clSettings.getColumnId(),
                                                                    clSettings.getColumnName(),
                                                                    clSettings.getValueExpression(),
                                                                    clSettings.getEmptyTemplate(),
                                                                    clSettings.getValuePattern());
        return ExternalColumn.of(cl.getId(),
                                 cl.getColumnType().name(),
                                 settings);

    }

    protected void updateFilterStatus() {
        filterLabelSet.clear();
        Set<String> columnFilters = filterColumns();
        if (displayerSettings.isFilterEnabled() && !columnFilters.isEmpty()) {

            for (String columnId : columnFilters) {
                List<Interval> selectedValues = filterIntervals(columnId);
                DataColumn column = dataSet.getColumnById(columnId);
                for (Interval interval : selectedValues) {
                    String formattedValue = formatInterval(interval, column);
                    FilterLabel filterLabel = filterLabelSet.addLabel(formattedValue);
                    filterLabel.setOnRemoveCommand(() -> onFilterLabelRemoved(columnId, interval.getIndex()));
                }
            }
        }
    }

    protected void onFilterLabelRemoved(String columnId, int row) {
        super.filterUpdate(columnId, row);
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    protected void onFilterClearAll() {
        super.filterReset();
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    private void receiveFilterRequest(ExternalFilterRequest filterRequest) {
        if (displayerSettings.isFilterEnabled()) {
            if (filterRequest.isReset()) {
                super.filterReset();
            } else {
                DataColumn column = dataSet.getColumnByIndex(filterRequest.getColumn());
                super.filterUpdate(column.getId(), filterRequest.getRow());
            }
            updateFilterStatus();
        }
    }

    private String getEvaluatedValue(DataSet ds, int i, int j) {
        String value = columnValueToString(ds.getValueAt(i, j));
        try {
            String columnId = ds.getColumnByIndex(j).getId();
            ColumnSettings settings = displayerSettings.getColumnSettings(columnId);
            if (settings != null) {
                String expression = settings.getValueExpression();
                if (!StringUtils.isBlank(expression)) {
                    return getEvaluator().evalExpression(value, expression);
                }
            }
        } catch (Exception e) {
            DomGlobal.console.debug("Error evaluating value at " + i + "," + j);
            DomGlobal.console.debug(e);
        }
        return value;
    }

}