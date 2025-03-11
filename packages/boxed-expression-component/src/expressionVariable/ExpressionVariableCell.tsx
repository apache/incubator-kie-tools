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

import { DMN15__tInformationItem } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import {
  Action,
  BeeTableCellProps,
  BoxedExpression,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  Normalized,
  VariableChangedArgs,
} from "../api";
import { useCellWidthToFitDataRef } from "../resizing/BeeTableCellWidthToFitDataContext";
import { getCanvasFont, getTextWidth } from "../resizing/WidthsToFitData";
import { useBeeTableSelectableCellRef } from "../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditorContext";
import {
  DEFAULT_EXPRESSION_VARIABLE_NAME,
  ExpressionVariableMenu,
  OnExpressionVariableUpdated,
} from "./ExpressionVariableMenu";
import "./ExpressionVariableCell.css";

export interface ExpressionWithVariable {
  expression: Normalized<BoxedExpression> | undefined;
  variable: Normalized<DMN15__tInformationItem>;
}

export type OnExpressionWithVariableUpdated = (
  index: number,
  { expression, variable }: ExpressionWithVariable,
  variableChangedArgs: VariableChangedArgs
) => void;

export const ExpressionVariableCell: React.FunctionComponent<
  BeeTableCellProps<ExpressionWithVariable & { index: number; isContentAFeelExpression?: boolean }> & {
    onExpressionWithVariableUpdated: OnExpressionWithVariableUpdated;
  }
> = ({ data, rowIndex, columnIndex, onExpressionWithVariableUpdated }) => {
  const ref = React.useRef<HTMLDivElement>(null);
  const { isReadOnly } = useBoxedExpressionEditor();
  const { expression, variable, index, isContentAFeelExpression } = data[rowIndex];

  const onVariableUpdated = useCallback<OnExpressionVariableUpdated>(
    ({ name = DEFAULT_EXPRESSION_VARIABLE_NAME, typeRef = undefined }) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: variable["@_id"],
        typeChange:
          variable["@_typeRef"] !== typeRef
            ? {
                from: variable["@_typeRef"],
                to: typeRef,
              }
            : undefined,
        nameChange:
          variable["@_name"] !== name
            ? {
                from: variable["@_name"],
                to: name,
              }
            : undefined,
      };

      onExpressionWithVariableUpdated(
        index,
        {
          // `expression` and `variable` must always have the same `typeRef` and `name/label`, as those are dictated by `variable`.
          expression: expression
            ? {
                ...expression,
                "@_label": name,
                "@_typeRef": typeRef,
              }
            : undefined!, // SPEC DISCREPANCY
          variable: {
            ...variable,
            "@_name": name,
            "@_typeRef": typeRef,
          },
        },
        expressionChangedArgs
      );
    },
    [onExpressionWithVariableUpdated, index, expression, variable]
  );

  useCellWidthToFitDataRef(
    rowIndex,
    columnIndex,
    useMemo(
      () => ({
        getWidthToFitData: () => {
          const name = ref.current!.querySelector(".expression-info-name")!;
          const typeRef = ref.current!.querySelector(".expression-info-data-type")!;

          const padding = 8 * 2; // 8px for each side, comes from .expression-variable div
          const border = 2; // that's the td border.

          return (
            padding +
            border +
            Math.max(
              getTextWidth(name.textContent ?? "", getCanvasFont(name)),
              getTextWidth(typeRef.textContent ?? "", getCanvasFont(typeRef))
            )
          );
        },
      }),
      []
    )
  );

  const { isActive } = useBeeTableSelectableCellRef(
    rowIndex,
    columnIndex,
    undefined,
    useCallback(() => `${variable["@_name"]} (${variable["@_typeRef"]}})`, [variable])
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject(variable["@_id"]);
    }
  }, [beeGwtService, variable, isActive]);

  const cellContent = useMemo(
    () => (
      <div className={`expression-info with-popover-menu`} ref={ref}>
        <p
          className="expression-info-name pf-v5-u-text-truncate"
          title={variable["@_name"]}
          data-ouia-component-id={"expression-info-name"}
          data-testid={"kie-tools--bee--expression-info-name"}
        >
          {variable["@_name"]}
        </p>
        <p
          className="expression-info-data-type pf-v5-u-text-truncate"
          title={variable["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
          data-ouia-component-id={"expression-info-data-type"}
          data-testid={"kie-tools--bee--expression-info-data-type"}
        >
          ({variable["@_typeRef"] ?? DmnBuiltInDataType.Undefined})
        </p>
      </div>
    ),
    [variable]
  );

  return (
    <div className="expression-variable-cell">
      <div className={`${variable["@_id"]} expression-variable`}>
        {isReadOnly ? (
          cellContent
        ) : (
          <ExpressionVariableMenu
            selectedExpressionName={variable["@_name"]}
            selectedDataType={variable["@_typeRef"]}
            onVariableUpdated={onVariableUpdated}
            variableUuid={variable["@_id"]}
            isContentAFeelExpression={isContentAFeelExpression}
          >
            {cellContent}
          </ExpressionVariableMenu>
        )}
      </div>
    </div>
  );
};
