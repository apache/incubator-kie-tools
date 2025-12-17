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

import { useArgs } from "@storybook/preview-api";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../src/BoxedExpressionEditor";
import { BeeGwtService, BoxedExpression, DmnBuiltInDataType, generateUuid, Normalized } from "../src/api";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../src/expressionVariable/ExpressionVariableMenu";
import { getDefaultBoxedExpressionForStories } from "./getDefaultBoxedExpressionForStories";
import { OnExpressionChange } from "../src/BoxedExpressionEditorContext";

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
  getDefaultExpressionDefinition(logicType: BoxedExpression["__$$element"] | undefined, dataType: string | undefined) {
    const widthsById = new Map();
    const expression = getDefaultBoxedExpressionForStories({ logicType, typeRef: dataType, widthsById });
    return {
      expression,
      widthsById,
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
  const [expressionState, setExpressionState] = useState<Normalized<BoxedExpression> | undefined>(
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

  const onExpressionChange = useCallback<OnExpressionChange>((args) => {
    setExpressionState(args.setExpressionAction);
  }, []);

  // Keep expression args in sync with state
  useEffect(() => {
    updateArgs({ expression: expressionState });
  }, [updateArgs, expressionState]);

  return (
    <>
      {args && (
        <div data-testid={"storybook--boxed-expression-component"} style={{ display: "none" }}>
          {JSON.stringify(args)}
        </div>
      )}

      <div
        onKeyDown={(e) => {
          // Prevent keys from propagating to Storybook
          console.log("wrapper stopped");
          // return e.stopPropagation();
        }}
      >
        <BoxedExpressionEditor
          expressionHolderId={props?.expressionHolderId ?? args?.expressionHolderId ?? generateUuid()}
          expressionHolderName={
            props?.expressionHolderName ?? args?.expressionHolderName ?? DEFAULT_EXPRESSION_VARIABLE_NAME
          }
          expressionHolderTypeRef={props?.expressionHolderTypeRef ?? args?.expressionHolderTypeRef}
          expression={expressionState}
          onExpressionChange={onExpressionChange}
          evaluationHitsCountById={props?.evaluationHitsCountById ?? args?.evaluationHitsCountById}
          onWidthsChange={onWidthsChange}
          dataTypes={props?.dataTypes ?? args?.dataTypes ?? dataTypes}
          scrollableParentRef={emptyRef}
          beeGwtService={props?.beeGwtService ?? args?.beeGwtService ?? beeGwtService}
          pmmlDocuments={props?.pmmlDocuments ?? args?.pmmlDocuments ?? pmmlDocuments}
          isReadOnly={props?.isReadOnly ?? args?.isReadOnly ?? false}
          isResetSupportedOnRootExpression={
            props?.isResetSupportedOnRootExpression ?? args?.isResetSupportedOnRootExpression ?? false
          }
          widthsById={widthsByIdMap}
        />
      </div>
    </>
  );
}

function toObject(map?: Map<string, number[]>): StorybookArgWidhtsById {
  return Array.from((map ?? new Map<string, number[]>()).entries()).reduce(
    (acc, [key, value]) => {
      acc[`${key}`] = value;
      return acc;
    },
    {} as Record<string, number[]>
  );
}

function toMap(object?: StorybookArgWidhtsById): Map<string, number[]> {
  return Array.from(Object.entries(object ?? {})).reduce((acc, [key, value]) => {
    acc.set(key, value);
    return acc;
  }, new Map<string, number[]>());
}
