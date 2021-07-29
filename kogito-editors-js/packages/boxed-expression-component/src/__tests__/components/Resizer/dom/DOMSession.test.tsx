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
import { Cell, DOMSession } from "../../../../components/Resizer/dom";

describe("DOMSession", () => {
  let session: DOMSession;
  let cells: Cell[];

  beforeEach(() => {
    render(
      <>
        <div data-test-id="cell-0" className="react-resizable">
          <div data-test-id="cell-1" className="react-resizable">
            <div data-test-id="cell-2" className="react-resizable"></div>
            <div data-test-id="cell-3" className="react-resizable"></div>
          </div>
          <div data-test-id="cell-4" className="react-resizable"></div>
        </div>
        <div data-test-id="cell-5" className="react-resizable"></div>
        <div data-test-id="cell-6" className="react-resizable"></div>
      </>
    );

    session = new DOMSession();
  });

  describe("getCells", () => {
    beforeEach(() => {
      cells = session.getCells();
    });

    it("returns the cells with depth information", () => {
      expect(cells[4].element.dataset.testId).toBe("cell-0");
      expect(cells[4].depth).toBe(0);
      expect(cells[5].element.dataset.testId).toBe("cell-5");
      expect(cells[5].depth).toBe(0);
      expect(cells[6].element.dataset.testId).toBe("cell-6");
      expect(cells[6].depth).toBe(0);

      expect(cells[2].element.dataset.testId).toBe("cell-1");
      expect(cells[2].depth).toBe(1);
      expect(cells[3].element.dataset.testId).toBe("cell-4");
      expect(cells[3].depth).toBe(1);

      expect(cells[0].element.dataset.testId).toBe("cell-2");
      expect(cells[0].depth).toBe(2);
      expect(cells[1].element.dataset.testId).toBe("cell-3");
      expect(cells[1].depth).toBe(2);
    });

    it("returns the cells with children", () => {
      expect(cells[0].children).toHaveLength(0);
      expect(cells[1].children).toHaveLength(0);
      expect(cells[2].children).toHaveLength(2);
      expect(cells[3].children).toHaveLength(0);
      expect(cells[4].children).toHaveLength(2);
      expect(cells[5].children).toHaveLength(0);
      expect(cells[6].children).toHaveLength(0);
    });
  });
});
