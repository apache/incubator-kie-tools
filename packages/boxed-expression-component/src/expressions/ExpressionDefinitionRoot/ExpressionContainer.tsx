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
  DmnBuiltInDataType,
  BoxedExpression,
  BoxedFunction,
  BoxedFunctionKind,
  generateUuid,
  BoxedInvocation,
  BoxedList,
  BoxedRelation,
} from "../../api";
import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import { ExpressionDefinitionLogicTypeSelector } from "./ExpressionDefinitionLogicTypeSelector";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { getNewBeeIdRandomizer } from "../../clipboard/clipboard";

export interface ExpressionContainerProps {
  expression?: BoxedExpression;
  isNested: boolean;
  isResetSupported: boolean;
  rowIndex: number;
  columnIndex: number;
  parentElementId: string | undefined;
  parentTypeRef: string | undefined;
  expressionName?: string;
}

export const ExpressionContainer: React.FunctionComponent<ExpressionContainerProps> = ({
  expression,
  isNested,
  isResetSupported,
  rowIndex,
  columnIndex,
  parentElementId,
  parentTypeRef,
  expressionName,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);

  const { beeGwtService, variables, expressionHolderId } = useBoxedExpressionEditor();
  const { setExpression, setWidthById } = useBoxedExpressionEditorDispatch();
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  const addContextExpressionToVariables = useCallback(
    (contextExpressionDefinition: BoxedContext) => {
      const contextEntries = contextExpressionDefinition.contextEntry ?? [];
      for (const contextEntry of contextEntries) {
        variables?.repository.addVariableToContext(
          contextEntry["@_id"]!,
          contextEntry.variable?.["@_name"] ?? "",
          contextExpressionDefinition["@_id"]!
        );
      }
    },
    [variables?.repository]
  );

  const expressionTypeRef = expression?.["@_typeRef"];

  const onLogicTypeSelected = useCallback(
    (logicType: BoxedExpression["__$$element"] | undefined) => {
      const { expression: defaultExpression, widthsById: defaultWidthsById } =
        beeGwtService!.getDefaultExpressionDefinition(
          logicType,
          parentTypeRef ?? expressionTypeRef ?? DmnBuiltInDataType.Undefined,
          !isNested
        );

      let id: string;

      setExpression((prev) => {
        const newExpression: BoxedExpression = {
          ...defaultExpression,
          "@_id": prev?.["@_id"] ?? generateUuid(),
          "@_label": prev?.["@_label"] ?? expressionName ?? DEFAULT_EXPRESSION_NAME,
        };

        id = newExpression["@_id"]!;

        if (parentElementId) {
          switch (newExpression.__$$element) {
            case "context":
              addContextExpressionToVariables(newExpression);
              break;
            default:
              // Expression without variables
              break;
          }
        }

        return newExpression;
      });

      defaultWidthsById.forEach((value, key) => {
        setWidthById(id, (prev) => value);
      });
    },
    [
      addContextExpressionToVariables,
      beeGwtService,
      expressionTypeRef,
      expressionName,
      isNested,
      parentElementId,
      parentTypeRef,
      setExpression,
      setWidthById,
    ]
  );

  const onLogicTypeReset = useCallback(() => {
    const originalIds = getNewBeeIdRandomizer()
      .ack({
        json: { __$$element: "decision", expression },
        type: "DMN15__tDecision",
        attr: "expression",
      })
      .getOriginalIds();

    for (const id of originalIds) {
      variables?.repository.removeVariable(id, true);
      setWidthById(id, () => []);
    }

    setExpression(undefined!); // SPEC DISCREPANCY: Undefined expressions gives users the ability to select the expression type.
  }, [expression, setExpression, setWidthById, variables?.repository]);

  const getPlacementRef = useCallback(() => containerRef.current!, []);

  return (
    <div ref={containerRef} className={"expression-container-box"} data-testid="expression-container">
      <ExpressionDefinitionLogicTypeSelector
        expression={expression}
        onLogicTypeSelected={onLogicTypeSelected}
        onLogicTypeReset={onLogicTypeReset}
        getPlacementRef={getPlacementRef}
        isResetSupported={isResetSupported}
        isNested={isNested}
        parentElementId={parentElementId ?? expressionHolderId}
      />
    </div>
  );
};
