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
// import { TextField } from "../";
// import { screen } from "@testing-library/react";
// import { createContext } from "./_createContext";
// import { render } from "./_render";
// import { act } from "react-dom/test-utils";

// test("<TextField> - renders an input", () => {
//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
// });

// test("<TextField> - renders an input with correct disabled state", () => {
//   const element = <TextField name="x" disabled />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("disabled")).toBe(true);
// });

// test("<TextField> - renders an input with correct id (inherited)", () => {
//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("id")).toBeTruthy();
// });

// test("<TextField> - renders an input with correct id (specified)", () => {
//   const element = <TextField name="x" id="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("id")).toBe("y");
// });

// test("<TextField> - renders an input with correct name", () => {
//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("name")).toBe("x");
// });

// test("<TextField> - renders an input with correct placeholder", () => {
//   const element = <TextField name="x" placeholder="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("placeholder")).toBe("y");
// });

// test("<TextField> - renders an input with correct type", () => {
//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("type")).toBe("text");
// });

// test("<TextField> - renders an input with correct value (default)", () => {
//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("value")).toBe("");
// });

// test("<TextField> - renders an input with correct value (model)", () => {
//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { model: { x: "y" } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("value")).toBe("y");
// });

// test("<TextField> - renders an input with correct value (specified)", () => {
//   const element = <TextField name="x" value="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("value")).toBe("y");
// });

// test("<TextField> - renders an input which correctly reacts on change", () => {
//   const onChange = jest.fn();

//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").simulate("change", { target: { value: "y" } })).toBeTruthy();
//   expect(onChange).toHaveBeenLastCalledWith("x", "y");
// });

// test("<TextField> - renders an input which correctly reacts on change (empty)", () => {
//   const onChange = jest.fn();

//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").simulate("change", { target: { value: "" } })).toBeTruthy();
//   expect(onChange).toHaveBeenLastCalledWith("x", "");
// });

// test("<TextField> - renders an input which correctly reacts on change (same value)", () => {
//   const onChange = jest.fn();

//   const element = <TextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { model: { x: "y" }, onChange }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").simulate("change", { target: { value: "y" } })).toBeTruthy();
//   expect(onChange).toHaveBeenLastCalledWith("x", "y");
// });

// test("<TextField> - renders a label", () => {
//   const element = <TextField required={false} name="x" label="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y");
//   expect(screen("label").prop("htmlFor")).toBe(screen("input").prop("id"));
// });

// test("<TextField> - renders a label", () => {
//   const element = <TextField required={true} name="x" label="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y *");
//   expect(screen("label").prop("htmlFor")).toBe(screen("input").prop("id"));
// });

// test("<TextField> - renders a help", () => {
//   const element = <TextField required={false} name="x" helperText="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("div.pf-c-form__helper-text")).toHaveLength(1);
//   expect(screen("div.pf-c-form__helper-text").text()).toBe("y");
// });

// test("<TextField> - renders a wrapper with unknown props", () => {
//   const element = <TextField name="x" data-x="x" data-y="y" data-z="z" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("div").at(0).prop("data-x")).toBe("x");
//   expect(screen("div").at(0).prop("data-y")).toBe("y");
//   expect(screen("div").at(0).prop("data-z")).toBe("z");
// });

// test("<TextField> - renders a initial value on date field (DatePicker)", () => {
//   const date = "2000-04-04";
//   const element = <TextField required={true} name="x" label="y" type={"date"} />;
//   render(element, createContext({ x: { type: String } }, { model: { x: date } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("value")).toBe(date);
// });

// test("<TextField> - renders a disabled date field (DatePicker)", () => {
//   const element = <TextField required={true} name="x" label="y" type={"date"} disabled={true} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("input")).toHaveLength(1);
//   expect(screen("input").prop("disabled")).toBe(true);
// });

// test("<TextField> - renders a input which correctly reacts on change (DatePicker)", () => {
//   const onChange = jest.fn();

//   const date = "2000-04-04";
//   const element = <TextField required={true} name="x" label="y" type={"date"} />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y *");

//   act(() => {
//     screen("DatePicker").find("input").prop("onChange")!({
//       currentTarget: { value: date },
//     } as any);
//   });

//   expect(onChange).toHaveBeenLastCalledWith("x", date);
// });

// test("<TextField> - renders a input which correctly reacts on change (DatePicker - empty)", () => {
//   const onChange = jest.fn();

//   const date = "";
//   const element = <TextField required={true} name="x" label="y" type={"date"} />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y *");

//   act(() => {
//     screen("DatePicker").find("input").prop("onChange")!({
//       currentTarget: { value: date },
//     } as any);
//   });

//   expect(onChange).toHaveBeenLastCalledWith("x", date);
// });

// test("<TextField> - renders a initial value on time field (TimePicker)", () => {
//   const time = "10:00";
//   const element = <TextField required={true} name="x" label="y" type={"time"} />;
//   render(element, createContext({ x: { type: String } }, { model: { x: time } }));

//   expect(screen("TimePicker")).toHaveLength(1);
//   expect(screen("TimePicker").prop("value")).toBe(time);
// });

// test("<TextField> - renders a disabled date field (TimePicker)", () => {
//   const element = <TextField required={true} name="x" label="y" type={"time"} disabled={true} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("TimePicker")).toHaveLength(1);
//   expect(screen("input").prop("disabled")).toBe(true);
// });

// test("<TextField> - renders a input which correctly reacts on change (TimePicker)", () => {
//   const onChange = jest.fn();

//   const time = "10:10";
//   const element = <TextField required={true} name="x" label="y" type={"time"} />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   act(() => {
//     screen("TimePicker").find("input").prop("onChange")!({
//       currentTarget: { value: time },
//     } as any);
//   });

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y *");
//   expect(onChange).toHaveBeenLastCalledWith("x", "10:10:00");
// });

// test("<TextField> - renders a input which correctly reacts on change (TimePicker - empty)", () => {
//   const onChange = jest.fn();

//   const time = "";
//   const element = <TextField required={true} name="x" label="y" type={"time"} />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y *");

//   act(() => {
//     screen("TimePicker").find("input").prop("onChange")!({
//       currentTarget: { value: time },
//     } as any);
//   });

//   expect(onChange).toHaveBeenLastCalledWith("x", time);
// });

// test("<TextField> - test max property (TimePicker - valid)", () => {
//   const time = "10:00";
//   const max = "12:00";
//   const element = <TextField name="x" label="y" max={max} type={"time"} value={time} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be before")).toBe(false);
// });

// test("<TextField> - test max property (TimePicker - invalid)", () => {
//   const time = "13:00";
//   const max = "12:00";
//   const element = <TextField name="x" label="y" max={max} type={"time"} value={time} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be before")).toBe(true);
// });

// test("<TextField> - test min property (TimePicker - valid)", () => {
//   const time = "13:00";
//   const min = "12:00";
//   const element = <TextField name="x" label="y" min={min} type={"time"} value={time} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be after")).toBe(false);
// });

// test("<TextField> - test min property (TimePicker - invalid)", () => {
//   const time = "10:00";
//   const min = "12:00";
//   const element = <TextField name="x" label="y" min={min} type={"time"} value={time} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be after")).toBe(true);
// });

// test("<TextField> - test max property (DatePicker - valid)", () => {
//   const date = "2000-01-01";
//   const max = "2000-01-02";
//   const element = <TextField name="x" label="y" max={max} type={"date"} value={date} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be before")).toBe(false);
// });

// test("<TextField> - test max property (DatePicker - invalid)", () => {
//   const date = "2000-01-02";
//   const max = "2000-01-01";
//   const element = <TextField name="x" label="y" max={max} type={"date"} value={date} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be before")).toBe(true);
// });

// test("<TextField> - test min property (DatePicker - valid)", () => {
//   const date = "2000-01-02";
//   const min = "2000-01-01";
//   const element = <TextField name="x" label="y" min={min} type={"date"} value={date} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be after")).toBe(false);
// });

// test("<TextField> - test min property (DatePicker - invalid)", () => {
//   const date = "2000-01-01";
//   const min = "2000-01-02";
//   const element = <TextField name="x" label="y" min={min} type={"date"} value={date} />;
//   render(element, createContext({ x: { type: String } }));

//   expect(wrapper.text().includes("Should be after")).toBe(true);
// });
