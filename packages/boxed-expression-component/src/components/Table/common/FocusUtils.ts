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

export const focusTextArea = (textarea?: HTMLTextAreaElement | null) => {
  const value = textarea?.value || "";
  textarea?.focus();
  textarea?.setSelectionRange(value.length, value.length);
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
export const getParentCell = (currentEl: HTMLElement | null): HTMLTableDataCellElement | null => {
  const cellSelector = "td, th";

  if (!currentEl) {
    return null;
  }

  return <HTMLTableDataCellElement>(currentEl.matches(cellSelector) ? currentEl : currentEl.closest(cellSelector));
};

/**
 * do the focus of a cell
 *
 * @param cell the cell to focus
 * @returns
 */
export const cellFocus = (cell: HTMLTableDataCellElement | null): void => {
  if (!cell) {
    return;
  }

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
  cellFocus(<HTMLTableDataCellElement>getParentCell(currentEl)?.nextElementSibling);
};

/**
 * Focus Prev Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusPrevCell = (currentEl: HTMLElement | null): void => {
  cellFocus(<HTMLTableDataCellElement>getParentCell(currentEl)?.previousElementSibling);
};

/**
 * Focus Upper Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusUpperCell = (currentEl: HTMLElement | null): void => {
  const currCell = <HTMLTableDataCellElement>getParentCell(currentEl);
  const currRow = <HTMLTableRowElement>currCell.parentElement;
  const currTable = currCell.closest("table");
  const gotoRow = currTable?.rows[currRow.rowIndex - 1];

  cellFocus(<HTMLTableDataCellElement>gotoRow?.cells[currCell.cellIndex]);
};

/**
 * Focus Lower Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusLowerCell = (currentEl: HTMLElement | null): void => {
  const currCell = <HTMLTableDataCellElement>getParentCell(currentEl);
  const currRow = <HTMLTableRowElement>currCell.parentElement;
  const currTable = currCell.closest("table");
  const gotoRow = currTable?.rows[currRow.rowIndex + 1];

  cellFocus(<HTMLTableDataCellElement>gotoRow?.cells[currCell.cellIndex]);
};
