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

import * as React from "react";
import { useRef, useState } from "react";
import {
  createHashRouter,
  createRoutesFromElements,
  Navigate,
  Outlet,
  Route,
  RouterProvider,
  useParams,
} from "react-router-dom";
import { EnvContextProvider } from "./env/EnvContextProvider";
import { EditorEnvelopeLocatorContextProvider } from "./envelopeLocator/EditorEnvelopeLocatorContext";
import { AppI18nContextProvider } from "./i18n";
import { NavigationContextProvider } from "./navigation/NavigationContextProvider";
import { OpenShiftContextProvider } from "./openshift/OpenShiftContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { VirtualServiceRegistryContextProvider } from "./virtualServiceRegistry/VirtualServiceRegistryContextProvider";
import { SampleContextProvider } from "./samples/hooks/SampleContext";
import { DevModeContextProvider } from "./openshift/swfDevMode/DevModeContext";
import { GlobalAlertsContextProvider } from "./alerts/GlobalAlertsContext";
import { EditorContextProvider } from "./editor/hooks/EditorContext";
import { WebToolsWorkspaceContextProvider } from "./workspace/hooks/WebToolsWorkspaceContextProvider";
import { UpgradeContextProvider } from "./upgrade/UpgradeContext";
import { WebToolsWorkflowDefinitionListContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowDefinitionListContextProvider";
import { WebToolsWorkflowListContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowListContextProvider";
import { WebToolsWorkflowDetailsContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowDetailsContextProvider";
import { WebToolsWorkflowFormContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowFormContextProvider";
import { WebToolsCloudEventFormContextProvider } from "./runtimeTools/contexts/WebToolsCloudEventFormContextProvider";
import { useRoutes } from "./navigation/Hooks";
import { OnlineEditorPage } from "./homepage/pageTemplate/OnlineEditorPage";
import { NoMatchPage } from "./navigation/NoMatchPage";
import { RuntimeToolsWorkflowInstances } from "./runtimeTools/pages/RuntimeToolsWorkflowInstances";
import { RuntimeToolsWorkflowDefinitions } from "./runtimeTools/pages/RuntimeToolsWorkflowDefinitions";
import { RuntimeToolsWorkflowForm } from "./runtimeTools/pages/RuntimeToolsWorkflowForm";
import { NewWorkspaceWithEmptyFilePage } from "./workspace/components/NewWorkspaceWithEmptyFilePage";
import { RuntimeToolsWorkflowDetails } from "./runtimeTools/pages/RuntimeToolsWorkflowDetails";
import { RuntimeToolsTriggerCloudEvent } from "./runtimeTools/pages/RuntimeToolsTriggerCloudEvent";
import { SamplesCatalog } from "./samples/SamplesCatalog";
import { WorkspaceFiles } from "./homepage/recentModels/workspaceFiles/WorkspaceFiles";
import { RecentModels } from "./homepage/recentModels/RecentModels";
import { Overview } from "./homepage/overView/Overview";
import { EditorPage } from "./editor/EditorPage";
import { NewWorkspaceFromSample } from "./workspace/components/NewWorkspaceFromSample";
import { NewWorkspaceFromUrlPage } from "./workspace/components/NewWorkspaceFromUrlPage";
import { supportedFileExtensionArray } from "./extension";
import { GitHubSettings } from "./settings/github/GitHubSettings";
import { OpenShiftSettings } from "./settings/openshift/OpenShiftSettings";
import { ServiceAccountSettings } from "./settings/serviceAccount/ServiceAccountSettings";
import { ServiceRegistrySettings } from "./settings/serviceRegistry/ServiceRegistrySettings";
import { RuntimeToolsSettings } from "./settings/runtimeTools/RuntimeToolsSettings";
import { StorageSettings } from "./settings/storage/StorageSettings";

export const App = () => {
  const routes = useRoutes();
  const pageContainerRef = useRef<HTMLDivElement>(null);
  const [isNavOpen, setIsNavOpen] = useState(true);

  return (
    <RouterProvider
      router={createHashRouter(
        createRoutesFromElements(
          <Route element={<AppContext />}>
            <Route
              path={routes.home.path({})}
              element={
                <OnlineEditorPage
                  pageContainerRef={pageContainerRef}
                  isNavOpen={isNavOpen}
                  setIsNavOpen={setIsNavOpen}
                />
              }
            >
              <Route
                path={routes.newModel.path({ extension: `:extension` })}
                element={<NewModelRoute supportedExtensions={supportedFileExtensionArray} />}
              />
              <Route path={routes.importModel.path({})} element={<NewWorkspaceFromUrlPage />} />
              <Route path={routes.sampleShowcase.path({})} element={<NewWorkspaceFromSample />} />
              <Route
                path={routes.workspaceWithFilePath.path({
                  workspaceId: ":workspaceId",
                  fileRelativePath: `*`,
                })}
                element={<EditorPage />}
              />
              <Route path={routes.home.path({})} element={<Overview isNavOpen={isNavOpen} />} />
              <Route path={routes.recentModels.path({})} element={<RecentModels />} />
              <Route
                path={routes.workspaceWithFiles.path({ workspaceId: ":workspaceId" })}
                element={<WorkspaceFiles />}
              />
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
              <Route
                path={routes.runtimeToolsWorkflowDefinitions.path({})}
                element={<RuntimeToolsWorkflowDefinitions />}
              />
              <Route path={routes.runtimeToolsWorkflowInstances.path({})} element={<RuntimeToolsWorkflowInstances />} />
              <Route
                path={routes.settings.github.path({})}
                element={<GitHubSettings pageContainerRef={pageContainerRef} />}
              />
              <Route
                path={routes.settings.openshift.path({})}
                element={<OpenShiftSettings pageContainerRef={pageContainerRef} />}
              />
              <Route
                path={routes.settings.service_account.path({})}
                element={<ServiceAccountSettings pageContainerRef={pageContainerRef} />}
              />
              <Route
                path={routes.settings.service_registry.path({})}
                element={<ServiceRegistrySettings pageContainerRef={pageContainerRef} />}
              />
              <Route path={routes.settings.storage.path({})} element={<StorageSettings />} />
              <Route
                path={routes.settings.runtime_tools.path({})}
                element={<RuntimeToolsSettings pageContainerRef={pageContainerRef} />}
              />
              <Route
                path={routes.settings.redirect.path({})}
                element={<Navigate replace to={routes.settings.github.path({})} />}
              />
            </Route>
            <Route path={routes.noMatch.path({})} element={<NoMatchPage />} />
          </Route>
        )
      )}
    />
  );
};

function NewModelRoute({ supportedExtensions }: { supportedExtensions: string[] }) {
  const routes = useRoutes();
  const { extension } = useParams();
  const foundExtension = supportedExtensions.find((supportedExtension) => supportedExtension === (extension ?? ""));

  if (foundExtension === undefined) {
    return <Navigate replace to={routes.home.path({})} />;
  }
  return <NewWorkspaceWithEmptyFilePage />;
}

const AppContext = () => {
  return nest(
    [AppI18nContextProvider, {}],
    [EditorEnvelopeLocatorContextProvider, {}],
    [EnvContextProvider, {}],
    [SettingsContextProvider, {}],
    [GlobalAlertsContextProvider, []],
    [WebToolsWorkspaceContextProvider, []],
    [UpgradeContextProvider, []],
    [OpenShiftContextProvider, {}],
    [DevModeContextProvider, {}],
    [VirtualServiceRegistryContextProvider, {}],
    [SampleContextProvider, {}],
    [NavigationContextProvider, {}],
    [EditorContextProvider, {}],
    [WebToolsWorkflowDefinitionListContextProvider, {}],
    [WebToolsWorkflowListContextProvider, {}],
    [WebToolsWorkflowDetailsContextProvider, {}],
    [WebToolsWorkflowFormContextProvider, {}],
    [WebToolsCloudEventFormContextProvider, {}],
    // Insert new context providers from here to beginning, always before Outlet
    [() => <Outlet />, {}]
  );
};

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight(
    (acc, [Component, props]) => {
      return <Component {...props}>{acc}</Component>;
    },
    <></>
  );
}
