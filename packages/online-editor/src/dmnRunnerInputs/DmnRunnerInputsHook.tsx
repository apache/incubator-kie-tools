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
import { useCallback, useRef, useState } from "react";
import { useCancelableEffect, usePreviousRef } from "../reactExt/Hooks";
import { WorkspaceFile } from "../workspace/WorkspacesContext";
import { InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerInputsDispatch } from "./DmnRunnerInputsDispatchContext";
import { decoder } from "../workspace/encoderdecoder/EncoderDecoder";
import { CompanionFsServiceBroadcastEvents } from "../companionFs/CompanionFsService";
import { EMPTY_DMN_RUNNER_INPUTS } from "./DmnRunnerInputsService";

interface DmnRunnerInputs {
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<InputRow>>>;
  didUpdateInputRows: boolean;
  setDidUpdateInputRows: React.Dispatch<React.SetStateAction<boolean>>;
  didUpdateOutputRows: boolean;
  setDidUpdateOutputRows: React.Dispatch<React.SetStateAction<boolean>>;
}

export function useDmnRunnerInputs(workspaceFile: WorkspaceFile): DmnRunnerInputs {
  const { dmnRunnerInputsService, updatePersistedInputRows } = useDmnRunnerInputsDispatch();

  const [inputRows, setInputRows] = useState<Array<InputRow>>(EMPTY_DMN_RUNNER_INPUTS);
  const lastInputRows = useRef<string>(JSON.stringify(EMPTY_DMN_RUNNER_INPUTS));
  const previousInputRows = usePreviousRef(inputRows);

  const [didUpdateInputRows, setDidUpdateInputRows] = useState<boolean>(false);
  const [didUpdateOutputRows, setDidUpdateOutputRows] = useState<boolean>(false);

  // TODO: Use useSyncedCompanionFsFile for keeping the files synced.
  //   I.e. When the DMN file is renamed, deleted, moved etc.

  // TODO: Create useCompanionFsFile for keeping the `inputRows` up to date with updates made to it.
  //   I.e. When the DMN Runner Inputs file is changed

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFile.relativePath || !workspaceFile.workspaceId) {
          return;
        }

        const dmnRunnerInputsFileUniqueId = dmnRunnerInputsService.companionFsService.getUniqueFileIdentifier({
          workspaceId: workspaceFile.workspaceId,
          workspaceFileRelativePath: workspaceFile.relativePath,
        });

        console.debug(`Subscribing to ${dmnRunnerInputsFileUniqueId}`);
        const broadcastChannel = new BroadcastChannel(dmnRunnerInputsFileUniqueId);
        broadcastChannel.onmessage = ({ data }: MessageEvent<CompanionFsServiceBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "CFSF_MOVE" || data.type == "CFSF_RENAME") {
            // Ignore, as content remains the same.
          } else if (data.type === "CFSF_UPDATE" || data.type === "CFSF_ADD" || data.type === "CFSF_DELETE") {
            // Triggered by the tab
            if (data.content === lastInputRows.current) {
              setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(data.content));
              return;
            }
            // Triggered by the other tab
            lastInputRows.current = data.content;
            setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(data.content));
            setDidUpdateInputRows(true);
          }
        };

        return () => {
          console.debug(`Unsubscribing to ${dmnRunnerInputsFileUniqueId}`);
          broadcastChannel.close();
        };
      },
      [dmnRunnerInputsService, workspaceFile]
    )
  );

  // On first render load the inputs;
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFile || !dmnRunnerInputsService) {
          return;
        }

        dmnRunnerInputsService.companionFsService
          .get({ workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath })
          .then((inputs) => {
            if (canceled.get()) {
              return;
            }
            // If inputs don't exist, create then.
            if (!inputs) {
              dmnRunnerInputsService.companionFsService.createOrOverwrite(
                { workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath },
                JSON.stringify(EMPTY_DMN_RUNNER_INPUTS)
              );
              return;
            }

            inputs.getFileContents().then((content) => {
              if (canceled.get()) {
                return;
              }
              const inputRows = decoder.decode(content);
              lastInputRows.current = inputRows;
              setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(inputRows));
            });
          });
      },
      [dmnRunnerInputsService, workspaceFile]
    )
  );

  // Debounce to avoid multiple updates on the filesystem
  const timeout = useRef<number | undefined>(undefined);
  const setInputRowsAndUpdatePersistence = useCallback(
    (newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)) => {
      if (timeout.current) {
        window.clearTimeout(timeout.current);
      }

      // After a re-render the callback is called by the first time, this avoids a filesystem unnecessary re-update
      if (
        lastInputRows.current ===
        dmnRunnerInputsService.stringifyDmnRunnerInputs(newInputRows, previousInputRows.current)
      ) {
        return;
      }

      timeout.current = window.setTimeout(() => {
        updatePersistedInputRows(workspaceFile, newInputRows);
        lastInputRows.current = dmnRunnerInputsService.stringifyDmnRunnerInputs(
          newInputRows,
          previousInputRows.current
        );
      }, 400);
    },
    [previousInputRows, updatePersistedInputRows, workspaceFile, dmnRunnerInputsService]
  );

  return {
    inputRows,
    setInputRows: setInputRowsAndUpdatePersistence,
    didUpdateInputRows,
    setDidUpdateInputRows,
    didUpdateOutputRows,
    setDidUpdateOutputRows,
  };
}
