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
  DMN15__tContextEntry,
  DMN15__tItemDefinition,
  DMN15__tOutputClause,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  BoxedContext,
  BoxedDecisionTable,
  BoxedExpression,
  BoxedFunction,
  BoxedFunctionKind,
  BoxedInvocation,
  BoxedList,
  BoxedLiteral,
  BoxedRelation,
  DmnBuiltInDataType,
  generateUuid,
} from "../src/api";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../src/expressions/DecisionTableExpression/DecisionTableExpression";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
} from "../src/expressions/InvocationExpression/InvocationExpression";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
} from "../src/resizing/WidthConstants";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "../src/expressions/RelationExpression/RelationExpression";

export function isStruct(itemDefinition: DMN15__tItemDefinition) {
  return !itemDefinition.typeRef && !!itemDefinition.itemComponent;
}

export function getDefaultBoxedExpressionForStories({
  logicType,
  typeRef,
  widthsById,
}: {
  logicType: BoxedExpression["__$$element"] | undefined;
  typeRef: string;
  widthsById: Map<string, number[]>;
}): BoxedExpression {
  if (logicType === "literalExpression") {
    const literalExpression: BoxedLiteral = {
      __$$element: "literalExpression",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
    };

    widthsById.set(literalExpression["@_id"]!, [LITERAL_EXPRESSION_MIN_WIDTH]);
    return literalExpression;
  }
  //
  else if (logicType === "functionDefinition") {
    const functionExpression: BoxedFunction = {
      __$$element: "functionDefinition",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      "@_kind": "FEEL",
      expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
    };
    return functionExpression;
  }
  //
  else if (logicType === "context") {
    const contextExpression: BoxedContext = {
      __$$element: "context",
      "@_typeRef": typeRef,
      "@_id": generateUuid(),
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
          },
          expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        },
        // <result>
        {
          "@_id": generateUuid(),
          expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        },
      ],
    };

    widthsById.set(contextExpression["@_id"]!, [CONTEXT_ENTRY_VARIABLE_MIN_WIDTH]);
    return contextExpression;
  } else if (logicType === "list") {
    const listExpression: BoxedList = {
      __$$element: "list",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      expression: [
        undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
      ],
    };
    return listExpression;
  } else if (logicType === "invocation") {
    const invocationExpression: BoxedInvocation = {
      __$$element: "invocation",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      binding: [
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            "@_typeRef": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
          expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        },
      ],
      expression: {
        __$$element: "literalExpression",
        "@_id": generateUuid(),
        text: { __$$text: "FUNCTION NAME" },
      },
    };
    widthsById.set(invocationExpression["@_id"]!, [CONTEXT_ENTRY_VARIABLE_MIN_WIDTH]);
    return invocationExpression;
  } else if (logicType === "relation") {
    const relationExpression: BoxedRelation = {
      __$$element: "relation",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      row: [
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: RELATION_EXPRESSION_DEFAULT_VALUE },
            },
          ],
        },
      ],
      column: [
        {
          "@_id": generateUuid(),
          "@_name": "column-1",
          "@_typeRef": DmnBuiltInDataType.Undefined,
        },
      ],
    };

    widthsById.set(relationExpression["@_id"]!, [
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
      RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
    ]);
    return relationExpression;
  } else if (logicType === "decisionTable") {
    const singleOutputColumn = {
      name: "output-1",
      typeRef: DmnBuiltInDataType.Undefined,
    };
    const singleInputColumn = {
      name: "input-1",
      typeRef: DmnBuiltInDataType.Undefined,
    };

    const input = [
      {
        "@_id": generateUuid(),
        inputExpression: {
          "@_id": generateUuid(),
          text: { __$$text: singleInputColumn.name },
          "@_typeRef": singleInputColumn.typeRef ?? DmnBuiltInDataType.Undefined,
        },
      },
    ];

    const output: DMN15__tOutputClause[] = [
      {
        "@_id": generateUuid(),
        "@_name": singleOutputColumn.name,
        "@_typeRef": singleOutputColumn.typeRef,
      },
    ];

    const decisionTableExpression: BoxedDecisionTable = {
      __$$element: "decisionTable",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      "@_hitPolicy": "UNIQUE",
      input,
      output,
      annotation: [
        {
          "@_name": "Annotations",
        },
      ],
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: input.map(() => ({
            "@_id": generateUuid(),
            text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE },
          })),
          outputEntry: output.map(() => ({
            "@_id": generateUuid(),
            text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
          })),
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    };

    widthsById.set(decisionTableExpression["@_id"]!, [
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
      DECISION_TABLE_INPUT_DEFAULT_WIDTH,
      DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
      DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
    ]);

    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}.`);
  }
}
