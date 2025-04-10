/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { LandingPage } from "@kie-tools/pmml-editor/dist/editor/components/LandingPage/templates";
import { Provider } from "react-redux";
import { createStore, Store } from "redux";
import { PMML, Scorecard, TreeModel } from "@kie-tools/pmml-editor-marshaller";
import { BrowserRouter } from "react-router-dom";

const PATH: string = "path";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useHistory: () => ({
    push: jest.fn(),
  }),
}));

describe("LandingPage", () => {
  test("render::No Models", () => {
    const pmml: PMML = { version: "1.0", DataDictionary: { DataField: [] }, Header: {} };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId } = render(
      <Provider store={store}>
        <BrowserRouter>
          <LandingPage path={PATH} />
        </BrowserRouter>
      </Provider>
    );
    expect(getByTestId("landing-page")).toMatchSnapshot();
    expect(getByTestId("empty-state-no-models")).not.toBeUndefined();
  });

  test("render::With Supported Model", () => {
    const pmml: PMML = {
      version: "1.0",
      DataDictionary: { DataField: [] },
      Header: {},
      models: [
        new Scorecard({
          functionName: "regression",
          MiningSchema: { MiningField: [] },
          Characteristics: { Characteristic: [] },
        }),
      ],
    };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId, getAllByTestId } = render(
      <Provider store={store}>
        <BrowserRouter>
          <LandingPage path={PATH} />
        </BrowserRouter>
      </Provider>
    );
    expect(getByTestId("landing-page")).toMatchSnapshot();
    expect(getAllByTestId("landing-page__model-card").length).toBe(1);
    expect(() => getAllByTestId("landing-page-toolbar__supported-models")).toThrow(
      'Unable to find an element by: [data-testid="landing-page-toolbar__supported-models"]'
    );
  });

  test("render::With Supported Model::Filter", () => {
    const pmml: PMML = {
      version: "1.0",
      DataDictionary: { DataField: [] },
      Header: {},
      models: [
        new Scorecard({
          modelName: "cheese",
          functionName: "regression",
          MiningSchema: { MiningField: [] },
          Characteristics: { Characteristic: [] },
        }),
      ],
    };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId, getAllByTestId } = render(
      <Provider store={store}>
        <BrowserRouter>
          <LandingPage path={PATH} />
        </BrowserRouter>
      </Provider>
    );
    expect(getAllByTestId("landing-page__model-card").length).toBe(1);

    const element1: HTMLElement = getByTestId("landing-page-toolbar__model-filter");
    const element2: HTMLElement = getByTestId("landing-page-toolbar__submit");
    expect(element1).toBeInstanceOf(HTMLInputElement);
    expect(element2).toBeInstanceOf(HTMLButtonElement);

    const input: HTMLInputElement = element1 as HTMLInputElement;
    const submit: HTMLButtonElement = element2 as HTMLButtonElement;

    fireEvent.change(input, { target: { value: "spam" } });
    submit.click();

    expect(getByTestId("empty-state-no-models")).not.toBeUndefined();
  });

  test("render::With Unsupported Model::Filter", () => {
    const pmml: PMML = {
      version: "1.0",
      DataDictionary: { DataField: [] },
      Header: {},
      models: [
        new TreeModel({
          missingValueStrategy: "none",
          functionName: "regression",
          MiningSchema: { MiningField: [] },
          Node: {},
        }),
      ],
    };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId, getAllByTestId } = render(
      <Provider store={store}>
        <BrowserRouter>
          <LandingPage path={PATH} />
        </BrowserRouter>
      </Provider>
    );
    expect(getAllByTestId("landing-page__model-card").length).toBe(1);

    const input: HTMLInputElement = getByTestId("landing-page-toolbar__supported-models") as HTMLInputElement;

    fireEvent.click(input);

    expect(getByTestId("empty-state-no-models")).not.toBeUndefined();
  });
});
/**/
