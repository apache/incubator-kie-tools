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
import { ListField } from "../";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<ListField> - works", () => {
  render(usingUniformsContext(<ListField name="x" />, { x: { type: Array }, "x.$": { type: String } }));

  expect(screen.getByTestId("list-field")).toBeInTheDocument();
});

test("<ListField> - renders ListAddField", () => {
  render(
    usingUniformsContext(<ListField name="x" label="ListFieldLabel" />, { x: { type: Array }, "x.$": { type: String } })
  );

  expect(screen.getByTestId("list-add-field")).toBeInTheDocument();
  expect(screen.getByText("ListFieldLabel")).toBeInTheDocument();
});

test("<ListField> - renders correct label (specified)", () => {
  render(
    usingUniformsContext(<ListField name="x" label="ListFieldLabel" />, { x: { type: Array }, "x.$": { type: String } })
  );

  expect(screen.getByText("ListFieldLabel")).toBeInTheDocument();
});

test("<ListField> - renders correct numer of items with initialCount (specified)", () => {
  render(
    usingUniformsContext(<ListField name="x" initialCount={3} />, { x: { type: Array }, "x.$": { type: String } })
  );

  expect(screen.getAllByTestId("text-field")).toHaveLength(3);
});

test("<ListField> - renders children (specified)", () => {
  const Child: () => null = jest.fn(() => null);

  render(
    usingUniformsContext(
      <ListField name="x" initialCount={2}>
        <Child />
      </ListField>,
      { x: { type: Array }, "x.$": { type: String } }
    )
  );

  expect(Child).toHaveBeenCalledTimes(2);
});

test("<ListField> - renders children with correct name (children)", () => {
  const Child: any = jest.fn((props: any) => <div {...props} data-testid={"child-div"} />);

  render(
    usingUniformsContext(
      <ListField name="x" initialCount={2}>
        <Child name="$" />
      </ListField>,
      { x: { type: Array }, "x.$": { type: String } }
    )
  );

  const childDivs = screen.getAllByTestId("child-div");
  expect(childDivs[0].getAttribute("name")).toBe("0");
  expect(childDivs[1].getAttribute("name")).toBe("1");
});

test("<ListField> - renders children with correct name (value)", () => {
  render(
    usingUniformsContext(<ListField name="x" initialCount={2} />, { x: { type: Array }, "x.$": { type: String } })
  );

  const textFields = screen.getAllByTestId("text-field");
  expect(textFields[0].getAttribute("name")).toBe("x.0");
  expect(textFields[1].getAttribute("name")).toBe("x.1");
});
