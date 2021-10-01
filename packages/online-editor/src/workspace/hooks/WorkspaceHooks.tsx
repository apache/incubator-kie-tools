import { useWorkspaces } from "../WorkspacesContext";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ActiveWorkspace } from "../model/ActiveWorkspace";

export function useWorkspace(workspaceId: string | undefined) {
  const workspaces = useWorkspaces();
  const [workspace, setWorkspace] = useState<ActiveWorkspace>();

  const refreshFiles = useCallback(async () => {
    if (!workspaceId) {
      return;
    }

    const descriptor = await workspaces.workspaceService.get(workspaceId);
    if (!descriptor) {
      //TODO: what to do?
      return;
    }
    const files = await workspaces.workspaceService.listFiles(descriptor);
    setWorkspace({ descriptor: descriptor, files });
  }, [workspaceId, workspaces.workspaceService]);

  const addEmptyWorkspaceFile = useCallback(
    async (fileExtension: string) => {
      if (!workspace) {
        throw new Error("Can't add file while there's no workspace.");
      }
      return await workspaces.addEmptyFile(workspace.descriptor.workspaceId, fileExtension);
    },
    [workspace]
  );

  useEffect(() => {
    refreshFiles();
  }, [refreshFiles]);

  useEffect(() => {
    if (!workspaceId) {
      return;
    }

    const broadcastChannel = new BroadcastChannel(workspaceId);
    broadcastChannel.onmessage = ({ data }) => {
      console.info(`WORKSPACE: ${JSON.stringify(data)}`);
      return refreshFiles();
    };

    return () => {
      broadcastChannel.close();
    };
  }, [workspaceId, refreshFiles]);

  return useMemo(() => {
    return { workspace, addEmptyWorkspaceFile };
  }, [workspace, addEmptyWorkspaceFile]);
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
