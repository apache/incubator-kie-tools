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

import { generateUuid, DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tConditional,
  DMN15__tContext,
  DMN15__tDecision,
  DMN15__tDecisionTable,
  DMN15__tDefinitions,
  DMN15__tFilter,
  DMN15__tFor,
  DMN15__tFunctionDefinition,
  DMN15__tInvocation,
  DMN15__tItemDefinition,
  DMN15__tList,
  DMN15__tQuantified,
  DMN15__tRelation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { DataTypeIndex } from "./DataTypes";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";

export function findDataTypeById({
  definitions,
  itemDefinitionId,
  allDataTypesById,
}: {
  allDataTypesById: DataTypeIndex;
  itemDefinitionId: string;
  definitions: DMN15__tDefinitions;
}) {
  const indexesPath: number[] = [];
  let current = allDataTypesById.get(itemDefinitionId);
  do {
    indexesPath.unshift(current!.index);
    current = current!.parentId ? allDataTypesById.get(current!.parentId) : undefined;
  } while (current);

  const last = indexesPath.pop()!; // Since we're using do-while, it's guaranteed we'll have at least one element on the `indexesPath` array.

  definitions.itemDefinition ??= [];
  let items = definitions.itemDefinition;
  for (const i of indexesPath) {
    items = items![i].itemComponent!;
  }
  const itemDefinition = items![last];
  return { items, itemDefinition, index: last };
}

export function getNewItemDefinition(partial?: Partial<DMN15__tItemDefinition>) {
  return {
    "@_id": generateUuid(),
    "@_name": "New data type",
    "@_isCollection": false,
    "@_typeLanguage": DMN15_SPEC.typeLanguage.default,
    ...(partial ?? {}),
  };
}

export function isCollection(itemDefinition: DMN15__tItemDefinition) {
  return itemDefinition["@_isCollection"] ?? false;
}

export function isStruct(itemDefinition: DMN15__tItemDefinition) {
  return !itemDefinition.typeRef && !!itemDefinition.itemComponent;
}

export const constrainableBuiltInFeelTypes = new Map<DmnBuiltInDataType, KIE__tConstraintType[]>([
  [DmnBuiltInDataType.Any, ["expression"]],
  [DmnBuiltInDataType.Boolean, []],
  [DmnBuiltInDataType.Context, []],
  [DmnBuiltInDataType.Number, ["expression", "enumeration", "range"]],
  [DmnBuiltInDataType.String, ["expression", "enumeration", "range"]],
  [DmnBuiltInDataType.DateTimeDuration, ["expression", "enumeration", "range"]],
  [DmnBuiltInDataType.YearsMonthsDuration, ["expression", "enumeration", "range"]],
  [DmnBuiltInDataType.Date, ["expression", "enumeration", "range"]],
  [DmnBuiltInDataType.Time, ["expression", "enumeration", "range"]],
  [DmnBuiltInDataType.DateTime, ["expression", "enumeration", "range"]],
]);

export function canHaveConstraints(itemDefinition: DMN15__tItemDefinition) {
  return (
    isCollection(itemDefinition) ||
    (!isStruct(itemDefinition) &&
      (constrainableBuiltInFeelTypes.get(itemDefinition.typeRef?.__$$text as DmnBuiltInDataType)?.length ?? 0) > 0)
  );
}

export function traverseItemDefinitions(
  items: DMN15__tItemDefinition[],
  consumer: (itemDefinition: DMN15__tItemDefinition) => void
) {
  for (let i = 0; i < (items.length ?? 0); i++) {
    consumer(items[i]);
    traverseItemDefinitions(items[i].itemComponent ?? [], consumer);
  }
}

export type AllExpressions = NonNullable<DMN15__tDecision["expression"]>;
export type AllExpressionsWithoutTypes = Omit<AllExpressions, "__$$element">;
export type AllExpressionTypes = AllExpressions["__$$element"];

// TypeRef'ed

export function traverseTypeRefedInExpressionHolders(
  expressionHolder:
    | (DMN15__tDecision & { __$$element: "decision" })
    | (DMN15__tBusinessKnowledgeModel & { __$$element: "businessKnowledgeModel" }),
  consumer: (typed: { "@_typeRef"?: string }) => void
) {
  if (expressionHolder.__$$element === "decision") {
    if (expressionHolder.expression) {
      traverseTypeRefedInExpressions(expressionHolder.expression, expressionHolder.expression?.__$$element, consumer);
    }
  } else if (expressionHolder.__$$element === "businessKnowledgeModel") {
    if (expressionHolder.encapsulatedLogic) {
      traverseTypeRefedInExpressions(expressionHolder.encapsulatedLogic, "functionDefinition", consumer);
    }
  } else {
    throw new Error(`Unknown type of expression holder '${(expressionHolder as any).__$$element}'`);
  }
}

// FIXME: `traverseTypeRefedInExpressions` could be refactored to be a special method that we execute inside the `consumer` of `traverseExpressions`.
export function traverseTypeRefedInExpressions(
  expression: AllExpressionsWithoutTypes | undefined,
  __$$element: AllExpressionTypes | undefined,
  consumer: (typed: { "@_typeRef"?: string }) => void
) {
  if (!expression || !__$$element) {
    return;
  }

  consumer(expression);

  if (__$$element === "literalExpression") {
    // Leaf expression.
  } else if (__$$element === "decisionTable") {
    for (const e of (expression as DMN15__tDecisionTable).input ?? []) {
      traverseTypeRefedInExpressions(e.inputExpression, "literalExpression", consumer);
    }

    for (const e of (expression as DMN15__tDecisionTable).output ?? []) {
      consumer(e);
      if (e.defaultOutputEntry) {
        consumer(e.defaultOutputEntry);
      }
      if (e.outputValues) {
        consumer(e.outputValues);
      }
    }
  } else if (__$$element === "relation") {
    for (const e of (expression as DMN15__tRelation).column ?? []) {
      consumer(e);
    }
    // Leaf expression.
  } else if (__$$element === "list") {
    for (const e of (expression as DMN15__tList).expression ?? []) {
      traverseTypeRefedInExpressions(e, e.__$$element, consumer);
    }
  } else if (__$$element === "context") {
    for (const e of (expression as DMN15__tContext).contextEntry ?? []) {
      if (e.variable) {
        consumer(e.variable);
      }
      traverseTypeRefedInExpressions(e.expression, e.expression?.__$$element, consumer);
    }
  } else if (__$$element === "invocation") {
    for (const e of (expression as DMN15__tInvocation).binding ?? []) {
      if (e.parameter) {
        consumer(e.parameter);
      }
      traverseTypeRefedInExpressions(e.expression, e.expression?.__$$element, consumer);
    }
  } else if (__$$element === "functionDefinition") {
    const e = expression as DMN15__tFunctionDefinition;
    traverseTypeRefedInExpressions(e.expression, e.expression?.__$$element, consumer);
  } else if (__$$element === "conditional") {
    const e = expression as DMN15__tConditional;
    traverseTypeRefedInExpressions(e.if.expression, e.if.expression?.__$$element, consumer);
    traverseTypeRefedInExpressions(e.then.expression, e.then.expression?.__$$element, consumer);
    traverseTypeRefedInExpressions(e.else.expression, e.else.expression?.__$$element, consumer);
  } else if (__$$element === "every") {
    const e = expression as DMN15__tQuantified;
    consumer(e.in);
    traverseTypeRefedInExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseTypeRefedInExpressions(e.satisfies.expression, e.satisfies.expression?.__$$element, consumer);
  } else if (__$$element === "some") {
    const e = expression as DMN15__tQuantified;
    consumer(e.in);
    traverseTypeRefedInExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseTypeRefedInExpressions(e.satisfies.expression, e.satisfies.expression?.__$$element, consumer);
  } else if (__$$element === "filter") {
    const e = expression as DMN15__tFilter;
    traverseTypeRefedInExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseTypeRefedInExpressions(e.match.expression, e.match.expression?.__$$element, consumer);
  } else if (__$$element === "for") {
    const e = expression as DMN15__tFor;
    consumer(e.in);
    traverseTypeRefedInExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseTypeRefedInExpressions(e.return.expression, e.return.expression?.__$$element, consumer);
  } else {
    throw new Error(`Unknown type of expression '${__$$element}'.`);
  }
}

// Expressions

export function traverseExpressionsInExpressionHolders(
  expressionHolder:
    | (DMN15__tDecision & { __$$element: "decision" })
    | (DMN15__tBusinessKnowledgeModel & { __$$element: "businessKnowledgeModel" }),
  consumer: (expression: AllExpressionsWithoutTypes | undefined, __$$element: AllExpressionTypes | undefined) => void
) {
  if (expressionHolder.__$$element === "decision") {
    if (expressionHolder.expression) {
      traverseExpressions(expressionHolder.expression, expressionHolder.expression?.__$$element, consumer);
    }
  } else if (expressionHolder.__$$element === "businessKnowledgeModel") {
    if (expressionHolder.encapsulatedLogic) {
      traverseExpressions(expressionHolder.encapsulatedLogic, "functionDefinition", consumer);
    }
  } else {
    throw new Error(`Unknown type of expression holder '${(expressionHolder as any).__$$element}'`);
  }
}

export function traverseExpressions(
  expression: AllExpressionsWithoutTypes | undefined,
  __$$element: AllExpressionTypes | undefined,
  consumer: (expression: AllExpressionsWithoutTypes | undefined, __$$element: AllExpressionTypes | undefined) => void
) {
  if (!expression || !__$$element) {
    return;
  }

  consumer(expression, __$$element);

  if (__$$element === "literalExpression") {
    // No nested expressions.
  } else if (__$$element === "decisionTable") {
    for (const e of (expression as DMN15__tDecisionTable).input ?? []) {
      traverseExpressions(e.inputExpression, "literalExpression", consumer);
    }
  } else if (__$$element === "relation") {
    // No nested expressions.
  } else if (__$$element === "list") {
    for (const e of (expression as DMN15__tList).expression ?? []) {
      traverseExpressions(e, e.__$$element, consumer);
    }
  } else if (__$$element === "context") {
    for (const e of (expression as DMN15__tContext).contextEntry ?? []) {
      traverseExpressions(e.expression, e.expression?.__$$element, consumer);
    }
  } else if (__$$element === "invocation") {
    for (const e of (expression as DMN15__tInvocation).binding ?? []) {
      traverseExpressions(e.expression, e.expression?.__$$element, consumer);
    }
  } else if (__$$element === "functionDefinition") {
    const e = expression as DMN15__tFunctionDefinition;
    traverseExpressions(e.expression, e.expression?.__$$element, consumer);
  } else if (__$$element === "conditional") {
    const e = expression as DMN15__tConditional;
    traverseExpressions(e.if.expression, e.if.expression?.__$$element, consumer);
    traverseExpressions(e.then.expression, e.then.expression?.__$$element, consumer);
    traverseExpressions(e.else.expression, e.else.expression?.__$$element, consumer);
  } else if (__$$element === "every") {
    const e = expression as DMN15__tQuantified;
    traverseExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseExpressions(e.satisfies.expression, e.satisfies.expression?.__$$element, consumer);
  } else if (__$$element === "some") {
    const e = expression as DMN15__tQuantified;
    traverseExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseExpressions(e.satisfies.expression, e.satisfies.expression?.__$$element, consumer);
  } else if (__$$element === "filter") {
    const e = expression as DMN15__tFilter;
    traverseExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseExpressions(e.match.expression, e.match.expression?.__$$element, consumer);
  } else if (__$$element === "for") {
    const e = expression as DMN15__tFor;
    traverseExpressions(e.in.expression, e.in.expression?.__$$element, consumer);
    traverseExpressions(e.return.expression, e.return.expression?.__$$element, consumer);
  } else {
    throw new Error(`Unknown type of expression '${__$$element}'.`);
  }
}
