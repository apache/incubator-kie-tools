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
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { EditTextInline } from "../../../components/EditExpressionMenu";
import * as React from "react";
import * as _ from "lodash";

describe("EditTextInline tests", () => {
  test("should render inline text", () => {
    const value = "Value";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<EditTextInline value={value} onTextChange={_.identity} />).wrapper
    );

    expect(container.querySelector("p")).toBeTruthy();
    expect(container.querySelector("p")).toHaveTextContent(value);
  });

  test("should activate text editing, when double clicking on it", () => {
    const value = "Value";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<EditTextInline value={value} onTextChange={_.identity} />).wrapper
    );
    fireEvent.click(container.querySelector("p")!);

    expect(container.querySelector("input")).toBeTruthy();
    expect(container.querySelector("input")).toHaveValue(value);
  });

  test("should call text editing callback, when clicking outside", () => {
    const value = "Value";
    const newValue = "New Value";
    const mockedOnTextChange = jest.fn();

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div id="container">
          <EditTextInline value={value} onTextChange={mockedOnTextChange} />
        </div>
      ).wrapper
    );
    fireEvent.click(container.querySelector("p")!);
    fireEvent.blur(changeInputValue(container, newValue));

    expect(mockedOnTextChange).toHaveBeenCalledWith(newValue);
  });

  test("should call text editing callback, when pressing enter", () => {
    const value = "Value";
    const newValue = "New Value";
    const mockedOnTextChange = jest.fn();

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div id="container">
          <EditTextInline value={value} onTextChange={mockedOnTextChange} />
        </div>
      ).wrapper
    );
    fireEvent.click(container.querySelector("p")!);
    fireEvent.keyDown(changeInputValue(container, newValue), { key: "enter", keyCode: 13 });

    expect(mockedOnTextChange).toHaveBeenCalledWith(newValue);
  });

  test("should not call text editing callback, when pressing escape", () => {
    const value = "Value";
    const newValue = "New Value";
    const mockedOnTextChange = jest.fn();

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div id="container">
          <EditTextInline value={value} onTextChange={mockedOnTextChange} />
        </div>
      ).wrapper
    );
    fireEvent.click(container.querySelector("p")!);
    fireEvent.keyDown(changeInputValue(container, newValue), { key: "escape", keyCode: 27 });

    expect(mockedOnTextChange).toHaveBeenCalledTimes(0);
  });

  function changeInputValue(container: Element, newValue: string) {
    const inputElement = container.querySelector("input")!;
    fireEvent.change(inputElement, {
      target: { value: newValue },
    });
    return inputElement;
  }
});
