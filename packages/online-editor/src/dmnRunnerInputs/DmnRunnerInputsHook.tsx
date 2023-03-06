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
import { useCallback, useEffect, useState } from "react";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerInputsDispatch } from "./DmnRunnerInputsDispatchContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { CompanionFsServiceBroadcastEvents } from "../companionFs/CompanionFsService";
import { EMPTY_DMN_RUNNER_INPUTS } from "./DmnRunnerInputsService";
import isEqual from "lodash/isEqual";

interface DmnRunnerInputs {
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<InputRow>>>;
}

export function useDmnRunnerInputs(workspaceFile: WorkspaceFile): DmnRunnerInputs {
  const [inputRows, setInputRows] = useState<Array<InputRow>>(EMPTY_DMN_RUNNER_INPUTS);
  const { dmnRunnerInputsService } = useDmnRunnerInputsDispatch();

  // When another TAB updates the FS, it should sync up
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
            setInputRows((currentInputRows) => {
              // Triggered by the tab; shouldn't update; safe comparison;
              if (isEqual(JSON.parse(companionEvent.content), currentInputRows)) {
                return currentInputRows;
              }
              // Triggered by the other tab; should update;
              return dmnRunnerInputsService.parseDmnRunnerInputs(companionEvent.content);
            });
          }
        };

        return () => {
          console.debug(`Unsubscribing to ${dmnRunnerInputsFileUniqueId}`);
          broadcastChannel.close();
        };
      },
      [dmnRunnerInputsService, workspaceFile, setInputRows]
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
              setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(inputRows));
            });
          });
      },
      [dmnRunnerInputsService, workspaceFile, setInputRows]
    )
  );

  // Updating the inputRows should update the FS
  useEffect(() => {
    console.log("use effect triggered by input rows", inputRows);
    if (!workspaceFile.relativePath || !workspaceFile.workspaceId) {
      return;
    }

    // safe comparison, it compares to an array with an empty object;
    if (JSON.stringify(inputRows) === JSON.stringify(EMPTY_DMN_RUNNER_INPUTS)) {
      return;
    }

    dmnRunnerInputsService.companionFsService.update(
      {
        workspaceId: workspaceFile.workspaceId,
        workspaceFileRelativePath: workspaceFile.relativePath,
      },
      JSON.stringify(inputRows)
    );
  }, [dmnRunnerInputsService, workspaceFile.workspaceId, workspaceFile.relativePath, inputRows]);

  return {
    inputRows,
    setInputRows,
  };
}
