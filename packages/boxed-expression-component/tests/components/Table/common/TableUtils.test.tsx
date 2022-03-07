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
import * as React from "react";
import {
  getCellCoordinates,
  getCellTableId,
  getCellByCoordinates,
  getHeaderRowsLenght,
  hasCellTabindex,
} from "@kie-tools/boxed-expression-component/dist/components/Table/common";
import { TableInstance } from "react-table";

describe("TableUtils", () => {
  describe("getCellCoordinates", () => {
    let container: Element;
    let cells: NodeListOf<Element>;
    beforeEach(() => {
      document.dispatchEvent = jest.fn();

      container = render(
        <>
          <table className="table-component table-event-0">
            <tbody>
              <tr>
                <td className="data-cell">A</td>
                <td className="data-cell">B</td>
                <td className="data-cell">C</td>
              </tr>
              <tr>
                <td className="data-cell">D</td>
                <td className="data-cell">E</td>
                <td className="data-cell">F</td>
              </tr>
              <tr>
                <td className="data-cell" colSpan={2}>
                  G
                </td>
                <td className="data-cell">H</td>
              </tr>
            </tbody>
          </table>
        </>
      ).container;

      cells = container.querySelectorAll(".data-cell");
    });

    test("valid coordinates", () => {
      expect(getCellCoordinates(cells[0])).toEqual({
        x: 0,
        y: 0,
      });
      expect(getCellCoordinates(cells[1])).toEqual({
        x: 1,
        y: 0,
      });
      expect(getCellCoordinates(cells[2])).toEqual({
        x: 2,
        y: 0,
      });

      expect(getCellCoordinates(cells[3])).toEqual({
        x: 0,
        y: 1,
      });
      expect(getCellCoordinates(cells[4])).toEqual({
        x: 1,
        y: 1,
      });
      expect(getCellCoordinates(cells[5])).toEqual({
        x: 2,
        y: 1,
      });
      expect(getCellCoordinates(cells[6])).toEqual({
        x: 0,
        y: 2,
      });
      expect(getCellCoordinates(cells[7])).toEqual({
        x: 2,
        y: 2,
      });
      expect(getCellCoordinates(null)).toEqual({
        x: 0,
        y: 0,
      });
    });
  });

  describe("getCellTableId", () => {
    let container: Element;
    let cells: NodeListOf<Element>;
    beforeEach(() => {
      document.dispatchEvent = jest.fn();

      container = render(
        <>
          <table className="table-component table-event-0">
            <tbody>
              <tr>
                <td className="data-cell">A</td>
                <td className="data-cell">B</td>
                <td className="data-cell">C</td>
              </tr>
              <tr>
                <td className="data-cell">D</td>
                <td className="data-cell">E</td>
                <td className="data-cell">F</td>
              </tr>
            </tbody>
          </table>
        </>
      ).container;

      cells = container.querySelectorAll(".data-cell");
    });

    test("dispatches paste event", () => {
      expect(getCellTableId(cells[0])).toEqual("table-event-0");
    });
  });

  describe("getHeaderRowsLenght", () => {
    it("get the number of header rows", () => {
      const tableInstance = { headerGroups: [{}, {}] } as TableInstance;

      expect(getHeaderRowsLenght(tableInstance, false)).toEqual(2);
      expect(getHeaderRowsLenght(tableInstance, true)).toEqual(1);
    });

    it("test with no headerGroups", () => {
      const tableInstance = {} as TableInstance;

      expect(getHeaderRowsLenght(tableInstance, false)).toEqual(0);
    });
  });

  describe("getCellByCoordinates", () => {
    let container: Element;
    let table: HTMLTableElement;

    beforeEach(() => {
      container = render(
        <>
          <table>
            <tbody>
              <tr>
                <td>A</td>
                <td>B</td>
                <td>C</td>
              </tr>
              <tr>
                <td colSpan={2}>D</td>
                <td>E</td>
              </tr>
            </tbody>
          </table>
        </>
      ).container;

      table = container.querySelector("table") || document.createElement("table");
    });

    test("get cell A", () => {
      expect(getCellByCoordinates(table, 0, 0)?.innerHTML).toBe("A");
    });

    test("get cell C", () => {
      expect(getCellByCoordinates(table, 0, 2)?.innerHTML).toBe("C");
    });

    test("get cell D", () => {
      expect(getCellByCoordinates(table, 1, 1)?.innerHTML).toBe("D");
    });

    test("get cell E", () => {
      expect(getCellByCoordinates(table, 1, 2)?.innerHTML).toBe("E");
    });

    test("empty table", () => {
      expect(getCellByCoordinates(document.createElement("table"), 100, 2)).toBeNull();
    });

    test("row out of range", () => {
      expect(getCellByCoordinates(table, 100, 2)).toBeNull();
    });

    test("cell out of range", () => {
      expect(getCellByCoordinates(table, 1, 200)).toBeNull();
    });
  });

  describe("hasCellTabindex", () => {
    let container: Element;

    beforeEach(() => {
      container = render(
        <>
          <table>
            <tbody>
              <tr>
                <td id="cellA">A</td>
                <td id="cellB" tabIndex={-1}>
                  B
                </td>
                <td id="cellC" tabIndex={0}>
                  C
                </td>
              </tr>
            </tbody>
          </table>
        </>
      ).container;
    });

    test("wrong input", () => {
      //@ts-ignore
      expect(hasCellTabindex()).toBe(false);
    });

    test("test cell A", () => {
      expect(hasCellTabindex(document.querySelector("#cellA")!)).toBe(false);
    });

    test("test cell B", () => {
      expect(hasCellTabindex(document.querySelector("#cellB")!)).toBe(true);
    });

    test("test cell C", () => {
      expect(hasCellTabindex(document.querySelector("#cellC")!)).toBe(true);
    });
  });
});
