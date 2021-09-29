import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useEffect, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { useHistory } from "react-router";
import { useGlobals } from "../../common/GlobalContext";
import { ChannelKind, MoveFileEvent } from "../model/Event";

export function useWorkspaceFile(workspace: ActiveWorkspace | undefined, path: string | undefined) {
  const history = useHistory();
  const globals = useGlobals();
  const workspaces = useWorkspaces();

  const [file, setFile] = useState<WorkspaceFile>();

  useEffect(() => {
    if (!path) {
      return;
    }

    const workspaceFile = workspace?.files.filter((f) => f.pathRelativeToWorkspaceRoot === path).pop();
    if (!workspaceFile) {
      return;
    }

    setFile(workspaceFile);
  }, [path, workspace]);

  useEffect(() => {
    if (!file) {
      return;
    }

    //FIXME: This is overriding the call on WorkspaceContext.tsx
    workspaces.workspaceService.broadcastService.onEvent<MoveFileEvent>(
      ChannelKind.MOVE_FILE,
      async (event: MoveFileEvent) => {
        const newFile = await workspaces.workspaceService.storageService.getFile(event.newPath);
        if (!newFile) {
          throw new Error(`File ${event.path} not found`);
        }

        if (file.path !== event.path) {
          return;
        }

        console.debug(`[WorkspaceFile] MOVE: ${newFile.path}`);
        history.replace({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: newFile.workspaceId,
            filePath: newFile.pathRelativeToWorkspaceRootWithoutExtension,
            extension: newFile.extension,
          }),
        });
      }
    );
  }, [file, history, globals, workspaces]);

  return file;
}
