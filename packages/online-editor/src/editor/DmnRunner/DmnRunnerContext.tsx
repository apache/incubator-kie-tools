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

import { InputRow, DmnSchema } from "@kie-tools/form-dmn";
import * as React from "react";
import { useContext } from "react";
import { KieSandboxExtendedServicesModelPayload } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";

export interface DmnRunnerContextType {
  currentInputRowIndex: number;
  error: boolean;
  inputRows: Array<InputRow>;
  isExpanded: boolean;
  isVisible: boolean;
  jsonSchema?: DmnSchema;
  mode: DmnRunnerMode;
  status: DmnRunnerStatus;
}

export interface DmnRunnerCallbacksContextType {
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDuplicated: (args: { rowIndex: number }) => void;
  onRowReset: (args: { rowIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
  preparePayload: (formData?: InputRow) => Promise<KieSandboxExtendedServicesModelPayload>;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  setExpanded: React.Dispatch<React.SetStateAction<boolean>>;
  setCurrentInputRowIndex: React.Dispatch<React.SetStateAction<number>>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<InputRow>>>;
  setMode: React.Dispatch<React.SetStateAction<DmnRunnerMode>>;
}

export const DmnRunnerStateContext = React.createContext<DmnRunnerContextType>({} as any);
export const DmnRunnerDispatchContext = React.createContext<DmnRunnerCallbacksContextType>({} as any);

export function useDmnRunnerState() {
  return useContext(DmnRunnerStateContext);
}

export function useDmnRunnerDispatch() {
  return useContext(DmnRunnerDispatchContext);
}
