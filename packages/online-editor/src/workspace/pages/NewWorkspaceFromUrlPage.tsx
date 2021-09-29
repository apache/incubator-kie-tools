import { useWorkspaces } from "../WorkspaceContext";
import { useGlobals } from "../../common/GlobalContext";
import { useHistory } from "react-router";
import * as React from "react";
import { useEffect } from "react";

export function NewWorkspaceFromUrlPage(props: { url: string | undefined }) {
  const workspaces = useWorkspaces();
  const globals = useGlobals();
  const history = useHistory();

  useEffect(() => {
    if (!props.url) {
      console.info("No URL provided!");
      return;
    }

    workspaces.createWorkspaceFromGitHubRepository(new URL(props.url), "main").then((descriptor) => {
      history.replace({
        pathname: globals.routes.workspaceOverview.path({ workspaceId: descriptor.workspaceId }),
      });
    });
  }, []);

  return <></>;
}
