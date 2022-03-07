import * as React from "react";
import { Route, Switch } from "react-router-dom";
import { CreateServerlessWorkflowPage } from "../components/ServerlessWorkflowMenu/CreateServerlessWorkflowPage";
import { useRoutes } from "./Hooks";

// TODO: CREATE A CONTEXT WITH EVERYTHING FROM ServerlessWorkflowManagementPage

function RoutesSwitch() {
  const routes = useRoutes();

  return (
    <Switch>
      <Route path={routes.newSwf.path({})}>{() => <CreateServerlessWorkflowPage />}</Route>
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
