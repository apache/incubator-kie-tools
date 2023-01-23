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
import { ErrorsField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

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

test("<ErrorsField> - works", () => {
  const element = <ErrorsField name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String } }));

  expect(wrapper.find(ErrorsField)).toHaveLength(1);
});

test("<ErrorsField> - renders list of correct error messages (context)", () => {
  const element = <ErrorsField name="x" />;
  const wrapper = mount(
    element,
    createContext({ x: { type: String }, y: { type: String }, z: { type: String } }, { error })
  );

  expect(wrapper.find("li")).toHaveLength(3);
  expect(wrapper.find("li").at(0).text()).toBe("X is required");
  expect(wrapper.find("li").at(1).text()).toBe("Y is required");
  expect(wrapper.find("li").at(2).text()).toBe("Z is required");
});

test("<ErrorsField> - renders children (specified)", () => {
  const element = <ErrorsField name="x" children="Error message list" />;
  const wrapper = mount(
    element,
    createContext({ x: { type: String }, y: { type: String }, z: { type: String } }, { error })
  );

  expect(wrapper.find(ErrorsField).text()).toEqual(expect.stringContaining("Error message list"));
});
