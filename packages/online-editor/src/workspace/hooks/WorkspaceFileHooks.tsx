import { useCallback } from "react";
import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { Holder, useCancelableEffect } from "../../common/Hooks";
import { usePromiseState } from "./PromiseState";
import { WorkspaceEvents } from "./WorkspaceHooks";

export function useWorkspaceFilePromise(workspaceId: string | undefined, relativePath: string | undefined) {
  const workspaces = useWorkspaces();
  const [workspaceFilePromise, setWorkspaceFilePromise] = usePromiseState<WorkspaceFile>();

  const refresh = useCallback(
    async (workspaceId: string, relativePath: string, canceled: Holder<boolean>) => {
      workspaces
        .getFile({ fs: await workspaces.fsService.getWorkspaceFs(workspaceId), workspaceId, relativePath })
        .then((workspaceFile) => {
          if (canceled.get()) {
            return;
          }

          if (!workspaceFile) {
            setWorkspaceFilePromise({
              error: `File '${relativePath}' not found in Workspace '${workspaceId}'`,
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
        if (!relativePath || !workspaceId) {
          return;
        }
        refresh(workspaceId, relativePath, canceled);
      },
      [relativePath, workspaceId, refresh]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }

        const uniqueFileIdentifier = workspaces.getUniqueFileIdentifier({ workspaceId, relativePath });

        console.info("Subscribing to " + uniqueFileIdentifier);
        const broadcastChannel = new BroadcastChannel(uniqueFileIdentifier);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          console.info(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            refresh(workspaceId, data.newRelativePath, canceled);
          }
          if (data.type === "UPDATE" || data.type === "DELETE" || data.type === "ADD") {
            refresh(workspaceId, data.relativePath, canceled);
          }
        };

        console.info("Subscribing to " + workspaceId);
        const broadcastChannel2 = new BroadcastChannel(workspaceId);
        broadcastChannel2.onmessage = ({ data }: MessageEvent<WorkspaceEvents>) => {
          console.info(`EVENT::WORKSPACE: ${JSON.stringify(data)}`);
          if (data.type === "PULL") {
            refresh(workspaceId, relativePath, canceled);
          }
        };

        return () => {
          console.info("Unsubscribing to " + uniqueFileIdentifier);
          broadcastChannel.close();
        };
      },
      [relativePath, workspaceId, workspaces, refresh]
    )
  );

  return workspaceFilePromise;
}

export type WorkspaceFileEvents =
  | { type: "MOVE"; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME"; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE"; relativePath: string }
  | { type: "DELETE"; relativePath: string }
  | { type: "ADD"; relativePath: string };
