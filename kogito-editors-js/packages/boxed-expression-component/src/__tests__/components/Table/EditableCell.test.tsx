/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import * as React from "react";
import { EDIT_MODE, EditableCell, READ_MODE } from "../../../components/Table";
import * as _ from "lodash";

describe("EditableCell", () => {
  const CELL_SELECTOR = ".editable-cell";

  let container: Element;

  describe("when it renders", () => {
    const initialValue = "INITIAL_VALUE";

    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={initialValue} row={{ index: 0 }} column={{ id: "col1" }} onCellUpdate={_.identity} />
        ).wrapper
      ).container;
    });

    it("renders the initial value", () => {
      expect(container.querySelector("textarea")).toBeTruthy();
      expect((container.querySelector("textarea") as HTMLTextAreaElement).value).toBe(initialValue);
    });

    it("renders on read mode", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains(READ_MODE)).toBeTruthy();
    });
  });

  describe("when the user double-click on it", () => {
    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={"value"} row={{ index: 0 }} column={{ id: "col1" }} onCellUpdate={_.identity} />
        ).wrapper
      ).container;

      fireEvent.doubleClick(container.querySelector(CELL_SELECTOR) as Element);
    });

    it("renders on edit mode", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains(EDIT_MODE)).toBeTruthy();
    });
  });

  describe("when the user presses Enter with the cell selected", () => {
    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={"value"} row={{ index: 0 }} column={{ id: "col1" }} onCellUpdate={_.identity} />
        ).wrapper
      ).container;

      fireEvent.click(container.querySelector(CELL_SELECTOR) as Element);
      fireEvent.keyPress(container.querySelector("textarea") as Element, { key: "Enter", keyCode: 13 });
    });

    it("renders on edit mode", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains(EDIT_MODE)).toBeTruthy();
    });
  });

  describe("when the user click on it", () => {
    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={"value"} row={{ index: 0 }} column={{ id: "col1" }} onCellUpdate={_.identity} />
        ).wrapper
      ).container;

      fireEvent.click(container.querySelector(CELL_SELECTOR) as Element);
    });

    it("focus on the text area", () => {
      expect(document.querySelector("textarea")).toEqual(document.activeElement);
    });

    it("enable the selected style", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains("editable-cell--selected")).toBeTruthy();
    });
  });

  describe("when the user changes a value", () => {
    const value = "value";
    const newValue = "changed";
    const rowIndex = 0;
    const columnId = "col1";
    const onCellUpdate = (rowIndex: number, columnId: string, value: string) => {
      _.identity({ rowIndex, columnId, value });
    };
    const mockedOnCellUpdate = jest.fn(onCellUpdate);

    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell
            value={value}
            row={{ index: rowIndex }}
            column={{ id: columnId }}
            onCellUpdate={mockedOnCellUpdate}
          />
        ).wrapper
      ).container;

      fireEvent.change(container.querySelector("textarea") as HTMLTextAreaElement, { target: { value: newValue } });
      fireEvent.blur(container.querySelector("textarea") as HTMLTextAreaElement);
    });

    it("triggers the onCellUpdate function ", () => {
      expect(mockedOnCellUpdate).toHaveBeenCalled();
      expect(mockedOnCellUpdate).toHaveBeenCalledWith(rowIndex, columnId, newValue);
    });
  });
});
