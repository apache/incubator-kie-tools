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
package org.dashbuilder.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface AppConstants extends Messages {

    public static final AppConstants INSTANCE = GWT.create(AppConstants.class);

    String logoBannerError();

    String logOut();

    String role();

    String menu_home();

    String menu_gallery();
    
    String menu_administration();

    String menu_security();

    String menu_dataset_authoring();

    String menu_dashboards();

    String menu_dashboards_salesdb();

    String menu_dashboards_salesreports();

    String menu_dashboards_new();

    String menu_content_manager();

    String menu_extensions_apps();

    String home_intro();

    String home_upcoming();

    String home_feature1();

    String home_feature2();

    String home_feature3();

    String home_feature4();

    String home_feature5();

    String home_feature6();

    String home_feature7();

    String home_feature8();

    String home_feature9();

    String home_feature10();

    String home_feature11();

    String home_feature12();

    String home_feature13();

    String home_arch();

    String home_arch1();

    String home_arch2();

    String home_arch3();

    String home_arch4();

    String home_arch5();

    String home_arch6a();

    String home_arch6b();

    String home_furtherinfo();

    String home_license();

    String notification_dashboard_created(String id);

    String notification_dashboard_deleted(String id);

    String dashboard_new_displayer();
    String dashboard_delete_dashboard();
    String dashboard_delete_popup_title();
    String dashboard_delete_popup_content();

    String salesdbpersp_salessummary();

    String salesreportspersp_salesreports();

    String expensesdb_title();

    String expensesdb_tab_exp_evolution();

    String expensesdb_tab_by_employee();

    String expensesdb_tab_all_exp();

    String expensesdb_pie_title();

    String expensesdb_pie_column1();

    String expensesdb_pie_column2();

    String expensesdb_pie_column3();

    String expensesdb_bar_title();

    String expensesdb_bar_column1();

    String expensesdb_bubble_title();

    String expensesdb_bubble_column1();

    String expensesdb_bubble_column2();

    String expensesdb_bubble_column3();

    String expensesdb_bubble_column4();

    String expensesdb_line_title();

    String expensesdb_line_column1();

    String expensesdb_table_title();

    String expensesdb_table_column1();

    String expensesdb_table_column2();

    String expensesdb_table_column3();

    String expensesdb_table_column4();

    String expensesdb_table_column5();

    String gallerytree_home();

    String gallerytree_home_p1();

    String gallerytree_home_s2a();

    String gallerytree_home_s2b();

    String gallerytree_home_ghublink();

    String gallerytree_title();

    String gallerytree_bar();

    String gallerytree_bar_horiz();

    String gallerytree_bar_horiz_title();

    String gallerytree_bar_horiz_column1();

    String gallerytree_bar_vert();

    String gallerytree_bar_vert_title();

    String gallerytree_bar_vert_column1();

    String gallerytree_bar_vert_dd();

    String gallerytree_bar_vert_dd_title();

    String gallerytree_bar_vert_dd_column1();

    String gallerytree_bar_vert_dd_column2();

    String gallerytree_bar_vert_dd_column3();

    String gallerytree_bar_multi();

    String gallerytree_bar_stacked();

    String gallerytree_bar_multi_title();

    String gallerytree_bar_multi_column1();

    String gallerytree_bar_multi_column2();

    String gallerytree_bar_multi_column3();

    String gallerytree_pie();

    String gallerytree_pie_basic();

    String gallerytree_pie_basic_title();

    String gallerytree_pie_basic_column1();

    String gallerytree_pie_3d();

    String gallerytree_pie_3d_title();

    String gallerytree_pie_3d_column1();

    String gallerytree_pie_donut();

    String gallerytree_pie_donut_title();

    String gallerytree_pie_donut_column1();

    String gallerytree_pie_dd();

    String gallerytree_pie_dd_title();

    String gallerytree_pie_dd_column1();

    String gallerytree_pie_dd_column2();

    String gallerytree_pie_dd_column3();

    String gallerytree_line();

    String gallerytree_line_basic();

    String gallerytree_line_basic_title();

    String gallerytree_line_basic_column1();

    String gallerytree_line_basic_column2();

    String gallerytree_line_multi();

    String gallerytree_line_multi_title();

    String gallerytree_line_multi_column1();

    String gallerytree_line_multi_column2();

    String gallerytree_line_multi_column3();

    String gallerytree_line_multi_static();

    String gallerytree_line_multi_static_title();

    String gallerytree_line_multi_static_column1();

    String gallerytree_line_multi_static_column2();

    String gallerytree_line_multi_static_column3();

    String gallerytree_area();

    String gallerytree_area_basic();

    String gallerytree_area_basic_title();

    String gallerytree_area_basic_column1();

    String gallerytree_area_fixed();

    String gallerytree_area_fixed_title();

    String gallerytree_area_fixed_column1();

    String gallerytree_area_fixed_column2();

    String gallerytree_area_dd();

    String gallerytree_area_dd_title();

    String gallerytree_area_dd_column1();

    String gallerytree_area_dd_column2();

    String gallerytree_bubble();

    String gallerytree_bubble_basic();

    String gallerytree_bubble_basic_title();

    String gallerytree_bubble_basic_column1();

    String gallerytree_bubble_basic_column2();

    String gallerytree_bubble_basic_column3();

    String gallerytree_bubble_basic_column4();

    String gallerytree_meter();

    String gallerytree_meter_basic();

    String gallerytree_meter_basic_title();

    String gallerytree_meter_basic_column1();

    String gallerytree_meter_multi();

    String gallerytree_meter_multi_title();

    String gallerytree_meter_multi_column1();

    String gallerytree_meter_multi_static();

    String gallerytree_meter_multi_static_title();

    String gallerytree_meter_multi_static_column1();

    String gallerytree_meter_multi_static_column2();

    String gallerytree_metrics();

    String gallerytree_metrics_basic();

    String gallerytree_metrics_basic_title();

    String gallerytree_metrics_basic_column1();

    String gallerytree_metrics_basic_static();

    String gallerytree_metrics_basic_static_title();

    String gallerytree_metrics_basic_static_column1();

    String gallerytree_map();

    String gallerytree_map_region();

    String gallerytree_map_region_title();

    String gallerytree_map_region_column1();

    String gallerytree_map_marker();

    String gallerytree_map_marker_title();

    String gallerytree_map_marker_column1();

    String gallerytree_table();

    String gallerytree_table_basic();

    String gallerytree_table_basic_title();

    String gallerytree_table_basic_column1();

    String gallerytree_table_basic_column2();

    String gallerytree_table_basic_column3();

    String gallerytree_table_basic_column4();

    String gallerytree_table_basic_column5();

    String gallerytree_table_basic_column6();

    String gallerytree_table_basic_column7();

    String gallerytree_table_basic_column8();

    String gallerytree_table_basic_column9();

    String gallerytree_table_basic_column10();

    String gallerytree_table_filtered();

    String gallerytree_table_filtered_title();

    String gallerytree_table_filtered_column1();

    String gallerytree_table_filtered_column2();

    String gallerytree_table_filtered_column3();

    String gallerytree_table_filtered_column4();

    String gallerytree_table_filtered_column5();

    String gallerytree_table_filtered_column6();

    String gallerytree_table_filtered_column7();

    String gallerytree_table_filtered_column8();

    String gallerytree_table_grouped();

    String gallerytree_table_grouped_title();

    String gallerytree_table_grouped_column1();

    String gallerytree_table_grouped_column2();

    String gallerytree_table_grouped_column3();

    String gallerytree_table_grouped_column4();

    String gallerytree_table_grouped_column5();

    String gallerytree_table_grouped_column6();

    String gallerytree_table_default_dd();

    String gallerytree_table_default_dd_title();

    String gallerytree_table_default_dd_column1();

    String gallerytree_table_default_dd_column2();

    String gallerytree_table_default_dd_column3();

    String gallerytree_table_default_dd_column4();

    String gallerytree_table_default_dd_column5();

    String gallerytree_table_default_dd_column6();

    String gallerytree_table_default_dd_column7();

    String gallerytree_table_default_dd_column8();

    String gallerytree_table_default_dd_column9();

    String gallerytree_table_default_dd_column10();

    String gallerytree_selector();

    String gallerytree_selector_dropdown();

    String gallerytree_selector_labels();

    String gallerytree_selector_slider();

    String gallerytree_db();

    String gallerytree_db_salesgoals();

    String gallerytree_db_salespipe();

    String gallerytree_db_salespcountry();

    String gallerytree_db_salesreps();

    String gallerytree_db_expreps();

    String gallerytree_db_clustermetrics();

    String gallerywidget_unknown();

    String gallerywidget_dataset_modif();

    String gallerywidget_dataset_loaded(String type, int size);

    String metrics_server_detail_title();

    String metrics_server_detail_backbutton_tt();

    String metrics_server_detail_modebutton_tt_viewtable();

    String metrics_server_detail_modebutton_tt_viewcharts();

    String metrics_server_detail_lasthour_summary();

    String metrics_server_detail_cpu_usage();

    String metrics_server_detail_mem_usage();

    String metrics_server_detail_netw_usage();

    String metrics_server_detail_disk_usage();

    String metrics_server_detail_live_procs();

    String metrics_server_detail_cpu1_title();

    String metrics_server_detail_cpu2_title();

    String metrics_server_detail_mem_title();

    String metrics_server_detail_netw_title();

    String metrics_server_detail_disk_title();

    String metrics_server_detail_disk_column1();

    String metrics_server_detail_disk_column2();

    String metrics_server_detail_procs_running_title();

    String metrics_server_detail_procs_running_column1();

    String metrics_server_detail_procs_sleeping_title();

    String metrics_server_detail_procs_sleeping_column1();

    String metrics_server_detail_rt_table_title(String server);

    String metrics_server_detail_rt_table_column1();

    String metrics_server_detail_rt_table_column2();

    String metrics_server_detail_rt_table_column3();

    String metrics_server_detail_rt_table_column4();

    String metrics_server_detail_rt_table_column5();

    String metrics_server_detail_rt_table_column6();

    String metrics_server_detail_rt_table_column7();

    String metrics_server_detail_rt_table_column8();

    String metrics_server_detail_rt_table_column9();

    String metrics_server_detail_rt_table_column10();

    String metrics_server_detail_rt_table_column11();

    String metrics_server_vert_title();

    String metrics_server_vert_default_tt();

    String metrics_server_vert_cpu_tt();

    String metrics_server_vert_usedmem_tt();

    String metrics_server_vert_netbw_tt();

    String metrics_server_vert_procs_tt();

    String metrics_server_vert_disk_tt();

    String metrics_server_vert_cpu1_title();

    String metrics_server_vert_cpu2_title();

    String metrics_server_vert_memconsumption_title();

    String metrics_server_vert_netbw_title();

    String metrics_server_vert_procs_title();

    String metrics_server_vert_du_title();

    String metrics_server_vert_du_free();

    String metrics_server_vert_du_used();

    String metrics_server_vert_serverdown(String server);

    String metrics_cluster_title();

    String metrics_cluster_messages_heading();

    String metrics_cluster_metricselector_label();

    String metrics_cluster_chartselector_label();

    String metrics_cluster_metricselector_cpu();

    String metrics_cluster_metricselector_mem();

    String metrics_cluster_metricselector_disk();

    String metrics_cluster_metricselector_netw();

    String metrics_cluster_metricselector_proc();

    String metrics_cluster_chartselector_bar();

    String metrics_cluster_chartselector_line();

    String metrics_cluster_chartselector_area();

    String metrics_cluster_column_cpu();

    String metrics_cluster_column_cpu_y();

    String metrics_cluster_column_df();

    String metrics_cluster_column_df_y();

    String metrics_cluster_column_du();

    String metrics_cluster_column_du_y();

    String metrics_cluster_column_memf();

    String metrics_cluster_column_memf_y();

    String metrics_cluster_column_memu();

    String metrics_cluster_column_memu_y();

    String metrics_cluster_column_procsrn();

    String metrics_cluster_column_procsrn_y();

    String metrics_cluster_column_procssl();

    String metrics_cluster_column_procssl_y();

    String metrics_cluster_column_netrx();

    String metrics_cluster_column_netrx_y();

    String metrics_cluster_column_nettx();

    String metrics_cluster_column_nettx_y();

    String metrics_cluster_column_time();

    String metrics_rt_title();

    String metrics_rt_serverup();

    String metrics_rt_serverdown();

    String sales_bycountry_title();

    String sales_bycountry_bubble_title();

    String sales_bycountry_bubble_column1();

    String sales_bycountry_bubble_column2();

    String sales_bycountry_bubble_column3();

    String sales_bycountry_map_title();

    String sales_bycountry_map_column1();

    String sales_bycountry_table_title();

    String sales_bycountry_table_column1();

    String sales_bycountry_table_column2();

    String sales_bycountry_table_column3();

    String sales_bycountry_table_column4();

    String sales_bycountry_table_column5();

    String sales_bycountry_table_column6();

    String sales_bycountry_table_column7();

    String sales_bycountry_table_column8();

    String sales_bycountry_table_column9();

    String sales_bydate_title();

    String sales_bydate_area_title();

    String sales_bydate_area_column1();

    String sales_bydate_pie_years_title();

    String sales_bydate_pie_years_column1();

    String sales_bydate_pie_quarters_title();

    String sales_bydate_pie_quarters_column1();

    String sales_bydate_bar_weekday_title();

    String sales_bydate_bar_weekday_column1();

    String sales_bydate_pie_pipe_title();

    String sales_bydate_pie_pipe_column1();

    String sales_bydate_table_title();

    String sales_bydate_table_column1();

    String sales_bydate_table_column2();

    String sales_bydate_table_column3();

    String sales_bydate_table_column4();

    String sales_bydate_table_column5();

    String sales_bydate_table_column6();

    String sales_bydate_table_column7();

    String sales_bydate_table_column8();

    String sales_bydate_table_column9();

    String sales_bydate_selector_total();

    String sales_goals_title();

    String sales_goals_meter_title();

    String sales_goals_meter_column1();

    String sales_goals_line_title();

    String sales_goals_line_column1();

    String sales_goals_line_column2();

    String sales_goals_line_column3();

    String sales_goals_bar_byproduct_title();

    String sales_goals_bar_byproduct_column1();

    String sales_goals_bar_byproduct_column2();

    String sales_goals_bar_byproduct_column3();

    String sales_goals_bar_byempl_title();

    String sales_goals_bar_byempl_column1();

    String sales_goals_bar_byempl_column2();

    String sales_goals_bubble_title();

    String sales_goals_bubble_column1();

    String sales_goals_bubble_column2();

    String sales_goals_bubble_column3();

    String sales_tablereports_title();

    String sales_tablereports_tab_byyear();

    String sales_tablereports_tab_bycountry();

    String sales_tablereports_tab_byproduct();

    String sales_tablereports_tab_bysalesman();

    String sales_tablereports_all_title();

    String sales_tablereports_all_column1();

    String sales_tablereports_all_column2();

    String sales_tablereports_all_column3();

    String sales_tablereports_all_column4();

    String sales_tablereports_all_column5();

    String sales_tablereports_all_column6();

    String sales_tablereports_all_column7();

    String sales_tablereports_all_column8();

    String sales_tablereports_all_column9();

    String sales_tablereports_bycountry_title();

    String sales_tablereports_bycountry_column1();

    String sales_tablereports_bycountry_column2();

    String sales_tablereports_bycountry_column3();

    String sales_tablereports_bycountry_column4();

    String sales_tablereports_bycountry_column5();

    String sales_tablereports_bycountry_column6();

    String sales_tablereports_byproduct_title();

    String sales_tablereports_byproduct_column1();

    String sales_tablereports_byproduct_column2();

    String sales_tablereports_byproduct_column3();

    String sales_tablereports_byproduct_column4();

    String sales_tablereports_byproduct_column5();

    String sales_tablereports_byproduct_column6();

    String sales_tablereports_bysalesman_title();

    String sales_tablereports_bysalesman_column1();

    String sales_tablereports_bysalesman_column2();

    String sales_tablereports_bysalesman_column3();

    String sales_tablereports_bysalesman_column4();

    String sales_tablereports_bysalesman_column5();

    String sales_tablereports_bysalesman_column6();

    String sales_tablereports_byyear_title();

    String sales_tablereports_byyear_column1();

    String sales_tablereports_byyear_column2();

    String sales_tablereports_byyear_column3();

    String sales_tablereports_byyear_column4();

    String sales_tablereports_byyear_column5();

    String sales_tablereports_byyear_column6();

    String salesopps_displayers_by_pipeline_title();

    String salesopps_displayers_by_status_title();

    String salesopps_displayers_by_salesman_title();

    String salesopps_displayers_by_exp_pipeline_title();

    String salesopps_displayers_by_exp_pipeline_column1();

    String salesopps_displayers_by_product_title();

    String salesopps_displayers_by_product_column1();

    String salesopps_displayers_by_country_title();

    String salesopps_displayers_by_country_column1();

    String salesopps_displayers_country_summary_title();

    String salesopps_displayers_country_summary_column1();

    String salesopps_displayers_country_summary_column2();

    String salesopps_displayers_country_summary_column3();

    String salesopps_displayers_country_summary_column4();

    String salesopps_displayers_country_summary_column5();

    String salesopps_displayers_country_summary_column6();

    String salesopps_displayers_all_list_title();

    String content_manager_dashboard();

    String content_manager_dashboards();

    String content_manager_noDashboards();
}