/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { FormDmn, InputRow } from "../src";
import { ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";
import { formDmnI18n } from "../src/i18n";
import { FormComponentProps } from "@kie-tools/form";

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

const props: FormComponentProps<InputRow, ExtendedServicesDmnJsonSchema> = {
  i18n: formDmnI18n.getCurrent(),
  formInputs: {},
  setFormInputs: jest.fn(),
  formError: false,
  setFormError: jest.fn(),
  showInlineError: true,
  notificationsPanel: false,
  autoSave: false,
  placeholder: false,
  onSubmit: jest.fn(),
  onValidate: jest.fn(),
  locale: "en",
  formSchema: schema,
  openValidationTab: jest.fn(),
};

describe("FormDmn tests", () => {
  it("should render the DMN Form", async () => {
    const newProps = { ...props, formInputs: { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "P1D" } };

    const { findByTestId } = render(<FormDmn {...newProps} />);

    expect(await findByTestId("form-base")).toMatchSnapshot();
  });

  it("should submit the formInputs", async () => {
    let formRef: HTMLFormElement;
    const setFormRef = (node: any) => {
      formRef = node;
    };
    const onSubmit = jest.fn();
    const formInputs = { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "P1D" };

    const { findByTestId } = render(
      <FormDmn {...props} onSubmit={onSubmit} setFormRef={setFormRef} formInputs={formInputs} />
    );

    expect(await findByTestId("form-base")).toMatchSnapshot();

    await act(async () => {
      formRef?.submit();
    });

    expect(onSubmit).toHaveBeenCalledWith(formInputs);
  });

  it("shouldn't submit the formInputs", async () => {
    let formRef: HTMLFormElement;
    const setFormRef = (node: any) => {
      formRef = node;
    };
    const onSubmit = jest.fn();
    const formInputs = { daysAndTimeDuration: "p" };

    const { findByTestId } = render(
      <FormDmn {...props} onSubmit={onSubmit} setFormRef={setFormRef} formInputs={formInputs} />
    );

    expect(await findByTestId("form-base")).toMatchSnapshot();

    await act(async () => {
      formRef?.submit();
    });

    expect(onSubmit).toHaveBeenCalledTimes(0);
  });

  it("should validate the formInputs - success", async () => {
    let formRef: HTMLFormElement;
    const setFormRef = (node: any) => {
      formRef = node;
    };
    const onValidate = jest.fn();
    const formInputs = { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "P1D" };

    const { findByTestId } = render(
      <FormDmn {...props} setFormRef={setFormRef} onValidate={onValidate} formInputs={formInputs} />
    );

    expect(await findByTestId("form-base")).toMatchSnapshot();

    await act(async () => {
      formRef?.submit();
    });

    expect(onValidate).toHaveBeenCalledWith(formInputs, null);
  });

  it("should validate the formInputs - invalid", async () => {
    let formRef: HTMLFormElement;
    const setFormRef = (node: any) => {
      formRef = node;
    };
    const onValidate = jest.fn();
    const formInputs = { name: "Kogito", lastName: "Tooling", daysAndTimeDuration: "p" };

    const { findByTestId } = render(
      <FormDmn {...props} setFormRef={setFormRef} onValidate={onValidate} formInputs={formInputs} />
    );

    expect(await findByTestId("form-base")).toMatchSnapshot();

    await act(async () => {
      formRef?.submit();
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

    const formInputs = {};

    const { getByText } = render(<FormDmn {...props} placeholder={true} formSchema={schema} formInputs={formInputs} />);

    expect(getByText(formDmnI18n.getCurrent().schema.selectPlaceholder)).toMatchSnapshot();
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

    let formRef: HTMLFormElement;
    const setFormRef = (node: any) => {
      formRef = node;
    };
    const formInputs = {};

    const { getByText } = render(
      <FormDmn {...props} placeholder={true} formSchema={schema} setFormRef={setFormRef} formInputs={formInputs} />
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

    let formRef: HTMLFormElement;
    const setFormRef = (node: any) => {
      formRef = node;
    };
    const formInputs = {};
    const onSubmit = jest.fn();

    const { container } = render(
      <FormDmn
        {...props}
        placeholder={true}
        formSchema={schema}
        onSubmit={onSubmit}
        setFormRef={setFormRef}
        formInputs={formInputs}
      />
    );

    expect(container).toMatchSnapshot();

    await act(async () => {
      formRef?.submit();
    });

    expect(onSubmit).toHaveBeenCalledTimes(2);
  });
});
