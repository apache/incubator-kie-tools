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
package org.dashbuilder.displayer.impl;

import java.util.List;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.impl.AbstractDataSetLookupBuilder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsBuilder;
import org.dashbuilder.displayer.DisplayerSubType;

/**
 * Base class for DisplayerSettingsBuilder implementations.
 */
public abstract class AbstractDisplayerSettingsBuilder<T> extends AbstractDataSetLookupBuilder<T> implements DisplayerSettingsBuilder<T> {

    protected DisplayerSettings displayerSettings = createDisplayerSettings();

    protected abstract DisplayerSettings createDisplayerSettings();

    public T uuid(String uuid) {
        displayerSettings.setUUID(uuid);
        return (T) this;
    }

    public T dataset(DataSet dataSet) {
        displayerSettings.setDataSet(dataSet);
        return (T) this;
    }

    public T title(String title) {
        displayerSettings.setTitle(title);
        return (T) this;
    }

    public T titleVisible(boolean visible) {
        displayerSettings.setTitleVisible(visible);
        return (T) this;
    }

    public T allowCsvExport(boolean allowCsvExport) {
        displayerSettings.setCSVExportAllowed(allowCsvExport);
        return (T) this;
    }

    public T allowExcelExport(boolean allowExcelExport) {
        displayerSettings.setExcelExportAllowed(allowExcelExport);
        return (T) this;
    }

    public T backgroundColor(String backgroundColor) {
        displayerSettings.setChartBackgroundColor(backgroundColor);
        return (T) this;
    }

    public T xAxisTitle(String title) {
        displayerSettings.setXAxisShowLabels(true);
        displayerSettings.setXAxisTitle(title);
        return (T) this;
    }

    public T xAxisAngle(int angle) {
        displayerSettings.setXAxisLabelsAngle(angle);
        return (T) this;
    }

    public T yAxisTitle(String title) {
        displayerSettings.setYAxisShowLabels(true);
        displayerSettings.setYAxisTitle(title);
        return (T) this;
    }

    public T renderer(String renderer) {
        displayerSettings.setRenderer(renderer);
        return (T) this;
    }

    @Override
    public T subtype(DisplayerSubType displayerSubType) {
        displayerSettings.setSubtype(displayerSubType);
        return (T) this;
    }

    public T filterOn(boolean applySelf, boolean notifyOthers, boolean receiveFromOthers) {
        displayerSettings.setFilterEnabled(true);
        displayerSettings.setFilterSelfApplyEnabled(applySelf);
        displayerSettings.setFilterNotificationEnabled(notifyOthers);
        displayerSettings.setFilterListeningEnabled(receiveFromOthers);
        return (T) this;
    }

    public T filterOff(boolean receiveFromOthers) {
        displayerSettings.setFilterEnabled(false);
        displayerSettings.setFilterListeningEnabled(receiveFromOthers);
        return (T) this;
    }

    public T refreshOn() {
        displayerSettings.setRefreshInterval(-1);
        displayerSettings.setRefreshStaleData(true);
        return (T) this;
    }

    public T refreshOn(int seconds, boolean staleData) {
        displayerSettings.setRefreshInterval(seconds);
        displayerSettings.setRefreshStaleData(staleData);
        return (T) this;
    }

    public T refreshOff() {
        displayerSettings.setRefreshInterval(-1);
        return (T) this;
    }

    public T format(String name) {
        return format(name, null);
    }

    public T format(String name, String pattern) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetGroup)) {
            throw new RuntimeException("column(...) must be called first.");
        }
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        List<GroupFunction> columns = gOp.getGroupFunctions();
        if (columns.isEmpty()) {
            throw new RuntimeException("column(...) must be called first.");
        }
        GroupFunction lastColumn = columns.get(columns.size() - 1);
        return format(lastColumn.getColumnId(), name, pattern);
    }

    public T format(String columnId, String name, String pattern) {
        displayerSettings.setColumnName(columnId, name);
        displayerSettings.setColumnValuePattern(columnId, pattern);
        return (T) this;
    }

    public T expression(String expression) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetGroup)) {
            throw new RuntimeException("column(...) must be called first.");
        }
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        List<GroupFunction> columns = gOp.getGroupFunctions();
        if (columns.isEmpty()) {
            throw new RuntimeException("column(...) must be called first.");
        }
        GroupFunction lastColumn = columns.get(columns.size() - 1);
        return expression(lastColumn.getColumnId(), expression);
    }

    public T expression(String columnId, String expression) {
        displayerSettings.setColumnValueExpression(columnId, expression);
        return (T) this;
    }

    public T htmlTemplate(String html) {
        displayerSettings.setHtmlTemplate(html);
        return (T) this;
    }

    public T jsTemplate(String onDrawJs) {
        displayerSettings.setJsTemplate(onDrawJs);
        return (T) this;
    }

    public DisplayerSettings buildSettings() {
        displayerSettings.setDataSetLookup(super.buildLookup());
        return displayerSettings;
    }
}
