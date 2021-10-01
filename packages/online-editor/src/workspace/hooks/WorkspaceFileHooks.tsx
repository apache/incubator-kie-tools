import { useCallback, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { useCancelableEffect } from "../../common/Hooks";

export function useWorkspaceFile(workspaceId: string | undefined, pathRelativeToWorkspaceRoot: string | undefined) {
  const workspaces = useWorkspaces();
  const [file, setFile] = useState<WorkspaceFile>();

  useCancelableEffect(
    useCallback(
      ({ ifNotCanceled }) => {
        if (!pathRelativeToWorkspaceRoot || !workspaceId) {
          return;
        }

        workspaces.workspaceService.storageService.getFile(`/${workspaceId}/${pathRelativeToWorkspaceRoot}`).then(
          ifNotCanceled.run((workspaceFile) => {
            if (!workspaceFile) {
              console.error(`File '${pathRelativeToWorkspaceRoot}' not found on workspace '${workspaceId}'`); //TODO indicate error in some way?
              return;
            }

            setFile(workspaceFile);
          })
        );
      },
      [pathRelativeToWorkspaceRoot, workspaceId, workspaces.workspaceService]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ ifNotCanceled }) => {
        if (!file) {
          return;
        }

        const broadcastChannel = new BroadcastChannel(file.path);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          console.info(`WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "UPDATE") {
            workspaces.workspaceService.storageService.getFile(data.path).then(
              ifNotCanceled.run((workspaceFile) => {
                if (!workspaceFile) {
                  console.error(`File '${data.path}' not found`); //TODO indicate error in some way?
                  return;
                }

                setFile(workspaceFile);
              })
            );
          }
          if (data.type === "MOVE" || data.type == "RENAME") {
            workspaces.workspaceService.storageService.getFile(data.newPath).then(
              ifNotCanceled.run((workspaceFile) => {
                if (!workspaceFile) {
                  console.error(`File '${data.newPath}' not found`); //TODO indicate error in some way?
                  return;
                }

                setFile(workspaceFile);
              })
            );
          }
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [file, workspaces.workspaceService]
    )
  );

  return file;
}

export type WorkspaceFileEvents =
  | { type: "MOVE"; newPath: string; oldPath: string }
  | { type: "RENAME"; newPath: string; oldPath: string }
  | { type: "UPDATE"; path: string }
  | { type: "DELETE"; path: string }
  | { type: "ADD"; path: string };
