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
import { merge } from "lodash";
import { ListDelField } from "../";
import { Button } from "@patternfly/react-core";
import { screen } from "@testing-library/react";
import { createContext } from "./_createContext";
import { render } from "./_render";

const onChange = jest.fn();
const context = (schema?: {}) =>
  createContext(merge({ x: { type: Array, maxCount: 3 }, "x.$": String }, schema), {
    onChange,
    model: { x: ["x", "y", "z"] },
  });

beforeEach(() => {
  onChange.mockClear();
});

test("<ListDelField> - works", () => {
  const element = <ListDelField name="x.1" />;
  render(element, context());

  expect(screen(ListDelField)).toHaveLength(1);
});

test("<ListDelField> - prevents onClick when disabled", () => {
  const element = <ListDelField name="x.1" disabled />;
  render(element, context());

  expect(screen(Button).simulate("click")).toBeTruthy();
  expect(onChange).not.toHaveBeenCalled();
});

test("<ListDelField> - prevents onClick when limit reached", () => {
  const element = <ListDelField name="x.1" />;
  render(element, context({ x: { minCount: 3 } }));

  expect(screen(Button).simulate("click")).toBeTruthy();
  expect(onChange).not.toHaveBeenCalled();
});

test("<ListDelField> - correctly reacts on click", () => {
  const element = <ListDelField name="x.1" />;
  render(element, context());

  expect(screen(Button).simulate("click")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", ["x", "z"]);
});
