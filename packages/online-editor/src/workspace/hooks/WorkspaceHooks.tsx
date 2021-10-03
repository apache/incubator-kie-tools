import { useWorkspaces } from "../WorkspacesContext";
import { useCallback, useMemo } from "react";
import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useDelayedPromiseState, usePromiseState } from "./PromiseState";
import { Holder, useCancelableEffect } from "../../common/Hooks";

export function useWorkspacePromise(workspaceId: string | undefined) {
  const workspaces = useWorkspaces();
  const [workspacePromise, setWorkspacePromise] = useDelayedPromiseState<ActiveWorkspace>(1000);

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
        setWorkspacePromise({ error: `Can't find Workspace with id ${workspaceId}` });
        return;
      }

      const files = await workspaces.workspaceService.listFiles(descriptor);
      if (canceled.get()) {
        return;
      }

      setWorkspacePromise({ data: { descriptor: descriptor, files } });
    },
    [setWorkspacePromise, workspaceId, workspaces.workspaceService]
  );

  const addEmptyWorkspaceFile = useCallback(
    async (fileExtension: string) => {
      if (!workspacePromise.data) {
        throw new Error("Can't add file while there's no workspace.");
      }
      return await workspaces.addEmptyFile(workspacePromise.data.descriptor.workspaceId, fileExtension);
    },
    [workspacePromise] //TODO: Fix dependency array
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

  return useMemo(() => {
    return { workspacePromise, addEmptyWorkspaceFile };
  }, [workspacePromise, addEmptyWorkspaceFile]);
}

export type WorkspaceEvents =
  | { type: "ADD"; workspaceId: string }
  | { type: "RENAME"; workspaceId: string }
  | { type: "DELETE"; workspaceId: string }
  | { type: "ADD_FILE"; path: string }
  | { type: "MOVE_FILE"; newPath: string; oldPath: string }
  | { type: "RENAME_FILE"; newPath: string; oldPath: string }
  | { type: "UPDATE_FILE"; path: string }
  | { type: "DELETE_FILE"; path: string }
  | { type: "ADD_BATCH"; workspaceId: string; paths: string[] }
  | { type: "MOVE_BATCH"; workspaceId: string; paths: Map<string, string> }
  | { type: "DELETE_BATCH"; workspaceId: string; paths: string[] };
