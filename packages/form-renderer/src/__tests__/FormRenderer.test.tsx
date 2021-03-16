/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { act, render, screen } from "@testing-library/react";
import { cloneDeep } from "lodash";
import FormRenderer, { FormApi, Props } from "../FormRenderer";

const schema = {
  type: "object",
  properties: {
    name: { type: "string" },
    lastName: { type: "string" },
    age: { type: "integer", minimum: 18 }
  },
  required: ["name", "lastName"]
};

const person: any = {
  name: "Jon",
  lastName: "Snow",
  age: 18
};

let props: Props;

describe("FormRenderer test", () => {

  beforeEach(() => {
    props = {
      formSchema: schema,
      model: cloneDeep(person),
      onSubmit: jest.fn(),
      showErrorsHeader: true
    };
  });

  it("Snapshot", () => {
    const { container } = render(<FormRenderer {...props}/>);

    expect(container.firstChild).toMatchSnapshot();

    expect(screen.getByRole("form")).toHaveFormValues({
      name: "Jon",
      lastName: "Snow",
      age: 18
    });
  });

  it("Form submit", async () => {
    const formApi = React.createRef<FormApi>();

    const { container } = render(<FormRenderer {...props} ref={formApi}/>);

    expect(container.firstChild).toMatchSnapshot();

    await act(async () => {
      formApi.current?.submit();
    });

    expect(props.onSubmit).toHaveBeenCalledWith(props.model);
  });

  it("Form change & reset", async () => {
    const formApi = React.createRef<FormApi>();

    const { container } = render(<FormRenderer {...props} ref={formApi}/>);

    expect(container.firstChild).toMatchSnapshot();

    expect(screen.getByRole("form")).toHaveFormValues({
      name: "Jon",
      lastName: "Snow",
      age: 18
    });

    await act(async () => {
      formApi.current?.change("name", "Harry");
      formApi.current?.change("lastName", "Potter");
      formApi.current?.change("age", 8);
    });

    expect(container.firstChild).toMatchSnapshot();

    expect(screen.getByRole("form")).toHaveFormValues({
      name: "Harry",
      lastName: "Potter",
      age: 8
    });

    await act(async () => {
      formApi.current?.reset();
    });

    expect(container.firstChild).toMatchSnapshot();

    expect(screen.getByRole("form")).toHaveFormValues({
      name: "Jon",
      lastName: "Snow",
      age: 18
    });
  });

  it("Form validation error", async () => {
    const formApi = React.createRef<FormApi>();

    const { container } = render(<FormRenderer {...props} ref={formApi}/>);

    expect(container.firstChild).toMatchSnapshot();

    expect(screen.getByRole("form")).toHaveFormValues({
      name: "Jon",
      lastName: "Snow",
      age: 18
    });

    await act(async () => {
      formApi.current?.change("name", null);
      formApi.current?.change("lastName", null);
    });

    await act(async () => {
      formApi.current?.submit();
    });

    expect(container.firstChild).toMatchSnapshot();

    expect(props.onSubmit).not.toHaveBeenCalled();

    expect(screen.getAllByText("should have required property 'name'")).toHaveLength(2);
    expect(screen.getAllByText("should have required property 'lastName'")).toHaveLength(2);
  });
});
