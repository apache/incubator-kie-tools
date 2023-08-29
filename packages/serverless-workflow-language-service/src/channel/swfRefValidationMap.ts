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

import { RefValidationMap } from "@kie-tools/json-yaml-language-service/dist/channel";

export const swfRefValidationMap: RefValidationMap = new Map([
  [
    {
      name: "auth",
      path: ["auth", "*", "name"],
    },
    [
      {
        path: ["functions", "*", "authRef"],
        type: "string",
      },
    ],
  ],
  [
    {
      name: "retries",
      path: ["retries", "*", "name"],
    },
    [
      {
        path: ["states", "*", "actions", "*", "retryRef"],
        type: "string",
      },
      {
        path: ["onEvents", "*", "actions", "*", "retryRef"],
        type: "string",
      },
    ],
  ],
  [
    {
      name: "events",
      path: ["events", "*", "name"],
    },
    [
      {
        path: ["onEvents", "*", "eventRefs"],
        type: "string",
        isArray: true,
      },
      {
        path: ["states", "*", "actions", "*", "functionRef", "name"],
        type: "string",
      },
      {
        path: ["onEvents", "*", "actions", "*", "eventRef", "resultEventRef"],
        type: "string",
      },
      {
        path: ["onEvents", "*", "actions", "*", "eventRef", "triggerEventRef"],
        type: "string",
      },
      {
        path: ["states", "*", "transition", "produceEvents", "*", "eventRef"],
        type: "string",
      },
      {
        path: ["states", "*", "dataConditions", "*", "transition", "produceEvents", "*", "eventRef"],
        type: "string",
      },
      {
        path: ["states", "*", "eventConditions", "*", "eventRef"],
        type: "string",
      },
      {
        path: ["states", "*", "eventConditions", "*", "transition", "produceEvents", "*", "eventRef"],
        type: "string",
      },
    ],
  ],
  [
    {
      name: "functions",
      path: ["functions", "*", "name"],
    },
    [
      {
        path: ["states", "*", "actions", "*", "functionRef"],
        type: "string",
      },
      {
        path: ["states", "*", "actions", "*", "functionRef", "refName"],
        type: "string",
      },
      {
        path: ["onEvents", "*", "actions", "*", "functionRef"],
        type: "string",
      },
      {
        path: ["onEvents", "*", "actions", "*", "functionRef", "refName"],
        type: "string",
      },
    ],
  ],
  [
    {
      name: "errors",
      path: ["errors", "*", "name"],
    },
    [
      {
        path: ["states", "*", "actions", "*", "retryableErrors"],
        type: "string",
        isArray: true,
      },
      {
        path: ["states", "*", "actions", "*", "nonRetryableErrors"],
        type: "string",
        isArray: true,
      },
      {
        path: ["states", "*", "onErrors", "*", "errorRef"],
        type: "string",
      },
      {
        path: ["states", "*", "onErrors", "*", "errorRefs"],
        type: "string",
        isArray: true,
      },
      {
        path: ["onEvents", "*", "actions", "*", "retryableErrors"],
        type: "string",
        isArray: true,
      },
      {
        path: ["onEvents", "*", "actions", "*", "nonRetryableErrors"],
        type: "string",
        isArray: true,
      },
    ],
  ],
  [
    {
      name: "states",
      path: ["states", "*", "name"],
    },
    [
      {
        path: ["start"],
        type: "string",
      },
      {
        path: ["start", "stateName"],
        type: "string",
      },
      {
        path: ["states", "*", "transition"],
        type: "string",
      },
      {
        path: ["states", "*", "transition", "nextState"],
        type: "string",
      },
      {
        path: ["states", "*", "compensatedBy"],
        type: "string",
      },
      {
        path: ["states", "*", "dataConditions", "*", "transition"],
        type: "string",
      },
      {
        path: ["states", "*", "dataConditions", "*", "transition", "nextState"],
        type: "string",
      },
      {
        path: ["states", "*", "eventConditions", "*", "transition"],
        type: "string",
      },
      {
        path: ["states", "*", "eventConditions", "*", "transition", "nextState"],
        type: "string",
      },
    ],
  ],
]);
