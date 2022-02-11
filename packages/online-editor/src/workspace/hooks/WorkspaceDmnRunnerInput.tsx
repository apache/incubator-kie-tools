/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React, { useCallback, useEffect, useRef, useState } from "react";
import { decoder, useWorkspacesDmnRunnerInputs, WorkspaceFile } from "../";
import { InputRow } from "../../editor/DmnRunner/DmnRunnerContext";
import { useCancelableEffect } from "../../reactExt/Hooks";

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
  const lastInputRows = useRef<string>();

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
            dmnRunnerService?.renameDmnRunnerData(workspaceFile, newRelativePathWithoutExtension);
          }
          if (data.type === "DELETE") {
            if (canceled.get()) {
              return;
            }
            dmnRunnerService?.delete(workspaceId);
          }
          if (data.type === "UPDATE") {
            if (canceled.get()) {
              return;
            }
            if (data.dmnRunnerData === lastInputRows.current) {
              return;
            }
            lastInputRows.current = data.dmnRunnerData;
            setInputRows(JSON.parse(data.dmnRunnerData));
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

        dmnRunnerService.getDmnRunnerData(workspaceFile).then((data) => {
          if (canceled.get()) {
            return;
          }
          if (!data) {
            return;
          }

          data.getFileContents().then((content) => {
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

  useEffect(() => {
    if (lastInputRows.current !== JSON.stringify(inputRows)) {
      lastInputRows.current = JSON.stringify(inputRows);

      const timeout = setTimeout(() => {
        updatePersistedInputRows(workspaceFile, inputRows);
      }, 200);
      return () => {
        clearTimeout(timeout);
      };
    }
  }, [inputRows, updatePersistedInputRows, workspaceFile]);

  const setInputRowsAndUpdatePersistence = useCallback(
    (newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)) => {
      if (typeof newInputRows === "function") {
        setInputRows((previous) => {
          const inputRows = newInputRows(previous);
          if (JSON.stringify(inputRows) === lastInputRows.current) {
            return previous;
          }
          return inputRows;
        });
        return;
      }
      if (JSON.stringify(newInputRows) === lastInputRows.current) {
        return;
      }
      setInputRows(newInputRows);
    },
    []
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
  | { type: "UPDATE"; dmnRunnerData: string }
  | { type: "DELETE"; relativePath: string }
  | { type: "ADD"; relativePath: string };
