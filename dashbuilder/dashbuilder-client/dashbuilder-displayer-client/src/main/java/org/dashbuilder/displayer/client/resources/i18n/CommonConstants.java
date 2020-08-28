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
package org.dashbuilder.displayer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface CommonConstants extends Messages {

    CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String ok();

    String cancel();

    String error();

    String cause();

    // Common settings editor literals

    String dataset_editor_notfound();

    String common_button_addnew();

    String common_dropdown_select();

    String settingsEditor_caption();

    String settingsJsonSource_caption();

    String common_group();

    String common_showTitle();

    String common_title();

    String common_title_placeholder();

    String common_export();

    String common_allowCSV();

    String common_allowExcel();

    String common_renderer();

    String common_columns();

    String columns_name();

    String columns_pattern();

    String columns_expression();

    String columns_emptyvalue();

    String common_columns_placeholder();

    String refresh_group();

    String refresh_interval();

    String refresh_stale_data();

    String chart_group();

    String chart_width();

    String chart_height();

    String chart_bgColor();

    String chart_marginGroup();

    String chart_topMargin();

    String chart_bottomMargin();

    String chart_leftMargin();

    String chart_rightMargin();

    String chart_legendGroup();

    String chart_legendShow();

    String chart_legendPosition();

    String chart_3d();

    String table_group();

    String table_pageSize();

    String table_width();

    String table_sortEnabled();

    String table_sortColumn();

    String table_sortColumn_placeholder();

    String table_sortOrder();

    String table_ascSortOrder();

    String table_descSortOrder();

    String table_columnPicker();

    String axis_group();

    String xaxis_showLabels();

    String xaxis_angle();

    String xaxis_title();

    String xaxis_title_placeholder();

    String yaxis_showLabels();

    String yaxis_angle();

    String yaxis_title();

    String yaxis_title_placeholder();

    String meter_group();

    String meter_start();

    String meter_warning();

    String meter_critical();

    String meter_end();

    String filter_group();

    String filter_enabled();

    String filter_self();

    String filter_listening();

    String filter_notifications();

    String selector_group();

    String selector_width();

    String selector_multiple();

    String selector_show_inputs();

    String filter_editor_selectcolumn();

    String timeframe_from();

    String timeframe_to();

    String timeframe_first_month_year();

    String case_sensitive();

    String like_to_pattern_instructions();

    String multiple_input_descriptions();

    String group_columnid_label();

    String dataset_groupdate_fixed_intervals();

    String dataset_groupdate_empty_intervals();

    String dataset_groupdate_interval_type();

    String dataset_groupdate_max_intervals();

    String dataset_groupdate_firstday();

    String dataset_groupdate_firstmonth();

    String settings_validation_integer();

    String settings_validation_double();

    String settings_validation_meter_unknown();

    String settings_validation_meter_higher(String value);

    String settings_validation_meter_lower(String value);

    String settings_validation_meter_invalid();

    // DataSetLookup editor literals

    String dataset_dataset();

    String dataset_filters();

    String dataset_rows();

    String dataset_columns();

    String dataset_lookup_group_columns_all();

    String dataset_lookup_dataset_notfound(String uuid);

    String dataset_lookup_init_error();

    // DisplayerEditor literals

    String displayer_editor_title();

    String displayer_editor_new();

    String displayer_editor_dataset_notfound();

    String displayer_editor_datasetmetadata_fetcherror();

    String displayer_editor_dataset_nolookuprequest();

    String displayer_editor_incompatible_settings();

    String displayer_editor_tab_type();

    String displayer_editor_tab_data();

    String displayer_editor_tab_display();

    String displayer_editor_view_as_table();

    String renderer_selector_title();

    String displayerlocator_default_renderer_undeclared(String targetType);

    String displayerlocator_unsupported_displayer_renderer(String targetType, String rendererUuid);

    String rendererliblocator_renderer_not_found(String renderer);

    String rendererliblocator_multiple_renderers_found(String renderer);

    String expand();

    String collapse();

    String removeFilter();

    String renderermanager_renderer_not_available(String type);

    String displayerviewer_displayer_not_created();

    String chart_resizable();

    String map_group();

    String color_scheme();

    String loadingComponent();
    
    String componentEditor();

    String componentNotFound();

    String noPropertiesComponent();
}