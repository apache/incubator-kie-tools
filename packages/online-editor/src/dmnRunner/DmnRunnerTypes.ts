/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { DecisionResult } from "@kie-tools/extended-services-api";

export interface DmnRunnerResults {
  results: Array<DecisionResult[] | undefined>;
  resultsDifference: Array<Array<object>>;
}

export enum DmnRunnerResultsActionType {
  CLONE_LAST,
  DEFAULT,
}

export interface DmnRunnerResultsAction {
  type: DmnRunnerResultsActionType;
  newResults?: Array<DecisionResult[] | undefined>;
}

export interface DmnRunnerProviderState {
  isExpanded: boolean;
  currentInputIndex: number;
}

export enum DmnRunnerProviderActionType {
  DEFAULT,
  ADD_ROW,
  TOGGLE_EXPANDED,
}

export interface DmnRunnerProviderActionToggleExpanded {
  type: DmnRunnerProviderActionType.TOGGLE_EXPANDED;
}

export interface DmnRunnerProviderActionAddRow {
  type: DmnRunnerProviderActionType.ADD_ROW;
  newState: (previous: DmnRunnerProviderState) => void;
}

export interface DmnRunnerProviderActionDefault {
  type: DmnRunnerProviderActionType.DEFAULT;
  newState: Partial<DmnRunnerProviderState>;
}

export type DmnRunnerProviderAction =
  | DmnRunnerProviderActionDefault
  | DmnRunnerProviderActionAddRow
  | DmnRunnerProviderActionToggleExpanded;
