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
import { ListField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

test("<ListField> - works", () => {
  const element = <ListField name="x" />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find(ListField)).toHaveLength(1);
});

test("<ListField> - renders ListAddField", () => {
  const element = <ListField name="x" label="ListFieldLabel" />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find("ListAdd")).toHaveLength(1);
  expect(wrapper.find("ListAdd").prop("name")).toBe("x.$");
});

test("<ListField> - renders correct label (specified)", () => {
  const element = <ListField name="x" label="ListFieldLabel" />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find("label").at(0).text()).toEqual(expect.stringContaining("ListFieldLabel"));
});

test("<ListField> - renders correct numer of items with initialCount (specified)", () => {
  const element = <ListField name="x" initialCount={3} />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find("input")).toHaveLength(3);
});

test("<ListField> - passes itemProps to its children", () => {
  const element = <ListField name="x" initialCount={3} itemProps={{ "data-xyz": 1 }} />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find("ListItemField").first().prop("data-xyz")).toBe(1);
});

test("<ListField> - renders children (specified)", () => {
  const Child = jest.fn(() => null);

  const element = (
    <ListField name="x" initialCount={2}>
      {/* <Child /> */}
    </ListField>
  );
  mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(Child).toHaveBeenCalledTimes(2);
});

test("<ListField> - renders children with correct name (children)", () => {
  const Child = jest.fn(() => <div />);

  const element = (
    <ListField name="x" initialCount={2}>
      {/* <Child name="$" /> */}
    </ListField>
  );
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find(Child).at(0).prop("name")).toBe("0");
  expect(wrapper.find(Child).at(1).prop("name")).toBe("1");
});

test("<ListField> - renders children with correct name (value)", () => {
  const element = <ListField name="x" initialCount={2} />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find("ListItemField").at(0).prop("name")).toBe("0");
  expect(wrapper.find("ListItemField").at(1).prop("name")).toBe("1");
});
