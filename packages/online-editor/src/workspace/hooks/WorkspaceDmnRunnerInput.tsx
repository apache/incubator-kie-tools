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

import { useCallback, useEffect, useMemo, useState } from "react";
import { decoder, useWorkspaces, WorkspaceFile } from "../WorkspacesContext";
import { useWorkspaceFilePromise } from "./WorkspaceFileHooks";
import { WorkspaceDmnRunnerDataService } from "../services/WorkspaceDmnRunnerDataService";
import { InputRow } from "../../editor/DmnRunner/DmnRunnerContext";

export function useWorkspaceDmnRunnerInput(
  workspaceId: string | undefined,
  relativePath: string | undefined,
  workspaceFile: WorkspaceFile
): [Array<InputRow>, (value: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)) => void] {
  const workspaces = useWorkspaces();
  const workspaceFilePromise = useWorkspaceFilePromise(workspaceId, relativePath);
  const [inputRows, setInputRows] = useState<Array<InputRow>>([{}]);

  const dmnRunnerInput = useMemo(() => {
    if (workspaceFile.extension !== "dmn") {
      return;
    }
    return new WorkspaceDmnRunnerDataService(workspaces.storageService);
  }, [workspaceFile.extension, workspaces.storageService]);

  const getInputRows = useCallback(() => {
    if (!workspaceFilePromise.data || !dmnRunnerInput) {
      return Promise.resolve([{}]);
    }
    return dmnRunnerInput.getDmnRunnerData(workspaceFilePromise.data).then((data) => {
      if (!data) {
        return [{}];
      }
      return data.getFileContents().then((content) => JSON.parse(decoder.decode(content)) as Array<object>);
    });
  }, [dmnRunnerInput, workspaceFilePromise.data]);

  const updateInputRows = useCallback(
    async (newInputRows: Array<object> | ((previous: Array<object>) => Array<object>)) => {
      if (!workspaceFilePromise.data || !dmnRunnerInput) {
        return;
      }
      if (typeof newInputRows === "function") {
        const data = await dmnRunnerInput.getDmnRunnerData(workspaceFilePromise.data);
        const currentInputRows = await data
          ?.getFileContents()
          .then((content) => JSON.parse(decoder.decode(content)) as Array<object>);
        await dmnRunnerInput.createOrOverwriteDmnRunnerData(
          workspaceFilePromise.data,
          newInputRows(currentInputRows ?? [{}])
        );
      } else {
        await dmnRunnerInput.createOrOverwriteDmnRunnerData(workspaceFilePromise.data, newInputRows);
      }
    },
    [dmnRunnerInput, workspaceFilePromise.data]
  );

  useEffect(() => {
    if (!workspaceFilePromise.data || !dmnRunnerInput) {
      return;
    }
    dmnRunnerInput.renameDmnRunnerData(workspaceFilePromise.data, workspaceFilePromise.data.relativePath);
  }, [dmnRunnerInput, inputRows, workspaceFilePromise.data]);

  useEffect(() => {
    let runEffect = true;
    getInputRows().then((inputRows) => {
      // avoid setState on unmounted component
      if (!runEffect) {
        return;
      }
      setInputRows(inputRows);
    });

    return () => {
      runEffect = false;
    };
  }, [getInputRows]);

  return [inputRows, updateInputRows];
}
