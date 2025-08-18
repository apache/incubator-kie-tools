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
  DMN_LATEST__tConditional,
  DMN_LATEST__tContext,
  DMN_LATEST__tDecisionTable,
  DMN_LATEST__tFilter,
  DMN_LATEST__tFor,
  DMN_LATEST__tFunctionDefinition,
  DMN_LATEST__tInvocation,
  DMN_LATEST__tList,
  DMN_LATEST__tLiteralExpression,
  DMN_LATEST__tQuantified,
  DMN_LATEST__tRelation,
} from "@kie-tools/dmn-marshaller";

export type BoxedLiteral = { __$$element: "literalExpression" } & DMN_LATEST__tLiteralExpression;
export type BoxedRelation = { __$$element: "relation" } & DMN_LATEST__tRelation;
export type BoxedContext = { __$$element: "context" } & DMN_LATEST__tContext;
export type BoxedDecisionTable = { __$$element: "decisionTable" } & DMN_LATEST__tDecisionTable;
export type BoxedList = { __$$element: "list" } & DMN_LATEST__tList;
export type BoxedInvocation = { __$$element: "invocation" } & DMN_LATEST__tInvocation;
export type BoxedFunction = { __$$element: "functionDefinition" } & DMN_LATEST__tFunctionDefinition;
export type BoxedFor = { __$$element: "for" } & DMN_LATEST__tFor;
export type BoxedEvery = { __$$element: "every" } & DMN_LATEST__tQuantified;
export type BoxedSome = { __$$element: "some" } & DMN_LATEST__tQuantified;
export type BoxedConditional = { __$$element: "conditional" } & DMN_LATEST__tConditional;
export type BoxedFilter = { __$$element: "filter" } & DMN_LATEST__tFilter;

export type Normalized<T> = WithRequiredDeep<T, "@_id">;

type WithRequiredDeep<T, K extends keyof any> = T extends undefined
  ? T
  : T extends Array<infer U>
    ? Array<WithRequiredDeep<U, K>>
    : { [P in keyof T]: WithRequiredDeep<T[P], K> } & (K extends keyof T
        ? { [P in K]-?: NonNullable<WithRequiredDeep<T[P], K>> }
        : T);
