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

import { render } from "@testing-library/react";
import * as React from "react";
import { DmnFormToolbar } from "../DmnFormToolbar";
import { usingTestingAppContext, usingTestingDmnFormI18nContext } from "./testing_utils";

describe("DmnFormToolbar", () => {
  it("should truncate the filename when it is too large", () => {
    const uri = "/a_really_really_really_really_large_filename_for_my_model.dmn";
    const { getByTestId } = render(
      usingTestingDmnFormI18nContext(
        usingTestingAppContext(<DmnFormToolbar uri={uri} />, {
          data: {
            baseUrl: "",
            forms: [{ uri: uri, modelName: "myModel", schema: {} }],
          },
        }).wrapper
      ).wrapper
    );
    expect(getByTestId("text-filename")).toHaveTextContent("a_really_really_really_re... .dmn");
  });

  it("should not truncate the filename when it is small enough", () => {
    const uri = "/my_model.dmn";
    const { getByTestId } = render(
      usingTestingDmnFormI18nContext(
        usingTestingAppContext(<DmnFormToolbar uri={uri} />, {
          data: {
            baseUrl: "",
            forms: [{ uri: uri, modelName: "myModel", schema: {} }],
          },
        }).wrapper
      ).wrapper
    );
    expect(getByTestId("text-filename")).toHaveTextContent("my_model.dmn");
  });
});
