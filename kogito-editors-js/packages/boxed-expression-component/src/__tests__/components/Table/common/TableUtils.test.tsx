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
import { getCellCoordinates, getCellTableId } from "./../../../../../showcase/src/lib/components/Table/common";

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
});
