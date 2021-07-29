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

import "../../../__mocks__/ReactWithSupervisor";
import { Cell, CELL_CSS_SELECTOR } from "../../../../components/Resizer/dom";
import { Resizer } from "../../../../components/Resizer";
import { render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext } from "../../test-utils";
import * as React from "react";
import { ContextExpression } from "../../../../components/ContextExpression";
import { ContextProps } from "../../../../api";
import { act } from "react-dom/test-utils";

let cell: Cell;
let element: HTMLElement;
let container: Element;

describe("Cell", () => {
  beforeAll(() => {
    document.dispatchEvent = jest.fn();
  });

  describe("getId", () => {
    beforeEach(createLiteral);
    it("returns the id present in the cell element", () => {
      expect(cell.getId()).toBe("uuid-0000-1111-2222-3333");
    });
  });

  describe("getRect", () => {
    beforeEach(createLiteral);
    it("returns the rect from the cell element", () => {
      expect(cell.getRect()).toEqual(element.getBoundingClientRect());
    });
  });

  describe("setWidth", () => {
    beforeEach(createLiteral);

    it("set the width in the element", () => {
      act(() => cell.setWidth(150));
      expect(element.style.width).toEqual("150px");
      expect(document.dispatchEvent).toBeCalled();
    });

    it("set the width in the element considering the minimum value", () => {
      act(() => cell.setWidth(80));
      expect(element.style.width).toEqual("150px");
      expect(document.dispatchEvent).toBeCalled();
    });
  });

  describe("refreshWidthAsParent", () => {
    it("set the width as parent", () => {
      act(() => {
        createContext();

        const elements = container.querySelectorAll(CELL_CSS_SELECTOR);
        const child1 = new Cell(elements.item(1) as HTMLElement, [], 1);
        const child2 = new Cell(elements.item(2) as HTMLElement, [], 1);

        element = elements.item(3) as HTMLElement;

        cell = new Cell(element, [child1, child2], 0);

        cell.refreshWidthAsParent();
      });
      expect(element.style.width).toBe("713px");
    });
  });

  describe("refreshWidthAsLastColumn", () => {
    it("set the width as the last column", () => {
      act(() => {
        createContext();

        const elements = container.querySelectorAll(CELL_CSS_SELECTOR);
        const child1 = new Cell(elements.item(0) as HTMLElement, [], 1);
        const child2 = new Cell(elements.item(1) as HTMLElement, [], 1);

        element = elements.item(2) as HTMLElement;
        cell = new Cell(element, [child1, child2], 0);

        cell.refreshWidthAsLastColumn();
      });
      expect(element.style.width).toBe("1468px");
    });

    it("does not change the width when it is not the last column", () => {
      act(() => {
        createLiteral();
        cell.refreshWidthAsLastColumn();
      });
      expect(element.style.width).toBe("250px");
    });
  });

  describe("isLastColumn", () => {
    it("returns true when the literal expression lives in the last column", () => {
      act(renderLiteralAtLastColumn);

      expect(cell.isLastColumn()).toBeTruthy();
    });

    it("returns false when the literal expression does not live in the last column", () => {
      act(renderLiteralAtRegularColumn);
      expect(cell.isLastColumn()).toBeFalsy();
    });
  });
});

function renderLiteralAtRegularColumn() {
  container = render(
    usingTestingBoxedExpressionI18nContext(
      <>
        <table>
          <tbody>
            <tr>
              <td></td>
              <td>
                <Resizer width={250}></Resizer>
              </td>
              <td></td>
            </tr>
          </tbody>
        </table>
      </>
    ).wrapper
  ).container;
  element = container.querySelector(CELL_CSS_SELECTOR) as HTMLElement;
  cell = new Cell(element, [], 0);
}

function renderLiteralAtLastColumn() {
  container = render(
    usingTestingBoxedExpressionI18nContext(
      <>
        <table>
          <tbody>
            <tr>
              <td></td>
              <td></td>
              <td>
                <Resizer width={250}></Resizer>
              </td>
            </tr>
          </tbody>
        </table>
      </>
    ).wrapper
  ).container;
  element = container.querySelector(CELL_CSS_SELECTOR) as HTMLElement;
  cell = new Cell(element, [], 0);
}

function createLiteral() {
  container = render(<Resizer width={250}></Resizer>).container;
  element = container.querySelector(CELL_CSS_SELECTOR) as HTMLElement;
  cell = new Cell(element, [], 0);
}

function createContext() {
  container = render(
    usingTestingBoxedExpressionI18nContext(
      <ContextExpression
        {...({
          uid: "id1",
          logicType: "Context",
          name: "Expression Name",
          dataType: "<Undefined>",
          contextEntries: [
            {
              entryInfo: {
                name: "ContextEntry-1",
                dataType: "<Undefined>",
              },
              entryExpression: {
                uid: "id2",
                logicType: "Context",
                contextEntries: [
                  {
                    entryInfo: {
                      name: "ContextEntry-1",
                      dataType: "<Undefined>",
                    },
                    entryExpression: {
                      uid: "id4",
                      logicType: "Context",
                      contextEntries: [
                        {
                          entryInfo: {
                            name: "ContextEntry-1",
                            dataType: "<Undefined>",
                          },
                          entryExpression: {},
                          editInfoPopoverLabel: "Edit Context Entry",
                        },
                      ],
                      result: {
                        uid: "id7",
                      },
                      entryInfoWidth: 257,
                      entryExpressionWidth: 370,
                    },
                    editInfoPopoverLabel: "Edit Context Entry",
                  },
                ],
                result: {
                  uid: "id5",
                },
                entryInfoWidth: 713,
                entryExpressionWidth: 691,
              },
              editInfoPopoverLabel: "Edit Context Entry",
            },
          ],
          result: {
            uid: "id3",
          },
          entryInfoWidth: 150,
          entryExpressionWidth: 1468,
        } as unknown as ContextProps)}
      />
    ).wrapper
  ).container;
}

jest.mock("uuid", () => {
  return { v4: () => "0000-1111-2222-3333" };
});
