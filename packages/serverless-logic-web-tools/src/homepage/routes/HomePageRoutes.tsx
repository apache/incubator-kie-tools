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
import { Routes, Route } from "react-router-dom";
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
  const supportedExtensions = useMemo(() => supportedFileExtensionArray.join("|"), []);
  return (
    <Routes>
      <Route
        path={routes.newModel.path({ extension: `:extension(${supportedExtensions})` })}
        element={<NewWorkspaceWithEmptyFilePage />}
      />
      <Route path={routes.importModel.path({})} element={<NewWorkspaceFromUrlPage />} />
      <Route path={routes.sampleShowcase.path({})} element={<NewWorkspaceFromSample />} />
      <Route
        path={routes.workspaceWithFilePath.path({
          workspaceId: ":workspaceId",
          fileRelativePath: `:fileRelativePath*`,
          extension: `:extension?`,
        })}
        element={<EditorPage />}
      />
      <Route path={routes.home.path({})} element={<Overview isNavOpen={props.isNavOpen} />} />
      <Route path={routes.recentModels.path({})} element={<RecentModels />} />
      <Route path={routes.workspaceWithFiles.path({ workspaceId: ":workspaceId" })} element={<WorkspaceFiles />} />
      <Route path={routes.sampleCatalog.path({})} element={<SamplesCatalog />} />
      <Route
        path={routes.runtimeToolsTriggerCloudEventForWorkflowInstance.path({ workflowId: ":workflowId" })}
        element={<RuntimeToolsTriggerCloudEvent />}
      />
      <Route
        path={routes.runtimeToolsTriggerCloudEventForWorkflowDefinition.path({ workflowName: ":workflowName" })}
        element={<RuntimeToolsTriggerCloudEvent />}
      />
      <Route
        path={routes.runtimeToolsWorkflowDetails.path({ workflowId: ":workflowId" })}
        element={<RuntimeToolsWorkflowDetails />}
      />
      <Route
        path={routes.runtimeToolsWorkflowForm.path({ workflowName: ":workflowName" })}
        element={<RuntimeToolsWorkflowForm />}
      />
      <Route path={routes.runtimeToolsWorkflowDefinitions.path({})} element={<RuntimeToolsWorkflowDefinitions />} />
      <Route path={routes.runtimeToolsWorkflowInstances.path({})} element={<RuntimeToolsWorkflowInstances />} />
      <Route element={<NoMatchPage />} />
    </Routes>
  );
}
