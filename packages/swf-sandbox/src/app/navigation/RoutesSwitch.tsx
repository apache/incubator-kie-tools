import * as React from "react";
import { Route, Switch } from "react-router-dom";
import { ServerlessWorkflowEditor } from "../components/Editor/Editor";
import { ServerlessWorkflowList } from "../components/List/ServerlessWorkflowList";
import { NewWorkspaceWithEmptyFilePage } from "../workspace/components/NewWorkspaceWithEmptyFilePage";
import { useRoutes } from "./Hooks";
import { NoMatchPage } from "./NoMatchPage";

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
        })}
      >
        {({ match }) => (
          <ServerlessWorkflowEditor
            workspaceId={match!.params.workspaceId!}
            fileRelativePath={`${match!.params.fileRelativePath}`}
          />
        )}
      </Route>
      <Route component={NoMatchPage} />
    </Switch>
  );
}
