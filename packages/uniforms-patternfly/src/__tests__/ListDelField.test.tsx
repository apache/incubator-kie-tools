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
import { ListDelField } from "../";
import { render, screen, fireEvent } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

const onChange = jest.fn();

const context = { onChange, model: { x: ["x", "y", "z"] } };
const schema = { x: { type: Array, maxCount: 3 }, "x.$": String };

beforeEach(() => {
  onChange.mockClear();
});

test("<ListDelField> - works", () => {
  render(usingUniformsContext(<ListDelField name="x.1" />, schema, context));

  expect(screen.getByTestId("list-del-field")).toBeInTheDocument();
});

test("<ListDelField> - prevents onClick when disabled", () => {
  render(usingUniformsContext(<ListDelField name="x.1" disabled />, schema, context));

  fireEvent.click(screen.getByTestId("list-del-field"));
  expect(onChange).not.toHaveBeenCalled();
});

test("<ListDelField> - prevents onClick when limit reached", () => {
  render(usingUniformsContext(<ListDelField name="x.1" />, { ...schema, x: { ...schema.x, minCount: 3 } }, context));

  fireEvent.click(screen.getByTestId("list-del-field"));
  expect(onChange).not.toHaveBeenCalled();
});

test("<ListDelField> - correctly reacts on click", () => {
  render(usingUniformsContext(<ListDelField name="x.1" />, schema, context));

  fireEvent.click(screen.getByTestId("list-del-field"));
  expect(onChange).toHaveBeenLastCalledWith("x", ["x", "z"]);
});
