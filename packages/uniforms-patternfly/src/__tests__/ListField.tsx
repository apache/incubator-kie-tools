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
// import { ListField } from "../";
// import { screen } from "@testing-library/react";
// import { createContext } from "./_createContext";
// import { render } from "./_render";

// test("<ListField> - works", () => {
//   const element = <ListField name="x" />;
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen(ListField)).toHaveLength(1);
// });

// test("<ListField> - renders ListAddField", () => {
//   const element = <ListField name="x" label="ListFieldLabel" />;
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen("ListAdd")).toHaveLength(1);
//   expect(screen("ListAdd").prop("name")).toBe("x.$");
// });

// test("<ListField> - renders correct label (specified)", () => {
//   const element = <ListField name="x" label="ListFieldLabel" />;
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen("label").at(0).text()).toEqual(expect.stringContaining("ListFieldLabel"));
// });

// test("<ListField> - renders correct numer of items with initialCount (specified)", () => {
//   const element = <ListField name="x" initialCount={3} />;
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen("input")).toHaveLength(3);
// });

// test("<ListField> - passes itemProps to its children", () => {
//   const element = <ListField name="x" initialCount={3} itemProps={{ "data-xyz": 1 }} />;
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen("ListItemField").first().prop("data-xyz")).toBe(1);
// });

// test("<ListField> - renders children (specified)", () => {
//   const Child = jest.fn(() => null);

//   const element = (
//     <ListField name="x" initialCount={2}>
//       {/* <Child /> */}
//     </ListField>
//   );
//   mount(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(Child).toHaveBeenCalledTimes(2);
// });

// test("<ListField> - renders children with correct name (children)", () => {
//   const Child = jest.fn(() => <div />);

//   const element = (
//     <ListField name="x" initialCount={2}>
//       {/* <Child name="$" /> */}
//     </ListField>
//   );
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen(Child).at(0).prop("name")).toBe("0");
//   expect(screen(Child).at(1).prop("name")).toBe("1");
// });

// test("<ListField> - renders children with correct name (value)", () => {
//   const element = <ListField name="x" initialCount={2} />;
//   render(element, createContext({ x: { type: Array }, "x.$": { type: String } }));

//   expect(screen("ListItemField").at(0).prop("name")).toBe("0");
//   expect(screen("ListItemField").at(1).prop("name")).toBe("1");
// });
