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

import * as React from "react";

// Extending react-table definitions with missing and custom properties
declare module "react-table" {
  export interface TableState {
    columnResizing: {
      isResizingColumn: boolean;
      columnWidths: {
        [columnName: string]: number;
      };
    };
  }

  export interface ColumnInstance<D extends object> {
    /** Current column is an empty TH element, created by react-table to fill a missing header cell element */
    placeholderOf?: ColumnInstance<D> | undefined;

    columns?: Array<ColumnInstance<D>>;
  }

  export interface ColumnInterface<D extends object> {
    /** Used by react-table to hold the original id chosen for the column, independently of applied operations */
    originalId?: string;
    /** Column identifier */
    accessor: string;
    /** Column group type */
    groupType?: string;
    /** Column css classes - appended as passed */
    cssClasses?: string;
    /** Column label */
    label: string;
    /** Custom Element to be rendered in place of the column label */
    headerCellElement?: JSX.Element;
    /** Additional Element to be rendered in the Header Cell top right corner */
    headerCellElementExtension?: JSX.Element;
    /** It makes this column header inline editable (with double-click) */
    isInlineEditable?: boolean;
    /** Column data type */
    dataType: string;
    /** It tells whether column is of type counter or not */
    isRowIndexColumn: boolean;
    /** It tells if a header is a Feel Expression or just plain text */
    isHeaderAFeelExpression?: boolean;

    cellDelegate?: (id: string) => React.ReactNode;

    width?: number;
    setWidth?: (newWidth: number) => void;
    isWidthPinned?: boolean;
    isWidthConstant?: boolean;

    columns?: Array<Column<D>>;
  }
}
