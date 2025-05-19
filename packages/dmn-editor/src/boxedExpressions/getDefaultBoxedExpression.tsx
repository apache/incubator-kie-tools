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
  BoxedConditional,
  BoxedContext,
  BoxedDecisionTable,
  BoxedEvery,
  BoxedExpression,
  BoxedFilter,
  BoxedFor,
  BoxedFunction,
  BoxedInvocation,
  BoxedList,
  BoxedLiteral,
  BoxedRelation,
  BoxedSome,
  DmnBuiltInDataType,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
} from "@kie-tools/boxed-expression-component/dist/resizing/WidthConstants";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "@kie-tools/boxed-expression-component/dist/expressions/DecisionTableExpression/DecisionTableExpression";
import { INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME } from "@kie-tools/boxed-expression-component/dist/expressions/InvocationExpression/InvocationExpression";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "@kie-tools/boxed-expression-component/dist/expressions/RelationExpression/RelationExpression";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { isStruct } from "../dataTypes/DataTypeSpec";
import {
  DMN15__tContextEntry,
  DMN15__tOutputClause,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";

export function getDefaultBoxedExpression({
  logicType,
  typeRef,
  allTopLevelDataTypesByFeelName,
  widthsById,
  getInputs,
  getDefaultColumnWidth,
}: {
  logicType: Normalized<BoxedExpression>["__$$element"] | undefined;
  typeRef: string | undefined;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  getInputs?: () => { name: string; typeRef: string | undefined }[] | undefined;
  getDefaultColumnWidth?: (args: { name: string; typeRef: string | undefined }) => number | undefined;
  widthsById: Map<string, number[]>;
}): Normalized<BoxedExpression> {
  const dataType = allTopLevelDataTypesByFeelName.get(typeRef ?? DmnBuiltInDataType.Undefined);

  if (logicType === "literalExpression") {
    const literalExpression: Normalized<BoxedLiteral> = {
      __$$element: "literalExpression",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
    };

    widthsById.set(literalExpression["@_id"]!, [LITERAL_EXPRESSION_MIN_WIDTH]);
    return literalExpression;
  }
  //
  else if (logicType === "functionDefinition") {
    const functionExpression: Normalized<BoxedFunction> = {
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
    let maxWidthBasedOnEntryNames = CONTEXT_ENTRY_VARIABLE_MIN_WIDTH;

    let contextEntries: Normalized<DMN15__tContextEntry>[];
    if (!dataType || !isStruct(dataType.itemDefinition)) {
      contextEntries = [
        {
          "@_id": generateUuid(),
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
          },
          expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        },
      ];
    } else {
      contextEntries = (dataType.itemDefinition.itemComponent ?? []).map((ic) => {
        const name = ic["@_name"];
        const typeRef = isStruct(ic) ? DmnBuiltInDataType.Any : ic.typeRef?.__$$text;
        maxWidthBasedOnEntryNames = Math.max(
          maxWidthBasedOnEntryNames,
          getDefaultColumnWidth?.({ name, typeRef }) ?? CONTEXT_ENTRY_VARIABLE_MIN_WIDTH
        );
        return {
          "@_id": generateUuid(),
          variable: {
            "@_id": generateUuid(),
            "@_name": name,
            "@_typeRef": typeRef,
          },
          expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        };
      });
    }

    // context <result> cell
    contextEntries.push({
      "@_id": generateUuid(),
      expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
    });

    const contextExpression: Normalized<BoxedContext> = {
      __$$element: "context",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      contextEntry: contextEntries,
    };

    widthsById.set(contextExpression["@_id"]!, [maxWidthBasedOnEntryNames]);
    return contextExpression;
  } else if (logicType === "list") {
    const listExpression: Normalized<BoxedList> = {
      __$$element: "list",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      expression: [
        undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
      ],
    };
    return listExpression;
  } else if (logicType === "invocation") {
    const invocationExpression: Normalized<BoxedInvocation> = {
      __$$element: "invocation",
      "@_id": generateUuid(),
      "@_typeRef": typeRef,
      binding: [
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            "@_typeRef": undefined,
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
    const isSimple = !dataType || !isStruct(dataType.itemDefinition);

    const relationExpression: Normalized<BoxedRelation> = {
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
      column: isSimple
        ? [
            {
              "@_id": generateUuid(),
              "@_name": dataType?.itemDefinition["@_name"] ?? "column-1",
              "@_typeRef": dataType?.feelName,
            },
          ]
        : (dataType.itemDefinition.itemComponent ?? []).map((ic) => {
            const name = ic["@_name"];
            const typeRef = isStruct(ic) ? DmnBuiltInDataType.Any : ic.typeRef?.__$$text;
            return {
              "@_id": generateUuid(),
              "@_name": name,
              "@_typeRef": typeRef,
            };
          }),
    };

    widthsById.set(relationExpression["@_id"]!, [
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
      ...(relationExpression.column ?? []).map(
        (c) =>
          getDefaultColumnWidth?.({
            name: c["@_name"],
            typeRef: c["@_typeRef"],
          }) ?? RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH
      ),
    ]);
    return relationExpression;
  } else if (logicType === "decisionTable") {
    const singleOutputColumn = {
      name: undefined,
      typeRef: dataType?.feelName,
    };
    const singleInputColumn = {
      name: "Input-1",
      typeRef: undefined,
    };

    const input = getInputs?.()?.map((input) => ({
      "@_id": generateUuid(),
      inputExpression: {
        "@_id": generateUuid(),
        text: { __$$text: input.name },
        "@_typeRef": input.typeRef,
      },
    })) ?? [
      {
        "@_id": generateUuid(),
        inputExpression: {
          "@_id": generateUuid(),
          text: { __$$text: singleInputColumn.name },
          "@_typeRef": singleInputColumn.typeRef,
        },
      },
    ];

    const output: Normalized<DMN15__tOutputClause>[] =
      !dataType || !isStruct(dataType.itemDefinition)
        ? [
            {
              "@_id": generateUuid(),
              "@_name": singleOutputColumn.name,
              "@_typeRef": singleOutputColumn.typeRef,
            },
          ]
        : (dataType.itemDefinition.itemComponent ?? []).map((ic) => ({
            "@_id": generateUuid(),
            "@_name": ic["@_name"],
            "@_typeRef": isStruct(ic) ? DmnBuiltInDataType.Any : ic.typeRef?.__$$text,
          }));

    const decisionTableExpression: Normalized<BoxedDecisionTable> = {
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
      ...(decisionTableExpression.input ?? []).map(
        (input) =>
          getDefaultColumnWidth?.({
            name: input["@_label"] ?? input.inputExpression["@_label"] ?? input.inputExpression.text?.__$$text ?? "",
            typeRef: input.inputExpression["@_typeRef"],
          }) ?? DECISION_TABLE_INPUT_DEFAULT_WIDTH
      ),
      ...(decisionTableExpression.output ?? []).map(
        (output) =>
          getDefaultColumnWidth?.({
            name: output["@_label"] ?? output["@_name"] ?? "",
            typeRef: output["@_typeRef"],
          }) ?? DECISION_TABLE_OUTPUT_DEFAULT_WIDTH
      ),
      ...(decisionTableExpression.annotation ?? []).map(() => DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH),
    ]);

    return decisionTableExpression;
  } else if (logicType === "conditional") {
    const conditionalExpression: Normalized<BoxedConditional> = {
      "@_id": generateUuid(),
      __$$element: "conditional",
      if: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
      then: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
      else: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
    };

    return conditionalExpression;
  } else if (logicType === "for") {
    const forExpression: Normalized<BoxedFor> = {
      "@_id": generateUuid(),
      __$$element: "for",
      return: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
      in: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
    };
    return forExpression;
  } else if (logicType == "some") {
    const someExpression: Normalized<BoxedSome> = {
      "@_id": generateUuid(),
      __$$element: "some",
      satisfies: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
      in: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
    };
    return someExpression;
  } else if (logicType === "every") {
    const everyExpression: Normalized<BoxedEvery> = {
      "@_id": generateUuid(),
      __$$element: "every",
      satisfies: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
      in: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
    };
    return everyExpression;
  } else if (logicType === "filter") {
    const filterExpression: Normalized<BoxedFilter> = {
      "@_id": generateUuid(),
      __$$element: "filter",
      match: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
      in: {
        "@_id": generateUuid(),
        expression: undefined as any,
      },
    };
    return filterExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}.`);
  }
}
