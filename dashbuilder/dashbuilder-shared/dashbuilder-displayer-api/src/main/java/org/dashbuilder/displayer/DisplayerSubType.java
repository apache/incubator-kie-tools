/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer;

public enum DisplayerSubType {
    LINE,
    SMOOTH,
    AREA,
    AREA_STACKED,
    //        STEPPED,
    BAR,
    BAR_STACKED,
    COLUMN,
    COLUMN_STACKED,
    PIE,
    PIE_3D,
    DONUT,
    MAP_REGIONS,
    MAP_MARKERS,
    METRIC_CARD,
    METRIC_CARD2,
    METRIC_PLAIN_TEXT,
    METRIC_QUOTA,
    SELECTOR_DROPDOWN,
    SELECTOR_SLIDER,
    SELECTOR_LABELS;

    public static DisplayerSubType getByName(String str) {
        if (str == null) return null;
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
