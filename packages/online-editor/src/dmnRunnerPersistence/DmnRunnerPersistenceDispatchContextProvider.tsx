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
import { useCallback, useMemo, useReducer, useRef } from "react";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerPersistenceService, getNewDefaultDmnRunnerPersistenceJson } from "./DmnRunnerPersistenceService";
import { DmnRunnerPersistenceDispatchContext } from "./DmnRunnerPersistenceDispatchContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useSyncedCompanionFs } from "../companionFs/CompanionFsHooks";
import isEqual from "lodash/isEqual";
import {
  DmnRunnerPersistenceJson,
  DmnRunnerPersistenceReducerAction,
  DmnRunnerPersistenceReducerActionType,
  DmnRunnerUpdatePersistenceJsonDeboucerArgs,
} from "./DmnRunnerPersistenceTypes";

// This variable ensures that a new update from the FS will NOT override the new value.
let LOCK = false;

// Update the state and update the FS;
function dmnRunnerPersistenceJsonReducer(
  persistenceJson: DmnRunnerPersistenceJson,
  action: DmnRunnerPersistenceReducerAction
) {
  if (action.cancellationToken?.get()) {
    return persistenceJson;
  }

  let newPersistenceJson;
  if (action.type === DmnRunnerPersistenceReducerActionType.PREVIOUS) {
    newPersistenceJson = action.newPersistenceJson(persistenceJson);
  } else if (action.type === DmnRunnerPersistenceReducerActionType.DEFAULT) {
    newPersistenceJson = action.newPersistenceJson;
  } else {
    throw new Error("Invalid action for DmnRunnerPersistence reducer");
  }

  // Check for changes before start;
  if (isEqual(persistenceJson, newPersistenceJson)) {
    LOCK = false;
    return persistenceJson;
  }

  // Updates from local FS and the current value is different from the change, hence, a change occured while the FS is being updated;
  if (!action.shouldUpdateFs) {
    if (LOCK) {
      // the last change was made by this tab; invalidate fsUpdate;
      LOCK = false;
      return persistenceJson;
    } else {
      // the last change wasn't made by this tab; overwrite
      LOCK = false;
      return newPersistenceJson;
    }
  }

  // The new value should update the FS;
  LOCK = true;
  action.updatePersistenceJsonDebouce({
    workspaceId: action.workspaceId,
    workspaceFileRelativePath: action.workspaceFileRelativePath,
    content: JSON.stringify(newPersistenceJson),
    cancellationToken: action.cancellationToken,
  });
  return newPersistenceJson;
}

const initialDmnRunnerPersistenceJson = getNewDefaultDmnRunnerPersistenceJson();

export function DmnRunnerPersistenceDispatchContextProvider(props: React.PropsWithChildren<{}>) {
  const timeout = useRef<number | undefined>(undefined);

  const [dmnRunnerPersistenceJson, dmnRunnerPersistenceJsonDispatcher] = useReducer(
    dmnRunnerPersistenceJsonReducer,
    initialDmnRunnerPersistenceJson
  );

  const dmnRunnerPersistenceService = useMemo(() => new DmnRunnerPersistenceService(), []);

  useSyncedCompanionFs(dmnRunnerPersistenceService.companionFsService);

  const updatePersistenceJsonDebouce = useCallback(
    (args: DmnRunnerUpdatePersistenceJsonDeboucerArgs) => {
      if (timeout.current) {
        window.clearTimeout(timeout.current);
      }

      timeout.current = window.setTimeout(() => {
        if (args.cancellationToken.get()) {
          return;
        }
        dmnRunnerPersistenceService.companionFsService.update(
          {
            workspaceId: args.workspaceId,
            workspaceFileRelativePath: args.workspaceFileRelativePath,
          },
          args.content
        );
      }, 100);
    },
    [dmnRunnerPersistenceService]
  );

  const onDeleteDmnRunnerPersistenceJson = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const newPersistenceJson = getNewDefaultDmnRunnerPersistenceJson();

      newPersistenceJson.configs.mode = dmnRunnerPersistenceJson.configs.mode;

      await dmnRunnerPersistenceService.companionFsService.createOrOverwrite(
        { workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath },
        JSON.stringify(newPersistenceJson)
      );
    },
    [dmnRunnerPersistenceJson.configs.mode, dmnRunnerPersistenceService.companionFsService]
  );

  const onDownloadDmnRunnerPersistenceJson = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const persistenceJson = await dmnRunnerPersistenceService.companionFsService.get({
        workspaceId: workspaceFile.workspaceId,
        workspaceFileRelativePath: workspaceFile.relativePath,
      });
      return await persistenceJson
        ?.getFileContents()
        .then((content) => new Blob([content], { type: "application/json" }));
    },
    [dmnRunnerPersistenceService.companionFsService]
  );

  const onUploadDmnRunnerPersistenceJson = useCallback(
    async (workspaceFile: WorkspaceFile, file: File) => {
      const content = await new Promise<string>((res) => {
        const reader = new FileReader();
        reader.onload = (event: ProgressEvent<FileReader>) => res(decoder.decode(event.target?.result as ArrayBuffer));
        reader.readAsArrayBuffer(file);
      });

      await dmnRunnerPersistenceService.companionFsService.createOrOverwrite(
        { workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath },
        content
      );
    },
    [dmnRunnerPersistenceService]
  );

  return (
    <DmnRunnerPersistenceDispatchContext.Provider
      value={{
        dmnRunnerPersistenceService,
        updatePersistenceJsonDebouce,
        dmnRunnerPersistenceJson,
        dmnRunnerPersistenceJsonDispatcher,
        onDeleteDmnRunnerPersistenceJson,
        onDownloadDmnRunnerPersistenceJson,
        onUploadDmnRunnerPersistenceJson,
      }}
    >
      {props.children}
    </DmnRunnerPersistenceDispatchContext.Provider>
  );
}
