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
    (workspaceId: string, pathRelativeToWorkspaceRoot: string, canceled: Holder<boolean>) => {
      workspaces.workspaceService.getFile({ workspaceId, pathRelativeToWorkspaceRoot }).then((workspaceFile) => {
        if (canceled.get()) {
          return;
        }

        if (!workspaceFile) {
          setWorkspaceFilePromise({
            error: `File '${pathRelativeToWorkspaceRoot}' not found in Workspace '${workspaceId}'`,
          });
          return;
        }

        setWorkspaceFilePromise({ data: workspaceFile });
      });
    },
    [workspaces, setWorkspaceFilePromise]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!pathRelativeToWorkspaceRoot || !workspaceId) {
          return;
        }
        refresh(workspaceId, pathRelativeToWorkspaceRoot, canceled);
      },
      [pathRelativeToWorkspaceRoot, workspaceId, refresh]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!pathRelativeToWorkspaceRoot || !workspaceId) {
          return;
        }

        const absolutePath = workspaces.getAbsolutePath({ workspaceId, pathRelativeToWorkspaceRoot });

        console.info("Subscribing to " + absolutePath);
        const broadcastChannel = new BroadcastChannel(absolutePath);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          console.info(`WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            refresh(workspaceId, data.newPathRelativeToWorkspaceRoot, canceled);
          }
          if (data.type === "UPDATE" || data.type === "DELETE" || data.type === "ADD") {
            refresh(workspaceId, data.pathRelativeToWorkspaceRoot, canceled);
          }
        };

        return () => {
          console.info("Unsubscribing to " + absolutePath);
          broadcastChannel.close();
        };
      },
      [pathRelativeToWorkspaceRoot, workspaceId, workspaces, refresh]
    )
  );

  return workspaceFilePromise;
}

export type WorkspaceFileEvents =
  | { type: "MOVE"; newPathRelativeToWorkspaceRoot: string; oldPathRelativeToWorkspaceRoot: string }
  | { type: "RENAME"; newPathRelativeToWorkspaceRoot: string; oldPathRelativeToWorkspaceRoot: string }
  | { type: "UPDATE"; pathRelativeToWorkspaceRoot: string }
  | { type: "DELETE"; pathRelativeToWorkspaceRoot: string }
  | { type: "ADD"; pathRelativeToWorkspaceRoot: string };
