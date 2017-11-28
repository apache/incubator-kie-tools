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
package org.dashbuilder.displayer.client.resources.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface DisplayerImagesResources extends ClientBundleWithLookup {

    DisplayerImagesResources INSTANCE = GWT.create(DisplayerImagesResources.class);

    String DEFAULT_SUFFIX = "_default";
    String SELECTED_SUFFIX = "_selected";
    String UNSELECTED_SUFFIX = "_unselected";

    // Convention for image resource method names: type_subtype_selected/unselected

    // TODO replace images with own harvest

    @Source("bar_selected.png")
    ImageResource BARCHART_BAR_selected();

    @Source("bar_unselected.png")
    ImageResource BARCHART_BAR_unselected();

    @Source("bar_stacked_selected.png")
    ImageResource BARCHART_BAR_STACKED_selected();

    @Source("bar_stacked_unselected.png")
    ImageResource BARCHART_BAR_STACKED_unselected();

    @Source("column_selected.png")
    ImageResource BARCHART_COLUMN_selected();

    @Source("column_unselected.png")
    ImageResource BARCHART_COLUMN_unselected();

    @Source("column_stacked_selected.png")
    ImageResource BARCHART_COLUMN_STACKED_selected();

    @Source("column_stacked_unselected.png")
    ImageResource BARCHART_COLUMN_STACKED_unselected();

    @Source("pie_selected.png")
    ImageResource PIECHART_PIE_selected();

    @Source("pie_unselected.png")
    ImageResource PIECHART_PIE_unselected();

    @Source("pie_3d_selected.png")
    ImageResource PIECHART_PIE_3D_selected();

    @Source("pie_3d_unselected.png")
    ImageResource PIECHART_PIE_3D_unselected();

    @Source("donut_selected.png")
    ImageResource PIECHART_DONUT_selected();

    @Source("donut_unselected.png")
    ImageResource PIECHART_DONUT_unselected();

    @Source("area_selected.png")
    ImageResource AREACHART_AREA_selected();

    @Source("area_unselected.png")
    ImageResource AREACHART_AREA_unselected();

    @Source("area_stacked_selected.png")
    ImageResource AREACHART_AREA_STACKED_selected();

    @Source("area_stacked_unselected.png")
    ImageResource AREACHART_AREA_STACKED_unselected();

    @Source("line_selected.png")
    ImageResource LINECHART_LINE_selected();

    @Source("line_unselected.png")
    ImageResource LINECHART_LINE_unselected();

    @Source("line_smooth_selected.png")
    ImageResource LINECHART_SMOOTH_selected();

    @Source("line_smooth_unselected.png")
    ImageResource LINECHART_SMOOTH_unselected();

    @Source("map_regions_selected.png")
    ImageResource MAP_MAP_REGIONS_selected();

    @Source("map_regions_unselected.png")
    ImageResource MAP_MAP_REGIONS_unselected();

    @Source("map_markers_selected.png")
    ImageResource MAP_MAP_MARKERS_selected();

    @Source("map_markers_unselected.png")
    ImageResource MAP_MAP_MARKERS_unselected();

    @Source("bubble_default.png")
    ImageResource BUBBLECHART_default();

    @Source("meter_default.png")
    ImageResource METERCHART_default();

    @Source("metric_card_selected.png")
    ImageResource METRIC_METRIC_CARD_selected();

    @Source("metric_card_unselected.png")
    ImageResource METRIC_METRIC_CARD_unselected();

    @Source("metric_card2_selected.png")
    ImageResource METRIC_METRIC_CARD2_selected();

    @Source("metric_card2_unselected.png")
    ImageResource METRIC_METRIC_CARD2_unselected();

    @Source("metric_quota_selected.png")
    ImageResource METRIC_METRIC_QUOTA_selected();

    @Source("metric_quota_unselected.png")
    ImageResource METRIC_METRIC_QUOTA_unselected();

    @Source("metric_plaintext_selected.png")
    ImageResource METRIC_METRIC_PLAIN_TEXT_selected();

    @Source("metric_plaintext_unselected.png")
    ImageResource METRIC_METRIC_PLAIN_TEXT_unselected();

    @Source("selector_dropdown_selected.png")
    ImageResource SELECTOR_SELECTOR_DROPDOWN_selected();

    @Source("selector_dropdown_unselected.png")
    ImageResource SELECTOR_SELECTOR_DROPDOWN_unselected();

    @Source("selector_labels_selected.png")
    ImageResource SELECTOR_SELECTOR_LABELS_selected();

    @Source("selector_labels_unselected.png")
    ImageResource SELECTOR_SELECTOR_LABELS_unselected();

    @Source("selector_slider_selected.png")
    ImageResource SELECTOR_SELECTOR_SLIDER_selected();

    @Source("selector_slider_unselected.png")
    ImageResource SELECTOR_SELECTOR_SLIDER_unselected();

    @Source("table_default.png")
    ImageResource TABLE_default();

}
