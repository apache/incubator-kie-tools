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
 * Focus Sibling Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currCell the current cell
 * @param getSibling callback to get the sibling cell
 * @returns
 */
const focusSibligngCell = (currCell: HTMLElement | null, getSibling: (parent: Element) => Element | null): void => {
  const cellSelector = "td";

  if (!currCell) {
    return;
  }

  const parent = currCell.matches(cellSelector) ? currCell : currCell.closest(cellSelector);

  if (!parent) {
    return;
  }

  const gotoEl = <HTMLElement>getSibling(parent);

  if (!gotoEl) {
    return;
  }

  gotoEl.focus();
};

/**
 * Focus Next Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currCell -
 * @returns
 */
export const focusNextCell = (currCell: HTMLElement | null): void => {
  focusSibligngCell(currCell, (parent) => {
    return parent?.nextElementSibling;
  });
};

/**
 * Focus Prev Cell of a react-table. Works from any element inside a cell or a cell itself.
 *
 * @param currCell -
 * @returns
 */
export const focusPrevCell = (currCell: HTMLElement | null): void => {
  focusSibligngCell(currCell, (parent) => {
    return parent?.previousElementSibling;
  });
};
