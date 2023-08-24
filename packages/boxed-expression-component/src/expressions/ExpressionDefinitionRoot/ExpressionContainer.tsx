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
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FeelFunctionExpressionDefinition,
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
  expression: ExpressionDefinition;
  isNested: boolean;
  isResetSupported: boolean;
  rowIndex: number;
  columnIndex: number;
  parentElementId?: string;
}

export const ExpressionContainer: React.FunctionComponent<ExpressionContainerProps> = ({
  expression,
  isNested,
  isResetSupported,
  rowIndex,
  columnIndex,
  parentElementId,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);

  const { beeGwtService, variables, decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  const onLogicTypeSelected = useCallback(
    (logicType) => {
      setExpression((prev: ExpressionDefinition) => {
        const newExpression = {
          ...beeGwtService!.getDefaultExpressionDefinition(logicType, prev.dataType),
          logicType,
          isNested,
          id: prev.id ?? generateUuid(),
          name: prev.name ?? DEFAULT_EXPRESSION_NAME,
        };

        if (parentElementId) {
          variables?.repository.addVariableToContext(newExpression.id, newExpression.name, parentElementId);

          switch (newExpression.logicType) {
            case ExpressionDefinitionLogicType.Context:
              addContextExpressionToVariables(newExpression as ContextExpressionDefinition);
              break;
            case ExpressionDefinitionLogicType.Relation:
              addRelationExpressionToVariables(newExpression);
              break;
            case ExpressionDefinitionLogicType.Invocation:
              addInvocationExpressionToVariables(newExpression as InvocationExpressionDefinition);
              break;
            case ExpressionDefinitionLogicType.List:
              addListExpressionToVariables(newExpression as ListExpressionDefinition);
              break;
            case ExpressionDefinitionLogicType.DecisionTable:
              addDecisionTableExpressionToVariables(newExpression as DecisionTableExpressionDefinition);
              break;
            case ExpressionDefinitionLogicType.Function:
              addFunctionExpressionToVariables(newExpression as FunctionExpressionDefinition);
              break;
            default:
              // Expression without variables
              break;
          }
        }

        return newExpression;
      });
    },
    [beeGwtService, isNested, setExpression]
  );

  function addContextExpressionToVariables(contextExpressionDefinition: ContextExpressionDefinition) {
    const contextEntries = contextExpressionDefinition.contextEntries;
    for (const contextEntry of contextEntries) {
      variables?.repository.addVariableToContext(
        contextEntry.entryInfo.id,
        contextEntry.entryInfo.name,
        contextExpressionDefinition.id
      );
    }
  }

  function addRelationExpressionToVariables(relationExpressionDefinition: RelationExpressionDefinition) {
    const rowEntries = relationExpressionDefinition.rows;
    if (rowEntries) {
      for (const rowEntry of rowEntries) {
        for (const cell of rowEntry.cells) {
          // The name is not relevant here because Relation does not declare variables, so we're reusing ID.
          variables?.repository.addVariableToContext(cell.id, cell.id, relationExpressionDefinition.id);
        }
      }
    }
  }

  function addInvocationExpressionToVariables(newExpression: InvocationExpressionDefinition) {
    const bindingEntries = newExpression.bindingEntries;
    for (const bindingEntry of bindingEntries) {
      variables?.repository.addVariableToContext(
        bindingEntry.entryInfo.id,
        bindingEntry.entryInfo.name,
        newExpression.id
      );
    }
  }

  function addListExpressionToVariables(newExpression: ListExpressionDefinition) {
    const items = newExpression.items;
    for (const item of items) {
      // The name is not relevant here because ListExpression does not declare variables, so we're reusing ID.
      variables?.repository.addVariableToContext(item.id, item.id, newExpression.id);
    }
  }

  function addDecisionTableExpressionToVariables(decisionTable: DecisionTableExpressionDefinition) {
    if (decisionTable.rules) {
      for (const rule of decisionTable.rules) {
        if (rule.inputEntries) {
          for (const inputEntry of rule.inputEntries) {
            variables?.repository.addVariableToContext(inputEntry.id, inputEntry.id, decisionTable.id);
          }
        }

        if (rule.outputEntries) {
          for (const outputEntry of rule.outputEntries) {
            variables?.repository.addVariableToContext(outputEntry.id, outputEntry.id, decisionTable.id);
          }
        }
      }
    }
  }

  function addFunctionExpressionToVariables(functionExpression: FunctionExpressionDefinition) {
    if (functionExpression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      const expression = (functionExpression as FeelFunctionExpressionDefinition).expression;
      variables?.repository.addVariableToContext(
        expression.id,
        expression.name ?? expression.id,
        functionExpression.id
      );
    }
  }

  const onLogicTypeReset = useCallback(() => {
    variables?.repository.removeVariable(expression.id, true);

    setExpression((prev) => ({
      id: prev.id,
      name: prev.name,
      dataType: prev.dataType,
      logicType: ExpressionDefinitionLogicType.Undefined,
    }));
  }, [setExpression]);

  const getPlacementRef = useCallback(() => containerRef.current!, []);

  return (
    <div ref={containerRef} className={"expression-container-box"} data-ouia-component-id="expression-container">
      <ExpressionDefinitionLogicTypeSelector
        expression={expression}
        onLogicTypeSelected={onLogicTypeSelected}
        onLogicTypeReset={onLogicTypeReset}
        getPlacementRef={getPlacementRef}
        isResetSupported={isResetSupported}
        isNested={isNested}
        parentElementId={parentElementId ?? decisionNodeId}
      />
    </div>
  );
};
