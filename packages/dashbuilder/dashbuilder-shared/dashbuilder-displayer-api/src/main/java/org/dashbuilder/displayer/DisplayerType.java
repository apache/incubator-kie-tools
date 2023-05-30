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

import java.util.ArrayList;
import java.util.List;

import static org.dashbuilder.displayer.DisplayerSubType.AREA;
import static org.dashbuilder.displayer.DisplayerSubType.AREA_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.BAR;
import static org.dashbuilder.displayer.DisplayerSubType.BAR_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.COLUMN;
import static org.dashbuilder.displayer.DisplayerSubType.COLUMN_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.DONUT;
import static org.dashbuilder.displayer.DisplayerSubType.LINE;
import static org.dashbuilder.displayer.DisplayerSubType.MAP_MARKERS;
import static org.dashbuilder.displayer.DisplayerSubType.MAP_REGIONS;
import static org.dashbuilder.displayer.DisplayerSubType.METRIC_CARD;
import static org.dashbuilder.displayer.DisplayerSubType.METRIC_CARD2;
import static org.dashbuilder.displayer.DisplayerSubType.METRIC_PLAIN_TEXT;
import static org.dashbuilder.displayer.DisplayerSubType.METRIC_QUOTA;
import static org.dashbuilder.displayer.DisplayerSubType.PIE;
import static org.dashbuilder.displayer.DisplayerSubType.PIE_3D;
import static org.dashbuilder.displayer.DisplayerSubType.SELECTOR_DROPDOWN;
import static org.dashbuilder.displayer.DisplayerSubType.SELECTOR_LABELS;
import static org.dashbuilder.displayer.DisplayerSubType.SELECTOR_SLIDER;
import static org.dashbuilder.displayer.DisplayerSubType.SMOOTH;

/**
 * An enumeration for the different types of displayers.
 */
public enum DisplayerType {

    /**
     * Bar Chart
     */
    BARCHART(BAR, BAR_STACKED, COLUMN, COLUMN_STACKED),

    /**
     * Pie Chart
     */
    PIECHART(PIE, PIE_3D, DONUT),

    /**
     * Area Chart
     */
    AREACHART(AREA, AREA_STACKED/*, STEPPED*/),

    /**
     * Line Chart
     */
    LINECHART(LINE, SMOOTH),

    /**
     * Bubble Chart
     */
    BUBBLECHART(),

    /**
     * Meter Chart
     */
    METERCHART(),

    /**
     * Scatter Chart
     */
    SCATTERCHART(),

    /**
     * Table reports
     */
    TABLE(),

    /**
     * Map
     */
    MAP(MAP_REGIONS, MAP_MARKERS),

    /**
     * Selector
     */
    SELECTOR(SELECTOR_DROPDOWN, SELECTOR_LABELS, SELECTOR_SLIDER),

    /**
     * Metric
     */
    METRIC(METRIC_CARD, METRIC_CARD2, METRIC_QUOTA, METRIC_PLAIN_TEXT),

    /**
     * External Component Displayer
     */
    EXTERNAL_COMPONENT(),

    /**
     * Timeseries displayer
     */
    TIMESERIES();

    DisplayerType(DisplayerSubType... subtypes) {
        for (DisplayerSubType displayerSubType : subtypes) {
            this.subtypes.add(displayerSubType);
        }
    }

    private List<DisplayerSubType> subtypes = new ArrayList<>();

    public static DisplayerType getByName(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public List<DisplayerSubType> getSubTypes() {
        return subtypes;
    }
}
