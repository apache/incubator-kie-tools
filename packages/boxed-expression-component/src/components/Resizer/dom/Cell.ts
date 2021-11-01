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
import { notifyCell, widthValue } from "../common";

const RESIZER_PADDING = 14;
const BORDER = 1;

export class Cell {
  private id?: string;
  private lastColumn?: boolean;
  private rect?: DOMRect;
  private parentRow?: HTMLTableRowElement | null;

  constructor(public element: HTMLElement, public children: Cell[], public depth: number) {}

  getId(): string {
    if (!this.id) {
      this.id = _.first([].slice.call(this.element.classList).filter((c: string) => c.match(/uuid-/g))) || "";
    }
    return this.id;
  }

  getRect(): DOMRect {
    if (!this.rect) {
      this.rect = this.element.getBoundingClientRect();
    }
    return this.rect;
  }

  isLastColumn(): boolean {
    if (!this.lastColumn) {
      this.lastColumn = this.getParentRow()?.lastChild === this.element.closest("th, td");
    }
    return this.lastColumn;
  }

  setWidth(width: number): void {
    const cellWidth = widthValue(width);
    notifyCell(this.getId(), cellWidth);
    this.element.style.width = cellWidth + "px";
  }

  refreshWidthAsParent(): void {
    if (this.children.length > 0) {
      this.setWidth(this.fetchChildWidth() + RESIZER_PADDING);
    }
  }

  refreshWidthAsLastColumn(): void {
    if (!this.isLastColumn()) {
      return;
    }

    const parentRect = this.getParentRow()!.getBoundingClientRect();

    if (parentRect) {
      this.setWidth(Math.round(parentRect.right) - Math.round(this.getRect().x) - BORDER);
    }
  }

  refreshWidthAsLastGroupColumn(): void {
    if (!this.isColSpanHeader()) {
      return;
    }

    const refSibling = this.getParent()?.parentElement?.nextSibling;

    if (!refSibling) {
      return;
    }

    const children = [].slice.call((refSibling as HTMLElement).querySelectorAll(`.${this.getHeaderType()}`));
    const childrenRects = children.map((c: HTMLElement) => c.getBoundingClientRect());
    const x = Math.min(...childrenRects.map((c: DOMRect) => c.x));
    const right = Math.max(...childrenRects.map((c: DOMRect) => c.right));

    this.setWidth(right - x - BORDER * 2);
  }

  refreshWidthAsLastGroupColumnRunner(properties: any): void {
    if (!this.isColSpanHeader() && !this.getParentRow()?.classList.contains("table-row")) {
      return;
    }

    let refSibling = this.getParent()?.parentElement?.nextSibling;

    if (!refSibling || (refSibling as any)?.classList?.contains("table-row")) {
      refSibling = document.querySelector('[role="row"]')?.nextSibling;
    }

    // sum the colSpan to determine the header size;
    const headerElements = document.querySelectorAll(".colspan-header");
    const headerSize = Array.from(headerElements).reduce(
      (acc, th: HTMLTableHeaderCellElement) => acc + (th.colSpan || 1),
      0
    );

    // if is header uses the headerType, if not (inputs/outputs cells) use the .data-cell class
    const children: HTMLElement[] = this.isColSpanHeader()
      ? [
          ...Array.from((refSibling as HTMLElement).querySelectorAll(`.input`)),
          ...Array.from((refSibling as HTMLElement).querySelectorAll(`.output`)),
        ]
      : [].slice.call((refSibling as HTMLElement).querySelectorAll(`.data-cell`));

    const colSpan = (this.element?.parentNode as HTMLTableHeaderCellElement)?.colSpan;
    if (colSpan > 1) {
      const firstReact = children[properties.cellIndex].getBoundingClientRect();
      const lastReact = children[properties.cellIndex + colSpan - 1].getBoundingClientRect();
      this.setWidth(lastReact.right - firstReact.x - BORDER * 2);
      properties.cellIndex += colSpan - 1;
    } else {
      const childrenA = children[properties.cellIndex];
      const childrenRects = childrenA?.getBoundingClientRect();
      if (childrenRects) {
        this.setWidth(childrenRects.width - BORDER * 2);
      } else {
        const entries = [
          ...Array.from((refSibling as HTMLElement)?.querySelectorAll(".input")),
          ...Array.from((refSibling as HTMLElement)?.querySelectorAll(".output")),
        ];
        const element =
          entries[
            (properties.originalIndex - headerSize - headerElements.length) % entries.length
          ]?.getBoundingClientRect();
        this.setWidth(element.width - BORDER * 2);
      }
    }
  }

  isColSpanHeader(): boolean {
    return this.getParent()?.classList.contains("colspan-header") || false;
  }

  private getHeaderType() {
    const cssClasses = (this.getParent()?.classList || []) as any as DOMTokenList;

    if (cssClasses.contains("input")) {
      return "input";
    }

    if (cssClasses.contains("output")) {
      return "output";
    }

    return "annotation";
  }

  private getParent() {
    return this.element.parentElement;
  }

  private getParentRow() {
    if (!this.parentRow) {
      this.parentRow = this.element.closest("tr");
    }
    return this.parentRow;
  }

  private fetchChildWidth() {
    const thead = this.element.querySelector("thead, tbody");
    return widthValue(thead?.getBoundingClientRect().width);
  }
}
