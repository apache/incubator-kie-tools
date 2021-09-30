import { useEffect, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";

export function useWorkspaceFile(workspaceId: string | undefined, pathRelativeToWorkspaceRoot: string | undefined) {
  const workspaces = useWorkspaces();
  const [file, setFile] = useState<WorkspaceFile>();

  useEffect(() => {
    if (!pathRelativeToWorkspaceRoot || !workspaceId) {
      return;
    }

    let canceled = false;
    workspaces.workspaceService.storageService
      .getFile(`/${workspaceId}/${pathRelativeToWorkspaceRoot}`)
      .then((workspaceFile) => {
        if (canceled) {
          return;
        }

        if (!workspaceFile) {
          console.error(`File '${pathRelativeToWorkspaceRoot}' not found on workspace '${workspaceId}'`); //TODO indicate error in some way?
          return;
        }

        setFile(workspaceFile);
      });

    return () => {
      canceled = true;
    };
  }, [pathRelativeToWorkspaceRoot, workspaceId, workspaces.workspaceService]);

  useEffect(() => {
    if (!file) {
      return;
    }

    let canceled = false;
    const broadcastChannel = new BroadcastChannel(file.path);
    broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
      console.info(`WORKSPACE_FILE: ${JSON.stringify(data)}`);
      if (data.type === "UPDATE") {
        workspaces.workspaceService.storageService.getFile(data.path).then((workspaceFile) => {
          if (canceled) {
            return;
          }

          if (!workspaceFile) {
            console.error(`File '${data.path}' not found`); //TODO indicate error in some way?
            return;
          }

          setFile(workspaceFile);
        });
      }
      if (data.type === "MOVE" || data.type == "RENAME") {
        workspaces.workspaceService.storageService.getFile(data.newPath).then((workspaceFile) => {
          if (canceled) {
            return;
          }

          if (!workspaceFile) {
            console.error(`File '${data.newPath}' not found`); //TODO indicate error in some way?
            return;
          }

          setFile(workspaceFile);
        });
      }
    };

    return () => {
      canceled = true;
      broadcastChannel.close();
    };
  }, [file, workspaces.workspaceService]);

  return file;
}

export type WorkspaceFileEvents =
  | { type: "MOVE"; newPath: string; oldPath: string }
  | { type: "RENAME"; newPath: string; oldPath: string }
  | { type: "UPDATE"; path: string }
  | { type: "DELETE"; path: string }
  | { type: "ADD"; path: string };
