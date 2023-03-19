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
import { ListAddField } from "../";
import { render, screen, fireEvent } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

const onChange = jest.fn();

const context = { onChange, model: { x: [] } };
const schema = { x: { type: Array, maxCount: 3 }, "x.$": String };

beforeEach(() => {
  onChange.mockClear();
});

test("<ListAddField> - works", () => {
  render(usingUniformsContext(<ListAddField name="x.$" />, schema, context));

  expect(screen.getByTestId("list-add-field")).toBeInTheDocument();
});

test("<ListAddField> - prevents onClick when disabled", () => {
  render(usingUniformsContext(<ListAddField name="x.1" disabled />, schema, context));

  fireEvent.click(screen.getByTestId("list-add-field"));
  expect(onChange).not.toHaveBeenCalled();
});

test("<ListAddField> - prevents onClick when limit reached", () => {
  render(usingUniformsContext(<ListAddField name="x.1" />, { ...schema, x: { ...schema.x, maxCount: 0 } }, context));

  fireEvent.click(screen.getByTestId("list-add-field"));
  expect(onChange).not.toHaveBeenCalled();
});

test("<ListAddField> - correctly reacts on click", () => {
  render(usingUniformsContext(<ListAddField name="x.1" value="y" />, schema, context));

  fireEvent.click(screen.getByTestId("list-add-field"));
  expect(onChange).toHaveBeenLastCalledWith("x", ["y"]);
});
