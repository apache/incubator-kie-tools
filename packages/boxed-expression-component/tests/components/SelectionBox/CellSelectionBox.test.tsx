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

import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { CellSelectionBox } from "@kie-tools/boxed-expression-component/dist/components/SelectionBox";
import "../../__mocks__/ReactWithSupervisor";
import { wrapComponentInContext } from "../test-utils";

describe("CellSelectionBox", () => {
  describe("when users select elements in a simple table", () => {
    it("stores element values in the text area element", async () => {
      const container = renderSimpleTable();

      fireEvent.mouseDown(container, { clientX: -1, clientY: -1 });
      fireEvent.mouseMove(container, { clientX: 1000, clientY: 1000 });
      fireEvent.mouseUp(container);

      const kieSelectionTextarea = container.querySelector(".kie-cell-selection-box textarea") as HTMLTextAreaElement;
      const selectionValue = kieSelectionTextarea!.value;

      expect(selectionValue).toMatch("Cell 1\tCell 2\tCell 3\tCell 4\tCell 5\tCell 6");
    });
  });

  describe("when users select elements in a table with newlines in cells", () => {
    it("stores element values in the text area element", async () => {
      const container = renderTableWithNewLines();

      container.querySelectorAll("tr").forEach((tr: HTMLTableRowElement, rowIndex: number) =>
        tr.querySelectorAll("td").forEach(
          (td: HTMLTableCellElement) =>
            (td.getBoundingClientRect = jest.fn(
              () =>
                ({
                  y: rowIndex,
                } as DOMRect)
            ))
        )
      );

      fireEvent.mouseDown(container, { clientX: -1, clientY: -1 });
      fireEvent.mouseMove(container, { clientX: 1000, clientY: 1000 });
      fireEvent.mouseUp(container);

      const kieSelectionTextarea = container.querySelector(".kie-cell-selection-box textarea") as HTMLTextAreaElement;
      const selectionValue = kieSelectionTextarea!.value;

      expect(selectionValue).toMatch(
        '"Cell 1\nnewline"\tCell 2\nCell 3\tCell 4\nCell 5\t"Cell 6\n\nindex of("list", "match")"'
      );
    });
  });
});

function renderSimpleTable() {
  return render(
    wrapComponentInContext(
      <>
        <CellSelectionBox />
        <div className="uuid-f1f0b02e react-resizable">
          <div className="editable-cell">
            <textarea defaultValue="Cell 1" />
          </div>
        </div>
        <div className="uuid-56cabb83 react-resizable">
          <div className="editable-cell">
            <textarea defaultValue="Cell 2" />
          </div>
        </div>
        <div className="uuid-3daf1136 react-resizable">
          <div className="editable-cell">
            <textarea defaultValue="Cell 3" />
          </div>
        </div>
        <div className="uuid-d45c5153 react-resizable">
          <div className="editable-cell">
            <textarea defaultValue="Cell 4" />
          </div>
        </div>
        <div className="uuid-8265fa47 react-resizable">
          <div className="editable-cell">
            <textarea defaultValue="Cell 5" />
          </div>
        </div>
        <div className="uuid-fb97017a react-resizable">
          <div className="editable-cell">
            <textarea defaultValue="Cell 6" />
          </div>
        </div>
      </>
    )
  ).container;
}

function renderTableWithNewLines() {
  return render(
    wrapComponentInContext(
      <>
        <CellSelectionBox />
        <table>
          <tbody>
            <tr>
              <td className="uuid-f1f0b02e react-resizable">
                <div className="editable-cell">
                  <textarea defaultValue={"Cell 1\nnewline"} />
                </div>
              </td>
              <td className="uuid-56cabb83 react-resizable">
                <div className="editable-cell">
                  <textarea defaultValue={"Cell 2"} />
                </div>
              </td>
            </tr>
            <tr>
              <td className="uuid-3daf1136 react-resizable">
                <div className="editable-cell">
                  <textarea defaultValue={"Cell 3"} />
                </div>
              </td>
              <td className="uuid-d45c5153 react-resizable">
                <div className="editable-cell">
                  <textarea defaultValue={"Cell 4"} />
                </div>
              </td>
            </tr>
            <tr>
              <td className="uuid-8265fa47 react-resizable">
                <div className="editable-cell">
                  <textarea defaultValue={"Cell 5"} />
                </div>
              </td>
              <td className="uuid-fb97017a react-resizable">
                <div className="editable-cell">
                  <textarea defaultValue={'Cell 6\n\nindex of("list", "match")'} />
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </>
    )
  ).container;
}
