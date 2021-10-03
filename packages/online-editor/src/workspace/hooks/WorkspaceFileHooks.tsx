import { useCallback } from "react";
import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { Holder, useCancelableEffect } from "../../common/Hooks";
import { usePromiseState } from "./PromiseState";

export function useWorkspaceFilePromise(
  workspaceId: string | undefined,
  pathRelativeToWorkspaceRoot: string | undefined
) {
  const workspaces = useWorkspaces();
  const [workspaceFilePromise, setWorkspaceFilePromise] = usePromiseState<WorkspaceFile>();

  const refresh = useCallback(
    (path: string, canceled: Holder<boolean>) => {
      workspaces.workspaceService.storageService.getFile(path).then((workspaceFile) => {
        if (canceled.get()) {
          return;
        }

        if (!workspaceFile) {
          setWorkspaceFilePromise({ error: `File '${path}' not found` });
          return;
        }

        setWorkspaceFilePromise({ data: workspaceFile });
      });
    },
    [workspaces.workspaceService, setWorkspaceFilePromise]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!pathRelativeToWorkspaceRoot || !workspaceId) {
          return;
        }

        refresh(`/${workspaceId}/${pathRelativeToWorkspaceRoot}`, canceled);
      },
      [pathRelativeToWorkspaceRoot, workspaceId, refresh]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFilePromise.data) {
          return;
        }

        const broadcastChannel = new BroadcastChannel(workspaceFilePromise.data.path);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          console.info(`WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "UPDATE") {
            refresh(data.path, canceled);
          }
          if (data.type === "MOVE" || data.type == "RENAME") {
            refresh(data.newPath, canceled);
          }
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [workspaceFilePromise, refresh]
    )
  );

  return workspaceFilePromise;
}

export type WorkspaceFileEvents =
  | { type: "MOVE"; newPath: string; oldPath: string }
  | { type: "RENAME"; newPath: string; oldPath: string }
  | { type: "UPDATE"; path: string }
  | { type: "DELETE"; path: string }
  | { type: "ADD"; path: string };
