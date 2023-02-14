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
import { ErrorField } from "..";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

const error = {
  error: "validation-error",
  reason: "X is required",
  details: [{ name: "x", type: "required", details: { value: null } }],
  message: "X is required [validation-error]",
};

test("<ErrorField> - works", () => {
  render(usingUniformsContext(<ErrorField name="x" error={true} />, { x: { type: String } }));

  expect(screen.getByTestId("error-field")).toBeInTheDocument();
});

test("<ErrorField> - renders correct error message (context)", () => {
  render(usingUniformsContext(<ErrorField name="x" />, { x: { type: String } }, { error }));

  expect(screen.getByTestId("error-field")).toBeInTheDocument();
  expect(screen.getByText(error.reason)).toBeInTheDocument();
});

test("<ErrorField> - renders correct error message (specified)", () => {
  render(
    usingUniformsContext(<ErrorField name="x" error={error.details[0]} errorMessage={error.reason} />, {
      x: { type: String },
    })
  );

  expect(screen.getByTestId("error-field")).toBeInTheDocument();
  expect(screen.getByText(error.reason)).toBeInTheDocument();
});
