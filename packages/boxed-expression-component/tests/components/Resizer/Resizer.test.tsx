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

import "../../__mocks__/ReactWithSupervisor";
import { fireEvent, render, waitFor } from "@testing-library/react";
import * as React from "react";
import { Resizer } from "@kie-tools/boxed-expression-component/dist/components/Resizer";
import { wrapComponentInContext } from "../test-utils";

describe("Resizer", () => {
  describe("when users drag the draggable element", () => {
    it("resizes the element", async () => {
      const container = renderTable();
      const draggable = container.querySelector(".col-2-3 .react-resizable .pf-c-drawer")!;
      const resizable = container.querySelectorAll(".react-resizable")!;
      const getWidth = (e: Node) => (e as HTMLElement).style.width;

      fireEvent.mouseDown(draggable);
      fireEvent.mouseMove(draggable, { clientX: 150 });
      fireEvent.mouseUp(draggable);

      await waitFor(() => {
        expect(getWidth(resizable.item(0))).toBe("250px");
        expect(getWidth(resizable.item(1))).toBe("250px");
        expect(getWidth(resizable.item(2))).toBe("400px");
        expect(getWidth(resizable.item(3))).toBe("250px");
        expect(getWidth(resizable.item(4))).toBe("250px");
        expect(getWidth(resizable.item(5))).toBe("400px");
      });
    });
  });
});

function renderTable() {
  return render(
    wrapComponentInContext(
      <>
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
