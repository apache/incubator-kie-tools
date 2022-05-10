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
import { decoder, WorkspaceFile } from "../workspace/WorkspacesContext";
import { InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerInputsDispatch } from "./DmnRunnerInputsContext";

interface DmnRunnerInputs {
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<InputRow>>>;
  didUpdateInputRows: boolean;
  setDidUpdateInputRows: React.Dispatch<React.SetStateAction<boolean>>;
  didUpdateOutputRows: boolean;
  setDidUpdateOutputRows: React.Dispatch<React.SetStateAction<boolean>>;
}

export function useDmnRunnerInputs(workspaceFile: WorkspaceFile): DmnRunnerInputs {
  const { dmnRunnerInputsService, updatePersistedInputRows, getUniqueFileIdentifier } = useDmnRunnerInputsDispatch();
  const [inputRows, setInputRows] = useState<Array<InputRow>>([{}]);
  const [didUpdateInputRows, setDidUpdateInputRows] = useState<boolean>(false);
  const [didUpdateOutputRows, setDidUpdateOutputRows] = useState<boolean>(false);
  const lastInputRows = useRef<string>("[{}]");
  const previousInputRows = usePreviousRef(inputRows);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFile.relativePath || !workspaceFile.workspaceId) {
          return;
        }

        const uniqueFileIdentifier = getUniqueFileIdentifier({
          workspaceId: workspaceFile.workspaceId,
          relativePath: workspaceFile.relativePath,
        });

        console.debug("Subscribing to " + uniqueFileIdentifier);
        const broadcastChannel = new BroadcastChannel(uniqueFileIdentifier);
        broadcastChannel.onmessage = ({ data }: MessageEvent<DmnRunnerInputsEvents>) => {
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            if (canceled.get()) {
              return;
            }
            const newRelativePathWithoutExtension = data.newRelativePath.slice(
              0,
              data.newRelativePath.lastIndexOf(".")
            );
            dmnRunnerInputsService?.renameDmnRunnerInputs(workspaceFile, newRelativePathWithoutExtension);
          }
          if (data.type === "UPDATE" || data.type === "ADD" || data.type === "DELETE") {
            if (canceled.get()) {
              return;
            }
            // Triggered by the tab
            if (data.dmnRunnerInputs === lastInputRows.current) {
              setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(data.dmnRunnerInputs));
              return;
            }
            // Triggered by the other tab
            lastInputRows.current = data.dmnRunnerInputs;
            setInputRows(dmnRunnerInputsService.parseDmnRunnerInputs(data.dmnRunnerInputs));
            setDidUpdateInputRows(true);
          }
        };

        return () => {
          console.debug("Unsubscribing to " + uniqueFileIdentifier);
          broadcastChannel.close();
        };
      },
      [getUniqueFileIdentifier, dmnRunnerInputsService, workspaceFile]
    )
  );

  // On first render load the inputs;
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFile || !dmnRunnerInputsService) {
          return;
        }

        dmnRunnerInputsService.getDmnRunnerInputs(workspaceFile).then((inputs) => {
          if (canceled.get()) {
            return;
          }
          // If inputs don't exist, create then.
          if (!inputs) {
            dmnRunnerInputsService.createOrOverwriteDmnRunnerInputs(workspaceFile, JSON.stringify([{}]));
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

      // After a re-render the callback is called by the first time, this avoid a filesystem unnecessary re-update
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

export type DmnRunnerInputsEvents =
  | { type: "MOVE"; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME"; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE"; dmnRunnerInputs: string }
  | { type: "DELETE"; dmnRunnerInputs: string }
  | { type: "ADD"; relativePath: string; dmnRunnerInputs: string };
