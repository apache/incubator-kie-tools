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

import React, { useCallback, useRef, useState } from "react";
import { useCancelableEffect } from "../reactExt/Hooks";
import { decoder, WorkspaceFile } from "../workspace/WorkspacesContext";
import { InputRow } from "../editor/DmnRunner/DmnRunnerContext";
import { useWorkspacesDmnRunnerInputs } from "./WorkspacesDmnInputsContext";

interface WorkspaceDmnRunnerInput {
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<InputRow>>>;
  inputRowsUpdated: boolean;
  setInputRowsUpdated: React.Dispatch<React.SetStateAction<boolean>>;
  outputRowsUpdated: boolean;
  setOutputRowsUpdated: React.Dispatch<React.SetStateAction<boolean>>;
}

export function useWorkspaceDmnRunnerInputs(
  workspaceId: string | undefined,
  relativePath: string | undefined,
  workspaceFile: WorkspaceFile
): WorkspaceDmnRunnerInput {
  const { dmnRunnerService, updatePersistedInputRows, getUniqueFileIdentifier } = useWorkspacesDmnRunnerInputs();
  const [inputRows, setInputRows] = useState<Array<InputRow>>([{}]);
  const [inputRowsUpdated, setInputRowsUpdated] = useState<boolean>(false);
  const [outputRowsUpdated, setOutputRowsUpdated] = useState<boolean>(false);
  const lastInputRows = useRef<string>("[{}]");

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }

        const uniqueFileIdentifier = getUniqueFileIdentifier({ workspaceId, relativePath });

        console.debug("Subscribing to " + uniqueFileIdentifier);
        const broadcastChannel = new BroadcastChannel(uniqueFileIdentifier);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceDmnRunnerEvents>) => {
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            if (canceled.get()) {
              return;
            }
            const newRelativePathWithoutExtension = data.newRelativePath.slice(
              0,
              data.newRelativePath.lastIndexOf(".")
            );
            dmnRunnerService?.renameDmnRunnerInputs(workspaceFile, newRelativePathWithoutExtension);
          }
          if (data.type === "UPDATE" || data.type === "ADD" || data.type === "DELETE") {
            if (canceled.get()) {
              return;
            }
            // Triggered by the tab
            if (data.dmnRunnerInputs === lastInputRows.current) {
              setInputRows(JSON.parse(data.dmnRunnerInputs));
              return;
            }
            // Triggered by the other tab
            lastInputRows.current = data.dmnRunnerInputs;
            setInputRows(JSON.parse(data.dmnRunnerInputs));
            setInputRowsUpdated(true);
          }
        };

        return () => {
          console.debug("Unsubscribing to " + uniqueFileIdentifier);
          broadcastChannel.close();
        };
      },
      [relativePath, workspaceId, getUniqueFileIdentifier, dmnRunnerService, workspaceFile]
    )
  );

  // On first render load the inputs;
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFile || !dmnRunnerService) {
          return;
        }

        dmnRunnerService.getDmnRunnerInputs(workspaceFile).then((inputs) => {
          if (canceled.get()) {
            return;
          }
          // If inputs don't exist, create then.
          if (!inputs) {
            dmnRunnerService.createOrOverwriteDmnRunnerInputs(workspaceFile, JSON.stringify([{}]));
            return;
          }

          inputs.getFileContents().then((content) => {
            if (canceled.get()) {
              return;
            }
            const inputRows = decoder.decode(content);
            lastInputRows.current = inputRows;
            setInputRows(JSON.parse(inputRows) as Array<InputRow>);
          });
        });
      },
      [dmnRunnerService, workspaceFile]
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
      if (typeof newInputRows === "function") {
        if (lastInputRows.current === JSON.stringify(newInputRows(inputRows))) {
          return;
        }
      }
      if (lastInputRows.current === JSON.stringify(newInputRows)) {
        return;
      }

      timeout.current = window.setTimeout(() => {
        updatePersistedInputRows(workspaceFile, newInputRows);
        if (typeof newInputRows === "function") {
          lastInputRows.current = JSON.stringify(newInputRows(inputRows));
          return;
        }
        lastInputRows.current = JSON.stringify(newInputRows);
      }, 200);
    },
    [inputRows, updatePersistedInputRows, workspaceFile]
  );

  return {
    inputRows,
    setInputRows: setInputRowsAndUpdatePersistence,
    inputRowsUpdated,
    setInputRowsUpdated,
    outputRowsUpdated,
    setOutputRowsUpdated,
  };
}

export type WorkspaceDmnRunnerEvents =
  | { type: "MOVE"; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME"; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE"; dmnRunnerInputs: string }
  | { type: "DELETE"; dmnRunnerInputs: string }
  | { type: "ADD"; relativePath: string; dmnRunnerInputs: string };
