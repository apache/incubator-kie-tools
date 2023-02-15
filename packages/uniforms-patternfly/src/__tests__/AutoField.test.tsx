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
import { AutoField } from "..";
import { usingUniformsContext } from "./test-utils";
import { render, screen } from "@testing-library/react";

test("<AutoField> - works - default field", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: String } }));
  expect(screen.getByTestId("text-field")).toBeInTheDocument();
});

test("<AutoField> - renders RadioField", () => {
  render(
    usingUniformsContext(<AutoField name="x" />, {
      x: {
        type: String,
        allowedValues: ["x", "y"],
        uniforms: { checkboxes: true },
      },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
});

test("<AutoField> - renders SelectField - input", () => {
  render(
    usingUniformsContext(<AutoField name="x" />, {
      x: { type: Array, allowedValues: ["x", "y"] },
      "x.$": { type: String },
    })
  );

  expect(screen.getByTestId("select-inputs-field")).toBeInTheDocument();
});

test("<AutoField> - renders SelectField - checkbox", () => {
  render(
    usingUniformsContext(
      <AutoField name="x" checkboxes={true} />,

      {
        x: { type: Array, allowedValues: ["x", "y"] },
        "x.$": { type: String },
      }
    )
  );

  expect(screen.getByTestId("select-checkbox-field")).toBeInTheDocument();
});

test("<AutoField> - renders DateField", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: Date } }));
  expect(screen.getByTestId("date-field")).toBeInTheDocument();
});

test("<AutoField> - renders ListField", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: Array }, "x.$": { type: String } }));
  expect(screen.getByTestId("list-field")).toBeInTheDocument();
});

test("<AutoField> - renders NumField", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: Number } }));
  expect(screen.getByTestId("num-field")).toBeInTheDocument();
});

test("<AutoField> - renders NestField", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: Object } }));
  expect(screen.getByTestId("nest-field")).toBeInTheDocument();
});

test("<AutoField> - renders TextField", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: String } }));
  expect(screen.getByTestId("text-field")).toBeInTheDocument();
});

test("<AutoField> - renders BoolField", () => {
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: Boolean } }));
  expect(screen.getByTestId("bool-field")).toBeInTheDocument();
});

test("<AutoField> - renders Component (model)", () => {
  const Component = jest.fn(() => null);
  render(usingUniformsContext(<AutoField name="x" />, { x: { type: String, uniforms: { component: Component } } }));
  expect(Component).toHaveBeenCalledTimes(1);
});

test("<AutoField> - renders Component (specified)", () => {
  const Component = jest.fn(() => null);
  render(usingUniformsContext(<AutoField name="x" component={Component} />, { x: { type: String } }));
  expect(Component).toHaveBeenCalledTimes(1);
});
