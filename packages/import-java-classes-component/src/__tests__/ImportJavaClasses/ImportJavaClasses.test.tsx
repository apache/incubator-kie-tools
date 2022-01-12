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
import { fireEvent, render, waitFor } from "@testing-library/react";
import { ImportJavaClasses } from "../../components";
import * as _ from "lodash";

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
    testSearchInput(baseElement, getByText);
    const resetButton = baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement;
    expect(resetButton).toBeInTheDocument();
    resetButton.click();
    expect(baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement).not.toBeInTheDocument();
  });

  test("Should search box with results works", () => {
    const mockedLSPGetClassService = jest.fn((value) => ["com.Book", "com.Author"]);
    lspGetClassServiceMock(mockedLSPGetClassService);
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    testSearchInput(baseElement, getByText);
    testJavaClassSelection(baseElement, false);
    let checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
    fireEvent.click(checkSecondElement);
    checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
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

  test("Should move to second step", async () => {
    lspGetClassServiceMock(jest.fn((value) => ["com.Book", "com.Author", "com.Test"]));
    lspGetClassFieldServiceMock();
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    testSearchInput(baseElement, getByText);
    testJavaClassSelection(baseElement, true);
    await testNextStepFieldsTable(baseElement, getByText);

    expect(baseElement).toMatchSnapshot();
  });

  test("Should move to second step and fetch a Java Class", async () => {
    lspGetClassServiceMock(jest.fn((value) => ["com.Book", "com.Author", "com.Test"]));
    lspGetClassFieldServiceMock();
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    testSearchInput(baseElement, getByText);
    testJavaClassSelection(baseElement, true);
    await testNextStepFieldsTable(baseElement, getByText);

    const fetchButton = getByText('Fetch "Test" class')! as HTMLButtonElement;
    fetchButton.click();

    await waitFor(() => {
      expect(getByText("(Test)")!).toBeInTheDocument();
    });

    expect(baseElement).toMatchSnapshot();
  });

  test("Should move to second step and fetch, remove a Java Class", async () => {
    lspGetClassServiceMock(jest.fn((value) => ["com.Book", "com.Author", "com.Test"]));
    lspGetClassFieldServiceMock();
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    testSearchInput(baseElement, getByText);
    testJavaClassSelection(baseElement, true);
    await testNextStepFieldsTable(baseElement, getByText);
    await testFetchClicked(getByText);

    const backButton = getByText("Back") as HTMLButtonElement;
    fireEvent.click(backButton);
    let checkThirdElement = baseElement.querySelector('[aria-labelledby="com.Test"]')! as HTMLInputElement;
    expect(checkThirdElement).toBeInTheDocument();
    expect(checkThirdElement).toBeChecked();
    fireEvent.click(checkThirdElement);
    checkThirdElement = baseElement.querySelector('[aria-labelledby="com.Test"]')! as HTMLInputElement;
    expect(checkThirdElement).not.toBeInTheDocument();

    const nextButton = getByText("Next") as HTMLButtonElement;
    fireEvent.click(nextButton);
    const fetchButton = getByText('Fetch "Test" class')! as HTMLButtonElement;
    expect(fetchButton).toBeInTheDocument();
  });

  test("Should move to third step", async () => {
    lspGetClassServiceMock(jest.fn((value) => ["com.Book", "com.Author", "com.Test"]));
    lspGetClassFieldServiceMock();
    const { baseElement, getByText } = render(<ImportJavaClasses buttonDisabledStatus={false} />);
    testSearchInput(baseElement, getByText);
    testJavaClassSelection(baseElement, true);
    /* Second Step */
    await testNextStepFieldsTable(baseElement, getByText);
    /* Third Step */
    await testNextStepFieldsTable(baseElement, getByText);

    expect(baseElement).toMatchSnapshot();
  });

  function testSearchInput(baseElement: Element, getByText: (text: string) => HTMLElement) {
    const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
    modalWizardButton.click();
    const inputElement = baseElement.querySelector('[aria-label="Search input"]')! as HTMLInputElement;
    expect(inputElement).toHaveValue("");
    expect(baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement).not.toBeInTheDocument();
    fireEvent.change(inputElement, { target: { value: "test" } });
    expect(inputElement).toHaveValue("test");
    expect(baseElement.querySelector('[aria-label="Reset"]')! as HTMLButtonElement).toBeInTheDocument();
  }

  function testJavaClassSelection(baseElement: Element, hasThirdElement: boolean) {
    const firstElement = baseElement.querySelector('[id="com.Book"]')! as HTMLSpanElement;
    expect(firstElement).toBeInTheDocument();
    const secondElement = baseElement.querySelector('[id="com.Author"]')! as HTMLSpanElement;
    expect(secondElement).toBeInTheDocument();
    if (hasThirdElement) {
      const thirdElement = baseElement.querySelector('[id="com.Test"]')! as HTMLSpanElement;
      expect(thirdElement).toBeInTheDocument();
    }
    let checkFirstElement = baseElement.querySelector('[aria-labelledby="com.Book"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeInTheDocument();
    expect(checkFirstElement).not.toBeChecked();
    let checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
    expect(checkSecondElement).toBeInTheDocument();
    expect(checkSecondElement).not.toBeChecked();
    const checkThirdElement = baseElement.querySelector('[aria-labelledby="com.Test"]')! as HTMLInputElement;
    if (hasThirdElement) {
      expect(checkThirdElement).toBeInTheDocument();
      expect(checkThirdElement).not.toBeChecked();
    }
    fireEvent.click(checkFirstElement);
    checkFirstElement = baseElement.querySelector('[aria-labelledby="com.Book"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeChecked();
    expect(checkSecondElement).not.toBeChecked();
    if (hasThirdElement) {
      expect(checkThirdElement).not.toBeChecked();
    }
    fireEvent.click(checkSecondElement);
    checkSecondElement = baseElement.querySelector('[aria-labelledby="com.Author"]')! as HTMLInputElement;
    expect(checkFirstElement).toBeChecked();
    expect(checkSecondElement).toBeChecked();
    if (hasThirdElement) {
      expect(checkThirdElement).not.toBeChecked();
    }
  }

  async function testNextStepFieldsTable(baseElement: Element, getByText: (text: string) => HTMLElement) {
    const nextButton = getByText("Next") as HTMLButtonElement;
    fireEvent.click(nextButton);
    await waitFor(() => {
      expect(baseElement.querySelector('[aria-label="field-table"]')!).toBeInTheDocument();
    });
    const expandToggle = baseElement.querySelector('[id="expand-toggle0"]')! as HTMLButtonElement;
    expect(expandToggle).toHaveAttribute("aria-expanded", "true");
    fireEvent.click(expandToggle);
    expect(expandToggle).toHaveAttribute("aria-expanded", "false");
    fireEvent.click(expandToggle);
  }

  async function testFetchClicked(getByText: (text: string) => HTMLElement) {
    const fetchButton = getByText('Fetch "Test" class')! as HTMLButtonElement;
    fetchButton.click();

    await waitFor(() => {
      expect(getByText("(Test)")!).toBeInTheDocument();
    });
  }

  function lspGetClassServiceMock(mockedBroadcastDefinition: jest.Mock) {
    window.envelopeMock = _.extend(window.envelopeMock || {}, {
      lspGetClassServiceMocked: (value: string) => mockedBroadcastDefinition(value),
    });
  }

  function lspGetClassFieldServiceMock() {
    window.envelopeMock.lspGetClassFieldsServiceMocked = jest.fn(lspGetClassFieldsServiceMocked);
  }

  const lspGetClassFieldsServiceMocked = async (className: string) => {
    const bookClassFieldsMap = new Map<string, string>();
    bookClassFieldsMap.set("title", "java.lang.String");
    bookClassFieldsMap.set("year", "java.lang.Integer");
    bookClassFieldsMap.set("test", "com.Test");
    const authorClassFieldsMap = new Map<string, string>();
    authorClassFieldsMap.set("name", "java.lang.String");
    authorClassFieldsMap.set("isAlive", "java.lang.Boolean");
    if (className === "com.Book") {
      return bookClassFieldsMap;
    } else if (className === "com.Author") {
      return authorClassFieldsMap;
    } else {
      return new Map<string, string>();
    }
  };
});
