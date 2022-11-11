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
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerInputsService } from "./DmnRunnerInputsService";
import { InputRow } from "@kie-tools/form-dmn";
import { DmnRunnerInputsDispatchContext } from "./DmnRunnerInputsDispatchContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useSyncedCompanionFs } from "../companionFs/CompanionFsHooks";

export function DmnRunnerInputsDispatchContextProvider(props: React.PropsWithChildren<{}>) {
  const dmnRunnerInputsService = useMemo(() => {
    return new DmnRunnerInputsService();
  }, []);

  useSyncedCompanionFs(dmnRunnerInputsService.companionFsService);

  const updatePersistedInputRows = useCallback(
    async (
      workspaceFile: WorkspaceFile,
      newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)
    ) => {
      if (typeof newInputRows === "function") {
        const inputs = await dmnRunnerInputsService.companionFsService.get({
          workspaceId: workspaceFile.workspaceId,
          workspaceFileRelativePath: workspaceFile.relativePath,
        });

        const content = await inputs?.getFileContents();
        const previousInputRows = dmnRunnerInputsService.parseDmnRunnerInputs(decoder.decode(content));
        await dmnRunnerInputsService.companionFsService.update(
          { workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath },
          dmnRunnerInputsService.stringifyDmnRunnerInputs(newInputRows, previousInputRows)
        );
      } else {
        await dmnRunnerInputsService.companionFsService.update(
          { workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath },
          dmnRunnerInputsService.stringifyDmnRunnerInputs(newInputRows)
        );
      }
    },
    [dmnRunnerInputsService]
  );

  const deletePersistedInputRows = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      await dmnRunnerInputsService.companionFsService.delete({
        workspaceId: workspaceFile.workspaceId,
        workspaceFileRelativePath: workspaceFile.relativePath,
      });
    },
    [dmnRunnerInputsService]
  );

  const getInputRowsForDownload = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const inputs = await dmnRunnerInputsService.companionFsService.get({
        workspaceId: workspaceFile.workspaceId,
        workspaceFileRelativePath: workspaceFile.relativePath,
      });
      return await inputs?.getFileContents().then((content) => new Blob([content], { type: "application/json" }));
    },
    [dmnRunnerInputsService]
  );

  const uploadInputRows = useCallback(
    async (workspaceFile: WorkspaceFile, file: File) => {
      const content = await new Promise<string>((res) => {
        const reader = new FileReader();
        reader.onload = (event: ProgressEvent<FileReader>) => res(decoder.decode(event.target?.result as ArrayBuffer));
        reader.readAsArrayBuffer(file);
      });
      await dmnRunnerInputsService.companionFsService.createOrOverwrite(
        { workspaceId: workspaceFile.workspaceId, workspaceFileRelativePath: workspaceFile.relativePath },
        content
      );
    },
    [dmnRunnerInputsService]
  );

  return (
    <DmnRunnerInputsDispatchContext.Provider
      value={{
        dmnRunnerInputsService,
        updatePersistedInputRows,
        deletePersistedInputRows,
        getInputRowsForDownload,
        uploadInputRows,
      }}
    >
      {props.children}
    </DmnRunnerInputsDispatchContext.Provider>
  );
}
