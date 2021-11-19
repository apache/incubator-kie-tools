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

import { EvaluationStatus } from "./unitables";

export const normalResults = [
  [
    {
      decisionId: "",
      decisionName: "StringDecision",
      result: "this is a test",
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const multipleResults = [
  [
    {
      decisionId: "",
      decisionName: "BooleanDecision",
      result: true,
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
    {
      decisionId: "",
      decisionName: "NumberDecision",
      result: 10,
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const arrayResult = [
  [
    {
      decisionId: "",
      decisionName: "Array",
      result: [{ test: "this is a test" }],
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const arrayMultipleResults = [
  [
    {
      decisionId: "",
      decisionName: "Array",
      result: [{ test: "this is a test", fee: "1" }, { test: "second test" }],
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const objectResults = [
  [
    {
      decisionId: "",
      decisionName: "Object",
      result: { test: "abc" },
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const skippedResult = [
  [
    {
      decisionId: "",
      decisionName: "Skipped",
      result: null,
      messages: [],
      evaluationStatus: EvaluationStatus.SKIPPED,
    },
  ],
];

export const failedResult = [
  [
    {
      decisionId: "",
      decisionName: "Failed",
      result: null,
      messages: [],
      evaluationStatus: EvaluationStatus.FAILED,
    },
  ],
];
