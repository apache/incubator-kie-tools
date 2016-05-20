/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.menu;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KieWorkbenchFeatures {

    // Top level menu entries plus Home menu sections

    // TODO review/adapt roles for new menu structure
    public static final String F_PROJECT_AUTHORING = "wb_project_authoring";
    public static final String F_ARTIFACT_REPO = "wb_artifact_repository";
    public static final String F_ADMINISTRATION = "wb_administration";
    public static final String F_PROCESS_DEFINITIONS = "wb_process_definitions";
    public static final String F_PROCESS_INSTANCES = "wb_process_instances";
    public static final String F_DEPLOYMENTS = "wb_deployments";
    public static final String F_MANAGEMENT = "wb_management";
    public static final String F_CONTRIBUTORS = "wb_contributors";
    public static final String F_ASSET_MANAGEMENT = "wb_asset_management";
    public static final String F_JOBS = "wb_jobs";
    public static final String F_TASKS = "wb_tasks";
    public static final String F_PROCESS_DASHBOARD = "wb_process_dashboard";
    public static final String F_DASHBOARD_BUILDER = "wb_dashboard_builder";
    public static final String F_SEARCH = "wb_search";
    public static final String F_PLUGIN_MANAGEMENT = "wb_plugin_management";
    public static final String F_PERSPECTIVE_EDITOR = "wb_perspective_editor";
    public static final String F_APPS = "wb_apps";
    public static final String F_DATASETS = "wb_datasets";
    public static final String F_EXTENSIONS = "wb_extensions";

    public static final String G_AUTHORING = "wb_authoring";
    public static final String G_DEPLOY = "wb_deploy";
    public static final String G_PROCESS_MANAGEMENT = "wb_process_management";
    public static final String G_TASKS = "wb_task_management";
    public static final String G_DASHBOARDS = "wb_dashboards";
    public static final String G_PLUGIN_MANAGEMENT = "wb_plugin_management";
}
