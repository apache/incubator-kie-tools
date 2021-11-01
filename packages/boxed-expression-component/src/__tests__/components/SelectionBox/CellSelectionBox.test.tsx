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
import { CellSelectionBox } from "../../../components/SelectionBox";
import "../../__mocks__/ReactWithSupervisor";

describe("CellSelectionBox", () => {
  describe("when users select elements", () => {
    it("stores element values in the text area element", async () => {
      const container = renderTable();

      fireEvent.mouseDown(container, { clientX: -1, clientY: -1 });
      fireEvent.mouseMove(container, { clientX: 1000, clientY: 1000 });
      fireEvent.mouseUp(container);

      const kieSelectionTextarea = container.querySelector(".kie-cell-selection-box textarea") as HTMLTextAreaElement;
      const selectionValue = kieSelectionTextarea!.value;

      expect(selectionValue).toMatch("Cell 1\tCell 2\tCell 3\tCell 4\tCell 5\tCell 6");
    });
  });
});

function renderTable() {
  return render(
    <>
      <CellSelectionBox />
      <div className="uuid-f1f0b02e react-resizable">
        <div className="editable-cell">
          <textarea defaultValue="Cell 1"></textarea>
        </div>
      </div>
      <div className="uuid-56cabb83 react-resizable">
        <div className="editable-cell">
          <textarea defaultValue="Cell 2"></textarea>
        </div>
      </div>
      <div className="uuid-3daf1136 react-resizable">
        <div className="editable-cell">
          <textarea defaultValue="Cell 3"></textarea>
        </div>
      </div>
      <div className="uuid-d45c5153 react-resizable">
        <div className="editable-cell">
          <textarea defaultValue="Cell 4"></textarea>
        </div>
      </div>
      <div className="uuid-8265fa47 react-resizable">
        <div className="editable-cell">
          <textarea defaultValue="Cell 5"></textarea>
        </div>
      </div>
      <div className="uuid-fb97017a react-resizable">
        <div className="editable-cell">
          <textarea defaultValue="Cell 6"></textarea>
        </div>
      </div>
    </>
  ).container;
}
