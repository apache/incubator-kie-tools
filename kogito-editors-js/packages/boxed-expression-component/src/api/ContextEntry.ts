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

import { DataType } from "./DataType";
import { ExpressionProps } from "./ExpressionProps";
import * as _ from "lodash";
import { DataRecord, Row } from "react-table";
import { TableHandlerConfiguration, TableOperation } from "./Table";
import { BoxedExpressionEditorI18n } from "../i18n";

export interface EntryInfo {
  /** Entry name */
  name: string;
  /** Entry data type */
  dataType: DataType;
}

export interface ContextEntryRecord {
  entryInfo: EntryInfo;
  /** Entry expression */
  entryExpression: ExpressionProps;
  /** Label used for the popover triggered when editing info section */
  editInfoPopoverLabel?: string;
  /** True, for synchronizing name and dataType parameters, between entryInfo and entryExpression */
  nameAndDataTypeSynchronized?: boolean;
  /** Callback to be invoked on expression resetting */
  onExpressionResetting?: () => void;
}

export type ContextEntries = ContextEntryRecord[];

export const DEFAULT_ENTRY_INFO_MIN_WIDTH = 150;
export const DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH = 370;

export const getHandlerConfiguration = (
  i18n: BoxedExpressionEditorI18n,
  groupName: string
): TableHandlerConfiguration => [
  {
    group: groupName,
    items: [
      { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
      { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
      { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      { name: i18n.rowOperations.clear, type: TableOperation.RowClear },
    ],
  },
];

export const generateNextAvailableEntryName = (
  entryInfos: EntryInfo[],
  namePrefix: string,
  lastIndex: number = entryInfos.length
): string => {
  const candidateName = `${namePrefix}-${lastIndex === 0 ? 1 : lastIndex}`;
  const entryWithCandidateName = _.find(entryInfos, { name: candidateName });
  return entryWithCandidateName ? generateNextAvailableEntryName(entryInfos, namePrefix, lastIndex + 1) : candidateName;
};

export const getEntryKey = (row: Row): string => {
  const entryRecord = row.original as ContextEntryRecord;
  return entryRecord.entryInfo.name + entryRecord.entryInfo.dataType;
};

export const resetEntry = (row: DataRecord): DataRecord => ({
  ...row,
  entryExpression: { uid: (row.entryExpression as ExpressionProps).uid },
});
