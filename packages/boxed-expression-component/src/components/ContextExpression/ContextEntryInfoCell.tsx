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

import { ContextExpressionDefinitionEntry, ExpressionDefinition, ROWGENERICTYPE } from "../../api";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { ContextEntryInfo } from "./ContextEntryInfo";
import * as _ from "lodash";

export interface ContextEntryInfoCellProps {
  // This name ('data') can't change, as this is used as a "defaultCell" on "defaultCellByColumnName".
  data: ContextExpressionDefinitionEntry[];
  onRowUpdate: (rowIndex: number, updatedRow: ROWGENERICTYPE) => void;
  editInfoPopoverLabel?: string;
  rowIndex: number;
  columnId: string;
}

export const ContextEntryInfoCell: React.FunctionComponent<ContextEntryInfoCellProps> = ({
  data: contextEntries,
  rowIndex,
  onRowUpdate,
  editInfoPopoverLabel,
}) => {
  const contextEntry = useMemo(() => contextEntries[rowIndex], [contextEntries, rowIndex]);
  const entryInfo = useMemo(() => contextEntry.entryInfo, [contextEntry.entryInfo]);
  const entryExpression = useMemo(() => contextEntry.entryExpression, [contextEntry.entryExpression]);

  const onContextEntryUpdate = useCallback(
    ({ name, dataType }: Pick<ExpressionDefinition, "name" | "dataType">) => {
      const updatedExpression = { ...entryExpression };
      if (_.size(name) && _.size(dataType)) {
        updatedExpression.name = name;
        updatedExpression.dataType = dataType;
      }
      onRowUpdate(rowIndex, {
        ...contextEntry,
        entryExpression: updatedExpression,
        entryInfo: { id: entryInfo.id, name, dataType },
      });
    },
    [entryExpression, contextEntry, rowIndex, onRowUpdate, entryInfo.id]
  );

  return (
    <div className="context-entry-info-cell">
      <ContextEntryInfo
        id={entryInfo.id}
        name={entryInfo.name}
        dataType={entryInfo.dataType}
        onContextEntryUpdate={onContextEntryUpdate}
        editInfoPopoverLabel={editInfoPopoverLabel}
      />
    </div>
  );
};
