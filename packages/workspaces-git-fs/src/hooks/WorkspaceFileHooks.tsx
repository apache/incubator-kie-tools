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

import { useCallback } from "react";
import { useWorkspaces, WorkspaceFile } from "../context/WorkspacesContext";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { WorkspaceBroadcastEvents } from "../worker/api/WorkspaceBroadcastEvents";
import { WorkspaceFileBroadcastEvents } from "../worker/api/WorkspaceFileBroadcastEvents";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";

export function useWorkspaceFilePromise(workspaceId: string | undefined, relativePath: string | undefined) {
  const workspaces = useWorkspaces();
  const [workspaceFilePromise, setWorkspaceFilePromise] = usePromiseState<{
    workspaceFile: WorkspaceFile;
    uniqueId: string;
  }>();

  const refresh = useCallback(
    async (workspaceId: string, relativePath: string, canceled: Holder<boolean>) => {
      workspaces.getFile({ workspaceId, relativePath }).then((workspaceFile) => {
        if (canceled.get()) {
          return;
        }

        if (!workspaceFile) {
          setWorkspaceFilePromise({
            error: `File '${relativePath}' not found in Workspace '${workspaceId}'`,
          });
          return;
        }

        workspaces.getUniqueFileIdentifier({ workspaceId, relativePath }).then((uniqueId) => {
          if (canceled.get()) {
            return;
          }

          setWorkspaceFilePromise({ data: { workspaceFile, uniqueId } });
        });
      });
    },
    [workspaces, setWorkspaceFilePromise]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }
        refresh(workspaceId, relativePath, canceled);
      },
      [relativePath, workspaceId, refresh]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }

        workspaces.getUniqueFileIdentifier({ workspaceId, relativePath }).then((uniqueFileIdentifier) => {
          if (canceled.get()) {
            return;
          }

          console.debug(`Subscribing to ${uniqueFileIdentifier}`);
          const broadcastChannel = new BroadcastChannel(uniqueFileIdentifier);
          broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileBroadcastEvents>) => {
            console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
            if (data.type === "WSF_MOVE" || data.type == "WSF_RENAME") {
              refresh(workspaceId, data.newRelativePath, canceled);
            }
            if (data.type === "WSF_UPDATE" || data.type === "WSF_DELETE" || data.type === "WSF_ADD") {
              refresh(workspaceId, data.relativePath, canceled);
            }
          };

          console.debug(`Subscribing to ${workspaceId}`);
          const broadcastChannel2 = new BroadcastChannel(workspaceId);
          broadcastChannel2.onmessage = ({ data }: MessageEvent<WorkspaceBroadcastEvents>) => {
            console.debug(`EVENT::WORKSPACE: ${JSON.stringify(data)}`);
            if (data.type === "WS_PULL") {
              refresh(workspaceId, relativePath, canceled);
            }
            if (data.type === "WS_CHECKOUT_FILES_FROM_LOCAL_HEAD" && data.relativePaths.includes(relativePath)) {
              refresh(workspaceId, relativePath, canceled);
            }
          };

          return () => {
            console.debug(`Unsubscribing to ${uniqueFileIdentifier}`);
            broadcastChannel.close();
          };
        });
      },
      [relativePath, workspaceId, workspaces, refresh]
    )
  );

  return workspaceFilePromise;
}
