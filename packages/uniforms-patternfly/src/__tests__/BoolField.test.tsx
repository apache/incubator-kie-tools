/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { BoolField } from "..";
import { screen, fireEvent } from "@testing-library/react";
import { createContext } from "./_createContext";
import { render } from "./_render";

test("<BoolField> - renders an input", () => {
  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
});

test("<BoolField> - renders a input with correct id (inherited)", () => {
  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("id")).toEqual("uniforms-0000-0001");
});

test("<BoolField> - renders a input with correct id (specified)", () => {
  render(<BoolField name="x" id="y" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("id")).toBe("y");
});

test("<BoolField> - renders a input with correct name", () => {
  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("name")).toBe("x");
});

test("<BoolField> - renders an input with correct type", () => {
  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("type")).toBe("checkbox");
});

test("<BoolField> - renders an input with correct disabled state", () => {
  render(<BoolField name="x" disabled />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("disabled")).toBe(true);
});

// test("<BoolField> - renders a input with correct label (specified)", () => {
//   render(<BoolField name="x" label="BoolFieldLabel" />, createContext({ x: { type: Boolean } }));

//   expect(screen.getByTestId("bool-field")).toBeInTheDocument();
//   expect(screen.getByTestId("bool-field").text()).toBe("BoolFieldLabel");
//   expect(screen.getByTestId("bool-field").getAttribute("htmlFor")).toBe(screen.getByTestId("bool-field").getAttribute("id"));
// });

test("<BoolField> - renders a input with correct value (default)", () => {
  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("checked")).toBe(false);
});

test("<BoolField> - renders a input with correct value (model)", () => {
  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }, { model: { x: true } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("checked")).toBe(true);
});

test("<BoolField> - renders a input with correct value (specified)", () => {
  render(<BoolField name="x" value />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("checked")).toBe(true);
});

test("<BoolField> - renders a input which correctly reacts on change", () => {
  const onChange = jest.fn();

  render(<BoolField name="x" />, createContext({ x: { type: Boolean } }, { onChange }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();

  fireEvent.click(screen.getByTestId("bool-field"));
  expect(onChange).toHaveBeenLastCalledWith("x", true);

  fireEvent.click(screen.getByTestId("bool-field"));
  expect(onChange).toHaveBeenLastCalledWith("x", false);
});

test("<BoolField> - renders a wrapper with unknown props", () => {
  render(<BoolField name="x" data-x="x" data-y="y" data-z="z" />, createContext({ x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toMatchSnapshot();
  expect(screen.getByTestId("bool-field").getAttribute("data-x")).toBe("x");
  expect(screen.getByTestId("bool-field").getAttribute("data-y")).toBe("y");
  expect(screen.getByTestId("bool-field").getAttribute("data-z")).toBe("z");
});
