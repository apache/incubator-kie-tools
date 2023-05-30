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
package org.dashbuilder.displayer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.ValidationError;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalPattern;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.export.ExportCallback;
import org.dashbuilder.displayer.client.export.ExportFormat;
import org.dashbuilder.displayer.client.formatter.ValueFormatter;

/**
 * Base class for implementing custom displayers.
 * <p>Any derived class must implement:
 * <ul>
 *     <li>The draw(), redraw() & close() methods.</li>
 *     <li>The capture of events coming from the DisplayerListener interface.</li>
 * </ul>
 */
public abstract class AbstractDisplayer<V extends AbstractDisplayer.View> implements Displayer {

    public static final String[] COLOR_PATTERN = {
                                                  "#0088CE", "#CC0000", "#EC7A08", "#3F9C35", "#F0AB00", "#703FEC",
                                                  "#007A87", "#92D400", "#35CAED",
                                                  "#00659C", "#A30000", "#B35C00", "#B58100", "#6CA100", "#2D7623",
                                                  "#005C66", "#008BAD", "#40199A"};

    public interface View extends IsWidget {

        void errorMissingSettings();

        void errorMissingHandler();

        void showLoading();

        void showVisualization();

        void clear();

        void setId(String id);

        void errorDataSetNotFound(String uuid);

        void error(ClientRuntimeError error);

        void enableRefreshTimer(int seconds);

        void cancelRefreshTimer();

    }

    public interface Formatter {

        String formatDate(String pattern, Date d);

        Date parseDate(String pattern, String d);

        String formatNumber(String pattern, Number n);

        String formatDayOfWeek(DayOfWeek dayOfWeek);

        String formatMonth(Month month);
    }

    public interface ExpressionEval {

        String evalExpression(String value, String expression);
    }

    protected DataSet dataSet;
    protected DataSetHandler dataSetHandler;
    protected DisplayerSettings displayerSettings;
    protected DisplayerConstraints displayerConstraints;
    protected List<DisplayerListener> listenerList = new ArrayList<>();
    protected Map<String, List<Interval>> columnSelectionMap = new HashMap<>();
    protected Map<String, ValueFormatter> formatterMap = new HashMap<>();
    protected Formatter formatter = null;
    protected ExpressionEval evaluator = null;
    protected DataSetFilter currentFilter = null;
    protected boolean refreshEnabled = true;
    protected boolean drawn = false;

    @Override
    public Widget asWidget() {
        return getView().asWidget();
    }

    /**
     * It returns the actual implementation of the View
     * <p>- To be provided by the concrete displayer implementation -</p>
     */
    public abstract V getView();

    /**
     * It initializes the constraints this displayer conforms to
     * <p>- To be provided by the concrete displayer implementation -</p>
     */
    public abstract DisplayerConstraints createDisplayerConstraints();

    /**
     * The required logic in charge of rendering the visualization
     * once the data has been retrieved during a call to draw()
     * <p>- To be provided by the concrete displayer implementation -</p>
     */
    protected abstract void createVisualization();

    /**
     * The required logic in charge of updating a visualization
     * once the data has been retrieved during a call to redraw()
     * <p>- To be provided by the concrete displayer implementation -</p>
     */
    protected abstract void updateVisualization();

    public DisplayerConstraints getDisplayerConstraints() {
        if (displayerConstraints == null) {
            displayerConstraints = createDisplayerConstraints();
        }
        return displayerConstraints;
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public void setDisplayerSettings(DisplayerSettings displayerSettings) {
        checkDisplayerSettings(displayerSettings);
        this.displayerSettings = displayerSettings;
    }

    public void checkDisplayerSettings(DisplayerSettings displayerSettings) {
        DisplayerConstraints constraints = getDisplayerConstraints();
        if (displayerConstraints != null) {
            ValidationError error = constraints.check(displayerSettings);
            if (error != null) {
                throw error;
            }
        }
    }

    public DataSetHandler getDataSetHandler() {
        return dataSetHandler;
    }

    public void setDataSetHandler(DataSetHandler dataSetHandler) {
        this.dataSetHandler = dataSetHandler;
    }

    public Formatter getFormatter() {
        if (formatter == null) {
            formatter = new DisplayerGwtFormatter();
        }
        return formatter;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public ExpressionEval getEvaluator() {
        if (evaluator == null) {
            evaluator = new DisplayerGwtExprEval(this);
        }
        return evaluator;
    }

    public void setEvaluator(ExpressionEval evaluator) {
        this.evaluator = evaluator;
    }

    public void addListener(DisplayerListener... listeners) {
        for (DisplayerListener listener : listeners) {
            listenerList.add(listener);
        }
    }

    public String getDisplayerId() {
        String id = displayerSettings.getUUID();
        if (!StringUtils.isBlank(id)) {
            return id;
        }

        id = displayerSettings.getTitle();
        if (!StringUtils.isBlank(id)) {
            int hash = id.hashCode();
            return Integer.toString(hash < 0 ? hash * -1 : hash);
        }
        return null;
    }

    // DRAW & REDRAW

    @Override
    public boolean isDrawn() {
        return drawn;
    }

    /**
     * Draw the displayer by executing first the lookup call to retrieve the target data set
     */
    @Override
    public void draw() {
        if (displayerSettings == null) {
            getView().errorMissingSettings();
        } else if (dataSetHandler == null) {
            getView().errorMissingHandler();
        } else if (!isDrawn()) {
            try {
                drawn = true;
                getView().showLoading();

                beforeLoad();
                beforeDataSetLookup();
                dataSetHandler.lookupDataSet(new DataSetReadyCallback() {

                    public void callback(DataSet result) {
                        try {
                            dataSet = result;
                            afterLoad();
                            afterDataSetLookup(result);
                            createVisualization();
                            getView().showVisualization();

                            // Set the id of the container panel so that the displayer can be easily located
                            // by testing tools for instance.
                            String id = getDisplayerId();
                            if (!StringUtils.isBlank(id)) {
                                getView().setId(id);
                            }
                            // Draw done
                            afterDraw();
                        } catch (Exception e) {
                            // Give feedback on any initialization error
                            showError(new ClientRuntimeError(e));
                        }
                    }

                    public void notFound() {
                        getView().errorDataSetNotFound(displayerSettings.getDataSetLookup().getDataSetUUID());
                    }

                    @Override
                    public boolean onError(final ClientRuntimeError error) {
                        showError(error);
                        return false;
                    }
                });
            } catch (Exception e) {
                showError(new ClientRuntimeError(e));
            }
        }
    }

    /**
     * Just reload the data set and make the current displayer to redraw.
     */
    @Override
    public void redraw() {
        if (!isDrawn()) {
            draw();
        } else {
            try {
                beforeLoad();
                beforeDataSetLookup();
                dataSetHandler.lookupDataSet(new DataSetReadyCallback() {

                    public void callback(DataSet result) {
                        try {
                            dataSet = result;
                            afterDataSetLookup(result);
                            updateVisualization();

                            // Redraw done
                            afterRedraw();
                        } catch (Exception e) {
                            // Give feedback on any initialization error
                            showError(new ClientRuntimeError(e));
                        }
                    }

                    public void notFound() {
                        String uuid = displayerSettings.getDataSetLookup().getDataSetUUID();
                        getView().errorDataSetNotFound(uuid);
                        handleError("Data set not found: " + uuid);
                    }

                    @Override
                    public boolean onError(final ClientRuntimeError error) {
                        showError(error);
                        requestDraw();
                        return false;
                    }

                });
            } catch (Exception e) {
                showError(new ClientRuntimeError(e));
            }
        }
    }

    private void requestDraw() {
        drawn = false;
    }

    public void showError(ClientRuntimeError error) {
        getView().error(error);
        handleError(error);
    }

    /**
     * Close the displayer
     */
    @Override
    public void close() {
        getView().clear();

        // Close done
        afterClose();
    }

    /**
     * Call back method invoked just before the data set lookup is executed.
     */
    protected void beforeDataSetLookup() {}

    /**
     * Call back method invoked just after the data set lookup is executed.
     */
    protected void afterDataSetLookup(DataSet dataSet) {}

    // REFRESH TIMER

    @Override
    public void setRefreshOn(boolean enabled) {
        boolean changed = enabled != refreshEnabled;
        refreshEnabled = enabled;
        if (changed) {
            updateRefreshTimer();
        }
    }

    @Override
    public boolean isRefreshOn() {
        return refreshEnabled;
    }

    protected void updateRefreshTimer() {
        if (isDrawn()) {
            int seconds = displayerSettings.getRefreshInterval();
            if (refreshEnabled && seconds > 0) {
                getView().enableRefreshTimer(seconds);
            } else {
                getView().cancelRefreshTimer();
            }
        }
    }

    // LIFECYCLE CALLBACKS

    protected void beforeLoad() {
        for (DisplayerListener listener : listenerList) {
            listener.onDataLookup(this);
        }
    }

    protected void afterLoad() {
        for (DisplayerListener listener : listenerList) {
            listener.onDataLoaded(this);
        }
    }

    protected void afterDraw() {
        updateRefreshTimer();
        for (DisplayerListener listener : listenerList) {
            listener.onDraw(this);
        }
    }

    protected void afterRedraw() {
        updateRefreshTimer();
        for (DisplayerListener listener : listenerList) {
            listener.onRedraw(this);
        }
    }

    protected void afterClose() {
        setRefreshOn(false);

        for (DisplayerListener listener : listenerList) {
            listener.onClose(this);
        }
    }

    public void handleError(final String message) {
        handleError(new ClientRuntimeError(message, null));
    }

    public void handleError(final String message, final Throwable error) {
        handleError(new ClientRuntimeError(message, error));
    }

    public void handleError(final Throwable error) {
        handleError(new ClientRuntimeError(error));
    }

    public void handleError(final ClientRuntimeError error) {
        for (DisplayerListener listener : listenerList) {
            listener.onError(this, error);
        }
    }

    // CAPTURE EVENTS RECEIVED FROM OTHER DISPLAYERS

    @Override
    public void onDataLookup(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onDataLoaded(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onDraw(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onRedraw(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onClose(Displayer displayer) {
        // Do nothing
    }

    @Override
    public void onError(final Displayer displayer, ClientRuntimeError error) {
        // Do nothing
    }

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetGroup groupOp) {
        if (displayerSettings.isFilterListeningEnabled()) {
            if (dataSetHandler.filter(groupOp)) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetFilter filter) {
        if (displayerSettings.isFilterListeningEnabled()) {
            if (dataSetHandler.filter(filter)) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterUpdate(Displayer displayer, DataSetFilter oldFilter, DataSetFilter newFilter) {
        if (displayerSettings.isFilterListeningEnabled()) {
            boolean unfilter = dataSetHandler.unfilter(oldFilter);
            boolean filter = dataSetHandler.filter(newFilter);
            if (unfilter || filter) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps) {
        if (displayerSettings.isFilterListeningEnabled()) {
            boolean applied = false;
            for (DataSetGroup groupOp : groupOps) {
                if (dataSetHandler.unfilter(groupOp)) {
                    applied = true;
                }
            }
            if (applied) {
                redraw();
            }
        }
    }

    @Override
    public void onFilterReset(Displayer displayer, DataSetFilter filter) {
        if (displayerSettings.isFilterListeningEnabled()) {
            if (dataSetHandler.unfilter(filter)) {
                redraw();
            }
        }
    }

    // DATA COLUMN VALUES SELECTION, FILTER & NOTIFICATION

    /**
     * Get the set of columns being filtered.
     */
    public Set<String> filterColumns() {
        return columnSelectionMap.keySet();
    }

    /**
     * Get the current filter intervals for the given data set column.
     *
     * @param columnId The column identifier.
     * @return A list of intervals.
     */
    public List<Interval> filterIntervals(String columnId) {
        List<Interval> selected = columnSelectionMap.get(columnId);
        if (selected == null) {
            return new ArrayList<>();
        }
        return selected;
    }

    /**
     * Get the current filter interval matching the specified index
     *
     * @param columnId The column identifier.
     * @param idx The index of the interval
     * @return The target interval matching the specified parameters or null if it does not exist.
     */
    public Interval filterInterval(String columnId, int idx) {
        List<Interval> selected = columnSelectionMap.get(columnId);
        if (selected != null && !selected.isEmpty()) {
            for (Interval interval : selected) {
                if (interval.getIndex() == idx) {
                    return interval;
                }
            }
        }
        return null;
    }

    /**
     * Get the current filter selected interval indexes for the given data set column.
     *
     * @param columnId The column identifier.
     * @return A list of interval indexes
     */
    public List<Integer> filterIndexes(String columnId) {
        List<Integer> result = new ArrayList<>();
        List<Interval> selected = columnSelectionMap.get(columnId);
        if (selected == null) {
            return result;
        }
        for (Interval interval : selected) {
            result.add(interval.getIndex());
        }
        return result;
    }

    /**
     * Updates the current filter values for the given data set column.
     *
     * @param columnId The column to filter for.
     * @param row The row selected.
     */
    public void filterUpdate(String columnId, int row) {
        filterUpdate(columnId, row, null);
    }

    /**
     * Updates the current filter values for the given data set column.
     *
     * @param columnId The column to filter for.
     * @param row The row selected.
     * @param maxSelections The number of different selectable values available.
     */
    public void filterUpdate(String columnId, int row, Integer maxSelections) {
        if (displayerSettings.isFilterEnabled()) {

            List<Interval> selectedIntervals = columnSelectionMap.get(columnId);
            Interval intervalFiltered = filterInterval(columnId, row);

            // Existing interval reset
            if (intervalFiltered != null) {
                selectedIntervals.remove(intervalFiltered);
                if (!selectedIntervals.isEmpty()) {
                    filterApply(columnId, selectedIntervals);
                } else {
                    filterReset(columnId);
                }
            }
            // No current filter => Add the selected interval
            else if (selectedIntervals == null) {
                Interval intervalSelected = dataSetHandler.getInterval(columnId, row);
                if (intervalSelected != null) {
                    selectedIntervals = new ArrayList<>();
                    selectedIntervals.add(intervalSelected);
                    columnSelectionMap.put(columnId, selectedIntervals);
                    filterApply(columnId, selectedIntervals);
                }
            }
            // Extra interval added to an already filtered column
            else {
                Interval intervalSelected = dataSetHandler.getInterval(columnId, row);
                if (intervalSelected != null) {
                    if (displayerSettings.isFilterSelfApplyEnabled()) {
                        selectedIntervals = new ArrayList<>();
                        columnSelectionMap.put(columnId, selectedIntervals);
                    }
                    selectedIntervals.add(intervalSelected);
                    if (maxSelections != null && maxSelections > 0 && selectedIntervals.size() >= maxSelections) {
                        filterReset(columnId);
                    } else {
                        filterApply(columnId, selectedIntervals);
                    }
                }
            }
        }
    }

    /**
     * Filter the values of the given column.
     *
     * @param columnId The name of the column to filter.
     * @param intervalList A list of interval selections to filter for.
     */
    public void filterApply(String columnId, List<Interval> intervalList) {
        if (displayerSettings.isFilterEnabled()) {

            // For string column filters, init the group interval selection operation.
            DataSetGroup groupOp = dataSetHandler.getGroupOperation(columnId);
            groupOp.setSelectedIntervalList(intervalList);

            // Notify to those interested parties the selection event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterEnabled(this, groupOp);
                }
            }
            // Drill-down support
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.drillDown(groupOp);
                redraw();
            }
        }
    }

    /**
     * Apply the given filter
     *
     * @param filter A filter
     */
    public void filterApply(DataSetFilter filter) {
        if (displayerSettings.isFilterEnabled()) {

            this.currentFilter = filter;

            // Notify to those interested parties the selection event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterEnabled(this, filter);
                }
            }
            // Drill-down support
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.filter(filter);
                redraw();
            }
        }
    }

    /**
     * Updates the current filter values for the given data set column. Any previous filter is reset.
     *
     * @param filter A filter
     */
    public void filterUpdate(DataSetFilter filter) {
        if (displayerSettings.isFilterEnabled()) {

            DataSetFilter oldFilter = currentFilter;
            this.currentFilter = filter;

            // Notify to those interested parties the selection event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterUpdate(this, oldFilter, filter);
                }
            }
            // Drill-down support
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.unfilter(oldFilter);
                dataSetHandler.filter(filter);
                redraw();
            }
        }
    }

    /**
     * Clear any filter on the given column.
     *
     * @param columnId The name of the column to reset.
     */
    public void filterReset(String columnId) {
        if (displayerSettings.isFilterEnabled()) {

            columnSelectionMap.remove(columnId);
            DataSetGroup groupOp = dataSetHandler.getGroupOperation(columnId);

            // Notify to those interested parties the reset event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    listener.onFilterReset(this, Arrays.asList(groupOp));
                }
            }
            // Apply the selection to this displayer
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                dataSetHandler.drillUp(groupOp);
                redraw();
            }
        }
    }

    /**
     * Clear any filter.
     */
    public void filterReset() {
        if (displayerSettings.isFilterEnabled()) {

            List<DataSetGroup> groupOpList = new ArrayList<DataSetGroup>();
            for (String columnId : columnSelectionMap.keySet()) {
                DataSetGroup groupOp = dataSetHandler.getGroupOperation(columnId);
                groupOpList.add(groupOp);

            }
            columnSelectionMap.clear();

            // Notify to those interested parties the reset event.
            if (displayerSettings.isFilterNotificationEnabled()) {
                for (DisplayerListener listener : listenerList) {
                    if (currentFilter != null) {
                        listener.onFilterReset(this, currentFilter);
                    }
                    listener.onFilterReset(this, groupOpList);
                }
            }
            // Apply the selection to this displayer
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                boolean applied = false;

                if (currentFilter != null) {
                    if (dataSetHandler.unfilter(currentFilter)) {
                        applied = true;
                    }
                }
                for (DataSetGroup groupOp : groupOpList) {
                    if (dataSetHandler.drillUp(groupOp)) {
                        applied = true;
                    }
                }
                if (applied) {
                    redraw();
                }
            }
            if (currentFilter != null) {
                currentFilter = null;
            }
        }
    }

    // DATA COLUMN SORT

    /**
     * Set the sort order operation to apply to the data set.
     *
     * @param columnId The name of the column to sort.
     * @param sortOrder The sort order.
     */
    public void sortApply(String columnId, SortOrder sortOrder) {
        dataSetHandler.sort(columnId, sortOrder);
    }

    // DATA FORMATTING

    public String formatInterval(Interval interval, DataColumn column) {

        // Raw values
        if (column == null || column.getColumnGroup() == null) {
            return interval.getName();
        }
        // Date interval
        String type = interval.getType();
        if (StringUtils.isBlank(type))
            type = column.getIntervalType();
        if (StringUtils.isBlank(type))
            type = column.getColumnGroup().getIntervalSize();
        DateIntervalType intervalType = DateIntervalType.getByName(type);
        if (intervalType != null) {
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(column.getId());
            String pattern = columnSettings != null ? columnSettings.getValuePattern() : ColumnSettings.getDatePattern(
                    intervalType);
            String expression = columnSettings != null ? columnSettings.getValueExpression() : null;

            if (pattern == null) {
                pattern = ColumnSettings.getDatePattern(intervalType);
            }
            if (expression == null && column.getColumnGroup().getStrategy().equals(GroupStrategy.FIXED)) {
                expression = ColumnSettings.getFixedExpression(intervalType);
            }

            return formatDate(intervalType,
                    column.getColumnGroup().getStrategy(),
                    interval.getName(), pattern, expression);
        }
        // Label interval
        ColumnSettings columnSettings = displayerSettings.getColumnSettings(column);
        String expression = columnSettings.getValueExpression();
        if (StringUtils.isBlank(expression))
            return interval.getName();
        return getEvaluator().evalExpression(interval.getName(), expression);
    }

    public void addFormatter(String columnId, ValueFormatter formatter) {
        formatterMap.put(columnId, formatter);
    }

    public ValueFormatter getFormatter(String columnId) {
        return formatterMap.get(columnId);
    }

    public String formatValue(int row, int column) {
        Object value = row < dataSet.getRowCount() ? dataSet.getValueAt(row, column) : null;
        DataColumn columnObj = dataSet.getColumnByIndex(column);
        ValueFormatter formatter = getFormatter(columnObj.getId());
        if (formatter != null) {
            return formatter.formatValue(dataSet, row, column);
        }
        return formatValue(value, columnObj);
    }

    public String formatValue(Object value, DataColumn column) {

        ValueFormatter formatter = getFormatter(column.getId());
        if (formatter != null) {
            return formatter.formatValue(value);
        }

        ColumnSettings columnSettings = displayerSettings.getColumnSettings(column);
        String pattern = columnSettings.getValuePattern();
        String empty = columnSettings.getEmptyTemplate();
        String expression = columnSettings.getValueExpression();

        if (value == null) {
            return empty;
        }

        // Date grouped columns
        DateIntervalType intervalType = DateIntervalType.getByName(column.getIntervalType());
        if (intervalType != null) {
            ColumnGroup columnGroup = column.getColumnGroup();
            return formatDate(intervalType,
                    columnGroup.getStrategy(),
                    value.toString(), pattern, expression);
        }
        // Label grouped columns, aggregations & raw values
        else {
            ColumnType columnType = column.getColumnType();
            if (ColumnType.DATE.equals(columnType)) {
                Date d = (Date) value;
                return getFormatter().formatDate(pattern, d);
            } else if (ColumnType.NUMBER.equals(columnType)) {
                OptionalDouble od = OptionalDouble.empty();
                if (value instanceof Number) {
                    od = OptionalDouble.of(((Number) value).doubleValue());
                }
                if (!StringUtils.isBlank(expression)) {
                    String r = getEvaluator().evalExpression(value.toString(), expression);
                    try {
                        od = OptionalDouble.of(Double.parseDouble(r));
                    } catch (NumberFormatException e) {
                        return r;
                    }
                }
                return getFormatter().formatNumber(pattern, od.getAsDouble());
            } else {
                if (StringUtils.isBlank(expression)) {
                    return value.toString();
                }
                return getEvaluator().evalExpression(value.toString(), expression);
            }
        }
    }

    // DATE FORMATTING

    protected String formatDate(DateIntervalType type,
                                GroupStrategy strategy,
                                String date,
                                String pattern,
                                String expression) {
        if (date == null) {
            return null;
        }
        String str = GroupStrategy.FIXED.equals(strategy) ? formatDateFixed(type, date) : formatDateDynamic(type, date,
                pattern);
        if (StringUtils.isBlank(expression)) {
            return str;
        }
        return getEvaluator().evalExpression(str, expression);
    }

    protected String formatDateFixed(DateIntervalType type, String date) {
        if (date == null) {
            return null;
        }
        int index = Integer.parseInt(date);
        if (DateIntervalType.DAY_OF_WEEK.equals(type)) {
            DayOfWeek dayOfWeek = DayOfWeek.getByIndex(index);
            return getFormatter().formatDayOfWeek(dayOfWeek);
        }
        if (DateIntervalType.MONTH.equals(type)) {
            Month month = Month.getByIndex(index);
            return getFormatter().formatMonth(month);
        }
        return date;
    }

    protected String formatDateDynamic(DateIntervalType type, String date, String pattern) {
        if (date == null) {
            return null;
        }
        Date d = parseDynamicGroupDate(type, date);
        return getFormatter().formatDate(pattern, d);
    }

    protected Date parseDynamicGroupDate(DateIntervalType type, String date) {
        String pattern = DateIntervalPattern.getPattern(type);
        return getFormatter().parseDate(pattern, date);
    }

    // EXPORT

    @Override
    public void export(ExportFormat format, int maxRows, ExportCallback callback) {
        if (dataSetHandler == null) {
            callback.noData();
        } else {
            Map<String, String> columnNameMap = new HashMap<>();
            displayerSettings.getColumnSettingsList().forEach(cs -> columnNameMap.put(cs.getColumnId(), cs
                    .getColumnName()));
            dataSetHandler.exportCurrentDataSetLookup(format, maxRows, callback, columnNameMap);
        }
    }

    protected String evaluateValueToString(Object mightBeNull, ColumnSettings settings) {
        var value = columnValueToString(mightBeNull);
        var expression = settings.getValueExpression();
        if (expression != null && !expression.trim().isEmpty()) {
            return getEvaluator().evalExpression(value, expression);
        }
        return value;
    }

    protected String columnValueToString(Object mightBeNull) {
        return mightBeNull == null ? "" : mightBeNull.toString();
    }

    protected OptionalDouble max(DataColumn column) {
        var values = getNumberValues(column);
        return Arrays.stream(values).reduce((v1, v2) -> v1 >= v2 ? v1 : v2);
    }

    protected OptionalDouble min(DataColumn column) {
        var values = getNumberValues(column);
        return Arrays.stream(values).reduce((v1, v2) -> v1 <= v2 ? v1 : v2);
    }

    protected double map(double value, double start1, double stop1, double start2, double stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    protected double[] getNumberValues(DataColumn column) {
        var values = new double[0];
        if (column.getColumnType() == ColumnType.NUMBER) {
            values = column.getValues().stream().mapToDouble(v -> Double
                    .valueOf(v.toString())).toArray();
        }
        return values;
    }
}
