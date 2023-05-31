/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useMemo } from "react";
import { Switch } from "react-router";
import { Overview } from "../overView/Overview";
import { RecentModels } from "../recentModels/RecentModels";
import { Route } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { supportedFileExtensionArray } from "../../extension";
import { NewWorkspaceFromSample } from "../../workspace/components/NewWorkspaceFromSample";
import { NewWorkspaceFromUrlPage } from "../../workspace/components/NewWorkspaceFromUrlPage";
import { NewWorkspaceWithEmptyFilePage } from "../../workspace/components/NewWorkspaceWithEmptyFilePage";
import { EditorPage } from "../../editor/EditorPage";
import { NoMatchPage } from "../../navigation/NoMatchPage";
import { SamplesCatalog } from "../../samples/SamplesCatalog";
import { WorkspaceFiles } from "../recentModels/workspaceFiles/WorkspaceFiles";

export function HomePageRoutes(props: { isNavOpen: boolean }) {
  const routes = useRoutes();
  const supportedExtensions = useMemo(() => supportedFileExtensionArray.join("|"), []);
  return (
    <Switch>
      <Route path={routes.newModel.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => <NewWorkspaceWithEmptyFilePage extension={match!.params.extension!} />}
      </Route>
      <Route path={routes.importModel.path({})}>
        <NewWorkspaceFromUrlPage />
      </Route>
      <Route path={routes.sampleShowcase.path({})}>
        <NewWorkspaceFromSample />
      </Route>
      <Route
        path={routes.workspaceWithFilePath.path({
          workspaceId: ":workspaceId",
          fileRelativePath: `:fileRelativePath*`,
          extension: `:extension?`,
        })}
      >
        {({ match }) => (
          <EditorPage
            workspaceId={match!.params.workspaceId!}
            fileRelativePath={`${match!.params.fileRelativePath ?? ""}${
              match!.params.extension ? `.${match!.params.extension}` : ""
            }`}
          />
        )}
      </Route>
      <Route path={routes.home.path({})} exact>
        <Overview isNavOpen={props.isNavOpen} />
      </Route>
      <Route path={routes.recentModels.path({})}>
        <RecentModels />
      </Route>
      <Route path={routes.workspaceWithFiles.path({ workspaceId: ":workspaceId" })}>
        {({ match }) => <WorkspaceFiles workspaceId={match!.params.workspaceId!} />}
      </Route>
      <Route path={routes.sampleCatalog.path({})}>
        <SamplesCatalog />
      </Route>
      <Route component={NoMatchPage} />
    </Switch>
  );
}
