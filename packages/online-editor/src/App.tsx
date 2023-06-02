/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useMemo } from "react";
import { Redirect, Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
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

export function App() {
  return (
    <HashRouter>
      {nest(
        [OnlineI18nContextProvider, {}],
        [EnvContextProvider, {}],
        [EditorEnvelopeLocatorContextProvider, {}],
        [ExtendedServicesContextProvider, {}],
        [SettingsContextProvider, {}],
        [AuthSessionsContextProvider, {}],
        [AccountsContextProvider, {}],
        [GlobalAlertsContextProvider, []],
        [WorkspacesContextProviderWithCustomCommitMessagesModal, {}],
        [DmnRunnerPersistenceDispatchContextProvider, {}],
        [DevDeploymentsContextProvider, {}],
        [NavigationContextProvider, {}],
        [PreviewSvgsContextProvider, {}],
        [RoutesSwitch, {}]
      )}
    </HashRouter>
  );
}

function RoutesSwitch() {
  const routes = useRoutes();
  const supportedExtensions = useMemo(() => "bpmn|bpmn2|dmn|pmml", []);

  return (
    <Switch>
      <Route path={routes.editor.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => <Redirect to={routes.newModel.path({ extension: match!.params.extension! })} />}
      </Route>
      <Route path={routes.newModel.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => <NewWorkspaceWithEmptyFilePage extension={match!.params.extension!} />}
      </Route>
      <Route path={routes.import.path({})}>
        <NewWorkspaceFromUrlPage />
      </Route>
      <Route
        path={routes.workspaceWithFilePath.path({
          workspaceId: ":workspaceId",
          fileRelativePath: `:fileRelativePath*`,
          extension: `:extension(${supportedExtensions})`,
        })}
      >
        {({ match }) => (
          <EditorPage
            workspaceId={match!.params.workspaceId!}
            fileRelativePath={`${match!.params.fileRelativePath}.${match!.params.extension}`}
          />
        )}
      </Route>
      <Route exact={true} path={routes.home.path({})}>
        <HomePage />
      </Route>
      <Route component={NoMatchPage} />
    </Switch>
  );
}

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight((acc, [Component, props]) => {
    return <Component {...props}>{acc}</Component>;
  }, <></>);
}
