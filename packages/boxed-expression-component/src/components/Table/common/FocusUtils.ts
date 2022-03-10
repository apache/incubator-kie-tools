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

export const focusTextArea = (textarea?: HTMLTextAreaElement | null, eraseContent = false) => {
  if (!textarea) {
    return;
  }

  if (eraseContent) {
    textarea.value = "";
  }

  const value = textarea.value || "";
  textarea.focus();
  textarea.setSelectionRange(value.length, value.length);
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

  return (currentEl.matches(cellSelector) ? currentEl : currentEl.closest(cellSelector)) as HTMLTableCellElement;
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
 * @returns
 */
export const focusNextCell = (currentEl: HTMLElement | null): void => {
  cellFocus(getParentCell(currentEl)?.nextElementSibling as HTMLTableCellElement);
};

/**
 * Focus Next Data Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowIndex the current row index
 * @returns
 */
export const focusNextDataCell = (currentEl: HTMLElement | null, rowIndex: number): void => {
  const currentCell = getParentCell(currentEl);

  if (!currentCell) {
    return;
  }

  const nextCell = currentCell.nextElementSibling as HTMLTableCellElement;

  if (!nextCell) {
    focusLowerCell(currentCell, rowIndex, 1);
  } else {
    cellFocus(nextCell);
  }
};

/**
 * Focus Prev Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusPrevCell = (currentEl: HTMLElement | null): void => {
  cellFocus(getParentCell(currentEl)?.previousElementSibling as HTMLTableCellElement);
};

/**
 * Focus Prev Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowIndex the current row index
 * @returns
 */
export const focusPrevDataCell = (currentEl: HTMLElement | null, rowIndex: number): void => {
  const currentCell = getParentCell(currentEl);

  if (!currentCell) {
    return;
  }

  const lastCellIndex = (currentCell.parentElement as HTMLTableRowElement).cells.length - 1;
  const cellIndex = currentCell.cellIndex;

  if (cellIndex <= 1) {
    focusUpperCell(currentCell, rowIndex, lastCellIndex);
  } else {
    focusPrevCell(currentCell);
  }
};

/**
 * Focus Upper Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowIndex the current row index
 * @returns
 */
export const focusUpperCell = (currentEl: HTMLElement | null, rowIndex: number, cellIndex?: number): void => {
  const currentCell = getParentCell(currentEl) as HTMLTableCellElement;

  if (!currentCell) {
    return;
  }

  const currentBody = currentCell.closest("tbody");
  const gotoRow = currentBody?.rows[rowIndex - 1];
  const gotoCellIndex = cellIndex === undefined ? currentCell.cellIndex : cellIndex;

  cellFocus((gotoRow?.cells[gotoCellIndex] as HTMLTableCellElement) || currentCell);
};

/**
 * Focus Lower Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowIndex the current row index
 * @param cellIndex the cell index to focus
 * @returns
 */
export const focusLowerCell = (currentEl: HTMLElement | null, rowIndex: number, cellIndex?: number): void => {
  const currentCell = getParentCell(currentEl) as HTMLTableCellElement;

  if (!currentCell) {
    return;
  }
  const currentBody = currentCell.closest("tbody");
  const gotoRow = currentBody?.rows[rowIndex + 1];
  const gotoCellIndex = cellIndex === undefined ? currentCell.cellIndex : cellIndex;

  cellFocus((gotoRow?.cells[gotoCellIndex] as HTMLTableCellElement) || currentCell);
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

  const cellWithPopoverMenu = currentEl.querySelector(".with-popover-menu, .logic-type-not-present") as HTMLElement;

  if (cellWithPopoverMenu) {
    cellWithPopoverMenu.click();
    return;
  }

  const cellWithSelect = currentEl.querySelector(".logic-type-selector button") as HTMLElement;

  if (cellWithSelect) {
    cellWithSelect.click();
    return;
  }

  focusTextArea(currentEl.querySelector("textarea"), eraseContent);
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

  cellFocus(currentCell.parentElement?.closest("td") || null);
};
