import { useWorkspaces } from "../WorkspacesContext";
import { useCallback } from "react";
import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { usePromiseState } from "./PromiseState";
import { Holder, useCancelableEffect } from "../../common/Hooks";

export function useWorkspacePromise(workspaceId: string | undefined) {
  const workspaces = useWorkspaces();
  const [workspacePromise, setWorkspacePromise] = usePromiseState<ActiveWorkspace>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      if (!workspaceId) {
        return;
      }

      const descriptor = await workspaces.workspaceService.get(workspaceId);
      if (canceled.get()) {
        return;
      }

      if (!descriptor) {
        setWorkspacePromise({ error: `Can't find Workspace with id '${workspaceId}'` });
        return;
      }

      const files = await workspaces.listFiles(workspaceId);

      if (canceled.get()) {
        return;
      }

      setWorkspacePromise({ data: { descriptor, files, isModified: await workspaces.isModified(workspaceId) } });
    },
    [setWorkspacePromise, workspaceId, workspaces]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        refresh(canceled);
      },
      [refresh]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceId) {
          return;
        }

        const broadcastChannel = new BroadcastChannel(workspaceId);
        broadcastChannel.onmessage = ({ data }) => {
          console.info(`WORKSPACE: ${JSON.stringify(data)}`);
          return refresh(canceled);
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [workspaceId, refresh]
    )
  );

  return workspacePromise;
}

export type WorkspaceEvents =
  | { type: "ADD"; workspaceId: string }
  | { type: "CREATE_SAVE_POINT"; workspaceId: string }
  | { type: "RENAME"; workspaceId: string }
  | { type: "DELETE"; workspaceId: string }
  | { type: "ADD_FILE"; relativePath: string }
  | { type: "MOVE_FILE"; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME_FILE"; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE_FILE"; relativePath: string }
  | { type: "DELETE_FILE"; relativePath: string }
  | { type: "ADD_BATCH"; workspaceId: string; relativePaths: string[] }
  | { type: "MOVE_BATCH"; workspaceId: string; relativePaths: Map<string, string> }
  | { type: "DELETE_BATCH"; workspaceId: string; relativePaths: string[] };
