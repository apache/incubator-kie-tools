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

import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useCallback } from "react";
import { deleteAllCookies } from "../../cookies";

const STORAGE_BROADCAST_CHANNEL = "storage" as const;
type StorageBroadcastEvents = { type: "STORAGE_WIPE_OUT" };

export function useStorage() {
  const workspaces = useWorkspaces();

  useCancelableEffect(
    useCallback(({ canceled }) => {
      const storageBroadcastChannel = new BroadcastChannel(STORAGE_BROADCAST_CHANNEL);
      storageBroadcastChannel.onmessage = async ({ data }: MessageEvent<StorageBroadcastEvents>) => {
        if (canceled.get()) {
          return;
        }

        if (data.type === "STORAGE_WIPE_OUT") {
          window.location.href = window.location.origin + window.location.pathname;
        }
      };
    }, [])
  );

  const wipeOutStorage = useCallback(
    async (args: { includeCookies: boolean; includeLocalStorage: boolean }) => {
      if (args.includeCookies) {
        deleteAllCookies();
      }

      if (args.includeLocalStorage) {
        localStorage.clear();
      }

      // Clean up workspaces first
      const wsDescriptors = await workspaces.listAllWorkspaces();
      for (const descriptor of wsDescriptors) {
        await workspaces.deleteWorkspace({ workspaceId: descriptor.workspaceId });
      }

      // Clean up remaining databases
      // Note: `indexedDB.databases()` is not implemented in Firefox
      // https://bugzilla.mozilla.org/show_bug.cgi?id=934640
      const databases = await indexedDB.databases();
      databases.filter((db) => db.name).forEach((db) => indexedDB.deleteDatabase(db.name!));

      new BroadcastChannel(STORAGE_BROADCAST_CHANNEL).postMessage({
        type: "STORAGE_WIPE_OUT",
      });
    },
    [workspaces]
  );

  return {
    wipeOutStorage,
  };
}
