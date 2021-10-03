import { useWorkspaces } from "../WorkspacesContext";
import { useCallback } from "react";
import { WorkspaceOverview } from "../model/WorkspaceOverview";
import { usePromiseState } from "./PromiseState";
import { Holder, useCancelableEffect } from "../../common/Hooks";

export function useWorkspaceOverviewsPromise() {
  const workspaces = useWorkspaces();
  const [workspaceOverviewsPromise, setWorkspaceOverviewsPromise] = usePromiseState<WorkspaceOverview[]>();

  const refresh = useCallback((canceled: Holder<boolean>) => {
    workspaces
      .listWorkspaceOverviews()
      .then((workspaceOverviews) => {
        if (!canceled.get()) {
          setWorkspaceOverviewsPromise({ data: workspaceOverviews });
        }
      })
      .catch((error) => {
        if (!canceled.get()) {
          setWorkspaceOverviewsPromise({ error });
        }
      });
  }, []); //TODO: fix this dependency array

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
        const broadcastChannel = new BroadcastChannel(workspaces.workspaceService.storageService.rootPath);
        broadcastChannel.onmessage = ({ data }) => {
          console.info(`WORKSPACES: ${JSON.stringify(data)}`);
          refresh(canceled);
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [workspaces.workspaceService, refresh]
    )
  );

  return workspaceOverviewsPromise;
}

export type WorkspacesEvents =
  | { type: "DELETE_ALL" }
  | { type: "ADD_WORKSPACE"; workspaceId: string }
  | { type: "RENAME_WORKSPACE"; workspaceId: string }
  | { type: "DELETE_WORKSPACE"; workspaceId: string };
