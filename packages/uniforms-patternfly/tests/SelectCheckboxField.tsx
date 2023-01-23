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
import { Radio } from "@patternfly/react-core";
import { SelectCheckboxField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct disabled state", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" disabled />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).prop("disabled")).toBe(true);
  expect(wrapper.find("input").at(1).prop("disabled")).toBe(true);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct id (inherited)", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).prop("id")).toBeTruthy();
  expect(wrapper.find("input").at(1).prop("id")).toBeTruthy();
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct id (specified)", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" id="y" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find(Radio).at(0).prop("id")).toBe("y-a");
  expect(wrapper.find(Radio).at(1).prop("id")).toBe("y-b");
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct name", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).prop("name")).toBe("x");
  expect(wrapper.find("input").at(1).prop("name")).toBe("x");
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct options", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("label")).toHaveLength(2);
  expect(wrapper.find("label").at(0).text()).toBe("a");
  expect(wrapper.find("label").at(1).text()).toBe("b");
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct options (transform)", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" transform={(x: string) => x.toUpperCase()} />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("label")).toHaveLength(2);
  expect(wrapper.find("label").at(0).text()).toBe("A");
  expect(wrapper.find("label").at(1).text()).toBe("B");
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct value (default)", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).prop("checked")).toBe(false);
  expect(wrapper.find("input").at(1).prop("checked")).toBe(false);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct value (model)", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(
    element,
    createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { model: { x: "b" } })
  );

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).prop("checked")).toBe(false);
  expect(wrapper.find("input").at(1).prop("checked")).toBe(true);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes with correct value (specified)", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" value="b" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).prop("checked")).toBe(false);
  expect(wrapper.find("input").at(1).prop("checked")).toBe(true);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes which correctly reacts on change", () => {
  const onChange = jest.fn();

  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { onChange }));

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(1).simulate("change")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", "b");
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes which correctly reacts on change (array check)", () => {
  const onChange = jest.fn();

  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(
    element,
    createContext(
      {
        x: { type: Array },
        "x.$": { type: String, allowedValues: ["a", "b"] },
      },
      { onChange }
    )
  );

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(1).simulate("change")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", ["b"]);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes which correctly reacts on change (array uncheck)", () => {
  const onChange = jest.fn();

  const element = <SelectCheckboxField onToggle={() => {}} name="x" value={["b"]} />;
  const wrapper = mount(
    element,
    createContext(
      {
        x: { type: Array },
        "x.$": { type: String, allowedValues: ["a", "b"] },
      },
      { onChange }
    )
  );

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(1).simulate("change")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", []);
});

test("<SelectCheckboxField checkboxes> - renders a set of checkboxes which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  const element = <SelectCheckboxField onToggle={() => {}} name="x" />;
  const wrapper = mount(
    element,
    createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { model: { x: "b" }, onChange })
  );

  expect(wrapper.find("input")).toHaveLength(2);
  expect(wrapper.find("input").at(0).simulate("change")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", "a");
});

test("<SelectCheckboxField checkboxes> - renders a label", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" label="y" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("label")).toHaveLength(3);
  expect(wrapper.find("label").at(0).text()).toBe("y");
});

test("<SelectCheckboxField checkboxes> - renders a wrapper with unknown props", () => {
  const element = <SelectCheckboxField onToggle={() => {}} name="x" data-x="x" data-y="y" data-z="z" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("div").at(0).prop("data-x")).toBe("x");
  expect(wrapper.find("div").at(0).prop("data-y")).toBe("y");
  expect(wrapper.find("div").at(0).prop("data-z")).toBe("z");
});
