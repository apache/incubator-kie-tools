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
import * as _ from "lodash";
import * as React from "react";
import { EditableCell, EDIT_MODE, READ_MODE } from "../../../components/Table";
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";

describe("EditableCell", () => {
  const CELL_SELECTOR = ".editable-cell";

  let container: Element;

  describe("when it renders", () => {
    const initialValue = "INITIAL_VALUE";

    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={initialValue} rowIndex={0} columnId={"col1"} onCellUpdate={_.identity} />
        ).wrapper
      ).container;
    });

    test("renders the initial value", () => {
      expect(container.querySelector("textarea")).toBeTruthy();
      expect((container.querySelector("textarea") as HTMLTextAreaElement).value).toBe(initialValue);
    });

    test("renders on read mode", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains(READ_MODE)).toBeTruthy();
    });
  });

  describe("when the user double-click on it", () => {
    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={"value"} rowIndex={0} columnId={"col1"} onCellUpdate={_.identity} />
        ).wrapper
      ).container;

      fireEvent.doubleClick(container.querySelector(CELL_SELECTOR) as Element);
    });

    test("renders on edit mode", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains(EDIT_MODE)).toBeTruthy();
    });
  });

  describe("when the user presses Enter with the cell selected", () => {
    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={"value"} rowIndex={0} columnId={"col1"} onCellUpdate={_.identity} />
        ).wrapper
      ).container;
    });

    test("renders on edit mode", () => {
      fireEvent.click(container.querySelector(CELL_SELECTOR) as Element);
      fireEvent.change(container.querySelector("textarea") as HTMLTextAreaElement, { target: { value: "Z" } });
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains(EDIT_MODE)).toBeTruthy();
    });
  });

  describe("when the user click on it", () => {
    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={"value"} rowIndex={0} columnId={"col1"} onCellUpdate={_.identity} />
        ).wrapper
      ).container;

      fireEvent.click(container.querySelector(CELL_SELECTOR) as Element);
    });

    test("focus on the text area", () => {
      expect(document.querySelector("textarea")).toEqual(document.activeElement);
    });

    test("enable the selected style", () => {
      expect(container.querySelector(CELL_SELECTOR)?.classList.contains("editable-cell--selected")).toBeTruthy();
    });
  });

  describe("when the on blur events happens", () => {
    const value = "value";
    const newValue = "new value";
    const rowIndex = 0;
    const columnId = "col1";
    const onCellUpdate = (rowIndex: number, columnId: string, value: string) => {
      _.identity({ rowIndex, columnId, value });
    };
    const mockedOnCellUpdate = jest.fn(onCellUpdate);

    beforeEach(() => {
      container = render(
        usingTestingBoxedExpressionI18nContext(
          <EditableCell value={value} rowIndex={rowIndex} columnId={columnId} onCellUpdate={mockedOnCellUpdate} />
        ).wrapper
      ).container;

      fireEvent.change(container.querySelector("textarea") as HTMLTextAreaElement, {
        target: { value: `${newValue}</>` },
      });
      // onblur is triggered by Monaco (mock), and the new value relies on Monaco implementation
    });

    test("triggers the onCellUpdate function", () => {
      expect(mockedOnCellUpdate).toHaveBeenCalled();
      expect(mockedOnCellUpdate).toHaveBeenCalledWith(rowIndex, columnId, newValue);
    });
  });
});
