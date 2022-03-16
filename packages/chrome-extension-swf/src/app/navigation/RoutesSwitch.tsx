import * as React from "react";
import { Route, Switch } from "react-router-dom";
import { ServerlessWorkflowEditor } from "../components/Editor/Editor";
import { ServerlessWorkflowList } from "../components/List/ServerlessWorkflowList";
import { SW_JSON_EXTENSION } from "../openshift/OpenShiftContext";
import { NewWorkspaceWithEmptyFilePage } from "../workspace/components/NewWorkspaceWithEmptyFilePage";
import { useRoutes } from "./Hooks";

export function RoutesSwitch() {
  const routes = useRoutes();

  return (
    <Switch>
      <Route exact={true} path={routes.home.path({})}>
        <ServerlessWorkflowList />
      </Route>
      <Route exact={true} path={routes.newWorskapce.path({})}>
        <NewWorkspaceWithEmptyFilePage />
      </Route>
      <Route
        path={routes.workspaceWithFilePath.path({
          workspaceId: ":workspaceId",
          fileRelativePath: `:fileRelativePath*`,
          extension: SW_JSON_EXTENSION,
        })}
      >
        {({ match }) => (
          <ServerlessWorkflowEditor
            workspaceId={match!.params.workspaceId!}
            fileRelativePath={`${match!.params.fileRelativePath}.${match!.params.extension}`}
          />
        )}
      </Route>
      {/* <Route path={routes.importModel.path({})}>
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
      <Route component={NoMatchPage} /> */}
    </Switch>
  );
}
