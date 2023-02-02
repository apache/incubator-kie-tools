/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ContextExpressionDefinitionEntry, DmnBuiltInDataType, ExpressionDefinition } from "../../api";
import * as React from "react";
import { useCallback, useMemo } from "react";
import * as _ from "lodash";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { DEFAULT_EXPRESSION_NAME, ExpressionDefinitionHeaderMenu } from "../ExpressionDefinitionHeaderMenu";
import "./ContextEntryInfoCell.css";
import { useCellWidthToFitDataRef } from "../../resizing/BeeTableCellWidthToFitDataContext";
import { getCanvasFont, getTextWidth } from "../../resizing/WidthsToFitData";

export interface ContextEntryInfoCellProps {
  // This name ('data') can't change, as this is used on "cellComponentByColumnAccessor".
  data: readonly ContextExpressionDefinitionEntry[];
  onEntryUpdate: (rowIndex: number, newEntry: ContextExpressionDefinitionEntry) => void;
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
  const entryInfo = useMemo(() => entry.entryInfo, [entry.entryInfo]);
  const entryExpression = useMemo(() => entry.entryExpression, [entry.entryExpression]);

  const ref = React.useRef<HTMLDivElement>(null);

  const onContextEntryInfoUpdated = useCallback(
    ({
      name = DEFAULT_EXPRESSION_NAME,
      dataType = DmnBuiltInDataType.Undefined,
    }: Pick<ExpressionDefinition, "name" | "dataType">) => {
      onEntryUpdate(rowIndex, {
        ...entry,
        // entryExpression and entryInfo must always have the same `dataType` and `name`, as those are dictated by the entryInfo.
        entryExpression: { ...entryExpression, name, dataType },
        entryInfo: { ...entryInfo, name, dataType },
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

  useBeeTableSelectableCellRef(
    rowIndex,
    columnIndex,
    undefined,
    useCallback(() => `${entryInfo.name} (${entryInfo.dataType})`, [entryInfo.dataType, entryInfo.name])
  );

  const renderEntryDefinition = useCallback(
    (args: { additionalCssClass?: string }) => (
      <div className={`expression-info ${args.additionalCssClass}`} ref={ref}>
        <p className="expression-info-name pf-u-text-truncate" title={entryInfo.name}>
          {entryInfo.name}
        </p>
        <p className="expression-info-data-type pf-u-text-truncate" title={entryInfo.dataType}>
          ({entryInfo.dataType})
        </p>
      </div>
    ),
    [entryInfo]
  );

  return (
    <div className="context-entry-info-cell">
      <div className={`${entryInfo.id} entry-info`}>
        <ExpressionDefinitionHeaderMenu
          selectedExpressionName={entryInfo.name}
          selectedDataType={entryInfo.dataType}
          onExpressionHeaderUpdated={onContextEntryInfoUpdated}
        >
          {renderEntryDefinition({ additionalCssClass: "with-popover-menu" })}
        </ExpressionDefinitionHeaderMenu>
      </div>
    </div>
  );
};
