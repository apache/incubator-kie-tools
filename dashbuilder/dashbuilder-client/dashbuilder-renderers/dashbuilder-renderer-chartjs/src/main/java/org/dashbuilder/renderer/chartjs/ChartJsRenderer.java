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
package org.dashbuilder.renderer.chartjs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractRendererLibrary;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.renderer.chartjs.lib.ChartJs;

import static org.dashbuilder.displayer.DisplayerSubType.*;
import static org.dashbuilder.displayer.DisplayerType.*;

/**
 * Chart JS renderer
 */
@ApplicationScoped
public class ChartJsRenderer extends AbstractRendererLibrary {

    public static final String UUID = "chartjs";

    @PostConstruct
    private void init() {
        publishChartJsFunctions();
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public String getName() {
        return "Chart JS";
    }

    @Override
    public List<DisplayerType> getSupportedTypes() {
        return Arrays.asList(BARCHART);
    }

    @Override
    public List<DisplayerSubType> getSupportedSubtypes(DisplayerType displayerType) {
        switch (displayerType) {
            case BARCHART:
                return Arrays.asList(COLUMN);
            case PIECHART:
                return Arrays.asList(PIE, DONUT);
            case AREACHART:
                return Arrays.asList(AREA);
            case LINECHART:
                return Arrays.asList(LINE);
            default:
                return Arrays.asList();
        }
    }

    @Override
    public Displayer lookupDisplayer(DisplayerSettings displayerSettings) {
        ChartJsDisplayer displayer = _lookupDisplayer(displayerSettings);
        if (displayer != null) {
            _displayerMap.put(displayerSettings.getUUID(), displayer);
        }
        return displayer;
    }

    protected ChartJsDisplayer _lookupDisplayer(DisplayerSettings displayerSettings) {
        ChartJs.ensureInjected();
        DisplayerType type = displayerSettings.getType();
        if ( DisplayerType.BARCHART.equals(type)) {
            return new ChartJsBarChartDisplayer();
        }
        return null;
    }

    private native void publishChartJsFunctions() /*-{
        $wnd.chartJsFormatValue = $entry(@org.dashbuilder.renderer.chartjs.ChartJsRenderer::formatValue(Ljava/lang/String;DI));
    }-*/;

    protected static Map<String,ChartJsDisplayer> _displayerMap = new HashMap<String, ChartJsDisplayer>();

    public static String formatValue(String displayerId, double value, int column) {
        ChartJsDisplayer displayer = _displayerMap.get(displayerId);
        if (displayer == null) return Double.toString(value);

        DataColumn dataColumn = displayer.getDataSetHandler().getLastDataSet().getColumnByIndex(column);
        return displayer.formatValue(value, dataColumn);
    }

    public static void closeDisplayer(ChartJsDisplayer displayer) {
        _displayerMap.remove(displayer.getDisplayerId());
    }
}
