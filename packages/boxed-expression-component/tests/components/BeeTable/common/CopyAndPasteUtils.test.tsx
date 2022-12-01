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
import { act } from "react-dom/test-utils";
import * as ReactTable from "react-table";
import {
  iterableValue,
  paste,
  pasteOnTable,
} from "@kie-tools/boxed-expression-component/dist/components/BeeTable/common";
import { wrapComponentInContext } from "../../test-utils";

describe("CopyAndPasteUtils", () => {
  describe("pasteOnTable", () => {
    let rows: ROWGENERICTYPE[];
    let rowFactory: () => ROWGENERICTYPE;

    beforeEach(() => {
      rows = [
        { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
      ];

      rowFactory = () => ({ "column-1": "-", "column-2": "-", "column-3": "-" });
    });

    describe("when the paste value is empty", () => {
      test("returns a new instance of rows with updated values", () => {
        let newRows;

        act(() => {
          newRows = pasteOnTable("", rows, rowFactory);
        });

        expect(newRows).not.toBe(rows);
        expect(newRows).toEqual([
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        ]);
      });
    });

    describe("when the paste value has a single row", () => {
      test("returns a new instance of rows with updated values", () => {
        let newRows;

        act(() => {
          newRows = pasteOnTable("aaaa\tbbbb", rows, rowFactory);
        });

        expect(newRows).not.toBe(rows);
        expect(newRows).toEqual([
          { "column-1": "aaaa", "column-2": "bbbb", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        ]);
      });
    });

    describe("when the paste value has multiple rows", () => {
      test("returns a new instance of rows with updated values", () => {
        let newRows;

        act(() => {
          newRows = pasteOnTable("aaaa\tbbbb\ncccc\tdddd", rows, rowFactory);
        });

        expect(newRows).not.toBe(rows);
        expect(newRows).toEqual([
          { "column-1": "aaaa", "column-2": "bbbb", "column-3": "0000" },
          { "column-1": "cccc", "column-2": "dddd", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        ]);
      });
    });

    describe("when the paste value has multiple rows and a custom initial coordinate", () => {
      test("returns a new instance of rows with updated values", () => {
        let newRows;

        act(() => {
          newRows = pasteOnTable("aaaa\tbbbb\ncccc\tdddd", rows, rowFactory, 1, 1);
        });

        expect(newRows).not.toBe(rows);
        expect(newRows).toEqual([
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "aaaa", "column-3": "bbbb" },
          { "column-1": "0000", "column-2": "cccc", "column-3": "dddd" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        ]);
      });
    });

    describe("when the paste value has multiple rows with extra reserved chars", () => {
      test("returns a new instance of rows with updated values", () => {
        let newRows;

        act(() => {
          newRows = pasteOnTable("aaaa\tbbbb\t\ncccc\tdddd\n", rows, rowFactory);
        });

        expect(newRows).not.toBe(rows);
        expect(newRows).toEqual([
          { "column-1": "aaaa", "column-2": "bbbb", "column-3": "0000" },
          { "column-1": "cccc", "column-2": "dddd", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
          { "column-1": "0000", "column-2": "0000", "column-3": "0000" },
        ]);
      });
    });
  });

  describe("iterableValue", () => {
    describe("when the paste value is empty", () => {
      test("returns an iterable data structure", () => {
        expect(iterableValue("")).toEqual([]);
      });
    });

    describe("when the paste value has a single row", () => {
      test("returns an iterable data structure", () => {
        expect(iterableValue("A\tB\tC")).toEqual([["A", "B", "C"]]);
      });
    });

    describe("when the paste value has multiple rows", () => {
      test.each([
        [
          "A\tB\tC\nD\tE\tF",
          [
            ["A", "B", "C"],
            ["D", "E", "F"],
          ],
        ],
        [
          '"Cell 1\nnewline"\tCell 2\nCell 3\tCell 4\nCell 5\t"Cell 6\n\nnewline"',
          [
            ["Cell 1\nnewline", "Cell 2"],
            ["Cell 3", "Cell 4"],
            ["Cell 5", "Cell 6\n\nnewline"],
          ],
        ],
        [
          "Cell 1\tCell 2\nCell 3\tCell 4\nCell 5\tCell 6\nCell 7\tCell 8",
          [
            ["Cell 1", "Cell 2"],
            ["Cell 3", "Cell 4"],
            ["Cell 5", "Cell 6"],
            ["Cell 7", "Cell 8"],
          ],
        ],
        [
          "Cell 1\tCell 2\n\t\nCell 3\tCell 4\nCell 5\tCell 6",
          [
            ["Cell 1", "Cell 2"],
            ["-", "-"],
            ["Cell 3", "Cell 4"],
            ["Cell 5", "Cell 6"],
          ],
        ],
        [
          'Cell 1\tCell 2\nCell 3\t"Cell 4\n\nnewline with ""quotes"""\nCell 4\tCell 5\nCell 6\tCell 7',
          [
            ["Cell 1", "Cell 2"],
            ["Cell 3", 'Cell 4\n\nnewline with "quotes"'],
            ["Cell 4", "Cell 5"],
            ["Cell 6", "Cell 7"],
          ],
        ],
        [
          '"cell 1 \nnewline"\t"cell 2 \nindex of("list", "match")"\ncell 3\t"cell 4\n \nnewline"',
          [
            ["cell 1 \nnewline", 'cell 2 \nindex of("list", "match")'],
            ["cell 3", "cell 4\n \nnewline"],
          ],
        ],
      ])("returns an iterable data structure with input: %j", (input, expected) => {
        expect(iterableValue(input)).toEqual(expected);
      });
    });
  });

  describe("paste", () => {
    beforeEach(() => {
      document.body.dispatchEvent = jest.fn();

      const container = render(
        wrapComponentInContext(
          <>
            <table className="table-component table-event-0">
              <tbody>
                <tr>
                  <td>A</td>
                  <td>B</td>
                  <td>C</td>
                </tr>
                <tr>
                  <td>D</td>
                  <td className="ref">E</td>
                  <td>F</td>
                </tr>
                <tr>
                  <td>G</td>
                  <td>H</td>
                  <td>I</td>
                </tr>
              </tbody>
            </table>
          </>
        )
      ).container;

      paste("Z\tZ\nZ\tZ", container.querySelector(".ref")!, document.body);
    });

    test("dispatches paste event", () => {
      expect(document.body.dispatchEvent).toBeCalled();
    });
  });
});
