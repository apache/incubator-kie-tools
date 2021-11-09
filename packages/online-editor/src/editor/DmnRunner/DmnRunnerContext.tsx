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
  data: Array<object>;
  dataIndex: number;
  error: boolean;
  isExpanded: boolean;
  mode: DmnRunnerMode;
  schema?: DmnSchema;
  service: DmnRunnerService;
  status: DmnRunnerStatus;
}

export interface DmnRunnerCallbacksContextType {
  preparePayload: (formData?: any) => Promise<DmnRunnerModelPayload>;
  setExpanded: React.Dispatch<React.SetStateAction<boolean>>;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  setDataIndex: React.Dispatch<React.SetStateAction<number>>;
  setMode: React.Dispatch<DmnRunnerMode>;
  setData: React.Dispatch<React.SetStateAction<any>>;
}

export const DmnRunnerContext = React.createContext<DmnRunnerContextType>({} as any);
export const DmnRunnerCallbacksContext = React.createContext<DmnRunnerCallbacksContextType>({} as any);

export function useDmnRunner() {
  return useContext(DmnRunnerContext);
}

export function useDmnRunnerCallbacks() {
  return useContext(DmnRunnerCallbacksContext);
}
