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

import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { useWorkspaces, WorkspaceFile } from "../context/WorkspacesContext";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCallback } from "react";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";

export function useWorkspacesFilesPromise(workspaceDescriptors: WorkspaceDescriptor[] | undefined) {
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
