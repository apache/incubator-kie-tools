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
import { LandingPage } from "../../../../../editor/components/LandingPage/templates";
import { Provider } from "react-redux";
import { createStore, Store } from "redux";
import { PMML, Scorecard, TreeModel } from "@kogito-tooling/pmml-editor-marshaller";

const PATH: string = "path";

describe("LandingPage", () => {
  test("render::No Models", () => {
    const pmml: PMML = { version: "1.0", DataDictionary: { DataField: [] }, Header: {} };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId } = render(
      <Provider store={store}>
        <LandingPage path={PATH} />
      </Provider>
    );
    expect(getByTestId("landing-page")).toMatchSnapshot();
    expect(getByTestId("empty-state-no-models")).not.toBeUndefined();
  });

  test("render::With Models", () => {
    const pmml: PMML = {
      version: "1.0",
      DataDictionary: { DataField: [] },
      Header: {},
      models: [
        new Scorecard({
          functionName: "regression",
          MiningSchema: { MiningField: [] },
          Characteristics: { Characteristic: [] }
        })
      ]
    };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId, getAllByTestId } = render(
      <Provider store={store}>
        <LandingPage path={PATH} />
      </Provider>
    );
    expect(getByTestId("landing-page")).toMatchSnapshot();
    expect(getAllByTestId("landing-page__model-card").length).toBe(1);
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
          Characteristics: { Characteristic: [] }
        })
      ]
    };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId, getAllByTestId } = render(
      <Provider store={store}>
        <LandingPage path={PATH} />
      </Provider>
    );
    expect(getAllByTestId("landing-page__model-card").length).toBe(1);

    const input: HTMLInputElement = getByTestId("landing-page-toolbar__model-filter") as HTMLInputElement;

    fireEvent.change(input, { target: { value: "spam" } });
    fireEvent.keyDown(input, { key: "Enter" });

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
          Node: {}
        })
      ]
    };
    const store: Store = createStore((state, action) => state, pmml);

    const { getByTestId, getAllByTestId } = render(
      <Provider store={store}>
        <LandingPage path={PATH} />
      </Provider>
    );
    expect(getAllByTestId("landing-page__model-card").length).toBe(1);

    const input: HTMLInputElement = getByTestId("landing-page-toolbar__supported-models") as HTMLInputElement;

    fireEvent.click(input);

    expect(getByTestId("empty-state-no-models")).not.toBeUndefined();
  });
});
