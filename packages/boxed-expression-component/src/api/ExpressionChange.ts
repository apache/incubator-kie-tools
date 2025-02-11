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

export type ExpressionChangedArgs =
  | ({
      action:
        | Action.ExpressionReset
        | Action.ExpressionCreated
        | Action.ExpressionPastedFromClipboard
        | Action.DecisionTableCellsUpdated
        | Action.DecisionTableHitPolicyChanged
        | Action.DecisionTableBuiltInAggregatorChanged
        | Action.FunctionParameterAdded
        | Action.FunctionParameterTypeChanged
        | Action.FunctionParameterRemoved
        | Action.IteratorVariableDefined
        | Action.RelationCellsUpdated
        | Action.InvocationParametersChanged
        | Action.ColumnChanged;
    } & {})
  | ({ action: Action.RowsAdded } & RowsAddedArgs)
  | ({ action: Action.RowDuplicated } & RowDuplicatedArgs)
  | ({ action: Action.ColumnAdded } & ColumnsAddedArgs)
  | ({ action: Action.RowRemoved } & RowRemovedArgs)
  | ({ action: Action.RowReset } & RowResetArgs)
  | ({ action: Action.ColumnRemoved } & ColumnRemovedArgs)
  | ({ action: Action.LiteralTextExpressionChanged } & LiteralTextExpressionChangedArgs)
  | ({ action: Action.FunctionKindChanged } & FunctionKindChangedArgs)
  | ({ action: Action.VariableChanged } & VariableChangedArgs);

export type VariableChangedProperty = {
  from: string | undefined;
  to: string | undefined;
};

export type VariableChangedArgs = {
  typeChange?: VariableChangedProperty | undefined;
  nameChange?: VariableChangedProperty | undefined;
  variableUuid: string;
};

export interface ColumnsAddedArgs {
  columnIndex: number;
  columnCount: number;
}

export interface RowsAddedArgs {
  rowIndex: number;
  rowsCount: number;
}

export interface RowDuplicatedArgs {
  rowIndex: number;
}

export interface RowRemovedArgs {
  rowIndex: number;
}
export interface RowResetArgs {
  rowIndex: number;
}

export interface ColumnRemovedArgs {
  columnIndex: number;
}

export interface LiteralTextExpressionChangedArgs {
  from: string;
  to: string;
}

export interface FunctionKindChangedArgs {
  from: string;
  to: string;
}

export enum Action {
  ExpressionReset,
  ExpressionCreated,
  ExpressionPastedFromClipboard,
  RowsAdded,
  RowRemoved,
  RowReset,
  RowDuplicated,
  ColumnAdded,
  ColumnRemoved,
  ColumnChanged,
  VariableChanged,
  LiteralTextExpressionChanged,
  DecisionTableCellsUpdated,
  DecisionTableHitPolicyChanged,
  DecisionTableBuiltInAggregatorChanged,
  FunctionKindChanged,
  FunctionParameterAdded,
  FunctionParameterTypeChanged,
  FunctionParameterRemoved,
  RelationCellsUpdated,
  InvocationParametersChanged,
  IteratorVariableDefined,
}
