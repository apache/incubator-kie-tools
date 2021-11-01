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

import { CellProps, ContextEntries, ContextEntryRecord, EntryInfo, ExpressionProps } from "../../api";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { DataRecord } from "react-table";
import { ContextEntryInfo } from "./ContextEntryInfo";
import * as _ from "lodash";

export interface ContextEntryInfoCellProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
  editInfoPopoverLabel?: string;
}

export const ContextEntryInfoCell: React.FunctionComponent<ContextEntryInfoCellProps> = ({
  data,
  rowIndex,
  onRowUpdate,
  editInfoPopoverLabel,
}) => {
  const contextEntry: ContextEntryRecord = useMemo(() => data[rowIndex], [data, rowIndex]);
  const entryInfo: EntryInfo = useMemo(() => contextEntry.entryInfo, [contextEntry.entryInfo]);
  const entryExpression: ExpressionProps = useMemo(() => contextEntry.entryExpression, [contextEntry.entryExpression]);

  const onContextEntryUpdate = useCallback(
    (name, dataType) => {
      const updatedExpression = { ...entryExpression };
      if (contextEntry.nameAndDataTypeSynchronized && _.size(name) && _.size(dataType)) {
        updatedExpression.name = name;
        updatedExpression.dataType = dataType;
      }
      onRowUpdate(rowIndex, { ...contextEntry, entryExpression: updatedExpression, entryInfo: { name, dataType } });
    },
    [entryExpression, contextEntry, rowIndex, onRowUpdate]
  );

  return (
    <div className="context-entry-info-cell">
      <ContextEntryInfo
        name={entryInfo.name}
        dataType={entryInfo.dataType}
        onContextEntryUpdate={onContextEntryUpdate}
        editInfoPopoverLabel={editInfoPopoverLabel}
      />
    </div>
  );
};

export const getContextEntryInfoCell = (editInfoPopoverLabel: string) => {
  return (props: ContextEntryInfoCellProps) => ContextEntryInfoCell({ ...props, editInfoPopoverLabel });
};
