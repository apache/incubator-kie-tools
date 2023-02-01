// /*
//  * Copyright 2023 Red Hat, Inc. and/or its affiliates.
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *       http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */

// import * as React from "react";
// import { act } from "react-dom/test-utils";
// import { DateField } from "../";
// import { screen } from "@testing-library/react";
// import { createContext } from "./_createContext";
// import { render } from "./_render";

// test("<DateField> - renders an input", () => {
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
// });

// test("<DateField> - renders a input with correct id (inherited)", () => {
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("DatePicker").prop("id")).toBeTruthy();
//   expect(screen("TimePicker").prop("id")).toBeTruthy();
// });

// test("<DateField> - renders a input with correct id (specified)", () => {
//   const element = <DateField name="x" id="y" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("Flex").prop("id")).toBe("y");
//   expect(screen("DatePicker").prop("id")).toBe("date-picker-y");
//   expect(screen("TimePicker").prop("id")).toBe("time-picker-y");
// });

// test("<DateField> - renders a input with correct name", () => {
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("Flex").prop("name")).toBe("x");
// });

// test("<DateField> - renders an input with correct disabled state", () => {
//   const element = <DateField name="x" disabled />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("DatePicker").prop("isDisabled")).toBe(true);
//   expect(screen("TimePicker").prop("isDisabled")).toBe(true);
// });

// test("<DateField> - renders a input with correct label (specified)", () => {
//   const element = <DateField required={false} name="x" label="DateFieldLabel" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("DateFieldLabel");
//   expect(screen("label").prop("htmlFor")).toBe(screen("Flex").prop("id"));
// });

// test("<DateField> - renders a input with correct label (specified)", () => {
//   const element = <DateField required={true} name="x" label="DateFieldLabel" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("DateFieldLabel *");
//   expect(screen("label").prop("htmlFor")).toBe(screen("Flex").prop("id"));
// });

// test("<DateField> - renders a input with correct value (default)", () => {
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("DatePicker").find("input").prop("value")).toBe("");
//   expect(screen("TimePicker").find("input").prop("value")).toBe("");
// });

// test("<DateField> - renders a input with correct value (model)", () => {
//   const now = new Date();
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }, { model: { x: now } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("TimePicker").prop("value")).toEqual(`${now.getUTCHours()}:${now.getUTCMinutes()}`);
// });

// test("<DateField> - renders a input with correct value (specified)", () => {
//   const now = new Date();
//   const element = <DateField name="x" value={now} />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("input")).toHaveLength(2);
//   expect(screen("DatePicker").find("input").prop("value")).toEqual(now.toISOString().slice(0, -14));
// });

// test("<DateField> - renders a input which correctly reacts on change (DatePicker)", () => {
//   const onChange = jest.fn();

//   const now = "2000-04-04";
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   act(() => {
//     screen("DatePicker").find("input").prop("onChange")!({
//       currentTarget: { value: now },
//     } as any);
//   });

//   expect(onChange).toHaveBeenLastCalledWith("x", new Date(`${now}T00:00:00Z`));
// });

// test("<DateField> - renders a input which correctly reacts on change (DatePicker - empty)", () => {
//   const onChange = jest.fn();

//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   act(() => {
//     screen("DatePicker").find("input").prop("onChange")!({
//       currentTarget: { value: "" },
//     } as any);
//   });

//   expect(onChange).toHaveBeenLastCalledWith("x", undefined);
// });

// test("<DateField> - renders a input which correctly reacts on change (TimePicker - invalid)", () => {
//   const onChange = jest.fn();

//   const now = "10:00";
//   const element = <DateField name="x" />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   act(() => {
//     screen("TimePicker").find("input").prop("onChange")!({
//       currentTarget: { value: now },
//     } as any);
//   });

//   expect(onChange).not.toHaveBeenCalled();
// });

// test("<DateField> - renders a input which correctly reacts on change (TimePicker - valid)", () => {
//   const onChange = jest.fn();

//   const date = "2000-04-04";
//   const time = "10:30";
//   const element = <DateField name="x" value={new Date(`${date}T00:00:00Z`)} />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   act(() => {
//     screen("TimePicker").find("input").prop("onChange")!({
//       currentTarget: { value: time },
//     } as any);
//   });

//   expect(onChange).toHaveBeenLastCalledWith("x", new Date(`${date}T10:30:00Z`));
// });

// test("<DateField> - renders a wrapper with unknown props", () => {
//   const element = <DateField name="x" data-x="x" data-y="y" data-z="z" />;
//   render(element, createContext({ x: { type: Date } }));

//   expect(screen("div").at(0).prop("data-x")).toBe("x");
//   expect(screen("div").at(0).prop("data-y")).toBe("y");
//   expect(screen("div").at(0).prop("data-z")).toBe("z");
// });

// test("<DateField> - test max property - valid", () => {
//   const onChange = jest.fn();

//   const date = "1998-12-31";
//   const max = "1999-01-01T00:00:00Z";
//   const element = <DateField name="x" max={max} value={new Date(`${date}T00:00:00Z`)} />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   expect(wrapper.text().includes("Should be before")).toBe(false);
// });

// test("<DateField> - test max property - invalid", () => {
//   const onChange = jest.fn();

//   const date = "1999-01-02";
//   const max = "1999-01-01T00:00:00Z";
//   const element = <DateField name="x" max={max} value={new Date(`${date}T00:00:00Z`)} />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   expect(wrapper.text().includes("Should be before")).toBe(true);
// });

// test("<DateField> - test min property - valid", () => {
//   const onChange = jest.fn();

//   const date = "1999-01-02";
//   const min = "1999-01-01T00:00:00Z";
//   const element = <DateField name="x" min={min} value={new Date(`${date}T00:00:00Z`)} />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   expect(wrapper.text().includes("Should be after")).toBe(false);
// });

// test("<DateField> - test min property - invalid", () => {
//   const onChange = jest.fn();

//   const date = "1998-12-31";
//   const min = "1999-01-01T00:00:00Z";
//   const element = <DateField name="x" min={min} value={new Date(`${date}T00:00:00Z`)} />;
//   render(element, createContext({ x: { type: Date } }, { onChange }));

//   expect(wrapper.text().includes("Should be after")).toBe(true);
// });
