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

export type TargetPathType = { path: string[]; type: "string[]" } | { path: string[]; type: "string" };

export const sourcePaths: Map<string, string[]> = new Map([
  ["auth", ["auth", "*", "name"]],
  ["retries", ["retries", "*", "name"]],
  ["events", ["events", "*", "name"]],
  ["functions", ["functions", "*", "name"]],
  ["errors", ["errors", "*", "name"]],
  ["states", ["states", "*", "name"]],
]);

export const targetPaths: Map<string, TargetPathType[]> = new Map([
  ["auth", [{ path: ["functions", "*", "authRef"], type: "string" }]],
  [
    "retries",
    [
      { path: ["states", "*", "actions", "*", "retryRef"], type: "string" },
      { path: ["onEvents", "*", "actions", "*", "retryRef"], type: "string" },
    ],
  ],
  [
    "events",
    [
      { path: ["onEvents", "*", "eventRefs"], type: "string[]" },
      { path: ["states", "*", "actions", "*", "functionRef", "name"], type: "string" },
      { path: ["onEvents", "*", "actions", "*", "eventRef", "resultEventRef"], type: "string" },
      { path: ["onEvents", "*", "actions", "*", "eventRef", "triggerEventRef"], type: "string" },
      { path: ["states", "*", "transition", "produceEvents", "*", "eventRef"], type: "string" },
      { path: ["states", "*", "dataConditions", "*", "transition", "produceEvents", "*", "eventRef"], type: "string" },
      { path: ["states", "*", "eventConditions", "*", "eventRef"], type: "string" },
      { path: ["states", "*", "eventConditions", "*", "transition", "produceEvents", "*", "eventRef"], type: "string" },
    ],
  ],
  [
    "functions",
    [
      { path: ["states", "*", "actions", "*", "functionRef"], type: "string" },
      { path: ["states", "*", "actions", "*", "functionRef", "refName"], type: "string" },
      { path: ["onEvents", "*", "actions", "*", "functionRef"], type: "string" },
      { path: ["onEvents", "*", "actions", "*", "functionRef", "refName"], type: "string" },
    ],
  ],
  [
    "errors",
    [
      { path: ["states", "*", "actions", "*", "retryableErrors"], type: "string[]" },
      { path: ["states", "*", "actions", "*", "nonRetryableErrors"], type: "string[]" },
      { path: ["states", "*", "onErrors", "*", "errorRef"], type: "string" },
      { path: ["states", "*", "onErrors", "*", "errorRefs"], type: "string[]" },
      { path: ["onEvents", "*", "actions", "*", "retryableErrors"], type: "string[]" },
      { path: ["onEvents", "*", "actions", "*", "nonRetryableErrors"], type: "string[]" },
    ],
  ],
  [
    "states",
    [
      { path: ["start"], type: "string" },
      { path: ["start", "stateName"], type: "string" },
      { path: ["states", "*", "transition"], type: "string" },
      { path: ["states", "*", "transition", "nextState"], type: "string" },
      { path: ["states", "*", "compensatedBy"], type: "string" },
      { path: ["states", "*", "dataConditions", "*", "transition"], type: "string" },
      { path: ["states", "*", "dataConditions", "*", "transition", "nextState"], type: "string" },
      { path: ["states", "*", "eventConditions", "*", "transition"], type: "string" },
      { path: ["states", "*", "eventConditions", "*", "transition", "nextState"], type: "string" },
    ],
  ],
]);
