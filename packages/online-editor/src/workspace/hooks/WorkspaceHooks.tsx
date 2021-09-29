import { useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useGlobals } from "../../common/GlobalContext";
import { SUPPORTED_FILES_EDITABLE_PATTERN } from "../SupportedFiles";
import { AddFileEvent, ChannelKind, MoveFileEvent, UpdateFileEvent } from "../model/Event";

export function useWorkspace(workspaceId: string | undefined) {
  const workspaces = useWorkspaces();
  const globals = useGlobals();
  const history = useHistory();

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

  useEffect(() => {
    refreshFiles();
  }, [refreshFiles]);

  useEffect(() => {
    const updateFiles = async (targetFilePath: string) => {
      if (!workspace) {
        return;
      }

      const descriptor = await workspaces.workspaceService.getByFilePath(targetFilePath);
      if (descriptor.workspaceId !== workspace.descriptor.workspaceId) {
        return;
      }

      const updatedFiles = await workspaces.workspaceService.listFiles(descriptor, SUPPORTED_FILES_EDITABLE_PATTERN);
      setWorkspace({ ...workspace, files: updatedFiles });
    };

    workspaces.workspaceService.broadcastService.onEvent<AddFileEvent>(
      ChannelKind.ADD_FILE,
      async (event: AddFileEvent) => {
        console.debug(`[Workspace] ADD: ${event.path}`);
        await updateFiles(event.path);
      }
    );

    workspaces.workspaceService.broadcastService.onEvent<UpdateFileEvent>(
      ChannelKind.UPDATE_FILE,
      async (event: UpdateFileEvent) => {
        console.debug(`[Workspace] UPDATE: ${event.path}`);
        await updateFiles(event.path);
      }
    );

    //FIXME: This is overriding the call on WorkspaceFileContext.tsx
    workspaces.workspaceService.broadcastService.onEvent<MoveFileEvent>(
      ChannelKind.MOVE_FILE,
      async (event: MoveFileEvent) => {
        console.debug(`[Workspace] MOVE: ${event.path}`);
        await updateFiles(event.path);
      }
    );
  }, [workspaces, workspace, history, globals]);

  const renameWorkspaceFile = useCallback(
    async (file: WorkspaceFile, newName: string) => {
      const renamedFile = await workspaces.renameFile(file, newName);
      await refreshFiles();
      return renamedFile;
    },
    [refreshFiles]
  );

  const addEmptyWorkspaceFile = useCallback(
    async (fileExtension: string) => {
      if (!workspace) {
        throw new Error("Can't add file while there's no workspace.");
      }
      const newFile = await workspaces.addEmptyFile(workspace.descriptor.workspaceId, fileExtension);
      await refreshFiles();
      return newFile;
    },
    [refreshFiles, workspace]
  );

  return useMemo(
    () => ({ workspace, renameWorkspaceFile, addEmptyWorkspaceFile }),
    [workspace, renameWorkspaceFile, addEmptyWorkspaceFile]
  );
}
