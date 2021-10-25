import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { usePromiseState } from "./PromiseState";
import { useCallback } from "react";
import { Holder, useCancelableEffect } from "../../common/Hooks";

export function useWorkspacesFiles(workspaceDescriptors: WorkspaceDescriptor[] | undefined) {
  const workspaces = useWorkspaces();
  const [workspacesFilesPromise, setWorkspacesFilesPromise] = usePromiseState<Map<string, WorkspaceFile[]>>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      if (!workspaceDescriptors) {
        return;
      }

      const state = new Map<string, WorkspaceFile[]>(
        await Promise.all(
          workspaceDescriptors.map(async (descriptor) => {
            const files = await workspaces.getFiles({
              fs: await workspaces.fsService.getWorkspaceFs(descriptor.workspaceId),
              workspaceId: descriptor.workspaceId,
            });
            return [descriptor.workspaceId, files] as [string, WorkspaceFile[]];
          })
        )
      );

      if (canceled.get()) {
        return;
      }

      setWorkspacesFilesPromise({ data: state });
    },
    [setWorkspacesFilesPromise, workspaceDescriptors, workspaces]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        refresh(canceled);
      },
      [refresh]
    )
  );

  return workspacesFilesPromise;
}
