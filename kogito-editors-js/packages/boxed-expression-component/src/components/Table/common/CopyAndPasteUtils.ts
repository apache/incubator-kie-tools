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

import { DataRecord } from "react-table";
import { getCellCoordinates, getCellTableId } from ".";
import { LogicType } from "../../../api/LogicType";

const DASH_SYMBOL = "-";

export const PASTE_OPERATION = "PASTE_OPERATION";

/**
 * Triggers paste operation by using an Element as a reference. Example:
 * +-----+-----+-----+
 * |  .  |  .  |  .  |
 * +-----+-----+-----+
 * |  .  |  A  |  .  |
 * +-----+-----+-----+
 * |  .  |  .  |  .  |
 * +-----+-----+-----+
 * Thus, considering A as the reference, a value ("B\tB\nB\tB") would feel that
 * table like this:
 * +-----+-----+-----+
 * |  .  |  .  |  .  |
 * +-----+-----+-----+
 * |  .  |  B  |  B  |
 * +-----+-----+-----+
 * |  .  |  B  |  B  |
 * +-----+-----+-----+
 * @param pasteValue   - value being pasted
 * @param reference    - reference element
 */
export const paste = (pasteValue: string, reference: Element) => {
  const cell = reference.closest("td");
  const coordinates = getCellCoordinates(cell);
  const detail = {
    ...coordinates,
    pasteValue,
    type: PASTE_OPERATION,
  };

  const eventId = getCellTableId(cell);

  document.dispatchEvent(new CustomEvent(eventId, { detail }));
};

/**
 * Paste a value into a table, by following the convention of other spreadsheet
 * tools.
 * @param pasteValue   - value being pasted
 * @param rows         - rows of the table
 * @param x (optional) - the initial X coordinate of the table
 * @param y (optional) - the initial Y coordinate of the table
 */
export const pasteOnTable = (
  pasteValue: string,
  rows: DataRecord[],
  rowFactory: () => DataRecord,
  x: number = 0,
  y: number = 0
): DataRecord[] => {
  const newRows = [...rows];
  const colsByIndex = Object.keys(rows[0]);
  const paste = iterableValue(pasteValue);

  const updateStringValue = (rows: DataRecord[], row: number, colName: string, value: string) => {
    rows[row][colName] = value;
  };

  const updateObjectValue = (rows: DataRecord[], row: number, colName: string, value: string) => {
    const currentElement: any = rows[row][colName];

    if (typeof currentElement !== "object") {
      return;
    }

    currentElement.content = value;
    currentElement.logicType = LogicType.LiteralExpression;
  };

  const hasAnyObject = (rows: DataRecord[]) => {
    if (rows.length > 0) {
      const cols = Object.keys(rows[0]);
      return cols.includes("entryExpression");
    }
    return false;
  };

  for (let i = 0; i < paste.length; i++) {
    const row = paste[i];

    if (i + y >= newRows.length) {
      newRows.push(rowFactory());
    }

    for (let j = 0; j < row.length; j++) {
      const colName = colsByIndex[j + x];
      const row = i + y;
      const updateValue = hasAnyObject(newRows) ? updateObjectValue : updateStringValue;

      updateValue(newRows, row, colName, paste[i][j]);
    }
  }

  return newRows;
};

/**
 * Covert a string value into an iterable data structure, by following the
 * convention of other spreadsheet tools.
 */
export const iterableValue = (value: string) => {
  const iterable: string[][] = value
    .trim()
    .split("\n")
    .map((strRow) => {
      const trimedValue = strRow.trim();
      return trimedValue === "" ? [] : trimedValue.split("\t");
    });

  ensureSameNumberOfColumns(iterable);

  return iterable;
};

/**
 * Get first value of the copy and paste
 */
export const firstIterableValue = (value: string) => {
  const rows: string[][] = iterableValue(value);

  if (rows.length > 0) {
    const cols = rows[0];
    if (cols.length > 0) {
      return cols[0];
    }
  }

  return "";
};

const ensureSameNumberOfColumns = (iterable: string[][]) => {
  const maxNumberOfColumns = Math.max(...iterable.map((i) => i.length));

  iterable.forEach((row) => {
    while (row.length < maxNumberOfColumns) {
      row.push(DASH_SYMBOL);
    }
  });
};
