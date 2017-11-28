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

import java.util.List;

import org.dashbuilder.common.client.widgets.FilterLabelSet;

public abstract class GoogleCategoriesDisplayer<V extends GoogleCategoriesDisplayer.View> extends GoogleChartDisplayer<V> {

    public interface View<P extends GoogleCategoriesDisplayer> extends GoogleChartDisplayer.View<P> {

        void setFilterEnabled(boolean filterEnabled);

        void setBgColor(String bgColor);

        void setShowXLabels(boolean showXLabels);

        void setShowYLabels(boolean showYLabels);

        void setXAxisTitle(String xAxisTitle);

        void setXAxisAngle(int xAxisAngle);

        void setYAxisTitle(String yAxisTitle);

        void setColors(String[] colors);

        void setAnimationOn(boolean animationOn);

        void setAnimationDuration(int animationDuration);

        void createChart();

        void nodata();

        void drawChart();
    }

    public static final String[] COLOR_ARRAY = new String[] {
            "#0088CE", "#CC0000", "#EC7A08", "#3F9C35", "#F0AB00", "#703FEC", "#007A87", "#92D400", "#35CAED",
            "#00659C", "#A30000", "#B35C00", "#B58100", "#6CA100", "#2D7623", "#005C66", "#008BAD", "#40199A"};

    public static final String COLOR_NOT_SELECTED = "#8B8D8F";

    public GoogleCategoriesDisplayer(FilterLabelSet filterLabelSet) {
        super(filterLabelSet);
    }

    protected String[] createColorArray() {
        String[] colorArray = new String[dataSet.getRowCount()];
        for (int i = 0, j = 0; i < dataSet.getRowCount(); i++, j++) {
            if (j >= COLOR_ARRAY.length) j = 0;
            colorArray[i] = COLOR_ARRAY[j];

            List<Integer> selectedIdxs = filterIndexes(dataSet.getColumnByIndex(0).getId());
            if (!displayerSettings.isFilterSelfApplyEnabled()
                    && selectedIdxs != null
                    && !selectedIdxs.isEmpty() && !selectedIdxs.contains(i)) {

                colorArray[i] = COLOR_NOT_SELECTED;
            }
        }
        return colorArray;
    }

    @Override
    protected void createVisualization() {
        super.createVisualization();

        getView().createChart();
        getView().setAnimationOn(true);
        getView().setAnimationDuration(700);
        getView().setFilterEnabled(displayerSettings.isFilterEnabled());
        getView().setBgColor(displayerSettings.getChartBackgroundColor());
        getView().setShowXLabels(displayerSettings.isXAxisShowLabels());
        getView().setShowYLabels(displayerSettings.isYAxisShowLabels());
        getView().setXAxisTitle(displayerSettings.getXAxisTitle());
        getView().setXAxisAngle(displayerSettings.getXAxisLabelsAngle());
        getView().setYAxisTitle(displayerSettings.getYAxisTitle());
        drawChart();
    }

    @Override
    protected void updateVisualization() {
        super.updateFilterStatus();
        drawChart();
    }

    protected void drawChart() {
        if (dataSet.getRowCount() == 0) {
            getView().nodata();
        } else {
            super.pushDataToView();
            getView().setColors(createColorArray());
            getView().drawChart();
        }
    }

    // View notifications

    public void onCategorySelected(String columnId, int row) {
        Integer maxSelections = displayerSettings.isFilterSelfApplyEnabled() ? null : dataSet.getRowCount();
        filterUpdate(columnId, row, maxSelections);

        // Update the displayer in order to reflect the current selection
        // (only if not has already been redrawn as part of the drill-down processing in the above filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }
}
