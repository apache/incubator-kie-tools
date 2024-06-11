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

import { BoxedExpression } from "@kie-tools/boxed-expression-component/dist/api";
import { GwtExpressionDefinition, GwtExpressionDefinitionLogicType } from "./types";
import { gwtToBee } from "./gwtToBee";
import { beeToGwt } from "./beeToGwt";

export function gwtExpressionToDmnExpression(gwtExpression: GwtExpressionDefinition): {
  expression: BoxedExpression;
  widthsById: Map<string, number[]>;
} {
  const widthsById = new Map<string, number[]>();
  return { expression: gwtToBee(gwtExpression, widthsById), widthsById: widthsById };
}

export function dmnExpressionToGwtExpression(
  widthsById: Map<string, number[]>,
  expression: BoxedExpression | undefined
): GwtExpressionDefinition {
  return beeToGwt(widthsById, expression);
}

export function gwtLogicType(logicType: BoxedExpression["__$$element"] | undefined): GwtExpressionDefinitionLogicType {
  switch (logicType) {
    case undefined:
      return GwtExpressionDefinitionLogicType.Undefined;
    case "literalExpression":
      return GwtExpressionDefinitionLogicType.Literal;
    case "context":
      return GwtExpressionDefinitionLogicType.Context;
    case "decisionTable":
      return GwtExpressionDefinitionLogicType.DecisionTable;
    case "functionDefinition":
      return GwtExpressionDefinitionLogicType.Function;
    case "invocation":
      return GwtExpressionDefinitionLogicType.Invocation;
    case "list":
      return GwtExpressionDefinitionLogicType.List;
    case "relation":
      return GwtExpressionDefinitionLogicType.Relation;
    default:
      throw new Error("Unsupported expression type: " + logicType);
  }
}
