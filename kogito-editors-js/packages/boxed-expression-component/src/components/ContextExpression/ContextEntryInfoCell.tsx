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

import { CellProps, ContextEntries } from "../../api";
import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import { DataRecord } from "react-table";
import { ContextEntryInfo } from "./ContextEntryInfo";
import * as _ from "lodash";

export interface ContextEntryInfoCellProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
}

export const ContextEntryInfoCell: React.FunctionComponent<ContextEntryInfoCellProps> = ({
  data,
  row: { index },
  onRowUpdate,
}) => {
  const contextEntry = data[index];

  const entryInfo = useRef(contextEntry.entryInfo);
  const entryExpression = useRef(contextEntry.entryExpression);

  useEffect(() => {
    entryInfo.current = contextEntry.entryInfo;
  }, [contextEntry.entryInfo]);
  useEffect(() => {
    entryExpression.current = contextEntry.entryExpression;
  }, [contextEntry.entryExpression]);

  const onContextEntryUpdate = useCallback(
    (name, dataType) => {
      const updatedExpression = { ...entryExpression.current };
      if (contextEntry.nameAndDataTypeSynchronized && _.size(name) && _.size(dataType)) {
        updatedExpression.name = name;
        updatedExpression.dataType = dataType;
      }
      onRowUpdate(index, { ...contextEntry, entryExpression: updatedExpression, entryInfo: { name, dataType } });
    },
    [contextEntry, index, onRowUpdate]
  );

  return (
    <div className="context-entry-info-cell">
      <ContextEntryInfo
        name={entryInfo.current.name}
        dataType={entryInfo.current.dataType}
        onContextEntryUpdate={onContextEntryUpdate}
        editInfoPopoverLabel={contextEntry.editInfoPopoverLabel}
      />
    </div>
  );
};
