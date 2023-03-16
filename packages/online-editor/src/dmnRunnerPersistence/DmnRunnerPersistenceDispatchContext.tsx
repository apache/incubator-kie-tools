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
import { DmnRunnerPersistenceService, DmnRunnerPersistenceJson } from "./DmnRunnerPersistenceService";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerPersistenceDebouncer } from "./DmnRunnerPersistenceDebouncer";

export enum DmnRunnerPersistenceReducerActionType {
  DEFAULT,
  PREVIOUS,
}

export interface DmnRunnerPersistenceReducerActionPrevious {
  type: DmnRunnerPersistenceReducerActionType.PREVIOUS;
  newPersistenceJson: (previous: DmnRunnerPersistenceJson) => DmnRunnerPersistenceJson;
}

export interface DmnRunnerPersistenceReducerActionDefault {
  type: DmnRunnerPersistenceReducerActionType.DEFAULT;
  newPersistenceJson: DmnRunnerPersistenceJson;
}

export type DmnRunnerPersistenceReducerAction = {
  dmnRunnerPersistenceDebouncer: DmnRunnerPersistenceDebouncer;
  workspaceFileRelativePath: string;
  workspaceId: string;
} & (DmnRunnerPersistenceReducerActionDefault | DmnRunnerPersistenceReducerActionPrevious);

interface DmnRunnerPersistenceDispatchContextType {
  dmnRunnerPersistenceService: DmnRunnerPersistenceService;
  deletePersistenceJson: (
    previousDmnRunnerPersisnteceJson: DmnRunnerPersistenceJson,
    workspaceFile: WorkspaceFile
  ) => void;
  getPersistenceJsonForDownload: (workspaceFile: WorkspaceFile) => Promise<Blob | undefined>;
  uploadPersistenceJson: (workspaceFile: WorkspaceFile, file: File) => void;
  dmnRunnerPersistenceJson: DmnRunnerPersistenceJson;
  dispatchDmnRunnerPersistenceJson: React.Dispatch<DmnRunnerPersistenceReducerAction>;
}

export const DmnRunnerPersistenceDispatchContext = createContext<DmnRunnerPersistenceDispatchContextType>({} as any);

export function useDmnRunnerPersistenceDispatch(): DmnRunnerPersistenceDispatchContextType {
  return useContext(DmnRunnerPersistenceDispatchContext);
}
