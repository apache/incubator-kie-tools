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
import { AutoFields } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

test("<AutoFields> - works", () => {
  const element = <AutoFields />;
  const wrapper = mount(element, createContext({ x: { type: String } }));

  expect(wrapper.find("AutoFields")).toHaveLength(1);
});

test("<AutoFields> - render all fields by default", () => {
  const element = <AutoFields />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: String },
      y: { type: String },
      z: { type: String },
    })
  );

  expect(wrapper.find("input")).toHaveLength(3);
});

test("<AutoFields> - renders only specified fields", () => {
  const element = <AutoFields fields={["x", "y"]} />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: String },
      y: { type: String },
      z: { type: String },
    })
  );

  expect(wrapper.find("input").someWhere((e) => e.prop("name") === "z")).toBe(false);
});

test("<AutoFields> - does not render ommited fields", () => {
  const element = <AutoFields omitFields={["x"]} />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: String },
      y: { type: String },
      z: { type: String },
    })
  );

  expect(wrapper.find("input").someWhere((e) => e.prop("name") === "x")).toBe(false);
});

test("<AutoFields> - works with custom component", () => {
  const Component = jest.fn(() => null);

  const element = <AutoFields autoField={Component} />;
  mount(
    element,
    createContext({
      x: { type: String },
      y: { type: String },
      z: { type: String },
    })
  );

  expect(Component).toHaveBeenCalledTimes(3);
});

test("<AutoFields> - wraps fields in specified element", () => {
  const element = <AutoFields element="section" />;
  const wrapper = mount(
    element,
    createContext({
      x: { type: String },
      y: { type: String },
      z: { type: String },
    })
  );

  expect(wrapper.find("section").find("input")).toHaveLength(3);
});
