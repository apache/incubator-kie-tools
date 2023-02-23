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
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerInputsDispatch } from "./DmnRunnerInputsDispatchContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { CompanionFsServiceBroadcastEvents } from "../companionFs/CompanionFsService";
import { EMPTY_DMN_RUNNER_INPUTS } from "./DmnRunnerInputsService";
import { usePreviousRef } from "@kie-tools-core/react-hooks/dist/usePreviousRef";

interface DmnRunnerInputs {
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<InputRow>>>;
}

export function useDmnRunnerInputs(workspaceFile: WorkspaceFile): DmnRunnerInputs {
  const { dmnRunnerInputsService, updatePersistedInputRows } = useDmnRunnerInputsDispatch();
  const [inputRows, setInputRows] = useState<Array<InputRow>>(EMPTY_DMN_RUNNER_INPUTS);
  const previousInputRows = usePreviousRef(inputRows);
  const previousInputRowsStringfied = useRef<string>(JSON.stringify(EMPTY_DMN_RUNNER_INPUTS));

  // TODO: Use useCompanionFsFile for keeping the `inputRows` up to date with updates made to it.
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
            // Triggered by the tab
            if (companionEvent.content === previousInputRowsStringfied.current) {
              setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(companionEvent.content));
              return;
            }
            // Triggered by the other tab
            previousInputRowsStringfied.current = companionEvent.content;
            setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(companionEvent.content));
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
              previousInputRowsStringfied.current = inputRows;
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
      const stringfiedDmnRunnerInputs = dmnRunnerInputsService.stringifyDmnRunnerInputs(
        newInputRows,
        previousInputRows.current
      );

      if (previousInputRowsStringfied.current === stringfiedDmnRunnerInputs) {
        return;
      }

      timeout.current = window.setTimeout(() => {
        updatePersistedInputRows(workspaceFile.workspaceId, workspaceFile.relativePath, stringfiedDmnRunnerInputs);
        previousInputRowsStringfied.current = stringfiedDmnRunnerInputs;
      }, 400);
    },
    [
      previousInputRows,
      updatePersistedInputRows,
      workspaceFile.workspaceId,
      workspaceFile.relativePath,
      dmnRunnerInputsService,
    ]
  );

  return {
    inputRows,
    setInputRows: setInputRowsAndUpdatePersistence,
  };
}
