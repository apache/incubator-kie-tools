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
import { Select } from "@patternfly/react-core";
import { act } from "@testing-library/react";
import { SelectInputsField } from "../src";

import createContext from "./_createContext";
import mount from "./_mount";
import { MouseEvent } from "react";

test("<SelectInputsField> - renders a select", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
});

test("<SelectInputsField> - renders a select with correct disabled state", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" disabled />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("isDisabled")).toBe(true);
});

test("<SelectInputsField> - renders a select with correct id (inherited)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("id")).toBeTruthy();
});

test("<SelectInputsField> - renders a select with correct id (specified)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" id="y" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("id")).toBe("y");
});

test("<SelectInputsField> - renders a select with correct name", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("name")).toBe("x");
});

test("<SelectInputsField> - renders a select with correct options", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("children")).toHaveLength(2);
  expect(wrapper.find(Select).prop("children")?.[0].props.value).toBe("a");
  expect(wrapper.find(Select).prop("children")?.[0].props.children).toBe("a");
  expect(wrapper.find(Select).prop("children")?.[1].props.value).toBe("b");
  expect(wrapper.find(Select).prop("children")?.[1].props.children).toBe("b");
});

test("<SelectInputsField> - renders a select with correct options (transform)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" transform={(x: string) => x.toUpperCase()} />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("children")).toHaveLength(2);
  expect(wrapper.find(Select).prop("children")?.[0].props.value).toBe("a");
  expect(wrapper.find(Select).prop("children")?.[0].props.children).toBe("A");
  expect(wrapper.find(Select).prop("children")?.[1].props.value).toBe("b");
  expect(wrapper.find(Select).prop("children")?.[1].props.children).toBe("B");
});

test("<SelectInputsField> - renders a select with correct placeholder (implicit)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" placeholder="y" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("placeholderText")).toBe("y");
  expect(wrapper.find(Select).prop("value")).toBe(undefined);
});

test("<SelectInputsField> - renders a select with correct value (default)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("value")).toBe(undefined);
});

test("<SelectInputsField> - renders a select with correct value (model)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(
    element,
    createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { model: { x: "b" } })
  );

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("value")).toBe("b");
});

test("<SelectInputsField> - renders a select with correct value (specified)", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" value="b" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(wrapper.find(Select).prop("value")).toBe("b");
});

test("<SelectInputsField> - renders a select which correctly reacts on change", () => {
  const onChange = jest.fn();
  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { onChange }));

  act(() => {
    const changeEvent = wrapper.find(Select).prop("onSelect")?.("event" as any, "b");
    expect(changeEvent).toBeFalsy();
  });

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(onChange).toHaveBeenLastCalledWith("x", "b");
});

test("<SelectInputsField> - renders a select which correctly reacts on change (array)", () => {
  const onChange = jest.fn();

  const element = <SelectInputsField onToggle={() => {}} name="x" value={undefined} />;
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

  act(() => {
    const changeEvent = wrapper.find(Select).prop("onSelect")?.("event" as any, "b");
    expect(changeEvent).toBeFalsy();
  });

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(onChange).toHaveBeenLastCalledWith("x", ["b"]);
});

test("<SelectInputsField> - renders a select which correctly reacts on change (placeholder)", () => {
  const onChange = jest.fn();

  const element = <SelectInputsField onToggle={() => {}} name="x" placeholder={"test"} />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { onChange }));

  act(() => {
    const changeEvent = wrapper.find(Select).prop("onSelect")?.("event" as any, "test");
    expect(changeEvent).toBeUndefined();
  });

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(onChange).toHaveBeenCalled();
});

test("<SelectInputsField> - renders a select which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  const element = <SelectInputsField onToggle={() => {}} name="x" />;
  const wrapper = mount(
    element,
    createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { model: { x: "b" }, onChange })
  );

  act(() => {
    const changeEvent = wrapper.find(Select).prop("onSelect")?.("event" as any, "b");
    expect(changeEvent).toBeFalsy();
  });

  expect(wrapper.find(Select)).toHaveLength(1);
  expect(onChange).toHaveBeenLastCalledWith("x", "b");
});

test("<SelectInputsField> - renders a label", () => {
  const element = <SelectInputsField onToggle={() => {}} required={false} name="x" label="y" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("label")).toHaveLength(1);
  expect(wrapper.find("label").text()).toBe("y");
  expect(wrapper.find("label").prop("htmlFor")).toBe(wrapper.find(Select).prop("id"));
});

test("<SelectInputsField> - renders a label", () => {
  const element = <SelectInputsField onToggle={() => {}} required={true} name="x" label="y" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("label")).toHaveLength(1);
  expect(wrapper.find("label").text()).toBe("y *");
  expect(wrapper.find("label").prop("htmlFor")).toBe(wrapper.find(Select).prop("id"));
});

test("<SelectInputsField> - renders a number label", () => {
  const element = <SelectInputsField onToggle={() => {}} required={true} name="x" label={1} />;
  const wrapper = mount(element, createContext({ x: { type: Number, allowedValues: [1, 2] } }));

  expect(wrapper.find("label")).toHaveLength(1);
  expect(wrapper.find("label").text()).toBe("1 *");
  expect(wrapper.find("label").prop("htmlFor")).toBe(wrapper.find(Select).prop("id"));
});

test("<SelectInputsField> - renders a wrapper with unknown props", () => {
  const element = <SelectInputsField onToggle={() => {}} name="x" data-x="x" data-y="y" data-z="z" />;
  const wrapper = mount(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(wrapper.find("div").at(0).prop("data-x")).toBe("x");
  expect(wrapper.find("div").at(0).prop("data-y")).toBe("y");
  expect(wrapper.find("div").at(0).prop("data-z")).toBe("z");
});
