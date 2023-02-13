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
import { ErrorsField } from "..";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

const error = {
  error: "validation-error",
  reason: "X is required",
  details: [
    { name: "x", type: "required", details: { value: null } },
    { name: "y", type: "required", details: { value: null } },
    { name: "z", type: "required", details: { value: null } },
  ],
  message: "X is required [validation-error]",
};

test("<ErrorsField> - renders list of correct error messages (context)", () => {
  render(
    usingUniformsContext(
      <ErrorsField name="x" />,
      { x: { type: String }, y: { type: String }, z: { type: String } },
      { error }
    )
  );

  expect(screen.getByTestId("errors-field")).toBeInTheDocument();
  expect(screen.getByText("X is required")).toBeInTheDocument();
  expect(screen.getByText("Y is required")).toBeInTheDocument();
  expect(screen.getByText("Z is required")).toBeInTheDocument();
});

test("<ErrorsField> - renders children (specified)", () => {
  render(
    usingUniformsContext(
      <ErrorsField name="x">Error message list</ErrorsField>,
      { x: { type: String }, y: { type: String }, z: { type: String } },
      { error }
    )
  );

  expect(screen.getByText("Error message list")).toBeInTheDocument();
});
