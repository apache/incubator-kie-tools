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
package org.dashbuilder.displayer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface DisplayerTypeConstants extends ConstantsWithLookup {

    DisplayerTypeConstants INSTANCE = GWT.create(DisplayerTypeConstants.class);

    String displayer_type_selector_tab_bar();

    String displayer_type_selector_tab_pie();

    String displayer_type_selector_tab_line();

    String displayer_type_selector_tab_area();

    String displayer_type_selector_tab_bubble();

    String displayer_type_selector_tab_meter();

    String displayer_type_selector_tab_metric();

    String displayer_type_selector_tab_map();

    String displayer_type_selector_tab_table();

    String displayer_type_selector_tab_selector();

    // Subtype enum literals

    String DISPLAYER_SUBTYPE_LINE();

    String DISPLAYER_SUBTYPE_SMOOTH();

    String DISPLAYER_SUBTYPE_AREA();

    String DISPLAYER_SUBTYPE_STACKED();

    String DISPLAYER_SUBTYPE_STEPPED();

    String DISPLAYER_SUBTYPE_BAR();

    String DISPLAYER_SUBTYPE_BAR_STACKED();

    String DISPLAYER_SUBTYPE_COLUMN();

    String DISPLAYER_SUBTYPE_COLUMN_STACKED();

    String DISPLAYER_SUBTYPE_HISTOGRAM();

    String DISPLAYER_SUBTYPE_PIE();

    String DISPLAYER_SUBTYPE_PIE_3D();

    String DISPLAYER_SUBTYPE_DONUT();

    String DISPLAYER_SUBTYPE_MAP_REGIONS();

    String DISPLAYER_SUBTYPE_MAP_MARKERS();

    String DISPLAYER_SUBTYPE_METRIC_CARD();

    String DISPLAYER_SUBTYPE_METRIC_CARD2();

    String DISPLAYER_SUBTYPE_METRIC_QUOTA();

    String DISPLAYER_SUBTYPE_METRIC_PLAIN_TEXT();

    String DISPLAYER_SUBTYPE_SELECTOR_DROPDOWN();

    String DISPLAYER_SUBTYPE_SELECTOR_LABELS();

    String DISPLAYER_SUBTYPE_SELECTOR_SLIDER();

    // Subtype selector tooltips

    String BARCHART_BAR_tt();

    String BARCHART_BAR_STACKED_tt();

    String BARCHART_COLUMN_tt();

    String BARCHART_COLUMN_STACKED_tt();

    String PIECHART_PIE_tt();

    String PIECHART_PIE_3D_tt();

    String PIECHART_DONUT_tt();

    String AREACHART_AREA_tt();

    String AREACHART_AREA_STACKED_tt();

    String LINECHART_LINE_tt();

    String LINECHART_SMOOTH_tt();

    String MAP_MAP_REGIONS_tt();

    String MAP_MAP_MARKERS_tt();

    String BUBBLECHART_default_tt();

    String METERCHART_default_tt();

    String METRIC_METRIC_CARD_tt();

    String METRIC_METRIC_CARD2_tt();

    String METRIC_METRIC_QUOTA_tt();

    String METRIC_METRIC_PLAIN_TEXT_tt();

    String TABLE_default_tt();
    
    String EXTERNAL_COMPONENT_default_tt();

    String SELECTOR_SELECTOR_DROPDOWN_tt();

    String SELECTOR_SELECTOR_LABELS_tt();

    String SELECTOR_SELECTOR_SLIDER_tt();

}
