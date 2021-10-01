import { useWorkspaces } from "../WorkspacesContext";
import { useEffect, useState } from "react";
import { WorkspaceOverview } from "../model/WorkspaceOverview";

export function useWorkspaceOverviews() {
  const workspaces = useWorkspaces();
  const [workspaceOverviews, setWorkspaceOverviews] = useState<WorkspaceOverview[]>([]);
  useEffect(() => {
    workspaces.listWorkspaceOverviews().then(setWorkspaceOverviews);
  }, []);

  useEffect(() => {
    const broadcastChannel = new BroadcastChannel(workspaces.workspaceService.storageService.rootPath);
    broadcastChannel.onmessage = ({ data }) => {
      console.info(`WORKSPACES: ${JSON.stringify(data)}`);
      workspaces.listWorkspaceOverviews().then(setWorkspaceOverviews);
    };

    return () => {
      broadcastChannel.close();
    };
  }, []);

  return workspaceOverviews;
}

export type WorkspacesEvents =
  | { type: "DELETE_ALL" }
  | { type: "ADD_WORKSPACE"; workspaceId: string }
  | { type: "RENAME_WORKSPACE"; workspaceId: string }
  | { type: "DELETE_WORKSPACE"; workspaceId: string };
