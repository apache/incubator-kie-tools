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
import { Resizer } from "@kie-tools/boxed-expression-component/dist/components/Resizer";
import { SelectionBox } from "@kie-tools/boxed-expression-component/dist/components/SelectionBox";
import "../../__mocks__/ReactWithSupervisor";
import { wrapComponentInContext } from "../test-utils";

describe("SelectionBox", () => {
  describe("when users drag the selection box element (but do not release the mouse button)", () => {
    it("it appears in the screen", async () => {
      const container = renderTable();
      const selectionBox = container.querySelector(".kie-selection-box") as HTMLElement;
      const selectionBoxStyle = selectionBox.style;

      fireEvent.mouseDown(container, { clientX: 10, clientY: 20 });
      fireEvent.mouseMove(container, { clientX: 300, clientY: 400 });

      expect(selectionBoxStyle.width).toEqual("290px");
      expect(selectionBoxStyle.height).toEqual("380px");
      expect(selectionBoxStyle.top).toEqual("20px");
      expect(selectionBoxStyle.left).toEqual("10px");
    });
  });

  describe("when users drag the selection box element and release the mouse button", () => {
    it("it doesn't appear in the screen", async () => {
      const container = renderTable();
      const selectionBox = container.querySelector(".kie-selection-box") as HTMLElement;
      const selectionBoxStyle = selectionBox.style;

      fireEvent.mouseDown(container, { clientX: 10, clientY: 10 });
      fireEvent.mouseMove(container, { clientX: 300, clientY: 400 });
      fireEvent.mouseUp(container);

      expect(selectionBoxStyle.width).toEqual("");
      expect(selectionBoxStyle.height).toEqual("");
      expect(selectionBoxStyle.top).toEqual("");
      expect(selectionBoxStyle.left).toEqual("");
    });
  });
});

function renderTable() {
  return render(
    wrapComponentInContext(
      <>
        <SelectionBox />
        <table>
          <thead>
            <tr>
              <th className="col-1-1">
                <Resizer width={250} />
              </th>
              <th className="col-1-2">
                <Resizer width={250} />
              </th>
              <th className="col-1-3">
                <Resizer width={250} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="col-2-1">
                <Resizer width={250} />
              </td>
              <td className="col-2-2">
                <Resizer width={250} />
              </td>
              <td className="col-2-3">
                <Resizer width={250} />
              </td>
            </tr>
          </tbody>
        </table>
      </>
    )
  ).container;
}
