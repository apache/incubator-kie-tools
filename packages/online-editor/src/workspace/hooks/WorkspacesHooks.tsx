import { useWorkspaces } from "../WorkspacesContext";
import { useCallback } from "react";
import { usePromiseState } from "./PromiseState";
import { Holder, useCancelableEffect } from "../../common/Hooks";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";

export function useWorkspaceDescriptorsPromise() {
  const workspaces = useWorkspaces();
  const [workspaceDescriptorsPromise, setWorkspaceDescriptorsPromise] = usePromiseState<WorkspaceDescriptor[]>();

  const refresh = useCallback(
    (canceled: Holder<boolean>) => {
      workspaces.workspaceService
        .list()
        .then((descriptors) => {
          if (!canceled.get()) {
            setWorkspaceDescriptorsPromise({
              data: descriptors,
            });
          }
        })
        .catch((error) => {
          if (!canceled.get()) {
            setWorkspaceDescriptorsPromise({ error });
          }
        });
    },
    [setWorkspaceDescriptorsPromise, workspaces]
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
        const broadcastChannel = new BroadcastChannel(workspaces.workspaceService.rootPath);
        broadcastChannel.onmessage = ({ data }) => {
          console.info(`WORKSPACES: ${JSON.stringify(data)}`);
          refresh(canceled);
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [workspaces, refresh]
    )
  );

  return workspaceDescriptorsPromise;
}

export type WorkspacesEvents =
  | { type: "DELETE_ALL" }
  | { type: "ADD_WORKSPACE"; workspaceId: string }
  | { type: "RENAME_WORKSPACE"; workspaceId: string }
  | { type: "DELETE_WORKSPACE"; workspaceId: string };
