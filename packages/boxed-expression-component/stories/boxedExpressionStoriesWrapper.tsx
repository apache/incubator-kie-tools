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

import * as React from "react";
import { useEffect, useMemo, useRef, useState } from "react";
import { useArgs } from "@storybook/preview-api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../src/expressions";
import {
  BeeGwtService,
  ContextExpressionDefinition,
  DecisionTableExpressionDefinition,
  DmnBuiltInDataType,
  ExpressionDefinition,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
  InvocationExpressionDefinition,
  ListExpressionDefinition,
  LiteralExpressionDefinition,
  RelationExpressionDefinition,
} from "../src/api";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../src/expressions/DecisionTableExpression";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
} from "../src/expressions/InvocationExpression";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
} from "../src/resizing/WidthConstants";

function getDefaultExpressionDefinitionByLogicType(
  logicType: ExpressionDefinition["__$$element"] | undefined,
  dataType: string,
  containerWidth: number
): ExpressionDefinition {
  if (!logicType) {
    return undefined as any;
  }
  if (logicType === "literalExpression") {
    const literalExpression: LiteralExpressionDefinition = {
      __$$element: "literalExpression",
      "@_typeRef": dataType,
      "@_id": generateUuid(),
    };
    return literalExpression;
  } else if (logicType === "functionDefinition") {
    const functionExpression: FunctionExpressionDefinition = {
      __$$element: "functionDefinition",
      "@_typeRef": dataType,
      "@_id": generateUuid(),
      "@_kind": FunctionExpressionDefinitionKind.Feel,
    };
    return functionExpression;
  } else if (logicType === "context") {
    const contextExpression: ContextExpressionDefinition = {
      __$$element: "context",
      "@_typeRef": dataType,
      "@_id": generateUuid(),
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
          },
          expression: undefined as any, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        },
      ],
    };
    return contextExpression;
  } else if (logicType === "list") {
    const listExpression: ListExpressionDefinition = {
      __$$element: "list",
      "@_typeRef": dataType,
      "@_id": generateUuid(),
      expression: [undefined as any], // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
    };
    return listExpression;
  } else if (logicType === "invocation") {
    const invocationExpression: InvocationExpressionDefinition = {
      __$$element: "invocation",
      "@_id": generateUuid(),
      "@_typeRef": dataType,
      binding: [
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            "@_typeRef": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
          expression: undefined as any, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
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
    const relationExpression: RelationExpressionDefinition = {
      __$$element: "relation",
      "@_typeRef": dataType,
      "@_id": generateUuid(),
      column: [
        {
          "@_id": generateUuid(),
          "@_name": "column-1",
        },
      ],
      row: [
        {
          "@_id": generateUuid(),
        },
      ],
    };
    return relationExpression;
  } else if (logicType === "decisionTable") {
    const decisionTableExpression: DecisionTableExpressionDefinition = {
      __$$element: "decisionTable",
      "@_id": generateUuid(),
      "@_typeRef": dataType,
      "@_hitPolicy": "UNIQUE",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-1" },
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_name": "output-1",
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
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE } }],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    };
    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}`);
  }
}

export const pmmlDocuments = [
  {
    document: "document",
    modelsFromDocument: [
      {
        model: "model",
        parametersFromModel: [{ "@_id": "p1", "@_name": "p-1", "@_typeRef": DmnBuiltInDataType.Number }],
      },
    ],
  },
  {
    document: "mining pmml",
    modelsFromDocument: [
      {
        model: "MiningModelSum",
        parametersFromModel: [{ "@_id": "i1", "@_name": "input1", "@_typeRef": DmnBuiltInDataType.Any }],
      },
    ],
  },
  {
    document: "regression pmml",
    modelsFromDocument: [
      {
        model: "RegressionLinear",
        parametersFromModel: [{ "@_id": "i1", "@_name": "i1", "@_typeRef": DmnBuiltInDataType.Number }],
      },
    ],
  },
];

export const dataTypes = [
  { name: "<Undefined>", isCustom: false },
  { name: "Any", isCustom: false },
  { name: "boolean", isCustom: false },
  { name: "context", isCustom: false },
  { name: "date", isCustom: false },
  { name: "date and time", isCustom: false },
  { name: "days and time duration", isCustom: false },
  { name: "number", isCustom: false },
  { name: "string", isCustom: false },
  { name: "time", isCustom: false },
  { name: "years and months duration", isCustom: false },
];

function getDefaultWidths(logicType: ExpressionDefinition["__$$element"] | undefined, id: string) {
  switch (logicType) {
    case "context":
      return new Map([[id, [CONTEXT_ENTRY_INFO_MIN_WIDTH, CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH]]]);

    case "literalExpression":
      return new Map([[id, [LITERAL_EXPRESSION_MIN_WIDTH]]]);

    case "relation":
      return new Map([[id, [100]]]);

    case "decisionTable":
      return new Map([
        [
          id,
          [
            BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
            DECISION_TABLE_INPUT_DEFAULT_WIDTH,
            DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
            DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
          ],
        ],
      ]);

    default:
      return new Map<string, number[]>();
  }
}

export const beeGwtService: BeeGwtService = {
  getDefaultExpressionDefinition(logicType: ExpressionDefinition["__$$element"] | undefined, dataType: string) {
    const expression = getDefaultExpressionDefinitionByLogicType(logicType, dataType, 0);
    return {
      expression: expression,
      widthsById: getDefaultWidths(logicType, expression["@_id"] ?? ""),
    };
  },
  openDataTypePage(): void {},
  selectObject(): void {},
};

export function BoxedExpressionEditorWrapper(props?: Partial<BoxedExpressionEditorProps>) {
  const emptyRef = useRef<HTMLDivElement>(null);
  const [args, updateArgs] = useArgs<BoxedExpressionEditorProps>();
  const argsCopy = useRef(args);
  const [expressionState, setExpressionState] = useState<ExpressionDefinition | undefined>(args.expression);
  const [widthsByIdState, setWidthsByIdState] = useState<Map<string, number[]>>(args.widthsById);

  const expression = useMemo(() => props?.expression ?? expressionState, [expressionState, props?.expression]);
  const widthsById = useMemo(
    () => props?.widthsById ?? widthsByIdState ?? new Map<string, number[]>(),
    [props?.widthsById, widthsByIdState]
  );

  const onExpressionChange = useMemo(
    () => (props?.onExpressionChange ? props.onExpressionChange : setExpressionState),
    [props?.onExpressionChange]
  );

  const onWidthsChange = useMemo(
    () => (props?.onWidthsChange ? props.onWidthsChange : setWidthsByIdState),
    [props?.onWidthsChange]
  );

  useEffect(() => {
    updateArgs({ ...argsCopy.current, expression, widthsById });
  }, [updateArgs, expression, widthsById]);

  useEffect(() => {
    if (args === argsCopy.current) {
      return;
    }
    onExpressionChange(args.expression);
    argsCopy.current = args;
  }, [args, onExpressionChange]);

  return (
    <div ref={emptyRef}>
      <BoxedExpressionEditor
        expressionHolderId={props?.expressionHolderId ?? args.expressionHolderId}
        expression={expression}
        onExpressionChange={onExpressionChange}
        onWidthsChange={onWidthsChange}
        dataTypes={props?.dataTypes ?? args.dataTypes}
        scrollableParentRef={props?.scrollableParentRef ?? emptyRef}
        beeGwtService={props?.beeGwtService ?? args.beeGwtService}
        pmmlDocuments={props?.pmmlDocuments ?? args.pmmlDocuments}
        isResetSupportedOnRootExpression={
          props?.isResetSupportedOnRootExpression ?? args.isResetSupportedOnRootExpression
        }
        widthsById={widthsById}
        expressionName={expression?.["@_label"]}
      />
    </div>
  );
}
