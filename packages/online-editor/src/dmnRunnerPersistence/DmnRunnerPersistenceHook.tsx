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

import * as React from "react";
import { useCallback } from "react";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useDmnRunnerPersistenceDispatch } from "./DmnRunnerPersistenceDispatchContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { getNewDefaultDmnRunnerPersistenceJson } from "./DmnRunnerPersistenceService";
import { DmnRunnerPersistenceReducerActionType, DmnRunnerPersistenceJson } from "./DmnRunnerPersistenceTypes";

// Handle the companion FS events;
export function useDmnRunnerPersistence(workspaceId?: string, workspaceFileRelativePath?: string) {
  const { dmnRunnerPersistenceService, dmnRunnerPersistenceJsonDispatcher, updatePersistenceJsonDebouce } =
    useDmnRunnerPersistenceDispatch();

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
                shouldUpdateFs: false,
                cancellationToken: canceled,
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
