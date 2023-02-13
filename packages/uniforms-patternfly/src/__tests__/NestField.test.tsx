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
import { NestField } from "..";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<NestField> - renders an <AutoField> for each field", () => {
  render(
    usingUniformsContext(<NestField name="x" />, {
      x: { type: Object },
      "x.a": { type: String },
      "x.b": { type: Number },
    })
  );

  expect(screen.getByTestId("nest-field")).toBeInTheDocument();
  expect(screen.getByTestId("nest-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("nest-field").getElementsByTagName("input")[0].getAttribute("name")).toBe("x.a");
  expect(screen.getByTestId("nest-field").getElementsByTagName("input")[1].getAttribute("name")).toBe("x.b");
});

test("<NestField> - renders custom content if given", () => {
  render(
    usingUniformsContext(
      <NestField name="x">
        <article data-test="content" />
      </NestField>,
      {
        x: { type: Object },
        "x.a": { type: String },
        "x.b": { type: Number },
      }
    )
  );

  expect(screen.getByTestId("nest-field")).toBeInTheDocument();
  expect(screen.getByTestId("nest-field").getElementsByTagName("article")).toHaveLength(1);
  expect(screen.getByTestId("nest-field").getElementsByTagName("article")[0].getAttribute("data-test")).toBe("content");
});

test("<NestField> - renders a label", () => {
  render(
    usingUniformsContext(<NestField name="x" label="y" />, {
      x: { type: Object },
      "x.a": { type: String },
      "x.b": { type: Number },
    })
  );

  expect(screen.getByTestId("nest-field")).toBeInTheDocument();
  expect(screen.getByTestId("nest-field").getElementsByTagName("label")).toHaveLength(3);
  expect(screen.getByTestId("nest-field").getElementsByTagName("label")[0].textContent).toBe("y");
  expect(screen.getByTestId("nest-field").getElementsByTagName("label")[1].textContent).toBe("A *");
  expect(screen.getByTestId("nest-field").getElementsByTagName("label")[2].textContent).toBe("B *");
});
