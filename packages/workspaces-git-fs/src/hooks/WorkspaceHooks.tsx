/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { UnstagedModifiedFilesStatusEntryType } from "../services/GitService";

export type WorkspaceGitStatusType = {
  hasLocalChanges: boolean;
  unstagedModifiedFilesStatus: UnstagedModifiedFilesStatusEntryType[];
  isSynced: boolean;
};

export function useWorkspaceGitStatusPromise(workspaceDescriptor: WorkspaceDescriptor | undefined) {
  const workspaces = useWorkspaces();
  const [isModifiedPromise, setModifiedPromise] = usePromiseState<WorkspaceGitStatusType>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      setModifiedPromise({ loading: true });

      if (!workspaceDescriptor) {
        return;
      }

      const unstagedModifiedFilesStatus = await workspaces.getUnstagedModifiedFilesStatus({
        workspaceId: workspaceDescriptor.workspaceId,
      });

      const hasLocalChanges = unstagedModifiedFilesStatus.length > 0;
      if (canceled.get()) {
        return;
      }

      if (workspaceDescriptor.origin.kind === WorkspaceKind.LOCAL) {
        setModifiedPromise({ data: { hasLocalChanges, unstagedModifiedFilesStatus, isSynced: true } });
        return;
      }

      if (isGitBasedWorkspaceKind(workspaceDescriptor.origin.kind)) {
        const head = await workspaces.resolveRef({
          workspaceId: workspaceDescriptor.workspaceId,
          ref: "HEAD",
        });

        const remote = await workspaces.resolveRef({
          workspaceId: workspaceDescriptor.workspaceId,
          ref: `${GIT_ORIGIN_REMOTE_NAME}/${workspaceDescriptor.origin.branch}`,
        });

        if (canceled.get()) {
          return;
        }

        setModifiedPromise({
          data: { hasLocalChanges, unstagedModifiedFilesStatus, isSynced: head === remote },
        });
      }
    },
    [setModifiedPromise, workspaceDescriptor, workspaces]
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
