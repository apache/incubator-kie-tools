/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { WORKSPACES_BROADCAST_CHANNEL } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspacesBroadcastEvents";
import { useCallback } from "react";

export function useActiveWorkspacesPromise(workspaceIds: WorkspaceDescriptor["workspaceId"][]) {
  const workspaces = useWorkspaces();
  const [activeWorkspacesPromise, setActiveWorkspacesPromise] = usePromiseState<ActiveWorkspace[]>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      try {
        const activeWorkspaces = await Promise.all(
          workspaceIds.map(async (workspaceId) => {
            const descriptor = await workspaces.getWorkspace({ workspaceId });
            const files = await workspaces.getFiles({ workspaceId });
            return { descriptor, files };
          })
        );
        if (canceled.get()) {
          return;
        }

        setActiveWorkspacesPromise({ data: activeWorkspaces });
      } catch (error) {
        setActiveWorkspacesPromise({ error: "Can't load data from workspaces" });
        console.error(error);
        return;
      }
    },
    [setActiveWorkspacesPromise, workspaceIds, workspaces]
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
        const workspacesBroadcastChannel = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
        workspacesBroadcastChannel.onmessage = ({ data }) => {
          console.debug(`EVENT::WORKSPACES: ${JSON.stringify(data)}`);
          return refresh(canceled);
        };

        const workspaceBroadcastChannels = workspaceIds.map((workspaceId) => {
          const bc = new BroadcastChannel(workspaceId);
          bc.onmessage = ({ data }) => {
            console.debug(`EVENT::WORKSPACE: ${JSON.stringify(data)}`);
            return refresh(canceled);
          };
          return bc;
        });

        return () => {
          workspacesBroadcastChannel.close();
          workspaceBroadcastChannels.forEach((bc) => bc.close());
        };
      },
      [workspaceIds, refresh]
    )
  );

  return activeWorkspacesPromise;
}
