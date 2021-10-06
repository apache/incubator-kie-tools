/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { render, waitFor } from "@testing-library/react";
import * as React from "react";
import * as dataApi from "../DmnDevSandboxAppDataApi";
import * as runtimeApi from "../DmnDevSandboxRuntimeApi";
import { DmnFormApp } from "../DmnFormApp";

describe("DmnFormApp", () => {
  const fetchAppData = jest.spyOn(dataApi, "fetchAppData");

  jest.spyOn(runtimeApi, "fetchDmnResult").mockImplementation(async () => Promise.resolve([]));

  it("should render the DmnFormPage", async () => {
    fetchAppData.mockImplementation(async () =>
      Promise.resolve({
        filename: "myModel.dmn",
        modelName: "My Model",
        schema: {},
        formUrl: "formUrl",
        modelUrl: "modelUrl",
        swaggerUIUrl: "swaggerUIUrl",
      })
    );

    const { getByTestId } = render(<DmnFormApp />);

    await waitFor(() => {
      expect(getByTestId("dmn-form-page")).toBeVisible();
    });
  });

  it("should render the DmnFormErrorPage", async () => {
    fetchAppData.mockImplementation(async () => Promise.reject());

    const { getByTestId } = render(<DmnFormApp />);

    await waitFor(() => {
      expect(getByTestId("dmn-form-error-page")).toBeVisible();
    });
  });
});
