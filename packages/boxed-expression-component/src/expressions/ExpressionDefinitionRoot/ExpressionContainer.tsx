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
  ContextExpressionDefinition,
  DecisionTableExpressionDefinition,
  DmnBuiltInDataType,
  ExpressionDefinition,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
  InvocationExpressionDefinition,
  ListExpressionDefinition,
  RelationExpressionDefinition,
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

export interface ExpressionContainerProps {
  expression?: ExpressionDefinition;
  isNested: boolean;
  isResetSupported: boolean;
  rowIndex: number;
  columnIndex: number;
  parentElementId?: string;
  expressionName?: string;
}

export const ExpressionContainer: React.FunctionComponent<ExpressionContainerProps> = ({
  expression,
  isNested,
  isResetSupported,
  rowIndex,
  columnIndex,
  parentElementId,
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
    (contextExpressionDefinition: ContextExpressionDefinition) => {
      const contextEntries = contextExpressionDefinition.contextEntry ?? [];
      for (const contextEntry of contextEntries) {
        variables?.repository.addVariableToContext(
          contextEntry["@_id"]!,
          contextEntry["@_label"] ?? "",
          contextExpressionDefinition["@_id"]!
        );
      }
    },
    [variables?.repository]
  );

  const addRelationExpressionToVariables = useCallback(
    (relationExpressionDefinition: RelationExpressionDefinition) => {
      const rowEntries = relationExpressionDefinition.row ?? [];
      if (rowEntries) {
        for (const rowEntry of rowEntries) {
          for (const cell of rowEntry.expression ?? []) {
            // The name is not relevant here because Relation does not declare variables, so we're reusing ID.
            variables?.repository.addVariableToContext(
              cell["@_id"]!,
              cell["@_id"]!,
              relationExpressionDefinition["@_id"]!
            );
          }
        }
      }
    },
    [variables?.repository]
  );

  const addInvocationExpressionToVariables = useCallback(
    (newExpression: InvocationExpressionDefinition) => {
      const bindingEntries = newExpression.binding ?? [];
      for (const bindingEntry of bindingEntries) {
        variables?.repository.addVariableToContext(
          bindingEntry.expression?.["@_id"] ?? "",
          bindingEntry.expression?.["@_label"] ?? "",
          newExpression["@_id"]!
        );
      }
    },
    [variables?.repository]
  );

  const addListExpressionToVariables = useCallback(
    (newExpression: ListExpressionDefinition) => {
      const items = newExpression.expression ?? [];
      for (const item of items) {
        if (item) {
          // The name is not relevant here because ListExpression does not declare variables, so we're reusing ID.
          variables?.repository.addVariableToContext(
            item["@_id"] ?? "",
            item["@_id"] ?? "",
            newExpression["@_id"] ?? ""
          );
        }
      }
    },
    [variables?.repository]
  );

  const addDecisionTableExpressionToVariables = useCallback(
    (decisionTable: DecisionTableExpressionDefinition) => {
      if (decisionTable.rule) {
        for (const rule of decisionTable.rule) {
          if (rule.inputEntry) {
            for (const inputEntry of rule.inputEntry) {
              variables?.repository.addVariableToContext(
                inputEntry["@_id"]!,
                inputEntry["@_id"]!,
                decisionTable["@_id"]!
              );
            }
          }

          if (rule.outputEntry) {
            for (const outputEntry of rule.outputEntry) {
              variables?.repository.addVariableToContext(
                outputEntry["@_id"]!,
                outputEntry["@_id"]!,
                decisionTable["@_id"]!
              );
            }
          }
        }
      }
    },
    [variables?.repository]
  );

  const addFunctionExpressionToVariables = useCallback(
    (functionExpression: FunctionExpressionDefinition) => {
      if (functionExpression["@_kind"] === FunctionExpressionDefinitionKind.Feel && functionExpression.expression) {
        const expression = functionExpression.expression;
        variables?.repository.addVariableToContext(
          expression["@_id"]!,
          expression["@_label"] ?? expression["@_id"]!,
          functionExpression["@_id"]!
        );
      }
    },
    [variables?.repository]
  );

  const expressionTypeRef = expression?.["@_typeRef"];

  const onLogicTypeSelected = useCallback(
    (logicType: ExpressionDefinition["__$$element"] | undefined) => {
      const { expression: defaultExpression, widthsById: defaultWidthsById } =
        beeGwtService!.getDefaultExpressionDefinition(
          logicType,
          expressionTypeRef ?? DmnBuiltInDataType.Undefined,
          !isNested
        );

      let id: string;

      setExpression((prev) => {
        const newExpression: ExpressionDefinition = {
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
            case "relation":
              addRelationExpressionToVariables(newExpression);
              break;
            case "invocation":
              addInvocationExpressionToVariables(newExpression);
              break;
            case "list":
              addListExpressionToVariables(newExpression);
              break;
            case "decisionTable":
              addDecisionTableExpressionToVariables(newExpression);
              break;
            case "functionDefinition":
              addFunctionExpressionToVariables(newExpression);
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
      addDecisionTableExpressionToVariables,
      addFunctionExpressionToVariables,
      addInvocationExpressionToVariables,
      addListExpressionToVariables,
      addRelationExpressionToVariables,
      beeGwtService,
      expressionTypeRef,
      expressionName,
      isNested,
      parentElementId,
      setExpression,
      setWidthById,
    ]
  );

  const onLogicTypeReset = useCallback(() => {
    if (expression?.["@_id"]) {
      variables?.repository.removeVariable(expression["@_id"], true);
      setWidthById(expression["@_id"], (prev) => []);
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
