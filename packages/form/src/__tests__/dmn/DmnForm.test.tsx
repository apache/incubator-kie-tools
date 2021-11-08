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
import { act, render } from "@testing-library/react";
import { DmnForm, Props } from "../../dmn";
import { dmnFormI18n } from "../../dmn/i18n";

const schema: any = {
  $ref: "#/definitions/InputSet",
  definitions: {
    InputSet: {
      type: "object",
      properties: {
        name: { type: "string" },
        lastName: { type: "string" },
        daysAndTimeDuration: { format: "days and time duration", type: "string" },
        yearsAndMonthDuration: { format: "years and months duration", type: "string" },
      },
    },
  },
};

const props: Props = {
  formData: jest.fn(),
  setFormData: jest.fn(),
  formError: false,
  setFormError: jest.fn(),
  showInlineError: true,
  notificationsPanel: false,
  autosave: false,
  placeholder: false,
  onSubmit: jest.fn(),
  onValidate: jest.fn(),
  locale: "en",
  formSchema: schema,
};

describe("DmnForm tests", () => {
  it("should render the DMN Form", async () => {
    const newProps = { ...props, formData: { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "P1D" } };

    const { findByTestId } = render(<DmnForm {...newProps} />);

    expect(await findByTestId("dmn-form")).toMatchSnapshot();
  });

  it("should submit the formData", async () => {
    const formRef = React.createRef<HTMLFormElement>();
    const onSubmit = jest.fn();
    const formData = { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "P1D" };

    const { findByTestId } = render(<DmnForm {...props} onSubmit={onSubmit} formRef={formRef} formData={formData} />);

    expect(await findByTestId("dmn-form")).toMatchSnapshot();

    await act(async () => {
      formRef.current?.submit();
    });

    expect(onSubmit).toHaveBeenCalledWith(formData);
  });

  it("shouldn't submit the formData", async () => {
    const formRef = React.createRef<HTMLFormElement>();
    const onSubmit = jest.fn();
    const formData = { daysAndTimeDuration: "p" };

    const { findByTestId } = render(<DmnForm {...props} onSubmit={onSubmit} formRef={formRef} formData={formData} />);

    expect(await findByTestId("dmn-form")).toMatchSnapshot();

    await act(async () => {
      formRef.current?.submit();
    });

    expect(onSubmit).toHaveBeenCalledTimes(0);
  });

  it("should validate the formData - success", async () => {
    const formRef = React.createRef<HTMLFormElement>();
    const onValidate = jest.fn();
    const formData = { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "P1D" };

    const { findByTestId } = render(
      <DmnForm {...props} formRef={formRef} onValidate={onValidate} formData={formData} />
    );

    expect(await findByTestId("dmn-form")).toMatchSnapshot();

    await act(async () => {
      formRef.current?.submit();
    });

    expect(onValidate).toHaveBeenCalledWith(formData, null);
  });

  it("should validate the formData - invalid", async () => {
    const formRef = React.createRef<HTMLFormElement>();
    const onValidate = jest.fn();
    const formData = { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "p" };

    const { findByTestId } = render(
      <DmnForm {...props} formRef={formRef} onValidate={onValidate} formData={formData} />
    );

    expect(await findByTestId("dmn-form")).toMatchSnapshot();

    await act(async () => {
      formRef.current?.submit();
    });

    expect(onValidate).toHaveBeenCalledTimes(2);
  });

  it("should have placeholder", () => {
    const schema: any = {
      $ref: "#/definitions/InputSet",
      definitions: {
        InputSet: {
          type: "object",
          properties: {
            name: { type: "string", enum: ["Tooling", "Kogito"] },
          },
        },
      },
    };

    const formRef = React.createRef<HTMLFormElement>();
    const formData = {};

    const { getByText } = render(
      <DmnForm {...props} placeholder={true} formSchema={schema} formRef={formRef} formData={formData} />
    );

    expect(getByText(dmnFormI18n.getCurrent().form.preProcessing.selectPlaceholder)).toMatchSnapshot();
  });

  it("should create a text field and a label", () => {
    const schema: any = {
      $ref: "#/definitions/InputSet",
      definitions: {
        InputSet: {
          type: "object",
          properties: {
            name: {},
          },
        },
      },
    };

    const formRef = React.createRef<HTMLFormElement>();
    const formData = {};

    const { getByText } = render(
      <DmnForm {...props} placeholder={true} formSchema={schema} formRef={formRef} formData={formData} />
    );

    expect(getByText("name")).toMatchSnapshot();
  });

  it("should remove require parameter", async () => {
    const schema: any = {
      $ref: "#/definitions/InputSet",
      definitions: {
        InputSet: {
          type: "object",
          properties: {
            name: {},
          },
          required: ["name"],
        },
      },
    };

    const formRef = React.createRef<HTMLFormElement>();
    const formData = {};
    const onSubmit = jest.fn();

    const { container } = render(
      <DmnForm
        {...props}
        placeholder={true}
        formSchema={schema}
        onSubmit={onSubmit}
        formRef={formRef}
        formData={formData}
      />
    );

    expect(container).toMatchSnapshot();

    await act(async () => {
      formRef.current?.submit();
    });

    expect(onSubmit).toHaveBeenCalledTimes(1);
  });
});
