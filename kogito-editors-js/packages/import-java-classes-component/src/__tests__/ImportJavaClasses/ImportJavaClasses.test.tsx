/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { fireEvent, render } from "@testing-library/react";
import { ImportJavaClasses } from "../../components";
import * as _ from "lodash";
import { act } from "react-dom/test-utils";

describe("ImportJavaClasses component tests", () => {
  test("should render ImportJavaClasses Button component", () => {
    const { container } = render(<ImportJavaClasses buttonDisabledStatus={false} />);

    expect(container).toMatchSnapshot();
  });

  test("Should show Modal after clicking on the button", () => {
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
    modalWizardButton.click();

    expect(baseElement).toMatchSnapshot();
  });

  test("Should search box works", () => {
    const mockedLSPGetClassService = jest.fn();
    lspGetClassServiceMock(mockedLSPGetClassService);
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
    modalWizardButton.click();
    const inputElement = baseElement.querySelector('[aria-label="Search input"]')! as HTMLInputElement;
    expect(inputElement).toHaveValue("");
    expect(baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement).not.toBeInTheDocument();
    fireEvent.change(inputElement, { target: { value: "test" } });
    expect(inputElement).toHaveValue("test");
    const resetButton = baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement;
    expect(resetButton).toBeInTheDocument();
    resetButton.click();
    expect(baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement).not.toBeInTheDocument();
  });

  test("Should search box with results works", async () => {
    const mockedLSPGetClassService = jest.fn((value) => ["com.Book", "com.Author"]);
    lspGetClassServiceMock(mockedLSPGetClassService);
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
    modalWizardButton.click();
    const inputElement = baseElement.querySelector('[aria-label="Search input"]')! as HTMLInputElement;
    expect(inputElement).toHaveValue("");
    expect(baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement).not.toBeInTheDocument();
    fireEvent.change(inputElement, { target: { value: "test" } });
    expect(inputElement).toHaveValue("test");
    const firstElement = baseElement.querySelector('[id="com.Book"]')! as HTMLSpanElement;
    expect(firstElement).toBeInTheDocument();
    const secondElement = baseElement.querySelector('[id="com.Author"]')! as HTMLSpanElement;
    expect(secondElement).toBeInTheDocument();
    let checkFirstElement = baseElement.querySelector('[aria-labelledby="com.Book"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeInTheDocument();
    expect(checkFirstElement).not.toBeChecked();
    let checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
    expect(checkSecondElement).toBeInTheDocument();
    expect(checkSecondElement).not.toBeChecked();
    fireEvent.click(checkFirstElement);
    checkFirstElement = baseElement.querySelector('[aria-labelledby="com.Book"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeChecked();
    expect(checkSecondElement).not.toBeChecked();
    await fireEvent.click(checkSecondElement);
    checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeChecked();
    expect(checkSecondElement).toBeChecked();
    await fireEvent.click(checkSecondElement);
    checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeChecked();
    expect(checkSecondElement).not.toBeChecked();

    expect(baseElement).toMatchSnapshot();
  });

  test("Should close Modal after opening it and clicking on the Cancel button", () => {
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
    modalWizardButton.click();
    const cancelButton = getByText("Cancel") as HTMLButtonElement;
    cancelButton.click();

    expect(baseElement).toMatchSnapshot();
  });

  function lspGetClassServiceMock(mockedBroadcastDefinition: jest.Mock) {
    window.envelopeMock = _.extend(window.envelopeMock || {}, {
      lspGetClassServiceMocked: (value: string) => mockedBroadcastDefinition(value),
    });
  }
});
