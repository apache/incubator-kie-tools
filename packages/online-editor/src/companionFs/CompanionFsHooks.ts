/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { CompanionFsService, CompanionFsServiceBroadcastEvents } from "./CompanionFsService";
import { Holder, useCancelableEffect } from "../reactExt/Hooks";
import { useCallback } from "react";
import {
  WORKSPACES_BROADCAST_CHANNEL,
  WORKSPACES_FILES_BROADCAST_CHANNEL,
  WorkspacesBroadcastEvents,
  WorkspacesFilesBroadcastEvents,
} from "../workspace/worker/api/WorkspacesBroadcastEvents";
import { usePromiseState } from "../workspace/hooks/PromiseState";
import { decoder } from "../workspace/encoderdecoder/EncoderDecoder";

export function useSyncedCompanionFs(companionFsService: CompanionFsService) {
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        console.debug(`Subscribing to ${WORKSPACES_BROADCAST_CHANNEL}`);
        const broadcastChannel = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);

        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspacesBroadcastEvents>) => {
          console.debug(`EVENT::WORKSPACE: ${JSON.stringify(data)}`);
          if (data.type === "WSS_DELETE_WORKSPACE") {
            if (canceled.get()) {
              return;
            }
            companionFsService.deleteAll(data.workspaceId);
          }
        };

        return () => {
          console.debug(`Unsubscribing to ${WORKSPACES_BROADCAST_CHANNEL}`);
          broadcastChannel.close();
        };
      },
      [companionFsService]
    )
  );

  // Keep companion FS structure synced with workspace FS
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const broadcastChannel = new BroadcastChannel(WORKSPACES_FILES_BROADCAST_CHANNEL);

        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspacesFilesBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }

          console.debug(`EVENT::WORKSPACES_FILES: ${JSON.stringify(data)}`);

          if (data.type === "WSSFS_ADD" || data.type === "WSSFS_UPDATE") {
            // Ignore, as this should be handled externally.
          } else if (data.type === "WSSFS_DELETE") {
            companionFsService.delete({ workspaceId: data.workspaceId, workspaceFileRelativePath: data.relativePath });
          } else if (data.type === "WSSFS_RENAME") {
            companionFsService
              .get({ workspaceId: data.workspaceId, workspaceFileRelativePath: data.oldRelativePath })
              .then(async (file) => {
                const companionFileContentBeforeRenaming = await file?.getFileContents();
                if (!companionFileContentBeforeRenaming) {
                  return;
                }
                await companionFsService.createOrOverwrite(
                  { workspaceId: data.workspaceId, workspaceFileRelativePath: data.newRelativePath },
                  decoder.decode(companionFileContentBeforeRenaming)
                );
                await companionFsService.delete({
                  workspaceId: data.workspaceId,
                  workspaceFileRelativePath: data.oldRelativePath,
                });
              });
          } else if (data.type === "WSSFS_MOVE") {
            throw new Error("Moving not supported.");
          } else {
            throw new Error(`Impossible scenario for companion file. ${JSON.stringify(data)}`);
          }
        };

        return () => {
          broadcastChannel.close();
        };
      },
      [companionFsService]
    )
  );
}

export function useSyncedCompanionFsFile<T>(
  companionFsService: CompanionFsService,
  workspaceId: string,
  workspaceFileRelativePath: string,
  refreshCallback: (
    cancellationToken: Holder<boolean>,
    workspaceFileEvent?: CompanionFsServiceBroadcastEvents
  ) => Promise<T | undefined>
) {
  const [contentPromise, setContentPromise] = usePromiseState<T>();

  const refresh = useCallback(
    (cancellationToken: Holder<boolean>, companionFileEvent?: CompanionFsServiceBroadcastEvents) => {
      setContentPromise({ loading: true });
      refreshCallback(cancellationToken, companionFileEvent)
        .then((newContent) => {
          if (cancellationToken.get()) {
            return;
          }

          if (newContent) {
            setContentPromise({ data: newContent });
          } else {
            setContentPromise({ error: `Undefined content for companion file ${workspaceFileRelativePath}` });
          }
        })
        .catch(() => {
          setContentPromise({ error: `Error refreshing companion file ${workspaceFileRelativePath}` });
        });
    },
    [refreshCallback, setContentPromise, workspaceFileRelativePath]
  );

  // First time
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        refresh(canceled);
      },
      [refresh]
    )
  );

  // Keep state synced with companion file
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const companionFileUniqueId = companionFsService.getUniqueFileIdentifier({
          workspaceId,
          workspaceFileRelativePath,
        });

        console.debug(`Subscribing to companion file ${companionFileUniqueId}`);
        const broadcastChannel = new BroadcastChannel(companionFileUniqueId);

        broadcastChannel.onmessage = ({ data }: MessageEvent<CompanionFsServiceBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }

          console.debug(`EVENT::COMPANION_FILE: ${JSON.stringify(data)}`);

          if (data.type === "CFSF_MOVE" || data.type === "CFSF_RENAME") {
            // Ignore, as content remains the same.
          } else if (data.type === "CFSF_ADD" || data.type === "CFSF_UPDATE" || data.type === "CFSF_DELETE") {
            refresh(canceled, data);
          } else {
            throw new Error("Impossible scenario for companion file." + JSON.stringify(data));
          }
        };

        return () => {
          console.debug(`Unsubscribing to companion file ${companionFileUniqueId}`);
          broadcastChannel.close();
        };
      },
      [companionFsService, refresh, workspaceFileRelativePath, workspaceId]
    )
  );

  return { contentPromise };
}
