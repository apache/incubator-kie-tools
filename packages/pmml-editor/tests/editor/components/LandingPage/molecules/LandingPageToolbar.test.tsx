/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { LandingPageToolbar } from "@kogito-tooling/pmml-editor/dist/editor/components/LandingPage/molecules";

const setFilter = jest.fn((_filter: string) => {
  /*NOP*/
});

const setShowUnsupportedModels = jest.fn((showUnsupportedModels: boolean) => {
  /*NOP*/
});

describe("LandingPageToolbar", () => {
  test("render::Has unsupported models", () => {
    const { getByTestId } = render(
      <LandingPageToolbar
        onFilter={setFilter}
        hasUnsupportedModels={true}
        onShowUnsupportedModels={setShowUnsupportedModels}
        showUnsupportedModels={true}
      />
    );
    expect(getByTestId("landing-page-toolbar")).toMatchSnapshot();
  });

  test("render::Has no unsupported models", () => {
    const { getByTestId } = render(
      <LandingPageToolbar
        onFilter={setFilter}
        hasUnsupportedModels={false}
        onShowUnsupportedModels={setShowUnsupportedModels}
        showUnsupportedModels={true}
      />
    );
    expect(getByTestId("landing-page-toolbar")).toMatchSnapshot();
  });

  test("render::Has no unsupported models", () => {
    const { getByTestId } = render(
      <LandingPageToolbar
        onFilter={setFilter}
        hasUnsupportedModels={false}
        onShowUnsupportedModels={setShowUnsupportedModels}
        showUnsupportedModels={true}
      />
    );
    expect(getByTestId("landing-page-toolbar")).toMatchSnapshot();
  });

  test("render::setFilter::Submit", () => {
    const { getByTestId } = render(
      <LandingPageToolbar
        onFilter={setFilter}
        hasUnsupportedModels={false}
        onShowUnsupportedModels={setShowUnsupportedModels}
        showUnsupportedModels={true}
      />
    );
    const element1: HTMLElement = getByTestId("landing-page-toolbar__model-filter");
    const element2: HTMLElement = getByTestId("landing-page-toolbar__submit");
    expect(element1).toBeInstanceOf(HTMLInputElement);
    expect(element2).toBeInstanceOf(HTMLButtonElement);

    const input: HTMLInputElement = element1 as HTMLInputElement;
    const submit: HTMLButtonElement = element2 as HTMLButtonElement;

    fireEvent.change(input, { target: { value: "filter" } });
    submit.click();

    expect(setFilter).toBeCalledWith("filter");
  });

  test("render::setShowUnsupportedModels", () => {
    const { getByTestId } = render(
      <LandingPageToolbar
        onFilter={setFilter}
        hasUnsupportedModels={true}
        onShowUnsupportedModels={setShowUnsupportedModels}
        showUnsupportedModels={true}
      />
    );
    const element1: HTMLElement = getByTestId("landing-page-toolbar__supported-models");
    expect(element1).toBeInstanceOf(HTMLInputElement);

    const input: HTMLInputElement = element1 as HTMLInputElement;

    fireEvent.click(input);

    expect(setShowUnsupportedModels).toBeCalledWith(false, expect.any(Object));
  });
});
