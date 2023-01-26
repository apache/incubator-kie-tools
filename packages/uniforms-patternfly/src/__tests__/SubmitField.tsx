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
import { Button } from "@patternfly/react-core";

import { SubmitField } from "../";
import { screen } from "@testing-library/react";
import { createContext } from "./_createContext";
import { render } from "./_render";

test("<SubmitField> - renders", () => {
  const element = <SubmitField />;
  render(element, createContext());

  expect(wrapper).toHaveLength(1);
});

test("<SubmitField> - renders disabled if error", () => {
  const element = <SubmitField />;
  render(element, createContext(undefined, { error: {} }));

  expect(wrapper).toHaveLength(1);
  expect(screen(Button).prop("isDisabled")).toBe(true);
});

test("<SubmitField> - renders enabled if error and enabled", () => {
  const element = <SubmitField disabled={false} />;
  render(element, createContext(undefined, { error: {} }));

  expect(wrapper).toHaveLength(1);
  expect(screen(Button).prop("isDisabled")).toBe(false);
});
