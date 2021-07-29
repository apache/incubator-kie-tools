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

import { applyDOMSupervisor, Cell } from "../../../../components/Resizer/dom";

const fakeCells = [fakeCell(0), fakeCell(2), fakeCell(4), fakeCell(8)];

describe("ResizerSupervisorDOM", () => {
  describe("applyDOMSupervisor", () => {
    beforeEach(() => {
      applyDOMSupervisor();
    });

    it("refreshes cell widths as parents", () => {
      expect(fakeCells[0].spyRefreshWidthAsParent).toBeCalled();
      expect(fakeCells[1].spyRefreshWidthAsParent).toBeCalled();
      expect(fakeCells[2].spyRefreshWidthAsParent).toBeCalled();
      expect(fakeCells[3].spyRefreshWidthAsParent).toBeCalled();
    });

    it("refreshes cell widths as last column", () => {
      expect(fakeCells[0].spyRefreshWidthAsLastColumn).toBeCalled();
      expect(fakeCells[1].spyRefreshWidthAsLastColumn).toBeCalled();
      expect(fakeCells[2].spyRefreshWidthAsLastColumn).toBeCalled();
      expect(fakeCells[3].spyRefreshWidthAsLastColumn).toBeCalled();
    });
  });
});

jest.mock("src/components/Resizer/dom", () => {
  const actualResizerDOM = jest.requireActual("src/components/Resizer/dom");
  return {
    ...actualResizerDOM,
    DOMSession: jest.fn(() => ({
      getCells: () => fakeCells.map((c) => c.cell),
    })),
    Cell: jest.fn(() => ({
      refreshWidthAsParent: () => ({}),
      refreshWidthAsLastColumn: () => ({}),
      refreshWidthAsLastGroupColumn: () => ({}),
    })),
  };
});

function fakeCell(depth: number) {
  const cell = new Cell({} as HTMLElement, [], depth);
  const spyRefreshWidthAsParent = jest.spyOn(cell, "refreshWidthAsParent");
  const spyRefreshWidthAsLastColumn = jest.spyOn(cell, "refreshWidthAsLastColumn");
  return { cell, spyRefreshWidthAsParent, spyRefreshWidthAsLastColumn };
}
