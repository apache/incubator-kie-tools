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

import { DmnBuiltInDataType, ExpressionDefinition } from "../../api";
import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { DEFAULT_EXPRESSION_NAME, ExpressionDefinitionHeaderMenu } from "../ExpressionDefinitionHeaderMenu";
import "./ContextEntryInfoCell.css";
import { useCellWidthToFitDataRef } from "../../resizing/BeeTableCellWidthToFitDataContext";
import { getCanvasFont, getTextWidth } from "../../resizing/WidthsToFitData";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DMN15__tInformationItem } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export interface Entry {
  variable: DMN15__tInformationItem;
  expression: ExpressionDefinition | undefined;
}

export interface ContextEntryInfoCellProps {
  // This name ('data') can't change, as this is used on "cellComponentByColumnAccessor".
  data: readonly Entry[];
  onEntryUpdate: (rowIndex: number, newEntry: Entry) => void;
  rowIndex: number;
  columnIndex: number;
  columnId: string;
}

export const ContextEntryInfoCell: React.FunctionComponent<ContextEntryInfoCellProps> = ({
  data: contextEntries,
  rowIndex,
  columnIndex,
  onEntryUpdate,
}) => {
  const entry = useMemo(() => contextEntries[rowIndex], [contextEntries, rowIndex]);
  const entryInfo = useMemo(() => entry.variable, [entry.variable]);
  const entryExpression = useMemo(() => entry.expression, [entry.expression]);

  const ref = React.useRef<HTMLDivElement>(null);

  const onContextEntryInfoUpdated = useCallback(
    ({
      "@_label": name = DEFAULT_EXPRESSION_NAME,
      "@_typeRef": dataType = DmnBuiltInDataType.Undefined,
    }: Pick<ExpressionDefinition, "@_label" | "@_typeRef">) => {
      onEntryUpdate(rowIndex, {
        ...entry,
        // entryExpression and entryInfo must always have the same `dataType` and `name`, as those are dictated by the entryInfo.
        expression: entryExpression
          ? ({
              ...entryExpression,
              "@_label": name,
              "@_typeRef": dataType,
            } as ExpressionDefinition)
          : entryExpression,
        variable: { ...entryInfo, "@_name": name, "@_typeRef": dataType },
      });
    },
    [onEntryUpdate, rowIndex, entry, entryExpression, entryInfo]
  );

  useCellWidthToFitDataRef(
    rowIndex,
    columnIndex,
    useMemo(
      () => ({
        getWidthToFitData: () => {
          const name = ref.current!.querySelector(".expression-info-name")!;
          const dataType = ref.current!.querySelector(".expression-info-data-type")!;

          const padding = 8 * 2; // 8px for each side, comes from .entry-info div
          const border = 2; // that's the td border.

          return (
            padding +
            border +
            Math.max(
              getTextWidth(name.textContent ?? "", getCanvasFont(name)),
              getTextWidth(dataType.textContent ?? "", getCanvasFont(dataType))
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
    useCallback(
      () => `${entryInfo?.["@_name"]} (${entryInfo?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}})`,
      [entryInfo]
    )
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject(entryInfo?.["@_id"]);
    }
  }, [beeGwtService, entryInfo, isActive]);

  const renderEntryDefinition = useCallback(
    (args: { additionalCssClass?: string }) => (
      <div className={`expression-info ${args.additionalCssClass}`} ref={ref}>
        <p
          className="expression-info-name pf-u-text-truncate"
          title={entryInfo?.["@_name"]}
          data-ouia-component-id={"expression-info-name"}
        >
          {entryInfo?.["@_name"]}
        </p>
        <p
          className="expression-info-data-type pf-u-text-truncate"
          title={entryInfo?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
          data-ouia-component-id={"expression-info-data-type"}
        >
          ({entryInfo?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined})
        </p>
      </div>
    ),
    [entryInfo]
  );

  return (
    <div className="context-entry-info-cell">
      <div className={`${entryInfo?.["@_id"]} entry-info`}>
        <ExpressionDefinitionHeaderMenu
          selectedExpressionName={entryInfo?.["@_name"] ?? ""}
          selectedDataType={entryInfo?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
          onExpressionHeaderUpdated={onContextEntryInfoUpdated}
        >
          {renderEntryDefinition({ additionalCssClass: "with-popover-menu" })}
        </ExpressionDefinitionHeaderMenu>
      </div>
    </div>
  );
};
