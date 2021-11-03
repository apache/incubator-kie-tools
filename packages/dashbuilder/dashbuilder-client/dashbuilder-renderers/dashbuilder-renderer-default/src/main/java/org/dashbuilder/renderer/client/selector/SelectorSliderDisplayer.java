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

import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.impl.DataColumnImpl;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;
import org.dashbuilder.displayer.client.widgets.filter.DateParameterEditor;
import org.dashbuilder.displayer.client.widgets.filter.NumberParameterEditor;

@Dependent
public class SelectorSliderDisplayer extends AbstractGwtDisplayer<SelectorSliderDisplayer.View> {

    public interface View extends AbstractGwtDisplayer.View<SelectorSliderDisplayer> {

        void showTitle(String title);

        void setWidth(int width);

        String getColumnsTitle();

        void setSliderEnabled(boolean enabled);

        void showSlider(double min, double max, double step, double minSelected, double maxSelected);

        void showInputs(IsWidget minValueEditor, IsWidget maxValueEditor);

        void margins(int top, int bottom, int left, int right);

        String formatRange(String from, String to);

        void textColumnsNotSupported();

        void noData();
    }

    protected View view;
    protected DataColumn dataColumnMin;
    protected DataColumn dataColumnMax;
    protected double rangeMin = -1;
    protected double rangeMax = -1;
    protected double selectedMin = -1;
    protected double selectedMax = -1;
    protected DateParameterEditor minDateEditor;
    protected DateParameterEditor maxDateEditor;
    protected NumberParameterEditor minNumberEditor;
    protected NumberParameterEditor maxNumberEditor;

    @Inject
    public SelectorSliderDisplayer(View view, DateParameterEditor minDateEditor,
                                   DateParameterEditor maxDateEditor,
                                   NumberParameterEditor minNumberEditor,
                                   NumberParameterEditor maxNumberEditor) {
        this.view = view;
        this.view.init(this);
        this.minDateEditor = minDateEditor;
        this.maxDateEditor = maxDateEditor;
        this.minNumberEditor = minNumberEditor;
        this.maxNumberEditor = maxNumberEditor;;
        this.minDateEditor.setOnChangeCommand(this::onMinDateInputChange);
        this.minDateEditor.setOnFocusCommand(this::onMinDateInputFocus);
        this.minDateEditor.setOnBlurCommand(this::onMinDateInputBlur);
        this.maxDateEditor.setOnChangeCommand(this::onMaxDateInputChange);
        this.maxDateEditor.setOnFocusCommand(this::onMaxDateInputFocus);
        this.maxDateEditor.setOnBlurCommand(this::onMaxDateInputBlur);
        this.minNumberEditor.setOnChangeCommand(this::onMinNumberInputChange);
        this.maxNumberEditor.setOnChangeCommand(this::onMaxNumberInputChange);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        // A single a number or date column is required as the filter column used by the slider
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupAllowed(false)
                .setMaxColumns(2)
                .setMinColumns(1)
                .setFunctionRequired(false)
                .setExtraColumnsAllowed(false)
                .setColumnsTitle(view.getColumnsTitle());

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.SUBTYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeDef.TITLE)
                .supportsAttribute(DisplayerAttributeDef.TITLE_VISIBLE)
                .supportsAttribute(DisplayerAttributeGroupDef.SELECTOR_GROUP)
                .excludeAttribute(DisplayerAttributeGroupDef.SELECTOR_MULTIPLE)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP);
    }

    @Override
    protected void beforeDataSetLookup() {
        DataSetGroup group = dataSetHandler.getCurrentDataSetLookup().getLastGroupOp();
        if (group != null && (group.getAggregationFunctions().isEmpty() || group.getColumnGroup() != null || group.getGroupFunctions().size() < 2)) {
            group.setColumnGroup(null);
            GroupFunction minFunction = group.getGroupFunctions().get(0);
            minFunction.setFunction(AggregateFunctionType.MIN);
            GroupFunction maxFunction = minFunction.cloneInstance();
            maxFunction.setFunction(AggregateFunctionType.MAX);
            group.addGroupFunction(maxFunction);
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

        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        DataColumn minColumn = dataSet.getColumnByIndex(0);
        DataColumn maxColumn = dataSet.getColumnByIndex(1);
        Object minValue = minColumn.getValues().isEmpty() ? null : minColumn.getValues().get(0);
        Object maxValue = maxColumn.getValues().isEmpty() ? null : maxColumn.getValues().get(0);
        String minColumnId = minColumn.getId();
        String maxColumnId = maxColumn.getId();
        ColumnType columnType = minColumn.getColumnType();
        dataColumnMin = new DataColumnImpl(minColumnId, columnType);
        dataColumnMax = new DataColumnImpl(maxColumnId, columnType);

        int inputsWidth = displayerSettings.getSelectorWidth();
        inputsWidth = inputsWidth > 0 ? (inputsWidth/2) - 10  : -1;
        inputsWidth = inputsWidth > 100 ? 100 : inputsWidth;

        if (minValue == null || maxValue == null) {
            view.noData();
        }
        else if (ColumnType.DATE.equals(columnType)) {
            rangeMin = ((Date) minValue).getTime();
            rangeMax = ((Date) maxValue).getTime() + 1;
            selectedMin = selectedMin == -1 ? rangeMin : selectedMin;
            selectedMax = selectedMax == -1 ? rangeMax : selectedMax;
            view.showSlider(rangeMin, rangeMax, 1, selectedMin, selectedMax);

            if (displayerSettings.isSelectorInputsEnabled()) {
                minDateEditor.setValue((Date) minValue);
                maxDateEditor.setValue((Date) maxValue);
                minDateEditor.setWidth(inputsWidth);
                maxDateEditor.setWidth(inputsWidth);
                view.showInputs(minDateEditor, maxDateEditor);
            }
        }
        else if (ColumnType.NUMBER.equals(columnType)) {
            // Round to integer
            rangeMin = ((Number) minValue).intValue();
            rangeMax = ((Number) maxValue).intValue() + 1;
            selectedMin = selectedMin == -1 ? rangeMin : selectedMin;
            selectedMax = selectedMax == -1 ? rangeMax : selectedMax;
            view.showSlider(rangeMin, rangeMax, 1, selectedMin, selectedMax);

            if (displayerSettings.isSelectorInputsEnabled()) {
                minNumberEditor.setValue((Number) minValue);
                maxNumberEditor.setValue((Number) maxValue);
                minNumberEditor.setWidth(inputsWidth);
                maxNumberEditor.setWidth(inputsWidth);
                view.showInputs(minNumberEditor, maxNumberEditor);
            }
        }
        else {
            view.textColumnsNotSupported();
        }
    }

    public Object getSelectedMin() {
        if (selectedMin == -1) {
            return new Date((long) rangeMin);
        }
        if (ColumnType.DATE.equals(dataColumnMin.getColumnType())) {
            return new Date((long) selectedMin);
        }
        else if (ColumnType.NUMBER.equals(dataColumnMin.getColumnType())) {
            return (long) selectedMin;
        }
        else {
            return null;
        }
    }

    public Object getSelectedMax() {
        if (selectedMax == -1) {
            return new Date((long) rangeMax);
        }
        if (ColumnType.DATE.equals(dataColumnMax.getColumnType())) {
            return new Date((long) selectedMax);
        }
        else if (ColumnType.NUMBER.equals(dataColumnMax.getColumnType())) {
            return (long) selectedMax;
        }
        else {
            return null;
        }
    }

    // View callbacks

    void onSliderChange(double min, double max) {
        if (selectedMin != min || selectedMax != max) {
            selectedMin = min;
            selectedMax = max;

            DataSetFilter filter = new DataSetFilter();
            CoreFunctionFilter columnFilter = new CoreFunctionFilter();
            columnFilter.setColumnId(dataColumnMin.getId());
            columnFilter.setType(CoreFunctionType.BETWEEN);
            filter.addFilterColumn(columnFilter);

            if (ColumnType.DATE.equals(dataColumnMin.getColumnType())) {
                Date dateFrom = new Date((long) min);
                Date dateTo = new Date((long) max);
                columnFilter.setParameters(dateFrom, dateTo);
                minDateEditor.setValue(dateFrom);
                maxDateEditor.setValue(dateTo);
                super.filterUpdate(filter);
            }
            else if (ColumnType.NUMBER.equals(dataColumnMin.getColumnType())) {
                columnFilter.setParameters(min, max);
                minNumberEditor.setValue(min);
                maxNumberEditor.setValue(max);
                super.filterUpdate(filter);
            }
        }
    }

    void onMinDateInputChange() {
        long min = minDateEditor.getValue().getTime();
        double max = selectedMax != -1 ? selectedMax : rangeMax;
        this.onSliderChange(min, max);
        view.showSlider(rangeMin, rangeMax, 1, min, max);
        view.setSliderEnabled(true);
    }

    void onMinDateInputFocus() {
        view.setSliderEnabled(false);
    }

    void onMinDateInputBlur() {
        view.setSliderEnabled(true);
    }

    void onMaxDateInputChange() {
        double min = selectedMin != -1 ? selectedMin : rangeMin;
        long max = maxDateEditor.getValue().getTime();
        this.onSliderChange(min, max);
        view.showSlider(rangeMin, rangeMax, 1, min, max);
        view.setSliderEnabled(true);
    }

    void onMaxDateInputFocus() {
        view.setSliderEnabled(false);
    }

    void onMaxDateInputBlur() {
        view.setSliderEnabled(true);
    }

    void onMinNumberInputChange() {
        double min = minNumberEditor.getValue().doubleValue();
        double max = selectedMax != -1 ? selectedMax : rangeMax;
        this.onSliderChange(min, max);
        view.showSlider(rangeMin, rangeMax, 1, min, max);
    }

    void onMaxNumberInputChange() {
        double min = selectedMin != -1 ? selectedMin : rangeMin;
        double max = maxNumberEditor.getValue().doubleValue();
        this.onSliderChange(min, max);
        view.showSlider(rangeMin, rangeMax, 1, min, max);
    }

    String formatRange(double min, double max) {
        String fromStr = this.formatValue(min, dataColumnMin);
        String toStr = this.formatValue(max, dataColumnMax);
        return view.formatRange(fromStr, toStr);
    }

    String formatValue(double val, DataColumn dataColumn) {
        if (ColumnType.DATE.equals(dataColumn.getColumnType())) {
            Date date = new Date((long) val);
            return super.formatValue(date, dataColumn);
        }
        else if (ColumnType.NUMBER.equals(dataColumn.getColumnType())) {
            return super.formatValue(val, dataColumn);
        }
        else {
            return Double.toString(val);
        }
    }
}
