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

import { DecisionResult } from "@kie-tools/extended-services-api/dist/dmnResult";
import { diff } from "deep-object-diff";

export function extractDifferences(
  currentDecisionResult: DecisionResult[] | undefined,
  previousDecisionResult: DecisionResult[] | undefined
): object[] {
  return (
    currentDecisionResult
      ?.map(
        (decisionResult, index): Partial<DecisionResult> => diff(previousDecisionResult?.[index] ?? [], decisionResult)
      )
      ?.map((difference) => {
        delete difference.messages;
        return difference;
      }) ?? []
  );
}

export function extractDifferencesFromArray(
  currentDecisionResults: Array<DecisionResult[] | undefined>,
  previousDecisionResults: Array<DecisionResult[] | undefined>
): object[][] {
  return currentDecisionResults.map((decisionResults, index) =>
    extractDifferences(decisionResults, previousDecisionResults[index])
  );
}
