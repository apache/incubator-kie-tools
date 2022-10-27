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
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { WORKSPACES_BROADCAST_CHANNEL } from "../worker/api/WorkspacesBroadcastEvents";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";

export function useWorkspaceDescriptorsPromise() {
  const workspaces = useWorkspaces();
  const [workspaceDescriptorsPromise, setWorkspaceDescriptorsPromise] = usePromiseState<WorkspaceDescriptor[]>();

  const refresh = useCallback(
    async (canceled: Holder<boolean>) => {
      workspaces
        .listAllWorkspaces()
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
        const broadcastChannel = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
        broadcastChannel.onmessage = ({ data }) => {
          console.debug(`EVENT::WORKSPACES: ${JSON.stringify(data)}`);
          refresh(canceled);
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [refresh]
    )
  );

  return workspaceDescriptorsPromise;
}
