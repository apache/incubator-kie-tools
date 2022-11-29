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

import { render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext } from "../../test-utils";
import * as React from "react";
import {
  cellFocus,
  focusCellByCoordinates,
  focusCurrentCell,
  focusInsideCell,
  focusLowerCell,
  focusNextCellByArrowKey,
  focusNextCellByTabKey,
  focusParentCell,
  focusPrevCellByArrowKey,
  focusPrevCellByTabKey,
  focusTextInput,
  focusUpperCell,
  getParentCell,
} from "@kie-tools/boxed-expression-component/dist/components/BeeTable/common";
import { PmmlLiteralExpression } from "@kie-tools/boxed-expression-component/dist/components/LiteralExpression";
import { ExpressionDefinitionLogicType } from "@kie-tools/boxed-expression-component/dist/api";

/**
 * Create Mock HTML Table.
 *
 * @param hrows number of header rows to create
 * @param rows number of rows to create
 * @param cells number of cells to create
 * @returns the new table
 */
const createMockTable = (hrows = 1, rows = 1, cells = 1): React.ReactElement => {
  return (
    <table>
      <thead>
        {Array(hrows)
          .fill(0)
          .map((_val, rowIndex) => (
            <tr key={`hr${rowIndex}`}>
              {Array(cells)
                .fill(0)
                .map((_val, cellIndex) => (
                  <th key={`hr${rowIndex}c${cellIndex}`} tabIndex={-1}>
                    <div>
                      Header RowIndex {rowIndex} CellIndex {cellIndex}
                    </div>
                  </th>
                ))}
            </tr>
          ))}
      </thead>
      <tbody>
        {Array(rows)
          .fill(0)
          .map((_val, rowIndex) => (
            <tr key={`br${rowIndex}`}>
              {Array(cells)
                .fill(0)
                .map((_val, cellIndex) => (
                  <td key={`br${rowIndex}c${cellIndex}`} tabIndex={-1}>
                    <div>
                      Body RowIndex {rowIndex} CellIndex {cellIndex}
                    </div>
                  </td>
                ))}
            </tr>
          ))}
      </tbody>
    </table>
  );
};

/**
 * Tests a Movement.
 *
 * @param element the element where to start
 * @param elementToBeFocused the element that should be focused
 * @param move the movement function
 */
const testFocus = (
  element: HTMLElement,
  elementToBeFocused: HTMLElement,
  move: (element: HTMLElement) => void
): void => {
  const mockElementFocus = jest.spyOn(element, "focus");
  const mockElementToBeFocused = jest.spyOn(elementToBeFocused, "focus");

  //ensure mocks counts are = 0. This allow to call testFocus multiple times
  mockElementFocus.mockClear();
  mockElementToBeFocused.mockClear();

  move(element);

  expect(document.activeElement?.innerHTML).toBe(elementToBeFocused.innerHTML);
  expect(mockElementFocus).not.toHaveBeenCalled();
  expect(mockElementToBeFocused).toHaveBeenCalled();
};

/**
 * Check a movement not to change the focus
 *
 * @param element the element where to start
 * @param move the movement function
 */
const shouldNotChangeFocus = (element: HTMLElement, move: (element: HTMLElement) => void): void => {
  const mockElementFocus = jest.spyOn(element, "focus");
  const activeElement = document.activeElement;

  move(element);

  expect(document.activeElement?.innerHTML).toBe(activeElement?.innerHTML);
  expect(mockElementFocus).not.toHaveBeenCalled();
};

/**
 * Check a movement keep a focus to the element
 *
 * @param element the element where to start
 * @param move the movement function
 */
const shouldKeepFocus = (element: HTMLElement, move: (element: HTMLElement) => void): void => {
  const mockElementFocus = jest.spyOn(element, "focus");

  move(element);
  expect(document.activeElement?.innerHTML).toBe(element.innerHTML);
  expect(mockElementFocus).toHaveBeenCalled();
};

describe("FocusUtils tests", () => {
  let mockTable: HTMLTableElement;
  let mockTableColRowspan: HTMLTableElement;

  beforeEach(() => {
    mockTable = render(createMockTable(2, 3, 6)).container.children[0] as HTMLTableElement;

    mockTableColRowspan = render(createMockTable(2, 3, 6)).container.children[0] as HTMLTableElement;

    mockTableColRowspan.rows[0].cells[1].setAttribute("colspan", "2");
    mockTableColRowspan.rows[0].cells[1].removeAttribute("tabindex");
    mockTableColRowspan.rows[0].cells[2].setAttribute("colspan", "2");
    mockTableColRowspan.rows[0].deleteCell(5);
    mockTableColRowspan.rows[0].deleteCell(4);
    mockTableColRowspan.rows[0].cells[3].removeAttribute("tabindex");

    jest.resetAllMocks();
  });

  describe("getParentCell tests", () => {
    it("should return null with wrong inputs", () => {
      // @ts-ignore
      expect(getParentCell()).toBeNull();
      expect(getParentCell(null)).toBeNull();
    });

    it("should return the input", () => {
      expect(getParentCell(mockTable.rows[2].cells[1])).toBe(mockTable.rows[2].cells[1]);
    });

    it("should return the parent data cell", () => {
      expect(getParentCell(mockTable.rows[2].cells[1].children[0] as HTMLElement)).toBe(mockTable.rows[2].cells[1]);
      expect(getParentCell(mockTable.rows[0].cells[1].children[0] as HTMLElement)).toBe(mockTable.rows[0].cells[1]);
    });

    it("should return null", () => {
      expect(getParentCell(document.createElement("span"))).toBeNull();
    });
  });

  describe("cellFocus tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => cellFocus()).not.toThrowError();
      expect(() => cellFocus(null)).not.toThrowError();
    });

    it("should focus the cell", () => {
      const element = mockTable.rows[2].cells[1];

      shouldKeepFocus(element, (element) => cellFocus(element as HTMLTableCellElement));
    });
  });

  describe("focusCurrentCell tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusCurrentCell(undefined)).not.toThrowError();
      expect(() => focusCurrentCell(null)).not.toThrowError();
    });

    it("should focus the body cell", () => {
      const element = mockTable.rows[2].cells[1];
      shouldKeepFocus(element, focusCurrentCell);
    });

    it("should focus the header cell", () => {
      const element = mockTable.rows[0].cells[1];
      shouldKeepFocus(element, focusCurrentCell);
    });

    it("should focus the cell called from an element inside the cell", () => {
      testFocus(mockTable.rows[2].cells[1].children[0] as HTMLElement, mockTable.rows[2].cells[1], focusCurrentCell);
    });
  });

  describe("focusParentCell tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusParentCell(undefined)).not.toThrowError();
      expect(() => focusParentCell(null)).not.toThrowError();
    });

    it("should focus the parent cell from the inner table", async () => {
      const parentCell = document.querySelector("td") as HTMLElement;
      const innerTable = render(
        <table>
          <tbody>
            <tr>
              <td></td>
            </tr>
          </tbody>
        </table>,
        {
          baseElement: parentCell,
        }
      ).container.children[0] as HTMLTableElement;

      testFocus(innerTable?.tBodies[0].rows[0].cells[0], parentCell, focusParentCell);
    });

    it("should not change the focus", () => {
      const emptyCell = document.createElement("td");
      shouldNotChangeFocus(emptyCell, focusParentCell);
    });
  });

  describe("focusCellByCoordinates tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusCellByCoordinates()).not.toThrowError();
      expect(() => focusCellByCoordinates(null, { y: 0, x: 0 })).not.toThrowError();
    });

    it("should focus the first data cell", () => {
      testFocus(mockTable.rows[2].cells[0], mockTable.rows[2].cells[1], (element) =>
        focusCellByCoordinates(element, { y: 2, x: 1 })
      );
    });

    it("should focus the second header cell", () => {
      testFocus(mockTable.rows[0].cells[1], mockTable.rows[0].cells[2], (element) =>
        focusCellByCoordinates(element, { y: 0, x: 2 })
      );
    });

    it("should focus the correct headers cell with rowspan focus", () => {
      testFocus(mockTableColRowspan.rows[0].cells[0], mockTableColRowspan.rows[1].cells[1], (element) =>
        focusCellByCoordinates(element, { y: 0, x: 1 })
      );
    });

    it("should focus the correct headers cell with colspan focus", () => {
      testFocus(mockTableColRowspan.rows[0].cells[0], mockTableColRowspan.rows[0].cells[2], (element) =>
        focusCellByCoordinates(element, { y: 0, x: 3 })
      );
      testFocus(mockTableColRowspan.rows[0].cells[0], mockTableColRowspan.rows[0].cells[2], (element) =>
        focusCellByCoordinates(element, { y: 0, x: 4 })
      );
    });

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTable.rows[4].cells[5], (element) => focusCellByCoordinates(element, { y: 100, x: 1 }));
      shouldKeepFocus(mockTable.rows[4].cells[5], (element) => focusCellByCoordinates(element, { y: 1, x: 100 }));
    });
  });

  describe("focusNextCellByArrowKey tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusNextCellByArrowKey()).not.toThrowError();
      expect(() => focusNextCellByArrowKey(null)).not.toThrowError();
    });

    it("should focus the next data cell", () => {
      testFocus(mockTable.rows[2].cells[1], mockTable.rows[2].cells[2], focusNextCellByArrowKey);
    });

    it("should focus the next header cell", () => {
      testFocus(mockTable.rows[0].cells[1], mockTable.rows[0].cells[2], focusNextCellByArrowKey);
    });

    it.each([
      { from: { rowIndex: 0, cellIndex: 0 }, to: { rowIndex: 1, cellIndex: 1 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 1 }, to: { rowIndex: 1, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 0, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 5 }, rowspan: 1 },
    ])(
      "should focus the correct cell on a table with headers with rowspan and colspan. Test index: %#",
      ({ from, to, rowspan }) => {
        testFocus(
          mockTableColRowspan.rows[from.rowIndex].cells[from.cellIndex],
          mockTableColRowspan.rows[to.rowIndex].cells[to.cellIndex],
          (element) => focusNextCellByArrowKey(element, rowspan)
        );
      }
    );

    it("should not change the focus", () => {
      shouldKeepFocus(mockTable.rows[4].cells[5], focusNextCellByArrowKey);
      shouldKeepFocus(mockTableColRowspan.rows[1].cells[5], focusNextCellByArrowKey);
    });
  });

  describe("focusNextCellByTabKey tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusNextCellByTabKey()).not.toThrowError();
      expect(() => focusNextCellByTabKey(null)).not.toThrowError();
    });

    it("should focus the next data cell", () => {
      testFocus(mockTable.rows[2].cells[1], mockTable.rows[2].cells[2], focusNextCellByTabKey);
    });

    it("should focus the next header cell", () => {
      testFocus(mockTable.rows[0].cells[1], mockTable.rows[0].cells[2], focusNextCellByTabKey);
    });

    it("should focus the first cell of the next line", () => {
      testFocus(mockTable.rows[2].cells[5], mockTable.rows[3].cells[1], (element) => focusNextCellByTabKey(element, 1));
    });

    it.each([
      { from: { rowIndex: 0, cellIndex: 0 }, to: { rowIndex: 1, cellIndex: 1 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 1 }, to: { rowIndex: 1, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 0, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 5 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 5 }, to: { rowIndex: 2, cellIndex: 1 }, rowspan: 2 },
    ])(
      "should focus the correct cell on a table with headers with rowspan and colspan. Test index: %#",
      ({ from, to, rowspan }) => {
        testFocus(
          mockTableColRowspan.rows[from.rowIndex].cells[from.cellIndex],
          mockTableColRowspan.rows[to.rowIndex].cells[to.cellIndex],
          (element) => focusNextCellByTabKey(element, rowspan)
        );
      }
    );

    it("should not change the focus", () => {
      shouldKeepFocus(mockTable.rows[4].cells[5], focusNextCellByTabKey);
    });
  });

  describe("focusPrevCellByArrowKey tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusPrevCellByArrowKey()).not.toThrowError();
      expect(() => focusPrevCellByArrowKey(null)).not.toThrowError();
    });

    it("should focus the previous cell", () => {
      testFocus(mockTable.rows[2].cells[2], mockTable.rows[2].cells[1], (element) =>
        focusPrevCellByArrowKey(element, 1)
      );
    });

    it("should focus the last cell of the previous line", () => {
      testFocus(mockTable.rows[3].cells[1], mockTable.rows[3].cells[0], (element) =>
        focusPrevCellByArrowKey(element, 1)
      );
    });

    it("should focus the previous data cell", () => {
      testFocus(mockTable.rows[2].cells[1], mockTable.rows[2].cells[0], focusPrevCellByArrowKey);
    });

    it("should focus the previous header cell", () => {
      testFocus(mockTable.rows[0].cells[1], mockTable.rows[0].cells[0], focusPrevCellByArrowKey);
    });

    it.each([
      { from: { rowIndex: 1, cellIndex: 5 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 0, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 2 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 1 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 1 }, to: { rowIndex: 0, cellIndex: 0 }, rowspan: 2 },
      { from: { rowIndex: 2, cellIndex: 1 }, to: { rowIndex: 2, cellIndex: 0 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 5 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 0, cellIndex: 3 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 1 }, rowspan: 2 },
      { from: { rowIndex: 1, cellIndex: 1 }, to: { rowIndex: 0, cellIndex: 0 }, rowspan: 2 },
    ])(
      "should focus the correct cell on a table with headers with rowspan and colspan. Test index: %#",
      ({ from, to, rowspan }) => {
        testFocus(
          mockTableColRowspan.rows[from.rowIndex].cells[from.cellIndex],
          mockTableColRowspan.rows[to.rowIndex].cells[to.cellIndex],
          (element) => focusPrevCellByArrowKey(element, rowspan)
        );
      }
    );

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTable.rows[2].cells[0], focusPrevCellByArrowKey);
      shouldKeepFocus(mockTableColRowspan.rows[0].cells[0], focusPrevCellByArrowKey);
      shouldKeepFocus(mockTable.rows[0].cells[0], (element) => focusPrevCellByArrowKey(element, 1));
    });
  });

  describe("focusPrevCellByTabKey tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusPrevCellByTabKey()).not.toThrowError();
      expect(() => focusPrevCellByTabKey(null)).not.toThrowError();
    });

    it("should focus the previous cell", () => {
      testFocus(mockTable.rows[2].cells[2], mockTable.rows[2].cells[1], (element) => focusPrevCellByTabKey(element, 1));
    });

    it("should focus the last cell of the previous line", () => {
      testFocus(mockTable.rows[3].cells[1], mockTable.rows[2].cells[5], (element) => focusPrevCellByTabKey(element, 1));
    });

    it.each([
      { from: { rowIndex: 1, cellIndex: 5 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 0, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 2 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 1 }, rowspan: 1 },
      { from: { rowIndex: 2, cellIndex: 1 }, to: { rowIndex: 1, cellIndex: 5 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 5 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 2 },
      { from: { rowIndex: 0, cellIndex: 3 }, to: { rowIndex: 0, cellIndex: 2 }, rowspan: 1 },
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 1, cellIndex: 1 }, rowspan: 2 },
    ])(
      "should focus the correct cell on a table with headers with rowspan and colspan. Test index: %#",
      ({ from, to, rowspan }) => {
        testFocus(
          mockTableColRowspan.rows[from.rowIndex].cells[from.cellIndex],
          mockTableColRowspan.rows[to.rowIndex].cells[to.cellIndex],
          (element) => focusPrevCellByTabKey(element, rowspan)
        );
      }
    );

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTableColRowspan.rows[0].cells[0], focusPrevCellByTabKey);
      shouldKeepFocus(mockTable.rows[0].cells[0], (element) => focusPrevCellByTabKey(element, 1));
      shouldKeepFocus(mockTable.rows[0].cells[1], (element) => focusPrevCellByTabKey(element, 1));
      shouldKeepFocus(mockTableColRowspan.rows[1].cells[1], (element) => focusPrevCellByTabKey(element, 2));
    });
  });

  describe("focusUpperCell tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusUpperCell(undefined)).not.toThrowError();
      expect(() => focusUpperCell(null)).not.toThrowError();
    });

    it("should focus the upper data cell", () => {
      testFocus(mockTable.rows[4].cells[2], mockTable.rows[3].cells[2], (element) => focusUpperCell(element));
    });

    it.each([
      { from: { rowIndex: 2, cellIndex: 1 }, to: { rowIndex: 1, cellIndex: 1 } },
      { from: { rowIndex: 2, cellIndex: 3 }, to: { rowIndex: 1, cellIndex: 3 } },
      { from: { rowIndex: 1, cellIndex: 3 }, to: { rowIndex: 0, cellIndex: 2 } },
    ])(
      "should focus the correct cell on a table with headers with rowspan and colspan. Test index: %#",
      ({ from, to }) => {
        testFocus(
          mockTableColRowspan.rows[from.rowIndex].cells[from.cellIndex],
          mockTableColRowspan.rows[to.rowIndex].cells[to.cellIndex],
          (element) => focusUpperCell(element)
        );
      }
    );

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTable.rows[0].cells[5], (element) => focusUpperCell(element));
      shouldKeepFocus(mockTableColRowspan.rows[1].cells[5], (element) => focusUpperCell(element));
    });
  });

  describe("focusLowerCell tests", () => {
    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusLowerCell(undefined)).not.toThrowError();
      expect(() => focusLowerCell(null)).not.toThrowError();
    });

    it("should focus the lower data cell", () => {
      testFocus(mockTable.rows[2].cells[2], mockTable.rows[3].cells[2], (element) => focusLowerCell(element));
    });

    it.each([
      { from: { rowIndex: 1, cellIndex: 2 }, to: { rowIndex: 2, cellIndex: 2 } },
      { from: { rowIndex: 0, cellIndex: 3 }, to: { rowIndex: 1, cellIndex: 5 } },
      { from: { rowIndex: 1, cellIndex: 3 }, to: { rowIndex: 2, cellIndex: 3 } },
    ])(
      "should focus the correct cell on a table with headers with rowspan and colspan. Test index: %#",
      ({ from, to }) => {
        testFocus(
          mockTableColRowspan.rows[from.rowIndex].cells[from.cellIndex],
          mockTableColRowspan.rows[to.rowIndex].cells[to.cellIndex],
          (element) => focusLowerCell(element)
        );
      }
    );

    it("should keep the focus to the current cell", () => {
      shouldKeepFocus(mockTable.rows[4].cells[5], (element) => focusLowerCell(element));
    });
  });

  describe("focusInsideCell tests", () => {
    let parentCell: HTMLTableCellElement;

    beforeEach(() => {
      parentCell = mockTable?.rows[2].cells[1];
    });

    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusInsideCell(undefined)).not.toThrowError();
      expect(() => focusInsideCell(null)).not.toThrowError();
    });

    it("should focus the textarea", () => {
      const elementToBeFocused = render(<textarea defaultValue="TextArea Value"></textarea>, {
        baseElement: parentCell,
      }).container.children[0] as HTMLTextAreaElement;

      testFocus(parentCell, elementToBeFocused, focusInsideCell);
    });

    it("should focus the input text", () => {
      const elementToBeFocused = render(<input type="text"></input>, { baseElement: parentCell }).container
        .children[0] as HTMLInputElement;

      testFocus(parentCell, elementToBeFocused, focusInsideCell);
    });

    it("should focus the second cell of the inner table", () => {
      const innerTable = render(createMockTable(1, 2, 3), { baseElement: parentCell }).container
        .children[0] as HTMLTableElement;

      testFocus(parentCell, innerTable.tBodies[0].rows[0].cells[1], focusInsideCell);
    });

    it("should open the PopoverMenu", () => {
      const elementWithPopoverMenu = render(<div className="with-popover-menu"></div>, { baseElement: parentCell })
        .container.children[0] as HTMLTableElement;
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockElementWithPopoverMenuClick = jest.spyOn(elementWithPopoverMenu, "click");

      focusInsideCell(parentCell);
      expect(mockParentCellFocus).not.toHaveBeenCalled();
      expect(mockElementWithPopoverMenuClick).toHaveBeenCalled();
    });

    it("should open the Select menu component", () => {
      const selectWrapper = render(
        usingTestingBoxedExpressionI18nContext(
          <div className="logic-type-selector logic-type-selected">
            <PmmlLiteralExpression
              logicType={ExpressionDefinitionLogicType.PmmlLiteralExpression}
              getOptions={() => ["a", "b", "c"]}
              selected="a"
              noOptionsLabel={"no options label"}
            />
          </div>
        ).wrapper,
        { baseElement: parentCell }
      ).container.children[0] as HTMLDivElement;
      const selectMenuBtn = (selectWrapper.querySelector("button") ||
        document.createElement("button")) as HTMLButtonElement;
      const mockParentCellFocus = jest.spyOn(parentCell, "focus");
      const mockSelectMenuBtnClick = jest.spyOn(selectMenuBtn, "click");

      focusInsideCell(parentCell);
      expect(mockParentCellFocus).not.toHaveBeenCalled();
      expect(mockSelectMenuBtnClick).toHaveBeenCalled();
    });

    it("should not change the focus", () => {
      shouldNotChangeFocus(document.createElement("td"), focusInsideCell);
    });
  });

  describe("focusTextInput tests", () => {
    let elementToBeFocused: HTMLTextAreaElement;

    beforeEach(() => {
      elementToBeFocused = render(<textarea defaultValue="TextArea Value"></textarea>).container
        .children[0] as HTMLTextAreaElement;
    });

    it("should not throw errors with wrong inputs", () => {
      // @ts-ignore
      expect(() => focusTextInput()).not.toThrowError();
      expect(() => focusTextInput(null)).not.toThrowError();
    });

    it("should focus the textarea without erasing content", () => {
      shouldKeepFocus(elementToBeFocused, (element) => focusTextInput(element as HTMLTextAreaElement));
      expect(elementToBeFocused.value).toBe("TextArea Value");
    });

    it("should focus the textarea without content", () => {
      shouldKeepFocus(elementToBeFocused, (element) => focusTextInput(element as HTMLTextAreaElement, true));
      expect(elementToBeFocused.value).toBe("");
    });
  });
});
