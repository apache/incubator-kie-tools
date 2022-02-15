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

import {
  focusPrevCell,
  focusNextCell,
  focusUpperCell,
  focusLowerCell,
  focusInsideCell,
  getParentCell,
  focusParentCell,
  cellFocus,
  focusCurrentCell,
  focusTextArea,
  focusNextDataCell,
  focusPrevDataCell,
} from "@kie-tools/boxed-expression-component/dist/components/Table/common";

/**
 * Create Mock HTML Table.
 *
 * @param rows number of rows to create
 * @param cells number of cells to create
 * @returns the new table
 */
function createMockTable(rows = 0, cells = 0): HTMLTableElement {
  const mockTable = document.createElement("table");
  const mockTbody = document.createElement("tbody");
  for (let rowIndex = 0; rowIndex < rows; rowIndex++) {
    const mockTr = document.createElement("tr");
    for (let cellIndex = 0; cellIndex < cells; cellIndex++) {
      const mockTd = document.createElement("td");
      mockTd.appendChild(document.createElement("div"));
      mockTr.appendChild(mockTd);
    }
    mockTbody.appendChild(mockTr);
  }
  mockTable.appendChild(mockTbody);
  return mockTable;
}

const mockTable = createMockTable(3, 6);
const mockTbody = mockTable.tBodies[0];

describe("FocusUtils tests", () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  describe("getParentCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(getParentCell()).toBeNull();
      expect(getParentCell(null)).toBeNull();
    });

    it("should return the input", () => {
      expect(getParentCell(mockTbody.rows[0].cells[1])).toBe(mockTbody.rows[0].cells[1]);
    });

    it("should return the parent", () => {
      expect(getParentCell(<HTMLElement>mockTbody.rows[0].cells[1].children[0])).toBe(mockTbody.rows[0].cells[1]);
    });

    it("should return null", () => {
      expect(getParentCell(document.createElement("span"))).toBeNull();
    });
  });

  describe("cellFocus tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => cellFocus()).not.toThrowError();
      expect(() => cellFocus(null)).not.toThrowError();
    });

    it("should focus the element", () => {
      const element = mockTbody.rows[0].cells[1];
      const mockElementFocus = jest.spyOn(element, "focus");
      cellFocus(element);
      expect(mockElementFocus).toHaveBeenCalled();
    });
  });

  describe("focusCurrentCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusCurrentCell(undefined)).not.toThrowError();
      expect(() => focusCurrentCell(null)).not.toThrowError();
    });

    it("should focus the element", () => {
      const element = mockTbody.rows[0].cells[1];
      const mockElementFocus = jest.spyOn(element, "focus");
      focusCurrentCell(element);
      expect(mockElementFocus).toHaveBeenCalled();
    });

    it("should focus the cell called from an element inside the cell", () => {
      const element = <HTMLElement>mockTbody.rows[0].cells[1].children[0];
      const parentElement = mockTbody.rows[0].cells[1];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockParentFocus = jest.spyOn(parentElement, "focus");

      focusCurrentCell(element);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockParentFocus).toHaveBeenCalled();
    });
  });

  describe("focusNextCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusNextCell()).not.toThrowError();
      expect(() => focusNextCell(null)).not.toThrowError();
    });

    it("should focus the next cell", () => {
      const element = mockTbody.rows[0].cells[1];
      const elementToBeFocused = mockTbody.rows[0].cells[2];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusNextCell(element);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should not change the focus", () => {
      const element = mockTbody.rows[2].cells[5];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusNextCell(element);
      focusNextCell(element);
      expect(mockElementFocus).not.toHaveBeenCalled();
    });
  });

  describe("focusNextDataCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusNextDataCell()).not.toThrowError();
      expect(() => focusNextDataCell(null, 0)).not.toThrowError();
    });

    it("should focus the next cell", () => {
      const element = mockTbody.rows[0].cells[1];
      const elementToBeFocused = mockTbody.rows[0].cells[2];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusNextDataCell(element, 0);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should focus the first cell of the next line", () => {
      const element = mockTbody.rows[0].cells[5];
      const elementToBeFocused = mockTbody.rows[1].cells[1];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusNextDataCell(element, 0);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should keep the focus to the current cell", () => {
      const element = mockTbody.rows[2].cells[5];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusNextDataCell(element, 2);
      expect(mockElementFocus).toHaveBeenCalled();
    });
  });

  describe("focusPrevCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusPrevCell()).not.toThrowError();
      expect(() => focusPrevCell(null)).not.toThrowError();
    });

    it("should focus the previous cell", () => {
      const element = mockTbody.rows[0].cells[1];
      const elementToBeFocused = mockTbody.rows[0].cells[0];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusPrevCell(element);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should not change the focus", () => {
      const element = mockTbody.rows[0].cells[0];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusPrevCell(element);
      expect(mockElementFocus).not.toHaveBeenCalled();
    });
  });

  describe("focusPrevDataCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusPrevDataCell()).not.toThrowError();
      expect(() => focusPrevDataCell(null, 0)).not.toThrowError();
    });

    it("should focus the previous cell", () => {
      const element = mockTbody.rows[0].cells[2];
      const elementToBeFocused = mockTbody.rows[0].cells[1];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusPrevDataCell(element, 0);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should focus the last cell of the previous line", () => {
      const element = mockTbody.rows[1].cells[1];
      const elementToBeFocused = mockTbody.rows[0].cells[5];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusPrevDataCell(element, 1);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should keep the focus to the current cell", () => {
      const element = mockTbody.rows[0].cells[1];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusPrevDataCell(element, 0);
      expect(mockElementFocus).toHaveBeenCalled();
    });

    it("should keep the focus to the current counter cell", () => {
      const element = mockTbody.rows[0].cells[0];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusPrevDataCell(element, 0);
      expect(mockElementFocus).toHaveBeenCalled();
    });
  });

  describe("focusUpperCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusUpperCell(undefined, 1)).not.toThrowError();
      expect(() => focusUpperCell(null, 1)).not.toThrowError();
    });

    it("should focus the upper cell", () => {
      const element = mockTbody.rows[2].cells[2];
      const elementToBeFocused = mockTbody.rows[1].cells[2];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusUpperCell(element, 2);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should keep the focus to the current cell", () => {
      const element = mockTbody.rows[0].cells[5];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusUpperCell(element, 0);
      expect(mockElementFocus).toHaveBeenCalled();
    });
  });

  describe("focusLowerCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusLowerCell(undefined, 1)).not.toThrowError();
      expect(() => focusLowerCell(null, 1)).not.toThrowError();
    });

    it("should focus the lower cell", () => {
      const element = mockTbody.rows[0].cells[2];
      const elementToBeFocused = mockTbody.rows[1].cells[2];
      const mockElementFocus = jest.spyOn(element, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      focusLowerCell(element, 0);
      expect(mockElementFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should keep the focus to the current cell", () => {
      const element = mockTbody.rows[2].cells[5];
      const mockElementFocus = jest.spyOn(element, "focus");

      focusLowerCell(element, 2);
      expect(mockElementFocus).toHaveBeenCalled();
    });
  });

  describe("focusInsideCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusInsideCell(undefined)).not.toThrowError();
      expect(() => focusInsideCell(null)).not.toThrowError();
    });

    it("should focus the textarea", () => {
      const parentCell = document.createElement("td");
      const elementToBeFocused = document.createElement("textarea");
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      parentCell.appendChild(elementToBeFocused);

      focusInsideCell(parentCell);
      expect(mockParentCellFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should focus the second cell of the inner table", () => {
      const parentCell = document.createElement("td");
      const innerTable = createMockTable(2, 3);
      const elementToBeFocused = innerTable.tBodies[0].rows[0].cells[1];
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      parentCell.appendChild(innerTable);

      focusInsideCell(parentCell);
      expect(mockParentCellFocus).not.toHaveBeenCalled();
      expect(mockElementToBeFocused).toHaveBeenCalled();
    });

    it("should open the PopoverMenu", () => {
      const parentCell = document.createElement("td");
      const elementWithPopoverMenu = document.createElement("div");
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockElementWithPopoverMenuClick = jest.spyOn(elementWithPopoverMenu, "click");

      elementWithPopoverMenu.classList.add("with-popover-menu");
      parentCell.appendChild(elementWithPopoverMenu);

      focusInsideCell(parentCell);
      expect(mockParentCellFocus).not.toHaveBeenCalled();
      expect(mockElementWithPopoverMenuClick).toHaveBeenCalled();
    });

    it("should open the Select menu component", () => {
      const parentCell = document.createElement("td");
      const selectWrapper = document.createElement("div");
      const selectMenuBtn = document.createElement("button");
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockSelectMenuBtnClick = jest.spyOn(selectMenuBtn, "click");

      selectWrapper.classList.add("logic-type-selector");
      selectWrapper.appendChild(selectMenuBtn);
      parentCell.appendChild(selectWrapper);

      focusInsideCell(parentCell);
      expect(mockParentCellFocus).not.toHaveBeenCalled();
      expect(mockSelectMenuBtnClick).toHaveBeenCalled();
    });

    it("should not change the focus", () => {
      const emptyCell = document.createElement("td");
      const mockEmptyCellFocus = jest.spyOn(emptyCell, "focus");

      focusInsideCell(emptyCell);
      expect(mockEmptyCellFocus).not.toHaveBeenCalled();
    });
  });

  describe("focusTextArea tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusTextArea()).not.toThrowError();
      expect(() => focusTextArea(null)).not.toThrowError();
    });

    it("should focus the textarea without erasing content", () => {
      const elementToBeFocused = document.createElement("textarea");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      elementToBeFocused.innerHTML = "TextArea Value";
      focusTextArea(elementToBeFocused);
      expect(mockElementToBeFocused).toHaveBeenCalled();
      expect(elementToBeFocused.value).toBe("TextArea Value");
    });

    it("should focus the textarea without content", () => {
      const elementToBeFocused = document.createElement("textarea");
      const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

      elementToBeFocused.value = "TextArea Value";
      focusTextArea(elementToBeFocused, true);
      expect(mockElementToBeFocused).toHaveBeenCalled();
      expect(elementToBeFocused.value).toBe("");
    });
  });

  describe("focusParentCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusParentCell(undefined)).not.toThrowError();
      expect(() => focusParentCell(null)).not.toThrowError();
    });

    it("should focus the parent cell from the inner table", () => {
      const parentCell = document.createElement("td");
      const innerTable = createMockTable(2, 3);
      const elementNotToBeFocused = innerTable.tBodies[0].rows[0].cells[1];
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockElementNotToBeFocused = jest.spyOn(elementNotToBeFocused, "focus");

      parentCell.appendChild(innerTable);

      focusParentCell(elementNotToBeFocused);
      expect(mockParentCellFocus).toHaveBeenCalled();
      expect(mockElementNotToBeFocused).not.toHaveBeenCalled();
    });

    it("should not change the focus", () => {
      const emptyCell = document.createElement("td");
      const mockEmptyCellFocus = jest.spyOn(emptyCell, "focus");

      focusParentCell(emptyCell);
      expect(mockEmptyCellFocus).not.toHaveBeenCalled();
    });
  });
});
