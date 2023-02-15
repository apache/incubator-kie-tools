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
import { SubmitField } from "..";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<SubmitField> - renders", () => {
  render(usingUniformsContext(<SubmitField />));

  expect(screen.getByTestId("submit-field")).toBeInTheDocument();
});

test("<SubmitField> - renders disabled if error", () => {
  render(usingUniformsContext(<SubmitField />, undefined, { error: {} }));

  expect(screen.getByTestId("submit-field")).toBeInTheDocument();
  expect(screen.getByTestId("submit-field")).toBeDisabled();
});

test("<SubmitField> - renders enabled if error and enabled", () => {
  render(usingUniformsContext(<SubmitField disabled={false} />, undefined, { error: {} }));

  expect(screen.getByTestId("submit-field")).toBeInTheDocument();
  expect(screen.getByTestId("submit-field")).toBeEnabled();
});
