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
import { AutoField, ListItemField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

test("<ListItemField> - works", () => {
  const element = <ListItemField name="x.1" />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find(ListItemField)).toHaveLength(1);
});

test("<ListItemField> - renders AutoField", () => {
  const element = <ListItemField name="x.1" />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find(AutoField)).toHaveLength(1);
});

test("<ListItemField> - renders children if specified", () => {
  const Child: () => null = jest.fn(() => null);

  const element = (
    <ListItemField name="x.1">
      <Child />
    </ListItemField>
  );
  mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(Child).toHaveBeenCalledTimes(1);
});

test("<ListItemField> - renders ListDelField", () => {
  const element = <ListItemField name="x.1" />;
  const wrapper = mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

  expect(wrapper.find("ListDel")).toHaveLength(1);
  expect(wrapper.find("ListDel").prop("name")).toBe("x.1");
});
