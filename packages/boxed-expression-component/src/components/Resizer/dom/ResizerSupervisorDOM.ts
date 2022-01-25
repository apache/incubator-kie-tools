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

import { DOMSession, Cell } from "../dom";

/*
 * =============================================================================
 * React state + hooks abstractions have a level of granularity that limits the
 * frame hate in the context of resizable boxed expression cells.
 *
 * This component intentionally accesses DOM elements, but it also propagates
 * new width information across React components once users commit an action.
 * =============================================================================
 */

export let DEFAULT_MIN_WIDTH = 100;

export const applyDOMSupervisor = (isRunnerTable: boolean, container: HTMLElement): void => {
  new SupervisorExecution(isRunnerTable, container).execute();
};

class SupervisorExecution {
  domSession: DOMSession;

  constructor(private readonly isRunnerTable: boolean, private readonly editorElement: HTMLElement) {
    this.domSession = new DOMSession(editorElement);
  }

  execute() {
    const cells = this.domSession.getCells().sort((c1, c2) => c2.depth - c1.depth);

    cells.forEach(this.refreshWidthAsParent);
    if (this.isRunnerTable) {
      DEFAULT_MIN_WIDTH = 150;
      const properties = { originalIndex: 0, cellIndex: 0 };
      for (properties.originalIndex; properties.originalIndex < cells.length; properties.originalIndex++) {
        this.refreshWidthAsLastGroupColumnRunner(cells[properties.originalIndex], properties);
      }
    } else {
      cells.forEach(this.refreshWidthAsLastGroupColumn);
    }
    cells.sort((c1, c2) => c1.depth - c2.depth).forEach(this.refreshWidthAsLastColumn);
  }

  private refreshWidthAsParent(cell: Cell) {
    cell.refreshWidthAsParent();
  }

  private refreshWidthAsLastColumn(cell: Cell) {
    cell.refreshWidthAsLastColumn();
  }

  private refreshWidthAsLastGroupColumnRunner(cell: Cell, properties: any) {
    cell.refreshWidthAsLastGroupColumnRunner(properties);
    properties.cellIndex++;
  }

  private refreshWidthAsLastGroupColumn(cell: Cell) {
    cell.refreshWidthAsLastGroupColumn();
  }
}
