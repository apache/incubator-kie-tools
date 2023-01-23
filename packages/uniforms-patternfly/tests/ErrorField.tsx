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
import { ErrorField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

const error = {
  error: "validation-error",
  reason: "X is required",
  details: [{ name: "x", type: "required", details: { value: null } }],
  message: "X is required [validation-error]",
};

test("<ErrorField> - works", () => {
  const element = <ErrorField name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String } }));

  expect(wrapper.find(ErrorField)).toHaveLength(1);
});

test("<ErrorField> - renders correct error message (context)", () => {
  const element = <ErrorField name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String } }, { error }));

  expect(wrapper.find(ErrorField)).toHaveLength(1);
  expect(wrapper.find(ErrorField).text()).toBe("X is required");
});

test("<ErrorField> - renders correct error message (specified)", () => {
  const element = <ErrorField name="x" error={error.details[0]} errorMessage="X is required" />;
  const wrapper = mount(element, createContext({ x: { type: String } }));

  expect(wrapper.find(ErrorField)).toHaveLength(1);
  expect(wrapper.find(ErrorField).text()).toBe("X is required");
});
