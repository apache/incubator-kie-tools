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
import { ListItemField } from "../";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<ListItemField> - works", () => {
  render(usingUniformsContext(<ListItemField name="x.1" />, { x: { type: Array }, "x.$": { type: String } }));

  expect(screen.getByTestId("list-item-field")).toBeInTheDocument();
});

test("<ListItemField> - renders AutoField", () => {
  const element = <ListItemField name="x.1" />;
  render(usingUniformsContext(element, { x: { type: Array }, "x.$": { type: String } }));

  expect(screen.getByTestId("text-field")).toBeInTheDocument();
});

test("<ListItemField> - renders children if specified", () => {
  const Child: () => null = jest.fn(() => null);

  render(
    usingUniformsContext(
      <ListItemField name="x.1">
        <Child />
      </ListItemField>,
      { x: { type: Array }, "x.$": { type: String } }
    )
  );

  expect(Child).toHaveBeenCalledTimes(1);
});

test("<ListItemField> - renders ListDelField", () => {
  render(usingUniformsContext(<ListItemField name="x.1" />, { x: { type: Array }, "x.$": { type: String } }));

  expect(screen.getByTestId("list-del-field")).toBeInTheDocument();
  expect(screen.getByTestId("text-field").getAttribute("name")).toBe("x.1");
});
