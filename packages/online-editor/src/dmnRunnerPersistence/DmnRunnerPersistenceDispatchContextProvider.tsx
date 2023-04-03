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

function checkIfHasChangesAndUpdateFs(
  persistenceJson: DmnRunnerPersistenceJson,
  newPersistenceJson: DmnRunnerPersistenceJson,
  updatePersistenceJsonDebouce: (args: DmnRunnerUpdatePersistenceJsonDeboucerArgs) => void,
  workspaceId: string,
  workspaceFileRelativePath: string,
  shouldUpdateFs: boolean
): DmnRunnerPersistenceJson {
  // Check for changes before update;
  if (isEqual(persistenceJson, newPersistenceJson)) {
    LOCK = false;
    return persistenceJson;
  }

  // Updates from local FS and the current value is different from the change, hence, a change occured while the FS was updated;
  if (!shouldUpdateFs) {
    if (LOCK) {
      // the last change was made by this tab; invalidate fsUpdate;
      LOCK = false;
      return persistenceJson;
    } else {
      // state wasn't changed by this tab; overwrite
      LOCK = false;
      return newPersistenceJson;
    }
  }

  // update FS;
  updatePersistenceJsonDebouce({
    workspaceId: workspaceId,
    workspaceFileRelativePath: workspaceFileRelativePath,
    content: JSON.stringify(newPersistenceJson),
  });

  LOCK = true;
  return newPersistenceJson;
}

// Update the state and update the FS;
function dmnRunnerPersistenceJsonReducer(
  persistenceJson: DmnRunnerPersistenceJson,
  action: DmnRunnerPersistenceReducerAction
) {
  if (action.type === DmnRunnerPersistenceReducerActionType.PREVIOUS) {
    return checkIfHasChangesAndUpdateFs(
      persistenceJson,
      action.newPersistenceJson(persistenceJson),
      action.updatePersistenceJsonDebouce,
      action.workspaceId,
      action.workspaceFileRelativePath,
      action.shouldUpdateFS
    );
  } else if (action.type === DmnRunnerPersistenceReducerActionType.DEFAULT) {
    return checkIfHasChangesAndUpdateFs(
      persistenceJson,
      action.newPersistenceJson,
      action.updatePersistenceJsonDebouce,
      action.workspaceId,
      action.workspaceFileRelativePath,
      action.shouldUpdateFS
    );
  } else {
    throw new Error("Invalid action for DmnRunnerPersistence reducer");
  }
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

  const deletePersistenceJson = useCallback(
    (previousDmnRunnerPersisnteceJson: DmnRunnerPersistenceJson, workspaceFile: WorkspaceFile) => {
      // overwrite the current persistenceJson with a new one;
      const newPersistenceJson = getNewDefaultDmnRunnerPersistenceJson();
      // keep current mode;
      newPersistenceJson.configs.mode = previousDmnRunnerPersisnteceJson.configs.mode;

      dmnRunnerPersistenceJsonDispatcher({
        updatePersistenceJsonDebouce,
        workspaceId: workspaceFile.workspaceId,
        workspaceFileRelativePath: workspaceFile.relativePath,
        type: DmnRunnerPersistenceReducerActionType.DEFAULT,
        newPersistenceJson,
        shouldUpdateFS: true,
      });
    },
    [updatePersistenceJsonDebouce]
  );

  const getPersistenceJsonForDownload = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const persistenceJson = await dmnRunnerPersistenceService.companionFsService.get({
        workspaceId: workspaceFile.workspaceId,
        workspaceFileRelativePath: workspaceFile.relativePath,
      });
      return await persistenceJson
        ?.getFileContents()
        .then((content) => new Blob([content], { type: "application/json" }));
    },
    [dmnRunnerPersistenceService]
  );

  const uploadPersistenceJson = useCallback(
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
        deletePersistenceJson,
        getPersistenceJsonForDownload,
        uploadPersistenceJson,
        updatePersistenceJsonDebouce,
        dmnRunnerPersistenceJson,
        dmnRunnerPersistenceJsonDispatcher,
      }}
    >
      {props.children}
    </DmnRunnerPersistenceDispatchContext.Provider>
  );
}
