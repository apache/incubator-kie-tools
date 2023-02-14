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
import { render, screen, fireEvent } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<BoolField> - renders an input", () => {
  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
});

test("<BoolField> - renders a input with correct id (inherited)", () => {
  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("id")).toEqual("uniforms-0000-0001");
});

test("<BoolField> - renders a input with correct id (specified)", () => {
  render(usingUniformsContext(<BoolField name="x" id="y" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("id")).toBe("y");
});

test("<BoolField> - renders a input with correct name", () => {
  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("name")).toBe("x");
});

test("<BoolField> - renders an input with correct type", () => {
  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field").getAttribute("type")).toBe("checkbox");
});

test("<BoolField> - renders an input with correct disabled state", () => {
  render(usingUniformsContext(<BoolField name="x" disabled={true} />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field")).toBeDisabled();
});

test("<BoolField> - renders a input with correct label (specified)", () => {
  render(usingUniformsContext(<BoolField name="x" label="BoolFieldLabel" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByText("BoolFieldLabel")).toBeInTheDocument();
});

test("<BoolField> - renders a input with correct value (default)", () => {
  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field")).not.toHaveAttribute("checked");
});

test("<BoolField> - renders a input with correct value (model)", () => {
  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }, { model: { x: true } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field")).toHaveAttribute("checked");
});

test("<BoolField> - renders a input with correct value (specified)", () => {
  render(usingUniformsContext(<BoolField name="x" value />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("bool-field")).toHaveAttribute("checked");
});

test("<BoolField> - renders a input which correctly reacts on change", () => {
  const onChange = jest.fn();

  render(usingUniformsContext(<BoolField name="x" />, { x: { type: Boolean } }, { onChange }));
  expect(screen.getByTestId("bool-field")).toBeInTheDocument();

  fireEvent.click(screen.getByTestId("bool-field"));
  expect(onChange).toHaveBeenLastCalledWith("x", true);
});

test("<BoolField> - renders a wrapper with unknown props", () => {
  render(usingUniformsContext(<BoolField name="x" data-x="x" data-y="y" data-z="z" />, { x: { type: Boolean } }));

  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
  expect(screen.getByTestId("wrapper-field").getAttribute("data-x")).toBe("x");
  expect(screen.getByTestId("wrapper-field").getAttribute("data-y")).toBe("y");
  expect(screen.getByTestId("wrapper-field").getAttribute("data-z")).toBe("z");
});
