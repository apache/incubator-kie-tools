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

import { getCellByCoordinates, getFullCellCoordinates, hasCellTabindex, TableCellCoordinates } from "./TableUtils";

export const focusTextInput = (input?: HTMLTextAreaElement | HTMLInputElement | null, eraseContent = false) => {
  if (!input) {
    return;
  }

  if (eraseContent) {
    input.value = "";
  }

  const value = input.value || "";
  input.focus();

  if (input.tagName === "TEXTAREA") {
    input.setSelectionRange(value.length, value.length);
  }
};

export const blurActiveElement = () => {
  if (!document.activeElement) {
    return;
  }
  const activeElement = document.activeElement as HTMLElement;
  activeElement.blur();
};

export const focusNextTextArea = (currentTextArea: HTMLTextAreaElement | null) => {
  if (!currentTextArea) {
    return;
  }

  const textAreas = document.querySelectorAll("textarea");
  const indexOfCurrent: number = [].slice.call(textAreas).indexOf(currentTextArea);
  const indexOfNext = indexOfCurrent < textAreas.length - 1 ? indexOfCurrent + 1 : 0;

  textAreas.item(indexOfNext).focus();
};

/**
 * Get Parent Cell of the current element.
 *
 * @param currentEl the current element
 * @returns the element of the td parent
 */
export const getParentCell = (currentEl: HTMLElement | null): HTMLTableCellElement | null => {
  const cellSelector = "td, th";

  if (!currentEl) {
    return null;
  }

  if (currentEl.matches(cellSelector)) {
    return currentEl as HTMLTableCellElement;
  } else {
    return currentEl.closest(cellSelector) as HTMLTableCellElement;
  }
};

/**
 * do the focus of a cell
 *
 * @param cell the cell to focus
 * @returns
 */
export const cellFocus = (cell: HTMLTableCellElement | null): void => {
  if (!cell) {
    return;
  }

  if (!hasCellTabindex(cell)) {
    return focusLowerCell(cell);
  }

  cell.click();
  cell.focus();
};

/**
 * Focus Current Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusCurrentCell = (currentEl: HTMLElement | null): void => {
  cellFocus(getParentCell(currentEl));
};

/**
 * Focus Next Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowSpan the rowSpan of the current cell
 * @param stopAtLastDataCell true to stop stop at end of row
 * @returns
 */
const focusNextCell = (currentEl: HTMLElement | null, rowSpan = 1, stopAtLastDataCell = true): void => {
  const currentCell = getParentCell(currentEl);

  if (!currentCell) {
    return;
  }

  const nextCell = currentCell.nextElementSibling as HTMLTableCellElement;
  const { x, y } = getFullCellCoordinates(currentCell);

  if (!nextCell) {
    if (!stopAtLastDataCell) {
      return focusCellByCoordinates(currentCell, { y: y + 1, x: 1 });
    }

    return cellFocus(currentCell);
  }

  if (rowSpan > 1) {
    return focusCellByCoordinates(nextCell, { y: y - 1, x: x + 1 });
  }

  cellFocus(nextCell);
};

/**
 * Focus Next Cell of a react-table, navigating with the arrow key.
 * Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowSpan the rowSpan of the current cell
 * @returns
 */
export const focusNextCellByArrowKey = (currentEl: HTMLElement | null, rowSpan = 1): void => {
  return focusNextCell(currentEl, rowSpan, true);
};

/**
 * Focus Next Cell of a react-table, navigating with the tab key.
 * Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowSpan the rowSpan of the current cell
 * @returns
 */
export const focusNextCellByTabKey = (currentEl: HTMLElement | null, rowSpan = 1): void => {
  return focusNextCell(currentEl, rowSpan, false);
};

/**
 * Focus the previous cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowSpan the rowSpan of the current cell
 * @param stopAtFirstDataCell true to stop at first data cell
 * @returns
 */
const focusPrevCell = (currentEl: HTMLElement | null, rowSpan = 1, stopAtFirstDataCell = true): void => {
  const currentCell = getParentCell(currentEl);

  if (!currentCell) {
    return;
  }

  const { x, y } = getFullCellCoordinates(currentCell);
  const xEdge = stopAtFirstDataCell ? 0 : 1;
  const yEdge = stopAtFirstDataCell ? y : 0;

  if (x <= xEdge && y <= yEdge) {
    return cellFocus(currentCell);
  }

  if (x <= xEdge && !stopAtFirstDataCell) {
    const lastCellPrevRow = getCellByCoordinates(currentCell.closest("table")!, { y: y - 1, x: -1 });

    return cellFocus(hasCellTabindex(lastCellPrevRow!) ? lastCellPrevRow : currentCell);
  }

  focusCellByCoordinates(currentCell, { y: rowSpan > 1 ? y - 1 : y, x: x - 1 });
};

/**
 * Focus the previous cell of a react-table, navigating with the arrow key.
 * Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowSpan the rowSpan of the current cell
 * @returns
 */
export const focusPrevCellByArrowKey = (currentEl: HTMLElement | null, rowSpan = 1): void => {
  return focusPrevCell(currentEl, rowSpan, true);
};

/**
 * Focus the previous cell of a react-table, navigating with tab key.
 * Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowSpan the rowSpan of the current cell
 * @returns
 */
export const focusPrevCellByTabKey = (currentEl: HTMLElement | null, rowSpan = 1): void => {
  return focusPrevCell(currentEl, rowSpan, false);
};

/**
 * Focus Upper Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusUpperCell = (currentEl: HTMLElement | null): void => {
  const currentCell = getParentCell(currentEl) as HTMLTableCellElement;

  if (!currentCell) {
    return;
  }

  const currentTable = currentCell.closest("table");

  if (!currentTable) {
    return;
  }

  const { x, y } = getFullCellCoordinates(currentCell);

  if (y <= 0) {
    return cellFocus(currentCell);
  }

  focusCellByCoordinates(currentEl, { x, y: y - 1 });
};

/**
 * Focus a cell by coordinates.
 *
 * @param currentEl the crrent element
 * @param cellCoordinates the cell coordinates. set x or y = -1 to focus the last element.
 * @returns
 */
export const focusCellByCoordinates = (currentEl: HTMLElement | null, cellCoordinates: TableCellCoordinates): void => {
  const currentCell = getParentCell(currentEl) as HTMLTableCellElement;
  const { x, y } = cellCoordinates || {};

  if (!currentCell || !cellCoordinates || x === undefined || x < -1 || y === undefined) {
    return;
  }

  const currentTable = currentCell.closest("table");

  if (!currentTable) {
    return;
  }

  const gotoCell = getCellByCoordinates(currentTable, { x, y });

  if (!gotoCell) {
    return cellFocus(currentCell);
  }
  if (!hasCellTabindex(gotoCell)) {
    return focusCellByCoordinates(currentEl, { x, y: y + 1 });
  }
  cellFocus(gotoCell);
};

/**
 * Focus Lower Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusLowerCell = (currentEl: HTMLElement | null): void => {
  const currentCell = getParentCell(currentEl) as HTMLTableCellElement;

  if (!currentCell) {
    return;
  }

  const { x, y } = getFullCellCoordinates(currentCell);

  focusCellByCoordinates(currentEl, { x, y: y + 1 });
};

/**
 * Focus Inside Cell of a react-table. Can focus an input or a nested table inside a cell.
 *
 * @param currentEl the crrent element
 * @param eraseContent set to true to Erase the Content, valid only for textarea cells
 * @returns
 */
export const focusInsideCell = (currentEl: HTMLElement | null, eraseContent = false): void => {
  if (!currentEl) {
    return;
  }

  const nestedTbody = currentEl.querySelector("table > tbody") as HTMLTableSectionElement;

  if (nestedTbody) {
    cellFocus(nestedTbody.rows[0].cells[1]);
    return;
  }

  const cellWithClickActionElement = currentEl.querySelector(`
    .counter-header-cell .selected-function-kind,
    .header-cell-info >:first-child:not(.function-definition-container), 
    .with-popover-menu, 
    .logic-type-not-present,
    .logic-type-selector button
  `) as HTMLElement;

  if (cellWithClickActionElement) {
    cellWithClickActionElement.click();
    return;
  }

  focusTextInput(currentEl.querySelector("textarea, input[type=text]") as HTMLInputElement, eraseContent);
};

/**
 * Focus Parent Cell of a cell.
 *
 * @param currentCell the current cell
 * @returns
 */
export const focusParentCell = (currentCell: HTMLElement | null): void => {
  if (!currentCell) {
    return;
  }

  cellFocus(currentCell.parentElement?.closest("td, th") || null);
};
