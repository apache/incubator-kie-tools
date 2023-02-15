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
import { RadioField } from "..";
import { render, screen, fireEvent } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";

test("<RadioField> - renders a set of checkboxes", () => {
  render(usingUniformsContext(<RadioField name="x" allowedValues={[]} />, { x: { type: String, allowedValues: [] } }));

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(0);
});

test("<RadioField> - renders a set of checkboxes", () => {
  render(
    usingUniformsContext(<RadioField name="x" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
});

test("<RadioField> - renders a set of checkboxes with correct disabled state", () => {
  render(
    usingUniformsContext(<RadioField name="x" disabled allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0]).toBeDisabled();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1]).toBeDisabled();
});

test("<RadioField> - renders a set of checkboxes with correct id (inherited)", () => {
  render(
    usingUniformsContext(<RadioField name="x" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0].getAttribute("id")).toBeTruthy();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1].getAttribute("id")).toBeTruthy();
});

test("<RadioField> - renders a set of checkboxes with correct id (specified)", () => {
  render(
    usingUniformsContext(<RadioField name="x" id="y" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0].getAttribute("id")).toBe("y");
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1].getAttribute("id")).toBe("y");
});

test("<RadioField> - renders a set of checkboxes with correct name", () => {
  render(
    usingUniformsContext(<RadioField name="x" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0].getAttribute("name")).toBe("x");
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1].getAttribute("name")).toBe("x");
});

test("<RadioField> - renders a set of checkboxes with correct options", () => {
  render(
    usingUniformsContext(<RadioField name="x" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("wrapper-field")).toBeInTheDocument();
  expect(screen.getByTestId("wrapper-field").getElementsByTagName("label")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("label")[0].textContent).toBe("a");
  expect(screen.getByTestId("radio-field").getElementsByTagName("label")[1].textContent).toBe("b");
});

test("<RadioField> - renders a set of checkboxes with correct options (transform)", () => {
  render(
    usingUniformsContext(
      <RadioField name="x" transform={(x: string) => x.toUpperCase()} allowedValues={["a", "b"]} />,
      { x: { type: String, allowedValues: ["a", "b"] } }
    )
  );

  expect(screen.getByTestId("wrapper-field")).toBeInTheDocument();
  expect(screen.getByTestId("wrapper-field").getElementsByTagName("label")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("label")[0].textContent).toBe("A");
  expect(screen.getByTestId("radio-field").getElementsByTagName("label")[1].textContent).toBe("B");
});

test("<RadioField> - renders a set of checkboxes with correct value (default)", () => {
  render(
    usingUniformsContext(<RadioField name="x" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0]).not.toBeChecked();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1]).not.toBeChecked();
});

test("<RadioField> - renders a set of checkboxes with correct value (model)", () => {
  render(
    usingUniformsContext(
      <RadioField name="x" allowedValues={["a", "b"]} />,
      { x: { type: String, allowedValues: ["a", "b"] } },
      { model: { x: "b" } }
    )
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0]).not.toBeChecked();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1]).toBeChecked();
});

test("<RadioField> - renders a set of checkboxes with correct value (specified)", () => {
  render(
    usingUniformsContext(<RadioField name="x" value="b" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")).toHaveLength(2);
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[0]).not.toBeChecked();
  expect(screen.getByTestId("radio-field").getElementsByTagName("input")[1]).toBeChecked();
});

test("<RadioField> - renders a set of checkboxes which correctly reacts on change", () => {
  const onChange = jest.fn();

  render(
    usingUniformsContext(
      <RadioField name="x" allowedValues={["a", "b"]} />,
      { x: { type: String, allowedValues: ["a", "b"] } },
      { onChange }
    )
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  const inputs = screen.getByTestId("radio-field").getElementsByTagName("input");
  expect(inputs).toHaveLength(2);
  fireEvent.click(inputs[1]);
  expect(onChange).toHaveBeenLastCalledWith("x", "b");
});

test("<RadioField> - renders a set of checkboxes which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  render(
    usingUniformsContext(
      <RadioField name="x" allowedValues={["a", "b"]} />,
      { x: { type: String, allowedValues: ["a", "b"] } },
      { model: { x: "b" }, onChange }
    )
  );

  expect(screen.getByTestId("radio-field")).toBeInTheDocument();
  const inputs = screen.getByTestId("radio-field").getElementsByTagName("input");
  expect(inputs).toHaveLength(2);
  fireEvent.click(inputs[0]);
  expect(onChange).toHaveBeenLastCalledWith("x", "a");
});

test("<RadioField> - renders a label", () => {
  render(
    usingUniformsContext(<RadioField required={false} name="x" label="y" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("wrapper-field")).toBeInTheDocument();
  expect(screen.getByTestId("wrapper-field").getElementsByTagName("label")).toHaveLength(3);
  expect(screen.getByTestId("wrapper-field").getElementsByTagName("label")[0].textContent).toBe("y");
});

test("<RadioField> - renders a label (required)", () => {
  render(
    usingUniformsContext(<RadioField required={true} name="x" label="y" allowedValues={["a", "b"]} />, {
      x: { type: String, allowedValues: ["a", "b"] },
    })
  );

  expect(screen.getByTestId("wrapper-field")).toBeInTheDocument();
  expect(screen.getByTestId("wrapper-field").getElementsByTagName("label")).toHaveLength(3);
  expect(screen.getByTestId("wrapper-field").getElementsByTagName("label")[0].textContent).toBe("y *");
});
