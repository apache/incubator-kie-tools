/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useWorkspaces } from "../context/WorkspacesContext";
import { useCallback } from "react";
import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { isGitBasedWorkspaceKind, WorkspaceKind } from "../worker/api/WorkspaceOrigin";
import { GIT_ORIGIN_REMOTE_NAME } from "../constants/GitConstants";
import { WorkspaceBroadcastEvents } from "../worker/api/WorkspaceBroadcastEvents";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";

export function useWorkspaceGitStatusPromise(workspace: ActiveWorkspace | undefined) {
  const workspaces = useWorkspaces();
  const [isModifiedPromise, setModifiedPromise] = usePromiseState<{ hasLocalChanges: boolean; isSynced: boolean }>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      setModifiedPromise({ loading: true });

      if (!workspace) {
        return;
      }

      const hasLocalChanges = await workspaces.hasLocalChanges({ workspaceId: workspace.descriptor.workspaceId });
      if (canceled.get()) {
        return;
      }

      if (workspace.descriptor.origin.kind === WorkspaceKind.LOCAL) {
        setModifiedPromise({ data: { hasLocalChanges, isSynced: true } });
        return;
      }

      if (isGitBasedWorkspaceKind(workspace.descriptor.origin.kind)) {
        const head = await workspaces.resolveRef({
          workspaceId: workspace.descriptor.workspaceId,
          ref: "HEAD",
        });

        const remote = await workspaces.resolveRef({
          workspaceId: workspace.descriptor.workspaceId,
          ref: `${GIT_ORIGIN_REMOTE_NAME}/${workspace.descriptor.origin.branch}`,
        });

        if (canceled.get()) {
          return;
        }

        setModifiedPromise({ data: { hasLocalChanges, isSynced: head === remote } });
      }
    },
    [workspace, workspaces, setModifiedPromise]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        refresh(canceled);
      },
      [refresh]
    )
  );

  return isModifiedPromise;
}

export function useWorkspacePromise(workspaceId: string | undefined) {
  const workspaces = useWorkspaces();
  const [workspacePromise, setWorkspacePromise] = usePromiseState<ActiveWorkspace>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      if (!workspaceId) {
        return;
      }

      try {
        const descriptor = await workspaces.getWorkspace({ workspaceId });
        if (canceled.get()) {
          return;
        }

        if (!descriptor) {
          setWorkspacePromise({ error: `Can't find Workspace with id '${workspaceId}'` });
          return;
        }

        const files = await workspaces.getFiles({
          workspaceId,
        });
        if (canceled.get()) {
          return;
        }

        setWorkspacePromise({ data: { descriptor, files } });
      } catch (error) {
        setWorkspacePromise({ error: `Can't find Workspace with id '${workspaceId}'` });
        console.error(error);
        return;
      }
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
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceBroadcastEvents>) => {
          console.debug(`EVENT::WORKSPACE: ${JSON.stringify(data)}`);
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
