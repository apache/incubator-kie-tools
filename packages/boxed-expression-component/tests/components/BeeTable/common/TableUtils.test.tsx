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
  getCellByCoordinatesFromDataAttributes,
  getCellByCoordinatesFromHTMLPosition,
  hasCellTabindex,
  getFullCellCoordinates,
  getFullCellCoordinatesByDataAttributes,
  getFullCellCoordinatesByHTMLPosition,
} from "@kie-tools/boxed-expression-component/dist/components/BeeTable/common";

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
      expect(getCellCoordinates(null)).toEqual({
        x: 0,
        y: 0,
      });
    });
  });

  describe("working with coordinates", () => {
    let container: Element;
    let parentTable: HTMLTableElement;
    let childTable: HTMLTableElement;
    let cells: NodeListOf<Element>;
    beforeEach(() => {
      document.dispatchEvent = jest.fn();

      container = render(
        <>
          <div id="notACell" data-xposition="4" data-yposition="6">
            Not a cell
          </div>
          <table id="parentTable">
            <thead>
              <tr>
                <th colSpan={2} data-xposition="0" data-yposition="0">
                  Cell 0
                </th>
                <th data-xposition="2" data-yposition="0">
                  Cell 1
                </th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td data-xposition="0" data-yposition="1">
                  Cell 2
                </td>
                <td data-xposition="1" data-yposition="1">
                  Cell 3
                </td>
                <td data-xposition="2" data-yposition="1">
                  Cell 4
                </td>
              </tr>
              <tr>
                <td>Cell 5</td>
                <td data-xposition="1" data-yposition="2">
                  Cell 6
                </td>
                <td data-xposition="2" data-yposition="2">
                  Cell 7
                </td>
              </tr>
              <tr>
                <td data-xposition="0" data-yposition="3">
                  Cell 8
                </td>
                <td data-xposition="1" data-yposition="3">
                  Cell 9
                </td>
                <td data-xposition="2" data-yposition="3">
                  <table id="childTable">
                    <thead>
                      <tr>
                        <th colSpan={2} data-xposition="0" data-yposition="0">
                          Childcell 11
                        </th>
                        <th data-xposition="2" data-yposition="0">
                          Childcell 12
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td data-xposition="0" data-yposition="1">
                          Childcell 13
                        </td>
                        <td data-xposition="1" data-yposition="1">
                          Childcell 14
                        </td>
                        <td data-xposition="2" data-yposition="1">
                          Childcell 15
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
            </tbody>
          </table>
        </>
      ).container;

      parentTable = container.querySelector("#parentTable") || document.createElement("table");
      childTable = container.querySelector("#childTable") || document.createElement("table");
      cells = container.querySelectorAll("th, td");
    });

    describe("getFullCellCoordinatesByDataAttributes", () => {
      it("should return null with a wront input", () => {
        expect(getFullCellCoordinatesByDataAttributes(document.getElementById("#notACell"))).toBe(null);
        expect(getFullCellCoordinatesByDataAttributes(null)).toBe(null);
      });

      test.each([
        [0, { x: 0, y: 0 }],
        [1, { x: 2, y: 0 }],
        [2, { x: 0, y: 1 }],
        [3, { x: 1, y: 1 }],
        [5, null],
        [4, { x: 2, y: 1 }],
        [6, { x: 1, y: 2 }],
        [7, { x: 2, y: 2 }],
        [12, { x: 2, y: 0 }],
      ])("checking coordinates of cell #%s, expecting: %s", (cellIndex, coordinates) => {
        // @ts-ignore
        expect(getFullCellCoordinatesByDataAttributes(cells[cellIndex])).toEqual(coordinates);
      });
    });

    describe("getFullCellCoordinatesByHTMLPosition", () => {
      it("should return null with an element that is not a cell", () => {
        expect(getFullCellCoordinatesByHTMLPosition(document.getElementById("#notACell"))).toBe(null);
        expect(getFullCellCoordinatesByHTMLPosition(null)).toBe(null);
      });

      test.each([
        [0, { x: 0, y: 0 }],
        [1, { x: 2, y: 0 }],
        [2, { x: 0, y: 1 }],
        [3, { x: 1, y: 1 }],
        [4, { x: 2, y: 1 }],
        [5, { x: 0, y: 2 }],
        [6, { x: 1, y: 2 }],
        [7, { x: 2, y: 2 }],
        [12, { x: 2, y: 0 }],
      ])("checking coordinates of cell #%s, expecting: %s", (cellIndex, coordinates) => {
        // @ts-ignore
        expect(getFullCellCoordinatesByHTMLPosition(cells[cellIndex])).toEqual(coordinates);
      });
    });

    describe("getFullCellCoordinates", () => {
      const DEFAULT = { x: 0, y: 0 };

      it("should return DEFAULT coordinates with an element that is not a cell", () => {
        expect(getFullCellCoordinates(document.getElementById("#notACell"))).toEqual(DEFAULT);
        expect(getFullCellCoordinates(null)).toEqual(DEFAULT);
      });

      test.each([
        [0, { x: 0, y: 0 }],
        [1, { x: 2, y: 0 }],
        [2, { x: 0, y: 1 }],
        [3, { x: 1, y: 1 }],
        [4, { x: 2, y: 1 }],
        [5, { x: 0, y: 2 }],
        [6, { x: 1, y: 2 }],
        [7, { x: 2, y: 2 }],
        [12, { x: 2, y: 0 }],
      ])("checking coordinates of cell #%s, expecting: %s", (cellIndex, coordinates) => {
        // @ts-ignore
        expect(getFullCellCoordinates(cells[cellIndex])).toEqual(coordinates);
      });
    });

    describe("getCellByCoordinatesFromDataAttributes", () => {
      it("should return null with a invalid inputs", () => {
        // @ts-ignore
        expect(getCellByCoordinatesFromDataAttributes(parentTable, undefined)).toBe(null);
        // @ts-ignore
        expect(getCellByCoordinatesFromDataAttributes(parentTable, null)).toBe(null);
        // @ts-ignore
        expect(getCellByCoordinatesFromDataAttributes(null, { x: 1, y: 1 })).toBe(null);
      });

      it("should return null if the element is not found", () => {
        expect(getCellByCoordinatesFromDataAttributes(parentTable, { x: 0, y: 2 })).toEqual(null);
      });

      test.each([
        [{ x: 0, y: 0 }, 0],
        [{ x: 2, y: 0 }, 1],
        [{ x: 0, y: 1 }, 2],
        [{ x: 1, y: 1 }, 3],
        [{ x: 2, y: 1 }, 4],
        [{ x: 1, y: 2 }, 6],
        [{ x: 2, y: 2 }, 7],
      ])("getting cell by coordinates %s, expecting cell %s", (coordinates, cellIndex) => {
        expect(getCellByCoordinatesFromDataAttributes(parentTable, coordinates)?.innerHTML).toEqual(
          cells[cellIndex].innerHTML
        );
      });

      it("get the second cell of the child table", () => {
        expect(getCellByCoordinatesFromDataAttributes(childTable, { x: 2, y: 0 })).toEqual(cells[12]);
      });
    });

    describe("getCellByCoordinatesFromHTMLPosition", () => {
      it("should return null with a invalid inputs", () => {
        // @ts-ignore
        expect(getCellByCoordinatesFromHTMLPosition(parentTable, undefined)).toBe(null);
        // @ts-ignore
        expect(getCellByCoordinatesFromHTMLPosition(parentTable, null)).toBe(null);
        // @ts-ignore
        expect(getCellByCoordinatesFromHTMLPosition(null, { x: 1, y: 1 })).toBe(null);
      });

      test.each([
        [{ x: 0, y: 0 }, 0],
        [{ x: 2, y: 0 }, 1],
        [{ x: 0, y: 1 }, 2],
        [{ x: 1, y: 1 }, 3],
        [{ x: 2, y: 1 }, 4],
        [{ x: 0, y: 2 }, 5],
        [{ x: 1, y: 2 }, 6],
        [{ x: 2, y: 2 }, 7],
      ])("getting cell by coordinates %s, expecting cell %s", (coordinates, cellIndex) => {
        expect(getCellByCoordinatesFromHTMLPosition(parentTable, coordinates)?.innerHTML).toEqual(
          cells[cellIndex].innerHTML
        );
      });

      it("get the second cell of the child table", () => {
        expect(getCellByCoordinatesFromHTMLPosition(childTable, { x: 2, y: 0 })).toEqual(cells[12]);
      });
    });

    describe("getCellByCoordinates", () => {
      it("should return null with a invalid inputs", () => {
        // @ts-ignore
        expect(getCellByCoordinates(parentTable, undefined)).toBe(null);
        // @ts-ignore
        expect(getCellByCoordinates(parentTable, null)).toBe(null);
        // @ts-ignore
        expect(getCellByCoordinates(null, { x: 1, y: 1 })).toBe(null);
      });

      test.each([
        [{ x: 0, y: 0 }, 0],
        [{ x: 2, y: 0 }, 1],
        [{ x: 0, y: 1 }, 2],
        [{ x: 1, y: 1 }, 3],
        [{ x: 2, y: 1 }, 4],
        [{ x: 0, y: 2 }, 5],
        [{ x: 1, y: 2 }, 6],
        [{ x: 2, y: 2 }, 7],
      ])("getting cell by coordinates %s, expecting cell %s", (coordinates, cellIndex) => {
        expect(getCellByCoordinates(parentTable, coordinates)?.innerHTML).toEqual(cells[cellIndex].innerHTML);
      });

      it("get the second cell of the child table", () => {
        expect(getCellByCoordinates(childTable, { x: 2, y: 0 })).toEqual(cells[12]);
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

    test("cell A should not have the tabindex attribute", () => {
      expect(hasCellTabindex(document.querySelector("#cellA")!)).toBe(false);
    });

    test("cell B should have the tabindex attribute", () => {
      expect(hasCellTabindex(document.querySelector("#cellB")!)).toBe(true);
    });

    test("cell C should have the tabindex attribute", () => {
      expect(hasCellTabindex(document.querySelector("#cellC")!)).toBe(true);
    });
  });
});
