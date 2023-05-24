/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { NotificationSeverity } from "@kie-tools-core/notifications/dist/api";

export enum DmnEvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED",
}

export interface DmnEvaluationMessages {
  severity: NotificationSeverity;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type DmnEvaluationResult =
  | boolean
  | number
  | null
  | Record<string, any>
  | Record<string, any>[]
  | string
  | DmnEvaluationResult[];

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: DmnEvaluationResult;
  messages?: DmnEvaluationMessages[];
  evaluationStatus: DmnEvaluationStatus;
}

// Result returned from extended-services /jitdmn/dmnresult;
export interface ExtendedServicesDmnResult {
  decisionResults?: DecisionResult[];
  dmnContext?: Record<string, any>;
  messages: DmnEvaluationMessages[];
  namespace?: string;
}
