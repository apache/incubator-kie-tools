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
import { DataRecord } from "react-table";
import { iterableValue, paste, pasteOnTable } from "./../../../../../showcase/src/lib/components/Table/common";

describe("CopyAndPasteUtils", () => {
  describe("pasteOnTable", () => {
    let rows: DataRecord[];
    let rowFactory: () => DataRecord;

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
        expect(iterableValue("")).toEqual([[]]);
      });
    });

    describe("when the paste value has a single row", () => {
      test("returns an iterable data structure", () => {
        expect(iterableValue("A\tB\tC")).toEqual([["A", "B", "C"]]);
      });
    });

    describe("when the paste value has multiple rows", () => {
      test("returns an iterable data structure", () => {
        expect(iterableValue("A\tB\tC\nD\tE\tF")).toEqual([
          ["A", "B", "C"],
          ["D", "E", "F"],
        ]);
      });
    });
  });

  describe("paste", () => {
    beforeEach(() => {
      document.dispatchEvent = jest.fn();

      const container = render(
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
      ).container;

      paste("Z\tZ\nZ\tZ", container.querySelector(".ref")!);
    });

    test("dispatches paste event", () => {
      expect(document.dispatchEvent).toBeCalled();
    });
  });
});
