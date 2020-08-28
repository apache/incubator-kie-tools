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

import static org.dashbuilder.displayer.DisplayerSubType.*;

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
    EXTERNAL_COMPONENT();

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
