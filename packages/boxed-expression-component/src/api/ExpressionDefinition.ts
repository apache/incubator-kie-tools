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

import {
  DMN15__tConditional,
  DMN15__tContext,
  DMN15__tDecisionTable,
  DMN15__tFilter,
  DMN15__tFor,
  DMN15__tFunctionDefinition,
  DMN15__tInformationItem,
  DMN15__tInvocation,
  DMN15__tList,
  DMN15__tLiteralExpression,
  DMN15__tQuantified,
  DMN15__tRelation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type LiteralExpressionDefinition = { __$$element: "literalExpression" } & DMN15__tLiteralExpression;
export type RelationExpressionDefinition = { __$$element: "relation" } & DMN15__tRelation;
export type ContextExpressionDefinition = { __$$element: "context" } & DMN15__tContext;
export type DecisionTableExpressionDefinition = { __$$element: "decisionTable" } & DMN15__tDecisionTable;
export type ListExpressionDefinition = { __$$element: "list" } & DMN15__tList;
export type InvocationExpressionDefinition = { __$$element: "invocation" } & DMN15__tInvocation;
export type FunctionExpressionDefinition = { __$$element: "functionDefinition" } & DMN15__tFunctionDefinition;
export type ForExpression = { __$$element: "for" } & DMN15__tFor;
export type EveryExpression = { __$$element: "every" } & DMN15__tQuantified;
export type SomeExpression = { __$$element: "some" } & DMN15__tQuantified;
export type ConditionalExpression = { __$$element: "conditional" } & DMN15__tConditional;
export type FilterExpression = { __$$element: "filter" } & DMN15__tFilter;

export enum FunctionExpressionDefinitionKind {
  Feel = "FEEL",
  Java = "Java",
  Pmml = "PMML",
}

export type ExpressionDefinition =
  | LiteralExpressionDefinition
  | RelationExpressionDefinition
  | ContextExpressionDefinition
  | DecisionTableExpressionDefinition
  | ListExpressionDefinition
  | InvocationExpressionDefinition
  | FunctionExpressionDefinition
  | ForExpression
  | EveryExpression
  | SomeExpression
  | ConditionalExpression
  | FilterExpression;

export interface PmmlDocument {
  document: string;
  modelsFromDocument?: {
    model: string;
    parametersFromModel?: DMN15__tInformationItem[];
  }[];
}

// Please find a better place for this function as part of https://github.com/kiegroup/kie-issues/issues/34
export const getNextAvailablePrefixedName = (
  names: string[],
  namePrefix: string,
  lastIndex: number = names.length
): string => {
  const candidate = `${namePrefix}-${lastIndex + 1}`;
  const elemWithCandidateName = names.indexOf(candidate);
  return elemWithCandidateName >= 0 ? getNextAvailablePrefixedName(names, namePrefix, lastIndex + 1) : candidate;
};
