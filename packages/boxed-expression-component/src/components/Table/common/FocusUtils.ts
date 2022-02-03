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
export const getParentCell = (currentEl: HTMLElement | null): HTMLTableCellElement | null => {
  const cellSelector = "td, th";

  if (!currentEl) {
    return null;
  }

  return <HTMLTableCellElement>(currentEl.matches(cellSelector) ? currentEl : currentEl.closest(cellSelector));
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
  cellFocus(<HTMLTableCellElement>getParentCell(currentEl)?.nextElementSibling);
};

/**
 * Focus Prev Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusPrevCell = (currentEl: HTMLElement | null): void => {
  cellFocus(<HTMLTableCellElement>getParentCell(currentEl)?.previousElementSibling);
};

/**
 * Focus Upper Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowIndex the current row index
 * @returns
 */
export const focusUpperCell = (currentEl: HTMLElement | null, rowIndex: number): void => {
  /* FIXME: Please, make it consistent with the currentEl. Use currentCell and currentBody  */
  const currCell = <HTMLTableCellElement>getParentCell(currentEl);
  const currBody = currCell.closest("tbody");
  const gotoRow = currBody?.rows[rowIndex - 1];

  cellFocus(<HTMLTableCellElement>gotoRow?.cells[currCell.cellIndex]);
};

/**
 * Focus Lower Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currentEl the crrent element
 * @param rowIndex the current row index
 * @returns
 */
export const focusLowerCell = (currentEl: HTMLElement | null, rowIndex: number): void => {
  /* FIXME: Please, make it consistent with the currentEl. Use currentCell and currentBody  */
  const currCell = <HTMLTableCellElement>getParentCell(currentEl);
  const currBody = currCell.closest("tbody");
  const gotoRow = currBody?.rows[rowIndex + 1];

  cellFocus(<HTMLTableCellElement>gotoRow?.cells[currCell.cellIndex]);
};

/**
 * Focus Inside Cell of a react-table. Can focus an input or a nested table inside a cell.
 *
 * @param currentEl the crrent element
 * @returns
 */
export const focusInsideCell = (currentEl: HTMLElement | null): void => {
  if (!currentEl) {
    return;
  }

  const nestedTbody = <HTMLTableSectionElement>currentEl.querySelector("table > tbody");
  const cellWithPopoverMenu = <HTMLElement>currentEl.querySelector(".with-popover-menu, .logic-type-not-present");

  if (nestedTbody) {
    cellFocus(nestedTbody.rows[0].cells[1]);
  } else if (cellWithPopoverMenu) {
    cellWithPopoverMenu.click();
  } else {
    focusTextArea(currentEl.querySelector("textarea"));
  }
};

/**
 * Focus Parent Cell of a cell.
 *
 * @param currCell the current cell
 * @returns
 */
export const focusParentCell = (currCell: HTMLElement | null): void => {
  if (!currCell) {
    return;
  }

  cellFocus(currCell.parentElement?.closest("td") || null);
};
