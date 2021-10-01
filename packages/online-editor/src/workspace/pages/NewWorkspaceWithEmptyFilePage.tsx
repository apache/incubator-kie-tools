import { useWorkspaces } from "../WorkspacesContext";
import * as React from "react";
import { useEffect } from "react";
import { useHistory } from "react-router";
import { useGlobals } from "../../common/GlobalContext";

export function NewWorkspaceWithEmptyFilePage(props: { extension: string }) {
  const workspaces = useWorkspaces();
  const history = useHistory();
  const globals = useGlobals();

  useEffect(() => {
    workspaces
      .createWorkspaceFromLocal([
        {
          path: "Untitled." + props.extension,
          getFileContents: () => Promise.resolve(""),
        },
      ])
      .then(({ suggestedFirstFile }) => {
        history.replace({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: suggestedFirstFile!.workspaceId,
            filePath: suggestedFirstFile!.pathRelativeToWorkspaceRootWithoutExtension,
            extension: suggestedFirstFile!.extension,
          }),
        });
      });
  }, []);

  return <></>;
}
