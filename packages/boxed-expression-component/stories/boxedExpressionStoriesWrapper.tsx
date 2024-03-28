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
import { useEffect, useRef, useState, useCallback, useMemo } from "react";
import { useArgs } from "@storybook/preview-api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../src/expressions";
import {
  BeeGwtService,
  BoxedContext,
  BoxedDecisionTable,
  DmnBuiltInDataType,
  BoxedExpression,
  BoxedFunction,
  BoxedFunctionKind,
  generateUuid,
  BoxedInvocation,
  BoxedList,
  BoxedLiteral,
  BoxedRelation,
} from "../src/api";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../src/expressions/DecisionTableExpression";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
} from "../src/expressions/InvocationExpression";

function getDefaultExpressionDefinitionByLogicType(
  logicType: BoxedExpression["__$$element"] | undefined,
  typeRef: string
): BoxedExpression {
  if (!logicType) {
    return undefined as any;
  }
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
    };
    return functionExpression;
  } else if (logicType === "context") {
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
          expression: undefined as any, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        },
      ],
    };
    return contextExpression;
  } else if (logicType === "list") {
    const listExpression: BoxedList = {
      __$$element: "list",
      "@_typeRef": typeRef,
      "@_id": generateUuid(),
      expression: [undefined as any], // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
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
    const relationExpression: BoxedRelation = {
      __$$element: "relation",
      "@_typeRef": typeRef,
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
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_name": "output-1",
        },
      ],
      annotation: [
        {
          "@_name": "Annotations",
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

export const beeGwtService: BeeGwtService = {
  getDefaultExpressionDefinition(logicType: BoxedExpression["__$$element"] | undefined, dataType: string) {
    const expression = getDefaultExpressionDefinitionByLogicType(logicType, dataType);
    return {
      expression: expression,
      widthsById: new Map(),
    };
  },
  openDataTypePage(): void {},
  selectObject(): void {},
};

type StorybookArgWidhtsById = Record<string, number[]>;

export type BoxedExpressionEditorStoryArgs = Omit<BoxedExpressionEditorProps, "widthsById" | "onWidthsChange"> & {
  widthsById?: Record<string, number[]>;
  onWidthsChange?: React.Dispatch<React.SetStateAction<Record<string, number[]>>>;
};

export function BoxedExpressionEditorStory(props?: Partial<BoxedExpressionEditorStoryArgs>) {
  const emptyRef = useRef<HTMLDivElement>(null);
  const [args, updateArgs] = useArgs<BoxedExpressionEditorStoryArgs>();
  const [expressionState, setExpressionState] = useState<BoxedExpression | undefined>(
    args?.expression ?? props?.expression
  );

  const [widthsByIdState, setWidthsByIdState] = useState<StorybookArgWidhtsById>(
    args.widthsById ?? props?.widthsById ?? {}
  );

  const onWidthsChange = useCallback(
    (newWidthsById) => {
      if (typeof newWidthsById === "function") {
        setWidthsByIdState((prev: Record<string, number[]>) => {
          const newWidhtsByIdState = toObject(newWidthsById(toMap(prev)));
          updateArgs({ widthsById: newWidhtsByIdState });
          return newWidhtsByIdState;
        });
      } else {
        setWidthsByIdState(toObject(newWidthsById));
        updateArgs({ widthsById: toObject(newWidthsById) });
      }
    },
    [updateArgs]
  );

  const widthsByIdMap = useMemo(() => toMap(widthsByIdState), [widthsByIdState]);

  useEffect(() => {
    setExpressionState(props?.expression);
  }, [props?.expression]);

  useEffect(() => {
    setExpressionState(args?.expression);
  }, [args?.expression]);

  // Args were updated, should update the state!
  useEffect(() => {
    setWidthsByIdState((prev) => {
      if (args.widthsById === undefined || JSON.stringify(prev) === JSON.stringify(args.widthsById)) {
        return prev;
      }
      return args.widthsById;
    });
  }, [args.widthsById]);

  // Props were updated, should update the state and the args!
  useEffect(() => {
    setWidthsByIdState((prev) => {
      if (props?.widthsById === undefined || JSON.stringify(prev) === JSON.stringify(props?.widthsById)) {
        return prev;
      }
      updateArgs({ widthsById: props?.widthsById });
      return props?.widthsById;
    });
  }, [props?.widthsById, updateArgs]);

  // Keep expression args in sync with state
  useEffect(() => {
    updateArgs({ expression: expressionState });
  }, [updateArgs, expressionState]);

  return (
    <div
      ref={emptyRef}
      onKeyDown={(e) => {
        // Prevent keys from propagating to Storybook
        return e.stopPropagation();
      }}
    >
      <BoxedExpressionEditor
        expressionHolderId={props?.expressionHolderId ?? args?.expressionHolderId ?? ""}
        expressionHolderTypeRef={DmnBuiltInDataType.Undefined}
        expression={expressionState}
        onExpressionChange={setExpressionState}
        onWidthsChange={onWidthsChange}
        dataTypes={props?.dataTypes ?? args?.dataTypes ?? dataTypes}
        scrollableParentRef={props?.scrollableParentRef ?? args?.scrollableParentRef ?? emptyRef}
        beeGwtService={props?.beeGwtService ?? args?.beeGwtService ?? beeGwtService}
        pmmlDocuments={props?.pmmlDocuments ?? args?.pmmlDocuments ?? pmmlDocuments}
        isResetSupportedOnRootExpression={
          props?.isResetSupportedOnRootExpression ?? args?.isResetSupportedOnRootExpression ?? false
        }
        widthsById={widthsByIdMap}
        expressionName={expressionState?.["@_label"]}
      />
    </div>
  );
}

function toObject(map?: Map<string, number[]>): StorybookArgWidhtsById {
  return Array.from((map ?? new Map()).entries()).reduce((acc, [key, value]) => {
    acc[`${key}`] = value;
    return acc;
  }, {});
}

function toMap(object?: StorybookArgWidhtsById): Map<string, number[]> {
  return Array.from(Object.entries(object ?? {})).reduce((acc, [key, value]) => {
    acc.set(key, value);
    return acc;
  }, new Map<string, number[]>());
}
