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

import * as _ from "lodash";

/**
 * TableCellCoordinates.
 * Colspan are also counted.
 */
export type TableCellCoordinates = {
  x: number;
  y: number;
};

const DEFAULT = { x: 0, y: 0 };

/**
 * Fetch cell coordinates.
 */
export const getCellCoordinates: (cell: Element | undefined | null) => TableCellCoordinates = (cell) => {
  const tbody = cell?.closest("tbody");
  if (!tbody) {
    return DEFAULT;
  }

  const rows = tbody.querySelectorAll("tr");

  for (let y = 0; y < rows.length; y++) {
    const row = rows[y];
    const cols = row.querySelectorAll(".data-cell");

    for (let x = 0; x < cols.length; x++) {
      if (cell === cols[x]) {
        return { x: x, y };
      }
    }
  }

  return DEFAULT;
};

/**
 * Fetch cell coordinates reading the data-xposition and data-yposition cell attributes for better perfomrmance. It counts all cells from the table header.
 * Note: supports the colspan.
 * @param cell the node of the cell
 * @return the coordinates of the cell, null if the attributes are not found.
 *
 */
export const getFullCellCoordinatesByDataAttributes: (cell: Element | undefined | null) => TableCellCoordinates | null =
  (cell) => {
    if (!cell || !(cell instanceof HTMLTableCellElement)) {
      return null;
    }

    const xPosition = parseInt(cell.getAttribute("data-xposition") + "");
    const yPosition = parseInt(cell.getAttribute("data-yposition") + "");

    if (isNaN(xPosition) || isNaN(yPosition)) {
      return null;
    }

    return {
      x: xPosition,
      y: yPosition,
    };
  };

/**
 * Fetch cell coordinates, counting the position cell by cell from the table header.
 * Note: supports the colspan.
 * @param cell the node of the cell
 * @return the coordinates of the cell, null otherwise
 *
 */
export const getFullCellCoordinatesByHTMLPosition: (cell: Element | undefined | null) => TableCellCoordinates | null = (
  cell
) => {
  const table = cell?.closest("table");
  if (!table) {
    return null;
  }

  const rows = table.rows;

  for (let y = 0; y < rows.length; y++) {
    const row = rows[y];
    const cols = row.cells;
    let colspansSum = 0;

    for (let x = 0; x < cols.length; x++) {
      if (cell === cols[x]) {
        return { x: x + colspansSum, y };
      }

      colspansSum += parseInt(cols[x].getAttribute("colspan") || "1") - 1;
    }
  }

  return null;
};

/**
 * Fetch cell coordinates, counting all cells from the table header. It reads data-xposition and data-yposition first for better perfomrmance.
 * Note: supports the colspan.
 * @param cell the node of the cell
 * @return the coordinates of the cell
 *
 */
export const getFullCellCoordinates: (cell: Element | undefined | null) => TableCellCoordinates = (cell) => {
  return getFullCellCoordinatesByDataAttributes(cell) || getFullCellCoordinatesByHTMLPosition(cell) || DEFAULT;
};

/**
 * Fetch the closest parent table id for a given cell.
 */
export const getCellTableId: (cell: Element | undefined | null) => string = (cell) => {
  const cssClasses = cell?.closest(".table-component")?.classList || [];
  return _.first([].slice.call(cssClasses).filter((c: string) => c.match(/table-event-/g))) || "";
};

/**
 * Get a cell by coordinates counting the colspans too reading the data-xposition and data-yposition attributes.
 *
 * @param table the table
 * @param cellCoordinates the cell coordinates. set x or y = -1 to get the last element.
 * @returns the table cell, null otherwise
 */
export const getCellByCoordinatesFromDataAttributes = (
  table: HTMLTableElement,
  cellCoordinates: TableCellCoordinates
): HTMLTableCellElement | null => {
  if (!table || !cellCoordinates) {
    return null;
  }

  const node = table.querySelector(`
    thead > tr > th[data-xposition='${cellCoordinates.x}'][data-yposition='${cellCoordinates.y}'], 
    tbody > tr > td[data-xposition='${cellCoordinates.x}'][data-yposition='${cellCoordinates.y}']
  `);

  // verify that node is really child of table. ":scope" selector cannot be used in jest: "SyntaxError: unknown pseudo-class selector"
  if (!node || node.closest("table") !== table) {
    return null;
  }

  return node as HTMLTableCellElement;
};

/**
 * Get a cell by coordinates counting the colspans too. Counting the node position in the HTML
 *
 * @param table the table
 * @param cellCoordinates the cell coordinates. set x or y = -1 to get the last element.
 * @returns the table cell, null otherwise
 */
export const getCellByCoordinatesFromHTMLPosition = (
  table: HTMLTableElement,
  cellCoordinates: TableCellCoordinates
): HTMLTableCellElement | null => {
  const { x, y } = cellCoordinates || {};
  const xOutOfIndex = x === undefined || x < -1;
  const yOutOfIndex = y === undefined || y < -1;

  if (!table || xOutOfIndex || yOutOfIndex || table.rows.length <= y) {
    return null;
  }

  const row = table.rows[y === -1 ? table.rows.length - 1 : y];

  if (!row) {
    return null;
  }

  if (x === -1) {
    return row.lastChild as HTMLTableCellElement;
  }

  let ci = 0,
    currentCell = null,
    nextCell = row.cells[0];

  for (let colspan = 1; nextCell && ci <= x; ci += colspan) {
    currentCell = nextCell;
    colspan = parseInt(currentCell.getAttribute("colspan") ?? "1");
    nextCell = currentCell.nextElementSibling as HTMLTableCellElement;
  }

  if (ci < x) {
    return null;
  }

  return currentCell;
};

/**
 * Get a cell by coordinates counting the colspans too
 *
 * @param table the table
 * @param cellCoordinates the cell coordinates. set x or y = -1 to get the last element.
 * @returns the table cell, null otherwise
 */
export const getCellByCoordinates = (
  table: HTMLTableElement,
  cellCoordinates: TableCellCoordinates
): HTMLTableCellElement | null => {
  return (
    getCellByCoordinatesFromDataAttributes(table, cellCoordinates) ||
    getCellByCoordinatesFromHTMLPosition(table, cellCoordinates)
  );
};

/**
 * Check if a cell has tabindex set.
 *
 * @param cell the cell to check
 * @returns true if yes, false otherwise
 */
export const hasCellTabindex = (cell: HTMLTableCellElement): boolean => {
  if (!cell) {
    return false;
  }
  return cell.hasAttribute("tabindex");
};
