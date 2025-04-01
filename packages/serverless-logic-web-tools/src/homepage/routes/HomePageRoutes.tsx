/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { useMemo } from "react";
import { Routes, Route, useParams, Navigate } from "react-router-dom";
import { Overview } from "../overView/Overview";
import { RecentModels } from "../recentModels/RecentModels";
import { useRoutes } from "../../navigation/Hooks";
import { supportedFileExtensionArray } from "../../extension";
import { NewWorkspaceFromSample } from "../../workspace/components/NewWorkspaceFromSample";
import { NewWorkspaceFromUrlPage } from "../../workspace/components/NewWorkspaceFromUrlPage";
import { NewWorkspaceWithEmptyFilePage } from "../../workspace/components/NewWorkspaceWithEmptyFilePage";
import { EditorPage } from "../../editor/EditorPage";
import { NoMatchPage } from "../../navigation/NoMatchPage";
import { SamplesCatalog } from "../../samples/SamplesCatalog";
import { WorkspaceFiles } from "../recentModels/workspaceFiles/WorkspaceFiles";
import { RuntimeToolsWorkflowInstances } from "../../runtimeTools/pages/RuntimeToolsWorkflowInstances";
import { RuntimeToolsWorkflowDetails } from "../../runtimeTools/pages/RuntimeToolsWorkflowDetails";
import { RuntimeToolsWorkflowDefinitions } from "../../runtimeTools/pages/RuntimeToolsWorkflowDefinitions";
import { RuntimeToolsTriggerCloudEvent } from "../../runtimeTools/pages/RuntimeToolsTriggerCloudEvent";
import { RuntimeToolsWorkflowForm } from "../../runtimeTools/pages/RuntimeToolsWorkflowForm";

export function HomePageRoutes(props: { isNavOpen: boolean }) {
  const routes = useRoutes();

  return <Routes></Routes>;
}
