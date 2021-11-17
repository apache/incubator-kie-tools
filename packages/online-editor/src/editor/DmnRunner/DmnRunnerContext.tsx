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

import { DmnSchema } from "@kogito-tooling/form/dist/dmn";
import * as React from "react";
import { useContext } from "react";
import { DmnRunnerModelPayload, DmnRunnerService } from "./DmnRunnerService";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";

export interface DmnRunnerContextType {
  inputRows: Array<object>;
  currentInputRowIndex: number;
  error: boolean;
  isExpanded: boolean;
  mode: DmnRunnerMode;
  jsonSchema?: DmnSchema;
  service: DmnRunnerService;
  status: DmnRunnerStatus;
}

export interface DmnRunnerCallbacksContextType {
  preparePayload: (formData?: any) => Promise<DmnRunnerModelPayload>;
  setExpanded: React.Dispatch<React.SetStateAction<boolean>>;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  setCurrentInputRowIndex: React.Dispatch<React.SetStateAction<number>>;
  setMode: React.Dispatch<React.SetStateAction<DmnRunnerMode>>;
  setInputRows: React.Dispatch<React.SetStateAction<any>>;
}

export const DmnRunnerStateContext = React.createContext<DmnRunnerContextType>({} as any);
export const DmnRunnerDispatchContext = React.createContext<DmnRunnerCallbacksContextType>({} as any);

export function useDmnRunnerState() {
  return useContext(DmnRunnerStateContext);
}

export function useDmnRunnerDispatch() {
  return useContext(DmnRunnerDispatchContext);
}
