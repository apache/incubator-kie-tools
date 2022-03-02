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
 * Create a Mock TableSection.
 *
 * @param tSection the section to modify
 * @param rows number of rows to create
 * @param cells number of cells to create
 * @returns the new section
 */
const createMockTableSection = (tSection: HTMLTableSectionElement, rows = 1, cells = 1): void => {
  for (let rowIndex = 0; rowIndex < rows; rowIndex++) {
    const tr = tSection.insertRow(rowIndex);
    for (let cellIndex = 0; cellIndex < cells; cellIndex++) {
      tr.insertCell(cellIndex).appendChild(document.createElement("div"));
      tr.lastElementChild?.setAttribute("tabindex", "-1");
    }
  }
};

/**
 * Create Mock HTML Table.
 *
 * @param hrows number of header rows to create
 * @param rows number of rows to create
 * @param cells number of cells to create
 * @returns the new table
 */
function createMockTable(hrows = 1, rows = 1, cells = 1): HTMLTableElement {
  const table = document.createElement("table");
  const thead = table.createTHead();
  const tbody = table.createTBody();

  // create thead
  createMockTableSection(thead, hrows, cells);

  // create tbody
  createMockTableSection(tbody, rows, cells);

  return table;
}

/**
 * Tests a Movement.
 *
 * @param element the element where to start
 * @param elementToBeFocused the element that should be focused
 * @param move the movement function
 */
function testFocus(element: HTMLElement, elementToBeFocused: HTMLElement, move: (element: HTMLElement) => void): void {
  const mockElementFocus = jest.spyOn(element, "focus");
  const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

  move(element);
  expect(mockElementFocus).not.toHaveBeenCalled();
  expect(mockElementToBeFocused).toHaveBeenCalled();
}

/**
 * Check a movement not to change the focus
 *
 * @param element the element where to start
 * @param move the movement function
 */
function shouldNotChangeFocus(element: HTMLElement, move: (element: HTMLElement) => void): void {
  const mockElementFocus = jest.spyOn(element, "focus");

  move(element);
  expect(mockElementFocus).not.toHaveBeenCalled();
}

/**
 * Check a movement keep a focus to the element
 *
 * @param element the element where to start
 * @param move the movement function
 */
function shouldKeepFocus(element: HTMLElement, move: (element: HTMLElement) => void): void {
  const mockElementFocus = jest.spyOn(element, "focus");

  move(element);
  expect(mockElementFocus).toHaveBeenCalled();
}

describe("FocusUtils tests", () => {
  const mockTable = createMockTable(2, 3, 6);
  const mockThead = mockTable.tHead || document.createElement("thead");
  const mockTbody = mockTable.tBodies[0];

  const mockTableColRowspan = createMockTable(2, 3, 6);
  const mockTableColRowspanThead = mockTableColRowspan.tHead || document.createElement("thead");
  const mockTableColRowspanTbody = mockTableColRowspan.tBodies[0];

  mockTableColRowspanThead.rows[0].cells[1].setAttribute("colspan", "2");
  mockTableColRowspanThead.rows[0].cells[1].removeAttribute("tabindex");
  mockTableColRowspanThead.rows[0].deleteCell(2);
  mockTableColRowspanThead.rows[0].cells[2].setAttribute("colspan", "2");
  mockTableColRowspanThead.rows[0].deleteCell(3);
  mockTableColRowspanThead.rows[0].cells[3].removeAttribute("tabindex");

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

    it("should return the parent data cell", () => {
      expect(getParentCell(mockTbody.rows[0].cells[1].children[0] as HTMLElement)).toBe(mockTbody.rows[0].cells[1]);
      expect(getParentCell(mockThead.rows[0].cells[1].children[0] as HTMLElement)).toBe(mockThead.rows[0].cells[1]);
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

      shouldKeepFocus(element, (element) => cellFocus(element as HTMLTableCellElement));
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
      shouldKeepFocus(element, focusCurrentCell);
    });

    it("should focus the cell called from an element inside the cell", () => {
      testFocus(mockTbody.rows[0].cells[1].children[0] as HTMLElement, mockTbody.rows[0].cells[1], focusCurrentCell);
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
      const innerTable = createMockTable(1, 2, 3);

      parentCell.appendChild(innerTable);

      testFocus(innerTable.tBodies[0].rows[0].cells[1], parentCell, focusParentCell);
    });

    it("should not change the focus", () => {
      const emptyCell = document.createElement("td");
      shouldNotChangeFocus(emptyCell, focusParentCell);
    });
  });

  describe("focusNextCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusNextCell()).not.toThrowError();
      expect(() => focusNextCell(null)).not.toThrowError();
    });

    it("should focus the next data cell", () => {
      testFocus(mockTbody.rows[0].cells[1], mockTbody.rows[0].cells[2], focusNextCell);
    });

    it("should focus the next header cell", () => {
      testFocus(mockThead.rows[0].cells[1], mockThead.rows[0].cells[2], focusNextCell);
    });

    it("test full headers with rowspan and colspan navigation", () => {
      testFocus(mockTableColRowspanThead.rows[0].cells[0], mockTableColRowspanThead.rows[1].cells[1], focusNextCell);
      testFocus(mockTableColRowspanThead.rows[1].cells[1], mockTableColRowspanThead.rows[1].cells[2], focusNextCell);
      testFocus(mockTableColRowspanThead.rows[1].cells[2], mockTableColRowspanThead.rows[0].cells[3], focusNextCell);
      testFocus(mockTableColRowspanThead.rows[0].cells[3], mockTableColRowspanThead.rows[1].cells[5], focusNextCell);
    });

    it("should not change the focus", () => {
      shouldNotChangeFocus(mockTbody.rows[2].cells[5], focusNextCell);
      shouldNotChangeFocus(mockTableColRowspanThead.rows[1].cells[5], focusNextCell);
    });
  });

  describe("focusNextDataCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusNextDataCell()).not.toThrowError();
      expect(() => focusNextDataCell(null, 0)).not.toThrowError();
    });

    it("should focus the next data cell", () => {
      testFocus(mockTbody.rows[0].cells[1], mockTbody.rows[0].cells[2], (element) => focusNextDataCell(element, 2));
    });

    it("should focus the next header cell", () => {
      testFocus(mockThead.rows[0].cells[1], mockThead.rows[0].cells[2], (element) => focusNextDataCell(element, 2));
    });

    it("should focus the first cell of the next line", () => {
      testFocus(mockTbody.rows[0].cells[5], mockTbody.rows[1].cells[1], (element) => focusNextDataCell(element, 2));
    });

    it("test full headers with rowspan and colspan navigation", () => {
      testFocus(mockTableColRowspanThead.rows[0].cells[0], mockTableColRowspanThead.rows[1].cells[1], (element) =>
        focusNextDataCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[1], mockTableColRowspanThead.rows[1].cells[2], (element) =>
        focusNextDataCell(element, 1)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[2], mockTableColRowspanThead.rows[0].cells[3], (element) =>
        focusNextDataCell(element, 1)
      );
      testFocus(mockTableColRowspanThead.rows[0].cells[3], mockTableColRowspanThead.rows[1].cells[5], (element) =>
        focusNextDataCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[5], mockTableColRowspanTbody.rows[0].cells[1], (element) =>
        focusNextDataCell(element, 1)
      );
    });

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTbody.rows[2].cells[5], (element) => focusNextDataCell(element, 7));
    });
  });

  describe("focusPrevCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusPrevCell()).not.toThrowError();
      expect(() => focusPrevCell(null)).not.toThrowError();
    });

    it("should focus the previous data cell", () => {
      testFocus(mockTbody.rows[0].cells[1], mockTbody.rows[0].cells[0], focusPrevCell);
    });

    it("should focus the previous header cell", () => {
      testFocus(mockThead.rows[0].cells[1], mockThead.rows[0].cells[0], focusPrevCell);
    });

    it("test full headers with rowspan and colspan navigation", () => {
      testFocus(mockTableColRowspanThead.rows[1].cells[5], mockTableColRowspanThead.rows[0].cells[3], focusPrevCell);
      testFocus(mockTableColRowspanThead.rows[0].cells[3], mockTableColRowspanThead.rows[1].cells[2], focusPrevCell);
      testFocus(mockTableColRowspanThead.rows[1].cells[2], mockTableColRowspanThead.rows[1].cells[1], focusPrevCell);
      testFocus(mockTableColRowspanThead.rows[1].cells[1], mockTableColRowspanThead.rows[0].cells[0], focusPrevCell);
    });

    it("should not change the focus", () => {
      shouldNotChangeFocus(mockTbody.rows[0].cells[0], focusPrevCell);
      shouldNotChangeFocus(mockTableColRowspanThead.rows[0].cells[0], focusPrevCell);
    });
  });

  describe("focusPrevDataCell tests", () => {
    /* TODO: FocusUtils.test: focusPrevDataCell: change indexes */
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusPrevDataCell()).not.toThrowError();
      expect(() => focusPrevDataCell(null, 0)).not.toThrowError();
    });

    it("should focus the previous cell", () => {
      testFocus(mockTbody.rows[0].cells[2], mockTbody.rows[0].cells[1], (element) => focusPrevDataCell(element, 0));
    });

    it("should focus the last cell of the previous line", () => {
      testFocus(mockTbody.rows[1].cells[1], mockTbody.rows[0].cells[5], (element) => focusPrevDataCell(element, 1));
    });

    it("should focus the last cell of the header", () => {
      testFocus(mockTbody.rows[0].cells[1], mockThead.rows[1].cells[5], (element) => focusPrevDataCell(element, 0));
    });

    it("test full headers with rowspan and colspan navigation", () => {
      testFocus(mockTableColRowspanTbody.rows[0].cells[1], mockTableColRowspanThead.rows[1].cells[5], (element) =>
        focusPrevDataCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[5], mockTableColRowspanThead.rows[0].cells[3], (element) =>
        focusPrevDataCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[0].cells[3], mockTableColRowspanThead.rows[1].cells[2], (element) =>
        focusPrevDataCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[2], mockTableColRowspanThead.rows[1].cells[1], (element) =>
        focusPrevDataCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[1], mockTableColRowspanThead.rows[0].cells[0], (element) =>
        focusPrevDataCell(element, 0)
      );
    });

    it("should keep the focus to the current counter cell", () => {
      shouldKeepFocus(mockTableColRowspanThead.rows[0].cells[1], (element) => focusPrevDataCell(element, 0));
    });
  });

  describe("focusUpperCell tests", () => {
    /* TODO: FocusUtils.test: focusUpperCell: change indexes */
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusUpperCell(undefined, 1)).not.toThrowError();
      expect(() => focusUpperCell(null, 1)).not.toThrowError();
    });

    it("should focus the upper data cell", () => {
      testFocus(mockTbody.rows[2].cells[2], mockTbody.rows[1].cells[2], (element) => focusUpperCell(element, 2));
    });

    it("test full headers with rowspan and colspan navigation", () => {
      testFocus(mockTableColRowspanTbody.rows[0].cells[1], mockTableColRowspanThead.rows[1].cells[1], (element) =>
        focusUpperCell(element, 0)
      );
      testFocus(mockTableColRowspanTbody.rows[0].cells[3], mockTableColRowspanThead.rows[1].cells[3], (element) =>
        focusUpperCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[3], mockTableColRowspanThead.rows[0].cells[3], (element) =>
        focusUpperCell(element, 0)
      );
    });

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockThead.rows[0].cells[5], (element) => focusUpperCell(element, 0));
      shouldKeepFocus(mockTableColRowspanThead.rows[0].cells[5], (element) => focusUpperCell(element, 0));
    });
  });

  describe("focusLowerCell tests", () => {
    /* TODO: FocusUtils.test: focusLowerCell: change indexes */
    it("should fail", () => {
      // @ts-ignore
      expect(() => focusLowerCell(undefined, 1)).not.toThrowError();
      expect(() => focusLowerCell(null, 1)).not.toThrowError();
    });

    it("should focus the lower data cell", () => {
      testFocus(mockTbody.rows[0].cells[2], mockTbody.rows[1].cells[2], (element) => focusLowerCell(element, 0));
    });

    it("test full headers with rowspan and colspan navigation", () => {
      testFocus(mockTableColRowspanThead.rows[1].cells[2], mockTableColRowspanTbody.rows[0].cells[2], (element) =>
        focusLowerCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[0].cells[3], mockTableColRowspanThead.rows[1].cells[3], (element) =>
        focusLowerCell(element, 0)
      );
      testFocus(mockTableColRowspanThead.rows[1].cells[3], mockTableColRowspanTbody.rows[0].cells[3], (element) =>
        focusLowerCell(element, 0)
      );
    });

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTbody.rows[2].cells[5], (element) => focusLowerCell(element, 2));
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

      parentCell.appendChild(elementToBeFocused);
      testFocus(parentCell, elementToBeFocused, focusInsideCell);
    });

    it("should focus the input text", () => {
      const parentCell = document.createElement("td");
      const elementToBeFocused = document.createElement("input");

      elementToBeFocused.setAttribute("type", "text");
      parentCell.appendChild(elementToBeFocused);
      testFocus(parentCell, elementToBeFocused, focusInsideCell);
    });

    it("should focus the second cell of the inner table", () => {
      const parentCell = document.createElement("td");
      const innerTable = createMockTable(1, 2, 3);

      parentCell.appendChild(innerTable);
      testFocus(parentCell, innerTable.tBodies[0].rows[0].cells[1], focusInsideCell);
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
      shouldNotChangeFocus(document.createElement("td"), focusInsideCell);
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

      elementToBeFocused.innerHTML = "TextArea Value";
      shouldKeepFocus(elementToBeFocused, (element) => focusTextArea(element as HTMLTextAreaElement));
      expect(elementToBeFocused.value).toBe("TextArea Value");
    });

    it("should focus the textarea without content", () => {
      const elementToBeFocused = document.createElement("textarea");

      elementToBeFocused.value = "TextArea Value";
      shouldKeepFocus(elementToBeFocused, (element) => focusTextArea(element as HTMLTextAreaElement, true));
      expect(elementToBeFocused.value).toBe("");
    });
  });
});
