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
import { useMemo } from "react";
import {
  createHashRouter,
  createRoutesFromElements,
  Navigate,
  Outlet,
  Route,
  RouterProvider,
  useParams,
} from "react-router-dom";
import { EditorEnvelopeLocatorContextProvider } from "./envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { EditorPage } from "./editor/EditorPage";
import { OnlineI18nContextProvider } from "./i18n";
import { NoMatchPage } from "./NoMatchPage";
import { ExtendedServicesContextProvider } from "./extendedServices/ExtendedServicesContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { HomePage } from "./home/HomePage";
import { NewWorkspaceWithEmptyFilePage } from "./importFromUrl/NewWorkspaceWithEmptyFilePage";
import { NewWorkspaceFromUrlPage } from "./importFromUrl/NewWorkspaceFromUrlPage";
import { DevDeploymentsContextProvider } from "./devDeployments/DevDeploymentsContextProvider";
import { NavigationContextProvider } from "./navigation/NavigationContextProvider";
import { useRoutes } from "./navigation/Hooks";
import { EnvContextProvider } from "./env/hooks/EnvContextProvider";
import { DmnRunnerPersistenceDispatchContextProvider } from "./dmnRunnerPersistence/DmnRunnerPersistenceDispatchContextProvider";
import { PreviewSvgsContextProvider } from "./previewSvgs/PreviewSvgsContext";
import { AuthSessionsContextProvider } from "./authSessions/AuthSessionsContext";
import { AccountsContextProvider } from "./accounts/AccountsContext";
import { GlobalAlertsContextProvider } from "./alerts";
import { WorkspacesContextProviderWithCustomCommitMessagesModal } from "./workspace/components/WorkspacesContextProviderWithCustomCommitMessagesModal";
import { StartupBlockerProvider } from "./workspace/startupBlockers/StartupBlockerProvider";

export function App() {
  const routes = useRoutes();
  const supportedExtensions = useMemo(() => ["bpmn", "bpmn2", "dmn", "pmml"], []);

  return (
    <RouterProvider
      router={createHashRouter(
        createRoutesFromElements(
          <Route element={<AppContexts />}>
            <Route
              path={routes.editor.path({ extension: `:extension` })}
              element={<EditorRouteElement supportedExtensions={supportedExtensions} />}
            />
            <Route
              path={routes.newModel.path({ extension: `:extension` })}
              element={<NewModelRouteElement supportedExtensions={supportedExtensions} />}
            />
            <Route path={routes.import.path({})} element={<NewWorkspaceFromUrlPage />} />
            <Route
              path={routes.workspaceWithFilePath.path({
                workspaceId: ":workspaceId",
                fileRelativePath: `*`,
              })}
              element={<WorkspaceWithFilePathRouteElement supportedExtensions={supportedExtensions} />}
            />
            <Route path={routes.home.path({})} element={<HomePage />} />
            <Route path={"*"} element={<NoMatchPage />} />
          </Route>
        )
      )}
    />
  );
}

function AppContexts() {
  return nest(
    [OnlineI18nContextProvider, {}],
    [EnvContextProvider, {}],
    [StartupBlockerProvider, {}],
    [EditorEnvelopeLocatorContextProvider, {}],
    [SettingsContextProvider, {}],
    [ExtendedServicesContextProvider, {}],
    [AuthSessionsContextProvider, {}],
    [AccountsContextProvider, {}],
    [GlobalAlertsContextProvider, []],
    [WorkspacesContextProviderWithCustomCommitMessagesModal, {}],
    [DmnRunnerPersistenceDispatchContextProvider, {}],
    [DevDeploymentsContextProvider, {}],
    [NavigationContextProvider, {}],
    [PreviewSvgsContextProvider, {}],
    [() => <Outlet />, {}]
  );
}

function EditorRouteElement({ supportedExtensions }: { supportedExtensions: string[] }) {
  const routes = useRoutes();
  const { extension } = useParams();
  const foundExtension = supportedExtensions.find((supportedExtension) => supportedExtension === (extension ?? ""));

  if (foundExtension === undefined) {
    return <Navigate to={routes.home.path({})} replace />;
  }
  return <Navigate to={routes.newModel.path({ extension: foundExtension })} />;
}

function NewModelRouteElement({ supportedExtensions }: { supportedExtensions: string[] }) {
  const routes = useRoutes();
  const { extension } = useParams();
  const foundExtension = supportedExtensions.find((supportedExtension) => supportedExtension === (extension ?? ""));

  if (foundExtension === undefined) {
    return <Navigate to={routes.home.path({})} replace />;
  }
  return <NewWorkspaceWithEmptyFilePage />;
}

function WorkspaceWithFilePathRouteElement({ supportedExtensions }: { supportedExtensions: string[] }) {
  const routes = useRoutes();
  const { "*": fileRelativePath } = useParams();
  // Get the file extension
  const extension = fileRelativePath?.split(".").slice(1).join(".");
  const foundExtension = supportedExtensions.find((supportedExtension) => supportedExtension === (extension ?? ""));
  if (foundExtension === undefined) {
    return <Navigate to={routes.home.path({})} replace />;
  }
  return <EditorPage />;
}

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight(
    (acc, [Component, props]) => {
      return <Component {...props}>{acc}</Component>;
    },
    <></>
  );
}
