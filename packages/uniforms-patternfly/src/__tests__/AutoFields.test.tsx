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
import { AutoFields } from "..";
import { usingUniformsContext } from "./test-utils";
import { render, screen } from "@testing-library/react";

test("<AutoFields> - works", () => {
  render(usingUniformsContext(<AutoFields />, { x: { type: String } }));
  expect(screen.getByTestId("text-field")).toBeInTheDocument();
});

test("<AutoFields> - render all fields by default", () => {
  render(
    usingUniformsContext(<AutoFields />, {
      x: { type: String },
      y: { type: String },
      z: { type: String },
    })
  );

  expect(screen.getAllByTestId("text-field")).toHaveLength(3);
});

test("<AutoFields> - renders only specified fields", () => {
  render(
    usingUniformsContext(
      <AutoFields fields={["x", "y"]} />,

      {
        x: { type: String },
        y: { type: String },
        z: { type: String },
      }
    )
  );

  expect(screen.getAllByTestId("text-field")).toHaveLength(2);
  expect(screen.queryByLabelText("z")).toBeNull();
});

test("<AutoFields> - does not render ommited fields", () => {
  render(
    usingUniformsContext(
      <AutoFields omitFields={["x"]} />,

      {
        x: { type: String },
        y: { type: String },
        z: { type: String },
      }
    )
  );

  expect(screen.getAllByTestId("text-field")).toHaveLength(2);
  expect(screen.queryByLabelText("x")).toBeNull();
});

test("<AutoFields> - works with custom component", () => {
  const Component = jest.fn(() => null);

  render(
    usingUniformsContext(
      <AutoFields autoField={Component} />,

      {
        x: { type: String },
        y: { type: String },
        z: { type: String },
      }
    )
  );

  expect(Component).toHaveBeenCalledTimes(3);
});

test("<AutoFields> - wraps fields in specified element", () => {
  render(
    usingUniformsContext(
      <AutoFields element="section" />,

      {
        x: { type: String },
        y: { type: String },
        z: { type: String },
      }
    )
  );

  expect(screen.getAllByTestId("text-field")).toHaveLength(3);
});
