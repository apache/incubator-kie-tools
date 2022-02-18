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
import { useCallback, useMemo } from "react";
import { WorkspacesDmnInputsContext } from "./WorkspacesDmnInputsContext";
import { decoder, useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { WorkspaceDmnRunnerInputsService } from "./WorkspaceDmnRunnerInputsService";
import { InputRow } from "../editor/DmnRunner/DmnRunnerContext";

export function WorkspacesDmnInputsContextProvider(props: React.PropsWithChildren<{}>) {
  const workspaces = useWorkspaces();

  const dmnRunnerService = useMemo(() => {
    return new WorkspaceDmnRunnerInputsService(workspaces.storageService);
  }, [workspaces.storageService]);

  const updatePersistedInputRows = useCallback(
    async (
      workspaceFile: WorkspaceFile,
      newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)
    ) => {
      if (typeof newInputRows === "function") {
        const inputs = await dmnRunnerService.getDmnRunnerInputs(workspaceFile);
        const previousInputRows = await inputs
          ?.getFileContents()
          .then((content) => JSON.parse(decoder.decode(content)) as Array<InputRow>);
        await dmnRunnerService.updateDmnRunnerInputs(
          workspaceFile,
          JSON.stringify(newInputRows(previousInputRows ?? [{}]))
        );
      } else {
        await dmnRunnerService.updateDmnRunnerInputs(workspaceFile, JSON.stringify(newInputRows));
      }
    },
    [dmnRunnerService]
  );

  const deletePersistedInputRows = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      await dmnRunnerService.deleteDmnRunnerInputs(workspaceFile);
    },
    [dmnRunnerService]
  );

  const getUniqueFileIdentifier = useCallback(
    (args: { workspaceId: string; relativePath: string }) => dmnRunnerService.getUniqueFileIdentifier(args),
    [dmnRunnerService]
  );

  const getInputRowsForDownload = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const inputs = await dmnRunnerService.getDmnRunnerInputs(workspaceFile);
      return await inputs?.getFileContents().then((content) => new Blob([content], { type: "application/json" }));
    },
    [dmnRunnerService]
  );

  const uploadInputRows = useCallback(
    async (workspaceFile: WorkspaceFile, file: File) => {
      const content = await new Promise<string>((res) => {
        const reader = new FileReader();
        reader.onload = (event: ProgressEvent<FileReader>) => res(decoder.decode(event.target?.result as ArrayBuffer));
        reader.readAsArrayBuffer(file);
      });
      await dmnRunnerService.createOrOverwriteDmnRunnerInputs(workspaceFile, content);
    },
    [dmnRunnerService]
  );

  return (
    <WorkspacesDmnInputsContext.Provider
      value={{
        dmnRunnerService,
        updatePersistedInputRows,
        getUniqueFileIdentifier,
        deletePersistedInputRows,
        getInputRowsForDownload,
        uploadInputRows,
      }}
    >
      {props.children}
    </WorkspacesDmnInputsContext.Provider>
  );
}
