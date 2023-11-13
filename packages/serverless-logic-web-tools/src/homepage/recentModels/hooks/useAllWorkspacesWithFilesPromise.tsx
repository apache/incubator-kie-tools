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

import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { WorkspaceFile, useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { WORKSPACES_BROADCAST_CHANNEL } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspacesBroadcastEvents";
import { useCallback, useState } from "react";

type WorkspaceWithFilesResponse =
  | {
      descriptor: WorkspaceDescriptor;
      files: WorkspaceFile[];
      success: true;
    }
  | (Partial<WorkspaceDescriptor> & {
      success: false;
      workspaceId: WorkspaceDescriptor["workspaceId"];
      errorMessage: string;
    });

export function useAllWorkspacesWithFilesPromise() {
  const workspaces = useWorkspaces();
  const [workspaceWithFilesResponsesPromise, setWorkspaceWithFilesResponsesPromise] =
    usePromiseState<WorkspaceWithFilesResponse[]>();
  const [ids, setIds] = useState<string[]>([]);

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      if (canceled.get()) {
        return;
      }

      try {
        const allDescriptors = await workspaces.listAllWorkspaces();
        const idsToFetch: WorkspaceDescriptor["workspaceId"][] = allDescriptors.map((w) => w.workspaceId);
        setIds(idsToFetch);

        const workspaceWithFilesResponses = await Promise.all(
          idsToFetch.map<Promise<WorkspaceWithFilesResponse>>(async (workspaceId) => {
            const descriptor = allDescriptors.find((d) => d.workspaceId === workspaceId);

            if (!descriptor) {
              return { success: false, errorMessage: "Workspace not found!", workspaceId };
            }

            try {
              const files = await workspaces.getFiles({ workspaceId });
              return { descriptor, files, success: true };
            } catch (e) {
              return { success: false, errorMessage: e, ...descriptor };
            }
          })
        );
        setWorkspaceWithFilesResponsesPromise({ data: workspaceWithFilesResponses });
      } catch (error) {
        setWorkspaceWithFilesResponsesPromise({ error: "Can't load data from workspaces" });
        console.error(error);
        return;
      }
    },
    [setWorkspaceWithFilesResponsesPromise, workspaces]
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

        const workspaceBroadcastChannels = ids.map((workspaceId) => {
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
      [ids, refresh]
    )
  );

  return workspaceWithFilesResponsesPromise;
}
