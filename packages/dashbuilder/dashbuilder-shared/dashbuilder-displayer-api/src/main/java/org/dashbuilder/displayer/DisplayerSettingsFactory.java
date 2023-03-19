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
package org.dashbuilder.displayer;

import org.dashbuilder.displayer.impl.AreaChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.BarChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.BubbleChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.ExternalDisplayerSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.LineChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.MapChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.MeterChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.MetricSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.PieChartSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.SelectorDisplayerSettingsBuilderImpl;
import org.dashbuilder.displayer.impl.TableDisplayerSettingsBuilderImpl;

/**
 * Factory class for creating  DisplayerSettingsBuilder instances.
 */
public final class DisplayerSettingsFactory {

    public static BarChartSettingsBuilder<BarChartSettingsBuilderImpl> newBarChartSettings() {
        return new BarChartSettingsBuilderImpl();
    }

    public static PieChartSettingsBuilder<PieChartSettingsBuilderImpl> newPieChartSettings() {
        return new PieChartSettingsBuilderImpl();
    }

    public static AreaChartSettingsBuilder<AreaChartSettingsBuilderImpl> newAreaChartSettings() {
        return new AreaChartSettingsBuilderImpl();
    }

    public static LineChartSettingsBuilder<LineChartSettingsBuilderImpl> newLineChartSettings() {
        return new LineChartSettingsBuilderImpl();
    }

    public static BubbleChartSettingsBuilder<BubbleChartSettingsBuilderImpl> newBubbleChartSettings() {
        return new BubbleChartSettingsBuilderImpl();
    }

    public static MapChartSettingsBuilder<MapChartSettingsBuilderImpl> newMapChartSettings() {
        return new MapChartSettingsBuilderImpl();
    }

    public static TableDisplayerSettingsBuilder<TableDisplayerSettingsBuilderImpl> newTableSettings() {
        return new TableDisplayerSettingsBuilderImpl();
    }

    public static MeterChartSettingsBuilder<MeterChartSettingsBuilderImpl> newMeterChartSettings() {
        return new MeterChartSettingsBuilderImpl();
    }

    public static SelectorDisplayerSettingsBuilder<SelectorDisplayerSettingsBuilderImpl> newSelectorSettings() {
        return new SelectorDisplayerSettingsBuilderImpl();
    }

    public static MetricSettingsBuilder<MetricSettingsBuilderImpl> newMetricSettings() {
        return new MetricSettingsBuilderImpl();
    }
    
    public static ExternalDisplayerSettingsBuilder<ExternalDisplayerSettingsBuilderImpl> newExternalDisplayerSettings() {
        return new ExternalDisplayerSettingsBuilderImpl();
    }
}
