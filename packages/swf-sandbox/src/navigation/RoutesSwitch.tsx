import * as React from "react";
import { useMemo } from "react";
import { Route, Switch } from "react-router-dom";
import { EditorPage } from "../editor/EditorPage";
import { HomePage } from "../home/HomePage";
import { NewWorkspaceFromUrlPage } from "../workspace/components/NewWorkspaceFromUrlPage";
import { NewWorkspaceWithEmptyFilePage } from "../workspace/components/NewWorkspaceWithEmptyFilePage";
import { useRoutes } from "./Hooks";
import { NoMatchPage } from "./NoMatchPage";

export function RoutesSwitch() {
  const routes = useRoutes();
  const supportedExtensions = useMemo(
    () => "sw.json|sw.yaml|sw.yml|yard.json|yard.yaml|yard.yml|dash.yaml|dash.yml",
    []
  );

  return (
    <Switch>
      <Route path={routes.newModel.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => <NewWorkspaceWithEmptyFilePage extension={match!.params.extension!} />}
      </Route>
      <Route path={routes.importModel.path({})}>
        <NewWorkspaceFromUrlPage />
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
      <Route exact={true} path={routes.home.path({})}>
        <HomePage />
      </Route>
      <Route component={NoMatchPage} />
    </Switch>
  );
}
