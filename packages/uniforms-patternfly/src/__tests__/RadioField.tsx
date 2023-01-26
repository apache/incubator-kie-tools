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
import { RadioField } from "../";
import { screen } from "@testing-library/react";
import { createContext } from "./_createContext";
import { render } from "./_render";

test("<RadioField> - renders a set of checkboxes", () => {
  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
});

test("<RadioField> - renders a set of checkboxes with correct disabled state", () => {
  const element = <RadioField name="x" disabled allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("disabled")).toBe(true);
  expect(screen("input").at(1).prop("disabled")).toBe(true);
});

test("<RadioField> - renders a set of checkboxes with correct id (inherited)", () => {
  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("id")).toBeTruthy();
  expect(screen("input").at(1).prop("id")).toBeTruthy();
});

test("<RadioField> - renders a set of checkboxes with correct id (specified)", () => {
  const element = <RadioField name="x" id="y" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("id")).toBe("y");
  expect(screen("input").at(1).prop("id")).toBe("y");
});

test("<RadioField> - renders a set of checkboxes with correct name", () => {
  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("name")).toBe("x");
  expect(screen("input").at(1).prop("name")).toBe("x");
});

test("<RadioField> - renders a set of checkboxes with correct options", () => {
  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("label")).toHaveLength(2);
  expect(screen("label").at(0).text()).toBe("a");
  expect(screen("label").at(1).text()).toBe("b");
});

test("<RadioField> - renders a set of checkboxes with correct options (transform)", () => {
  const element = <RadioField name="x" transform={(x: string) => x.toUpperCase()} allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("label")).toHaveLength(2);
  expect(screen("label").at(0).text()).toBe("A");
  expect(screen("label").at(1).text()).toBe("B");
});

test("<RadioField> - renders a set of checkboxes with correct value (default)", () => {
  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("checked")).toBe(false);
  expect(screen("input").at(1).prop("checked")).toBe(false);
});

test("<RadioField> - renders a set of checkboxes with correct value (model)", () => {
  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { model: { x: "b" } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("checked")).toBe(false);
  expect(screen("input").at(1).prop("checked")).toBe(true);
});

test("<RadioField> - renders a set of checkboxes with correct value (specified)", () => {
  const element = <RadioField name="x" value="b" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).prop("checked")).toBe(false);
  expect(screen("input").at(1).prop("checked")).toBe(true);
});

test("<RadioField> - renders a set of checkboxes which correctly reacts on change", () => {
  const onChange = jest.fn();

  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { onChange }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(1).simulate("change")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", "b");
});

test("<RadioField> - renders a set of checkboxes which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  const element = <RadioField name="x" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }, { model: { x: "b" }, onChange }));

  expect(screen("input")).toHaveLength(2);
  expect(screen("input").at(0).simulate("change")).toBeTruthy();
  expect(onChange).toHaveBeenLastCalledWith("x", "a");
});

test("<RadioField> - renders a label", () => {
  const element = <RadioField required={false} name="x" label="y" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("label")).toHaveLength(3);
  expect(screen("label").at(0).text()).toBe("y");
});

test("<RadioField> - renders a label", () => {
  const element = <RadioField required={true} name="x" label="y" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("label")).toHaveLength(3);
  expect(screen("label").at(0).text()).toBe("y *");
});

test("<RadioField> - renders a wrapper with unknown props", () => {
  const element = <RadioField name="x" data-x="x" data-y="y" data-z="z" allowedValues={["a", "b"]} />;
  render(element, createContext({ x: { type: String, allowedValues: ["a", "b"] } }));

  expect(screen("div").at(0).prop("data-x")).toBe("x");
  expect(screen("div").at(0).prop("data-y")).toBe("y");
  expect(screen("div").at(0).prop("data-z")).toBe("z");
});
