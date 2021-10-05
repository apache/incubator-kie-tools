import { useCallback, useMemo } from "react";
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

  const completePath = useMemo(() => {
    if (!pathRelativeToWorkspaceRoot || !workspaceId) {
      return undefined;
    } else {
      return `/${workspaceId}/${pathRelativeToWorkspaceRoot}`;
    }
  }, [workspaceId, pathRelativeToWorkspaceRoot]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!completePath) {
          return;
        }
        refresh(completePath, canceled);
      },
      [refresh, completePath]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!completePath) {
          return;
        }

        console.info("Subscribing to " + completePath);
        const broadcastChannel = new BroadcastChannel(completePath);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          console.info(`WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            refresh(data.newPath, canceled);
          }
          if (data.type === "UPDATE" || data.type === "DELETE" || data.type === "ADD") {
            refresh(data.path, canceled);
          }
        };

        return () => {
          console.info("Unsubscribing to " + completePath);
          broadcastChannel.close();
        };
      },
      [refresh, completePath]
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
