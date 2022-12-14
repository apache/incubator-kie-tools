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

import { DmnBuiltInDataType } from "./DmnBuiltInDataType";
import { ExpressionDefinition } from "./ExpressionDefinition";
import * as _ from "lodash";
import { BeeTableOperationHandlerConfig, BeeTableOperation } from "./BeeTable";
import { BoxedExpressionEditorI18n } from "../i18n";
import { ExpressionDefinitionLogicType } from "./ExpressionDefinitionLogicType";

export interface ContextExpressionDefinitionEntryInfo {
  /** Entry id */
  id: string;
  /** Entry name */
  name: string;
  /** Entry data type */
  dataType: DmnBuiltInDataType;
}

export interface ContextExpressionDefinitionEntry<T extends ExpressionDefinition = ExpressionDefinition> {
  entryInfo: ContextExpressionDefinitionEntryInfo;
  /** Entry expression */
  entryExpression: T;
  /** True, for synchronizing name and dataType parameters, between entryInfo and entryExpression */
  nameAndDataTypeSynchronized?: boolean;
}

export const CONTEXT_ENTRY_INFO_MIN_WIDTH = 150;
export const CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH = 370;

export const getOperationHandlerConfig = (
  i18n: BoxedExpressionEditorI18n,
  groupName: string
): BeeTableOperationHandlerConfig => [
  {
    group: groupName,
    items: [
      { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
      { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
      { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
      { name: i18n.rowOperations.clear, type: BeeTableOperation.RowClear },
    ],
  },
];

export const getNextAvailableContextExpressionEntryName = (
  entryInfos: ContextExpressionDefinitionEntryInfo[],
  namePrefix: string,
  lastIndex: number = entryInfos.length
): string => {
  const candidateName = `${namePrefix}-${lastIndex === 0 ? 1 : lastIndex}`;
  const entryWithCandidateName = _.find(entryInfos, { name: candidateName });
  return entryWithCandidateName
    ? getNextAvailableContextExpressionEntryName(entryInfos, namePrefix, lastIndex + 1)
    : candidateName;
};

export function resetContextExpressionEntry(entry: ContextExpressionDefinitionEntry): ContextExpressionDefinitionEntry {
  return {
    ...entry,
    entryExpression: {
      id: entry.entryExpression.id,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
  };
}
