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
import { DateField } from "../";
import { render, screen, fireEvent } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<DateField> - renders an input", () => {
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
});

test("<DateField> - renders a input with correct id (inherited)", () => {
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
  expect(screen.getByTestId("date-picker")).toBeInTheDocument();
  expect(screen.getByTestId("time-picker")).toBeInTheDocument();
});

test("<DateField> - renders a input with correct id (specified)", () => {
  render(usingUniformsContext(<DateField name="x" id="y" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
  expect(screen.getByTestId("date-picker").getAttribute("id")).toBe("date-picker-y");
  // TimePicker component id is in an inside div
  expect(
    screen.getByTestId("time-picker").getElementsByClassName("pf-c-input-group")[0].children[0].getAttribute("id")
  ).toBe("time-picker-y");
});

test("<DateField> - renders a input with correct name", () => {
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
  expect(screen.getByTestId("date-field").getAttribute("name")).toBe("x");
});

test("<DateField> - renders an input with correct disabled state", () => {
  render(usingUniformsContext(<DateField name="x" disabled />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();

  const inputs = screen.getByTestId("date-field").getElementsByTagName("input");
  expect(inputs[0]).toBeDisabled();
  expect(inputs[1]).toBeDisabled();
});

test("<DateField> - renders a input with correct label (specified)", () => {
  render(usingUniformsContext(<DateField required={false} name="x" label="DateFieldLabel" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
  expect(screen.getByText("DateFieldLabel")).toBeInTheDocument();
});

test("<DateField> - renders a input with correct label (specified)", () => {
  render(usingUniformsContext(<DateField required={true} name="x" label="DateFieldLabel" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
  expect(screen.getByText("DateFieldLabel")).toBeInTheDocument();
  expect(screen.getByText("*")).toBeInTheDocument();
});

test("<DateField> - renders a input with correct value (default)", () => {
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();

  const inputs = screen.getByTestId("date-field").getElementsByTagName("input");
  expect(inputs[0].getAttribute("value")).toBe("");
  expect(inputs[1].getAttribute("value")).toBe("");
});

test("<DateField> - renders a input with correct value (model)", () => {
  const now = new Date();
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }, { model: { x: now } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();
  expect(screen.getByTestId("time-picker").getAttribute("value")).toEqual(
    `${now.getUTCHours()}:${now.getUTCMinutes()}`
  );
});

test("<DateField> - renders a input with correct value (specified)", () => {
  const now = new Date();
  render(usingUniformsContext(<DateField name="x" value={now.toISOString()} />, { x: { type: Date } }));

  expect(screen.getByTestId("date-field")).toBeInTheDocument();

  const inputs = screen.getByTestId("date-field").getElementsByTagName("input");
  expect(inputs[0].getAttribute("value")).toEqual(now.toISOString().slice(0, -14));
  expect(inputs[1].getAttribute("value")).toBe("");
});

test("<DateField> - renders a input which correctly reacts on change (DatePicker)", () => {
  const onChange = jest.fn();

  const now = "2000-04-04";
  const dateNow = new Date(now);
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }, { onChange }));

  const input = screen.getByTestId("date-picker").getElementsByTagName("input")[0];
  fireEvent.change(input, { target: { value: now } });

  expect(onChange).toHaveBeenLastCalledWith("x", `${dateNow.toISOString()}`);
});

test("<DateField> - renders a input which correctly reacts on change (empty value) (DatePicker)", () => {
  const onChange = jest.fn();

  render(usingUniformsContext(<DateField name="x" value={"2000-04-04"} />, { x: { type: Date } }, { onChange }));

  const input = screen.getByTestId("date-picker").getElementsByTagName("input")[0];
  fireEvent.change(input, { target: { value: "" } });

  expect(onChange).toHaveBeenLastCalledWith("x", "");
});

test("<DateField> - renders a input which correctly reacts on change (DatePicker - empty)", () => {
  const onChange = jest.fn();

  render(usingUniformsContext(<DateField name="x" onChange={onChange} />, { x: { type: Date } }));

  const input = screen.getByTestId("date-picker").getElementsByTagName("input")[0];
  fireEvent.change(input, { target: { value: "" } });

  expect(onChange).not.toHaveBeenCalled();
});

test("<DateField> - renders a input which correctly reacts on change (TimePicker - invalid)", () => {
  const onChange = jest.fn();

  const now = "10:00";
  render(usingUniformsContext(<DateField name="x" />, { x: { type: Date } }, { onChange }));

  const input = screen.getByTestId("time-picker").getElementsByTagName("input")[0];
  fireEvent.change(input, { target: { value: now } });

  expect(onChange).not.toHaveBeenCalled();
});

test("<DateField> - renders a input which correctly reacts on change (TimePicker - valid)", () => {
  const onChange = jest.fn();

  const date = "2000-04-04";
  const time = "10:30";
  render(usingUniformsContext(<DateField name="x" value={`${date}T00:00:00Z`} />, { x: { type: Date } }, { onChange }));

  const input = screen.getByTestId("time-picker").getElementsByTagName("input")[0];
  fireEvent.change(input, { target: { value: time } });

  expect(onChange).toHaveBeenLastCalledWith("x", `${date}T${time}:00.000Z`);
});

test("<DateField> - test max property - valid", () => {
  const date = "1998-12-31";
  const max = "1999-01-01T00:00:00Z";
  render(usingUniformsContext(<DateField name="x" max={max} value={`${date}T00:00:00Z`} />, { x: { type: Date } }));

  expect(screen.queryByTestId("Should be before")).toBeNull();
});

test("<DateField> - test max property - invalid", () => {
  const date = "1999-01-02";
  const max = "1999-01-01T00:00:00.000Z";
  render(usingUniformsContext(<DateField name="x" max={max} value={`${date}T00:00:00Z`} />, { x: { type: Date } }));

  expect(screen.getByText(`Should be before ${max}`)).toBeInTheDocument();
});

test("<DateField> - test min property - valid", () => {
  const date = "1999-01-02";
  const min = "1999-01-01T00:00:00Z";
  render(usingUniformsContext(<DateField name="x" min={min} value={`${date}T00:00:00Z`} />, { x: { type: Date } }));

  expect(screen.queryByTestId("Should be after")).toBeNull();
});

test("<DateField> - test min property - invalid", () => {
  const date = "1998-12-31";
  const min = "1999-01-01T00:00:00.000Z";
  render(usingUniformsContext(<DateField name="x" min={min} value={`${date}T00:00:00Z`} />, { x: { type: Date } }));

  expect(screen.getByText(`Should be after ${min}`)).toBeInTheDocument();
});
