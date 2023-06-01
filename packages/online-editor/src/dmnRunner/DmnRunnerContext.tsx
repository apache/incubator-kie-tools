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

import * as React from "react";
import { useContext } from "react";
import { DmnRunnerPersistenceJson } from "../dmnRunnerPersistence/DmnRunnerPersistenceTypes";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { UnitablesInputsConfigs } from "@kie-tools/unitables";
import { InputRow } from "@kie-tools/form-dmn";
import { DecisionResult, ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";
import { DmnRunnerProviderAction } from "./DmnRunnerTypes";

export interface DmnRunnerContextType {
  configs: UnitablesInputsConfigs;
  currentInputIndex: number;
  extendedServicesError: boolean;
  dmnRunnerKey: number;
  dmnRunnerPersistenceJson: DmnRunnerPersistenceJson;
  inputs: Array<InputRow>;
  isExpanded: boolean;
  canBeVisualized: boolean;
  jsonSchema?: ExtendedServicesDmnJsonSchema;
  mode: DmnRunnerMode;
  results: Array<DecisionResult[] | undefined>;
  resultsDifference: Array<Array<object>>;
  status: DmnRunnerStatus;
}

export interface DmnRunnerCallbacksContextType {
  setDmnRunnerContextProviderState: React.Dispatch<DmnRunnerProviderAction>;
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDuplicated: (args: { rowIndex: number }) => void;
  onRowReset: (args: { rowIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
  setDmnRunnerInputs: (newInputsRow: (previousInputs: Array<InputRow>) => Array<InputRow> | Array<InputRow>) => void;
  setDmnRunnerMode: (newMode: DmnRunnerMode) => void;
  setDmnRunnerConfigInputs: (
    newConfigInputs: (previousConfigInputs: UnitablesInputsConfigs) => UnitablesInputsConfigs | UnitablesInputsConfigs
  ) => void;
  setDmnRunnerPersistenceJson: (args: {
    newInputsRow?: (previousInputs: Array<InputRow>) => Array<InputRow> | Array<InputRow>;
    newMode?: DmnRunnerMode;
    newConfigInputs?: (previousConfigInputs: UnitablesInputsConfigs) => UnitablesInputsConfigs | UnitablesInputsConfigs;
  }) => void;
}

export const DmnRunnerStateContext = React.createContext<DmnRunnerContextType>({} as any);
export const DmnRunnerDispatchContext = React.createContext<DmnRunnerCallbacksContextType>({} as any);

export function useDmnRunnerState() {
  return useContext(DmnRunnerStateContext);
}

export function useDmnRunnerDispatch() {
  return useContext(DmnRunnerDispatchContext);
}
