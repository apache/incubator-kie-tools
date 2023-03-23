/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback } from "react";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import {
  DmnRunnerPersistenceReducerActionType,
  useDmnRunnerPersistenceDispatch,
} from "./DmnRunnerPersistenceDispatchContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { CompanionFsServiceBroadcastEvents } from "../companionFs/CompanionFsService";
import { DmnRunnerPersistenceJson } from "./DmnRunnerPersistenceService";
import { getNewDefaultDmnRunnerPersistenceJson } from "./DmnRunnerPersistenceService";

// Handle the companion FS events;
export function useDmnRunnerPersistence(workspaceId?: string, workspaceFileRelativePath?: string) {
  const { dmnRunnerPersistenceService, dmnRunnerPersistenceJsonDispatcher, updatePersistenceJsonDebouce } =
    useDmnRunnerPersistenceDispatch();

  // When another TAB updates the FS, it should sync up
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFileRelativePath || !workspaceId) {
          return;
        }

        const dmnRunnerPersistenceJsonFileUniqueId =
          dmnRunnerPersistenceService.companionFsService.getUniqueFileIdentifier({
            workspaceId: workspaceId,
            workspaceFileRelativePath: workspaceFileRelativePath,
          });

        console.debug(`Subscribing to ${dmnRunnerPersistenceJsonFileUniqueId}`);
        const broadcastChannel = new BroadcastChannel(dmnRunnerPersistenceJsonFileUniqueId);
        broadcastChannel.onmessage = ({ data: companionEvent }: MessageEvent<CompanionFsServiceBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(companionEvent)}`);
          if (companionEvent.type === "CFSF_MOVE" || companionEvent.type == "CFSF_RENAME") {
            // Ignore, as content remains the same.
          } else if (
            companionEvent.type === "CFSF_UPDATE" ||
            companionEvent.type === "CFSF_ADD" ||
            companionEvent.type === "CFSF_DELETE"
          ) {
            const dmnRunnerPersistenceJson: DmnRunnerPersistenceJson =
              dmnRunnerPersistenceService.parseDmnRunnerPersistenceJson(companionEvent.content);

            dmnRunnerPersistenceJsonDispatcher({
              updatePersistenceJsonDebouce,
              workspaceId: workspaceId,
              workspaceFileRelativePath: workspaceFileRelativePath,
              type: DmnRunnerPersistenceReducerActionType.DEFAULT,
              newPersistenceJson: dmnRunnerPersistenceJson,
              fsUpdate: true,
            });
          }
        };

        return () => {
          console.debug(`Unsubscribing to ${dmnRunnerPersistenceJsonFileUniqueId}`);
          broadcastChannel.close();
        };
      },
      [
        updatePersistenceJsonDebouce,
        dmnRunnerPersistenceService,
        workspaceId,
        workspaceFileRelativePath,
        dmnRunnerPersistenceJsonDispatcher,
      ]
    )
  );

  // On first render load the persistence json;
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceId || !workspaceFileRelativePath || !dmnRunnerPersistenceService) {
          return;
        }

        dmnRunnerPersistenceService.companionFsService
          .get({ workspaceId: workspaceId, workspaceFileRelativePath: workspaceFileRelativePath })
          .then((persistenceJson) => {
            if (canceled.get()) {
              return;
            }
            // If persistence doesn't exist, create then.
            if (!persistenceJson) {
              const newDmnRunnerPersistenceJson = getNewDefaultDmnRunnerPersistenceJson();
              dmnRunnerPersistenceService.companionFsService.createOrOverwrite(
                { workspaceId: workspaceId, workspaceFileRelativePath: workspaceFileRelativePath },
                JSON.stringify(newDmnRunnerPersistenceJson)
              );
              return;
            }

            persistenceJson.getFileContents().then((content) => {
              if (canceled.get()) {
                return;
              }
              const dmnRunnerPersistenceJson = dmnRunnerPersistenceService.parseDmnRunnerPersistenceJson(
                decoder.decode(content)
              );
              dmnRunnerPersistenceJsonDispatcher({
                updatePersistenceJsonDebouce,
                workspaceId: workspaceId,
                workspaceFileRelativePath: workspaceFileRelativePath,
                type: DmnRunnerPersistenceReducerActionType.DEFAULT,
                newPersistenceJson: dmnRunnerPersistenceJson,
                fsUpdate: true,
              });
            });
          });
      },
      [
        updatePersistenceJsonDebouce,
        dmnRunnerPersistenceService,
        workspaceId,
        workspaceFileRelativePath,
        dmnRunnerPersistenceJsonDispatcher,
      ]
    )
  );
}
