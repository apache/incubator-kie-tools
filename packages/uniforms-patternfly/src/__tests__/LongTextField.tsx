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
// import { LongTextField } from "../";
// import { screen } from "@testing-library/react";
// import { createContext } from "./_createContext";
// import { render } from "./_render";

// test("<LongTextField> - renders a textarea", () => {
//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
// });

// test("<LongTextField> - renders a textarea with correct disabled state", () => {
//   const element = <LongTextField name="x" disabled />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("disabled")).toBe(true);
// });

// test("<LongTextField> - renders a textarea with correct id (inherited)", () => {
//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("id")).toBeTruthy();
// });

// test("<LongTextField> - renders a textarea with correct id (specified)", () => {
//   const element = <LongTextField name="x" id="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("id")).toBe("y");
// });

// test("<LongTextField> - renders a textarea with correct name", () => {
//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("name")).toBe("x");
// });

// test("<LongTextField> - renders a textarea with correct placeholder", () => {
//   const element = <LongTextField name="x" placeholder="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("placeholder")).toBe("y");
// });

// test("<LongTextField> - renders a textarea with correct value (default)", () => {
//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("value")).toBe("");
// });

// test("<LongTextField> - renders a textarea with correct value (model)", () => {
//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { model: { x: "y" } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("value")).toBe("y");
// });

// test("<LongTextField> - renders a textarea with correct value (specified)", () => {
//   const element = <LongTextField name="x" value="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").prop("value")).toBe("y");
// });

// test("<LongTextField> - renders a textarea which correctly reacts on change", () => {
//   const onChange = jest.fn();

//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").simulate("change", { target: { value: "y" } })).toBeTruthy();
//   expect(onChange).toHaveBeenLastCalledWith("x", "y");
// });

// test("<LongTextField> - renders a textarea which correctly reacts on change (empty)", () => {
//   const onChange = jest.fn();

//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { onChange }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").simulate("change", { target: { value: "" } })).toBeTruthy();
//   expect(onChange).toHaveBeenLastCalledWith("x", "");
// });

// test("<LongTextField> - renders a textarea which correctly reacts on change (same value)", () => {
//   const onChange = jest.fn();

//   const element = <LongTextField name="x" />;
//   render(element, createContext({ x: { type: String } }, { model: { x: "y" }, onChange }));

//   expect(screen("textarea")).toHaveLength(1);
//   expect(screen("textarea").simulate("change", { target: { value: "y" } })).toBeTruthy();
//   expect(onChange).toHaveBeenLastCalledWith("x", "y");
// });

// test("<LongTextField> - renders a label", () => {
//   const element = <LongTextField name="x" label="y" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("label")).toHaveLength(1);
//   expect(screen("label").text()).toBe("y");
//   expect(screen("label").prop("htmlFor")).toBe(screen("textarea").prop("id"));
// });

// test("<LongTextField> - renders a wrapper with unknown props", () => {
//   const element = <LongTextField name="x" data-x="x" data-y="y" data-z="z" />;
//   render(element, createContext({ x: { type: String } }));

//   expect(screen("div").at(0).prop("data-x")).toBe("x");
//   expect(screen("div").at(0).prop("data-y")).toBe("y");
//   expect(screen("div").at(0).prop("data-z")).toBe("z");
// });
