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
import { NumField } from "../";
import { screen } from "@testing-library/react";
import { createContext } from "./_createContext";
import { render } from "./_render";

test("<NumField> - renders an input", () => {
  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
});

test("<NumField> - renders an input with correct disabled state", () => {
  const element = <NumField name="x" disabled />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("disabled")).toBe(true);
});

test("<NumField> - renders an input with correct id (inherited)", () => {
  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("id")).toBeTruthy();
});

test("<NumField> - renders an input with correct id (specified)", () => {
  const element = <NumField name="x" id="y" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("id")).toBe("y");
});

test("<NumField> - renders an input with correct max", () => {
  const element = <NumField name="x" max={10} />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("max")).toBe(10);
});

test("<NumField> - renders an input with correct min", () => {
  const element = <NumField name="x" min={10} />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("min")).toBe(10);
});

test("<NumField> - renders an input with correct name", () => {
  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("name")).toBe("x");
});

test("<NumField> - renders an input with correct placeholder", () => {
  const element = <NumField name="x" placeholder="y" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("placeholder")).toBe("y");
});

test("<NumField> - renders an input with correct step (decimal)", () => {
  const element = <NumField name="x" decimal />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("step")).toBe(0.01);
});

test("<NumField> - renders an input with correct step (integer)", () => {
  const element = <NumField name="x" decimal={false} />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("step")).toBe(1);
});

test("<NumField> - renders an input with correct type", () => {
  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("type")).toBe("number");
});

test("<NumField> - renders an input with correct value (default)", () => {
  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("value")).toBe("");
});

test("<NumField> - renders an input with correct value (model)", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }, { model: { x: 1 }, onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("value")).toBe(1);

  // NOTE: All following tests are here to cover hacky NumField implementation.
  const spy = jest.spyOn(global.console, "error").mockImplementation(() => {});

  [
    { value: 0.1 },
    { value: undefined },
    { value: undefined },
    { value: 2 },
    { value: 2 },
    { value: 1, decimal: false },
    { value: 1, decimal: false },
  ].forEach(({ decimal = true, value }) => {
    wrapper.setProps({ decimal });

    expect(screen("input").simulate("change", { target: { value: "" } })).toBeTruthy();
    expect(screen("input").simulate("change", { target: { value } })).toBeTruthy();
    expect(onChange).toHaveBeenLastCalledWith("x", value);

    wrapper.setProps({ value: undefined });
    wrapper.setProps({ value });
    expect(screen("input").prop("value")).toBe(value ?? "");
  });

  spy.mockRestore();
});

test("<NumField> - renders an input with correct value (specified)", () => {
  const element = <NumField name="x" value={2} />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").prop("value")).toBe(2);
});

test("<NumField> - renders an input which correctly reacts on change", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }, { onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").simulate("change", { target: { value: "1" } })).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", 1);
});

test("<NumField> - renders an input which correctly reacts on change (decimal on decimal)", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" decimal />;
  render(element, createContext({ x: { type: Number } }, { onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").simulate("change", { target: { value: "2.5" } })).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", 2.5);
});

test("<NumField> - renders an input which correctly reacts on change (decimal on integer)", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" decimal={false} />;
  render(element, createContext({ x: { type: Number } }, { onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").simulate("change", { target: { value: "2.5" } })).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", 2);
});

test("<NumField> - renders an input which correctly reacts on change (empty)", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }, { onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").simulate("change", { target: { value: "" } })).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", undefined);
});

test("<NumField> - renders an input which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }, { model: { x: 1 }, onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").simulate("change", { target: { value: "1" } })).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", 1);
});

test("<NumField> - renders an input which correctly reacts on change (zero)", () => {
  const onChange = jest.fn();

  const element = <NumField name="x" />;
  render(element, createContext({ x: { type: Number } }, { onChange }));

  expect(screen("input")).toHaveLength(1);
  expect(screen("input").simulate("change", { target: { value: "0" } })).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", 0);
});

test("<NumField> - renders a label", () => {
  const element = <NumField required={false} name="x" label="y" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("label")).toHaveLength(1);
  expect(screen("label").text()).toBe("y");
  expect(screen("label").prop("htmlFor")).toBe(screen("input").prop("id"));
});

test("<NumField> - renders a label", () => {
  const element = <NumField required={true} name="x" label="y" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("label")).toHaveLength(1);
  expect(screen("label").text()).toBe("y *");
  expect(screen("label").prop("htmlFor")).toBe(screen("input").prop("id"));
});

test("<NumField> - renders a wrapper with unknown props", () => {
  const element = <NumField name="x" data-x="x" data-y="y" data-z="z" />;
  render(element, createContext({ x: { type: Number } }));

  expect(screen("div").at(0).prop("data-x")).toBe("x");
  expect(screen("div").at(0).prop("data-y")).toBe("y");
  expect(screen("div").at(0).prop("data-z")).toBe("z");
});
