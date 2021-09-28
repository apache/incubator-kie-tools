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

import { File } from "@kie-tooling-core/editor/dist/channel";
import * as React from "react";
import { useMemo } from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GlobalContextProvider, SupportedFileExtensions, useGlobals } from "./common/GlobalContext";
import { EditorPage } from "./editor/EditorPage";
import { OnlineI18nContextProvider } from "./common/i18n";
import { HomePage } from "./home/HomePage";
import { DownloadHubModal } from "./home/DownloadHubModal";
import { NoMatchPage } from "./NoMatchPage";
import { KieToolingExtendedServicesContextProvider } from "./editor/KieToolingExtendedServices/KieToolingExtendedServicesContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { WorkspaceContextProvider } from "./workspace/WorkspaceContextProvider";
import { NewHomePage } from "./home/NewHomePage";
import { extractFileExtension } from "./common/utils";
import { useQueryParams } from "./queryParams/QueryParamsContext";
import { QueryParams } from "./common/Routes";
import { WorkspaceOverviewPage } from "./workspace/WorkspaceOverviewPage";

export function App(props: { externalFile?: File; senderTabId?: string }) {
  return (
    <HashRouter>
      {nest(
        [OnlineI18nContextProvider, {}],
        [GlobalContextProvider, props],
        [KieToolingExtendedServicesContextProvider, {}],
        [SettingsContextProvider, {}],
        [WorkspaceContextProvider, {}],
        [RoutesSwitch, {}]
      )}
    </HashRouter>
  );
}

function RoutesSwitch() {
  const globals = useGlobals();
  const queryParams = useQueryParams();

  const supportedExtensions = useMemo(
    () => Array.from(globals.editorEnvelopeLocator.mapping.keys()).join("|"),
    [globals.editorEnvelopeLocator]
  );

  const queryParamUrl = useMemo(() => {
    return queryParams.get(QueryParams.URL);
  }, [queryParams]);

  return (
    <Switch>
      <Route path={globals.routes.editor.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => (
          <EditorPage forExtension={match!.params.extension as SupportedFileExtensions} forWorkspace={false} />
        )}
      </Route>
      <Route
        exact={true}
        path={globals.routes.sketchWithEmptyFile.path({ extension: `:extension(${supportedExtensions})` })}
      >
        {({ match }) => (
          <EditorPage forExtension={match!.params.extension as SupportedFileExtensions} forWorkspace={false} />
        )}
      </Route>
      {queryParamUrl && (
        <Route exact={true} path={globals.routes.sketchWithUrl.path({})}>
          {({ match }) => (
            <EditorPage
              forExtension={extractFileExtension(queryParamUrl) as SupportedFileExtensions}
              forWorkspace={false}
            />
          )}
        </Route>
      )}
      <Route path={globals.routes.newWorkspaceWithEmptyFile.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => (
          <EditorPage forExtension={match!.params.extension as SupportedFileExtensions} forWorkspace={true} />
        )}
      </Route>
      {queryParamUrl && (
        <Route path={globals.routes.newWorkspaceWithUrl.path({})}>
          {({ match }) => (
            <EditorPage
              forExtension={extractFileExtension(queryParamUrl) as SupportedFileExtensions}
              forWorkspace={true}
            />
          )}
        </Route>
      )}
      <Route path={globals.routes.workspaceOverview.path({ workspaceId: ":workspaceId" })}>
        {({ match }) => <WorkspaceOverviewPage workspaceId={match!.params.workspaceId!} />}
      </Route>
      <Route
        path={globals.routes.workspaceWithFilePath.path({
          workspaceId: ":workspaceId",
          filePath: `:filePath*`,
          extension: `:extension(${supportedExtensions})`,
        })}
      >
        {({ match }) => (
          <EditorPage
            forExtension={match!.params.extension as SupportedFileExtensions}
            forWorkspace={true}
            workspaceId={match!.params.workspaceId}
            filePath={`${match!.params.filePath}.${match!.params.extension}`}
          />
        )}
      </Route>
      <Route exact={true} path={"/home-new"}>
        <NewHomePage />
      </Route>
      <Route exact={true} path={globals.routes.home.path({})}>
        <HomePage />
      </Route>
      <Route exact={true} path={globals.routes.download.path({})}>
        <HomePage />
        <DownloadHubModal />
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
