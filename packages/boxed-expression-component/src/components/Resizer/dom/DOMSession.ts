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

import { Cell } from "./";

export const CELL_CSS_SELECTOR = ".react-resizable";

export class DOMSession {
  private cells: Cell[] | undefined;

  getCells(): Cell[] {
    if (this.cells === undefined) {
      this.cells = this.buildCells();
    }
    return this.cells;
  }

  private buildCells() {
    const cells: Cell[] = [];
    this.fetchCellElements(document.body).forEach((cellElement) => {
      this.buildCell(cellElement, cells, 0);
    });
    return cells;
  }

  private buildCell(htmlElement: HTMLElement, cells: Cell[], depthLevel: number): Cell {
    const exitingElement = cells.find((c) => c.element === htmlElement);
    if (exitingElement) {
      return exitingElement;
    }

    const cell = new Cell(
      htmlElement,
      this.fetchCellElements(htmlElement)
        .map((child) => this.buildCell(child, cells, depthLevel + 1))
        .filter((c) => c.depth == depthLevel + 1),
      depthLevel
    );

    cells.push(cell);

    return cell;
  }

  private fetchCellElements(parent: HTMLElement): HTMLElement[] {
    const htmlElements = parent.querySelectorAll(CELL_CSS_SELECTOR);
    return [].slice.call(htmlElements);
  }
}
