/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { InputRow } from "@kie-tools/form-dmn";
import { UnitablesInputsConfigs } from "@kie-tools/unitables";

export enum DmnRunnerMode {
  FORM = "form",
  TABLE = "table",
}

interface DmnRunnerPersistenceJsonConfigs {
  version: string;
  mode: DmnRunnerMode;
  inputs: UnitablesInputsConfigs;
}

export interface DmnRunnerPersistenceJson {
  configs: DmnRunnerPersistenceJsonConfigs;
  inputs: Array<InputRow>;
}

export type DmnRunnerUpdatePersistenceJsonDeboucerArgs = {
  workspaceId: string;
  workspaceFileRelativePath: string;
  content: string;
  cancellationToken: Holder<boolean>;
};

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
  updatePersistenceJsonDebouce: (args: DmnRunnerUpdatePersistenceJsonDeboucerArgs) => void;
  workspaceFileRelativePath: string;
  workspaceId: string;
  shouldUpdateFs: boolean;
  cancellationToken: Holder<boolean>;
} & (DmnRunnerPersistenceReducerActionDefault | DmnRunnerPersistenceReducerActionPrevious);
