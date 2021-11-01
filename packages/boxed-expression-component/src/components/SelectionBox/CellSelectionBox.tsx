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

import _ from "lodash";
import * as React from "react";
import { useCallback, useMemo, useRef } from "react";
import { SelectionBox, SelectionRect } from ".";
import { CELL_CSS_SELECTOR } from "../Resizer/dom/DOMSession";
import { paste } from "../Table/common";
import "./CellSelectionBox.css";

const SELECTED_CELL = "cell--selected";

const EDITABLE_CELL = "editable-cell";

export const CellSelectionBox: React.FunctionComponent = () => {
  const textarea = useRef<HTMLTextAreaElement>(null);

  const allEditableCells = useCallback((): Element[] => {
    const hasEditableCell = (cell: Element) => !!cell.querySelector(`.${EDITABLE_CELL}`);
    const allCells = document.querySelectorAll(CELL_CSS_SELECTOR);

    return [].slice.call(allCells).filter(hasEditableCell);
  }, []);

  const findCell = useCallback((x: number, y: number): Element | null => {
    let refElement = null;
    try {
      refElement = document.elementFromPoint(x, y);
      const closest = refElement?.closest(CELL_CSS_SELECTOR);
      if (closest) {
        return closest;
      }
    } catch (e) {
      return null;
    }
    return refElement?.closest("td")?.querySelector(CELL_CSS_SELECTOR) || null;
  }, []);

  const findFirstCell = useCallback(
    (rect): Element | null => {
      const x = rect.x;
      const y = rect.y;
      return findCell(x, y);
    },
    [findCell]
  );

  const findLastCell = useCallback(
    (rect): Element | null => {
      const x = rect.x + rect.width;
      const y = rect.y + rect.height;
      return findCell(x, y);
    },
    [findCell]
  );

  const lowlightCells = useCallback(() => {
    document.querySelectorAll(`.${SELECTED_CELL}`).forEach((c) => c.classList.remove(SELECTED_CELL));
  }, []);

  const highlightCells = useCallback(
    (cells: Element[]) => {
      lowlightCells();
      cells.forEach((c) => c.classList.add(SELECTED_CELL));
    },
    [lowlightCells]
  );

  const enableSelection = useCallback(
    (rect: SelectionRect | null) => {
      if (!rect) {
        return;
      }

      const firstCell = findFirstCell(rect)?.getBoundingClientRect();
      const lastCell = findLastCell(rect)?.getBoundingClientRect();

      const xStart = firstCell?.x || rect.x;
      const xEnd = lastCell?.x || rect.x + rect.width;

      const yStart = firstCell?.y || rect.y;
      const yEnd = lastCell?.y || rect.y + rect.height;

      const selectedCells = allEditableCells().filter((cell: HTMLElement) => {
        const cellRect = cell.getBoundingClientRect();
        const cellRectX = cellRect.x ?? 0;
        const cellRectY = cellRect.y ?? 0;
        return cellRectX >= xStart && cellRectX <= xEnd && cellRectY >= yStart && cellRectY <= yEnd;
      });

      if (textarea.current) {
        highlightCells(selectedCells);
        const rowsGroupedByY = _(selectedCells).groupBy((e) => e.getBoundingClientRect().y);
        let selectedValue = "";

        rowsGroupedByY.forEach((row: HTMLElement[]) => {
          for (let i = 0; i < row.length; i++) {
            const value = row[i].querySelector("textarea")!.textContent;
            selectedValue += `${value}`;
            if (i < row.length - 1) {
              selectedValue += "\t";
            }
          }
          selectedValue += "\n";
        });

        textarea.current.value = selectedValue;
        textarea.current.focus();
        textarea.current.setSelectionRange(0, selectedValue.length);
      }
    },
    [findFirstCell, highlightCells, findLastCell, allEditableCells, textarea]
  );

  const disableSelection = useCallback(() => {
    lowlightCells();
  }, [lowlightCells]);

  const disableHighlightedCells = useCallback(() => {
    const selectedCellClassName = "editable-cell--selected";
    const selectedCell = document.querySelector(`.${selectedCellClassName}`);
    selectedCell?.classList.remove(selectedCellClassName);
  }, []);

  const ignoredElements = useMemo(
    () => [
      "pf-c-drawer__splitter",
      "pf-m-vertical",
      "pf-c-form-control",
      "pf-u-text-truncate",
      "data-type",
      "form-control",
    ],
    []
  );

  const setCellsValue = useCallback(
    (event) => {
      const pasteValue = event.target.value;
      const selectedCell = document.querySelector(`.${SELECTED_CELL}`);

      if (!selectedCell) {
        return;
      }

      paste(pasteValue, selectedCell);
      disableSelection();
    },
    [disableSelection]
  );

  return useMemo(
    () => (
      <div className="kie-cell-selection-box">
        <SelectionBox
          ignoredElements={ignoredElements}
          onDragMove={disableHighlightedCells}
          onDragStop={enableSelection}
        />
        <textarea ref={textarea} onBlur={disableSelection} onChange={setCellsValue}></textarea>
      </div>
    ),
    [enableSelection, disableSelection, disableHighlightedCells, ignoredElements, setCellsValue]
  );
};
