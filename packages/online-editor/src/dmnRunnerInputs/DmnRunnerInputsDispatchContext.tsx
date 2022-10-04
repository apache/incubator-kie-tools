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

import { createContext, useContext } from "react";
import { InputRow } from "@kie-tools/form-dmn";
import { DmnRunnerInputsService } from "./DmnRunnerInputsService";
import { WorkspaceFile } from "../workspace/WorkspacesContext";

interface DmnInputsDispatchContextType {
  dmnRunnerInputsService: DmnRunnerInputsService;
  deletePersistedInputRows: (workspaceFile: WorkspaceFile) => void;
  updatePersistedInputRows: (
    workspaceFile: WorkspaceFile,
    newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)
  ) => void;
  getInputRowsForDownload: (workspaceFile: WorkspaceFile) => Promise<Blob | undefined>;
  uploadInputRows: (workspaceFile: WorkspaceFile, file: File) => void;
}

export const DmnRunnerInputsDispatchContext = createContext<DmnInputsDispatchContextType>({} as any);

export function useDmnRunnerInputsDispatch(): DmnInputsDispatchContextType {
  return useContext(DmnRunnerInputsDispatchContext);
}
