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
  BoxedContext,
  BoxedDecisionTable,
  BoxedExpression,
  BoxedFunction,
  BoxedFunctionKind,
  generateUuid,
  BoxedInvocation,
  BoxedList,
  BoxedLiteral,
  BoxedRelation,
} from "../../src/api";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
} from "../../src/expressions/InvocationExpression";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../../src/expressions/DecisionTableExpression";

export function getDefaultExpressionDefinitionByLogicType(
  logicType: BoxedExpression["__$$element"] | undefined,
  typeRef: string,
  containerWidth: number
): BoxedExpression {
  if (logicType === "literalExpression") {
    const literalExpression: BoxedLiteral = {
      __$$element: "literalExpression",
      "@_typeRef": typeRef,
      "@_id": generateUuid(),
    };
    return literalExpression;
  } else if (logicType === "functionDefinition") {
    const functionExpression: BoxedFunction = {
      __$$element: "functionDefinition",
      "@_typeRef": typeRef,
      "@_id": generateUuid(),
      "@_kind": BoxedFunctionKind.Feel,
      expression: {
        __$$element: "literalExpression",
        "@_id": generateUuid(),
      },
    };
    return functionExpression;
  } else if (logicType === "context") {
    const contextExpression: BoxedContext = {
      __$$element: "context",
      "@_typeRef": typeRef,
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
          },
          expression: {
            "@_id": generateUuid(),
            __$$element: "literalExpression",
            "@_label": "ContextEntry-1",
          },
        },
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-2",
          },
          expression: {
            "@_id": generateUuid(),
            __$$element: "literalExpression",
            "@_label": "ContextEntry-2",
          },
        },
      ],
    };
    return contextExpression;
  } else if (logicType === "list") {
    const listExpression: BoxedList = {
      __$$element: "list",
      "@_typeRef": typeRef,
      expression: [undefined!, undefined!, undefined!],
    };
    return listExpression;
  } else if (logicType === "invocation") {
    const invocationExpression: BoxedInvocation = {
      __$$element: "invocation",
      "@_typeRef": typeRef,
      binding: [
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            "@_typeRef": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
        },
      ],
      expression: {
        "@_id": generateUuid(),
        __$$element: "literalExpression",
        text: { __$$text: "FUNCTION" },
      },
    };
    return invocationExpression;
  } else if (logicType === "relation") {
    const relationExpression: BoxedRelation = {
      __$$element: "relation",
      "@_typeRef": typeRef,
      column: [
        {
          "@_id": generateUuid(),
          "@_name": "column-1",
        },
        {
          "@_id": generateUuid(),
          "@_name": "column-2",
        },
        {
          "@_id": generateUuid(),
          "@_name": "column-3",
        },
      ],
      row: [
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
          ],
        },
      ],
    };
    return relationExpression;
  } else if (logicType === "decisionTable") {
    const decisionTableExpression: BoxedDecisionTable = {
      __$$element: "decisionTable",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      "@_hitPolicy": "UNIQUE",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-1" },
          },
        },
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-2" },
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_label": "output-1",
        },
        {
          "@_id": generateUuid(),
          "@_label": "output-2",
        },
        {
          "@_id": generateUuid(),
          "@_label": "output-3",
        },
      ],
      annotation: [
        {
          "@_name": "annotation-1",
        },
      ],
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: [
            { "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE } },
            { "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE } },
          ],
          outputEntry: [
            { "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE } },
            { "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE } },
            { "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE } },
          ],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    };
    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}`);
  }
}
