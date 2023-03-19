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
import { LongTextField } from "../";
import { render, screen, fireEvent } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<LongTextField> - renders a textarea", () => {
  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
});

test("<LongTextField> - renders a textarea with correct disabled state", () => {
  render(usingUniformsContext(<LongTextField name="x" disabled />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  expect(textarea).toBeDisabled();
});

test("<LongTextField> - renders a textarea with correct id (inherited)", () => {
  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  expect(textarea.getAttribute("id")).toBeTruthy();
});

test("<LongTextField> - renders a textarea with correct id (specified)", () => {
  render(usingUniformsContext(<LongTextField name="x" id="y" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  expect(textarea.getAttribute("id")).toBe("y");
});

test("<LongTextField> - renders a textarea with correct name", () => {
  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  expect(textarea.getAttribute("name")).toBe("x");
});

test("<LongTextField> - renders a textarea with correct placeholder", () => {
  render(usingUniformsContext(<LongTextField name="x" placeholder="y" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  expect(textarea.getAttribute("placeholder")).toBe("y");
});

test("<LongTextField> - renders a textarea with correct value (default)", () => {
  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  expect(textarea.getAttribute("value")).toBe(null);
});

test("<LongTextField> - renders a textarea with correct value (model)", () => {
  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }, { model: { x: "y" } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  expect(screen.getByText("y")).toBeInTheDocument();
});

test("<LongTextField> - renders a textarea with correct value (specified)", () => {
  render(usingUniformsContext(<LongTextField name="x" value="y" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  expect(screen.getByText("y")).toBeInTheDocument();
});

test("<LongTextField> - renders a textarea which correctly reacts on change", () => {
  const onChange = jest.fn();

  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }, { onChange }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  fireEvent.change(textarea, { target: { value: "y" } });
  expect(onChange).toHaveBeenLastCalledWith("x", "y");
});

test("<LongTextField> - renders a textarea which correctly reacts on change (empty)", () => {
  const onChange = jest.fn();

  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }, { onChange }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  fireEvent.change(textarea, { target: { value: "" } });
  expect(onChange).not.toHaveBeenCalled();
});

test("<LongTextField> - renders a textarea which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  render(usingUniformsContext(<LongTextField name="x" />, { x: { type: String } }, { model: { x: "y" }, onChange }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  const textarea = screen.getByTestId("long-text-field").getElementsByTagName("textarea")[0];
  fireEvent.change(textarea, { target: { value: "y" } });
  expect(screen.getByText("y")).toBeInTheDocument();
  expect(onChange).not.toHaveBeenCalled();
});

test("<LongTextField> - renders a label", () => {
  render(usingUniformsContext(<LongTextField name="x" label="y" />, { x: { type: String } }));

  expect(screen.getByTestId("long-text-field")).toBeInTheDocument();
  expect(screen.getByText("y")).toBeInTheDocument();
});
