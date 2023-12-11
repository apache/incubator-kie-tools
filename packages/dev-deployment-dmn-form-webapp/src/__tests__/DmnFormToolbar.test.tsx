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

import * as React from "react";
import { render } from "@testing-library/react";
import { DmnFormToolbar } from "../DmnFormToolbar";
import { usingTestingAppContext, usingTestingDmnFormI18nContext } from "./testing_utils";

describe("DmnFormToolbar", () => {
  it("should truncate the model name when it is too large", () => {
    const modelName = "a_really_really_really_really_large_model_name_for_my_model";
    const { getByTestId } = render(
      usingTestingDmnFormI18nContext(
        usingTestingAppContext(<DmnFormToolbar modelName={modelName} />, {
          data: {
            forms: [{ modelName, schema: {} }],
            baseOrigin: "http://localhost",
            basePath: "",
          },
        }).wrapper
      ).wrapper
    );
    expect(getByTestId("text-model-name")).toHaveTextContent("a_really_really_really_really_la...");
  });

  it("should not truncate the model name when it is small enough", () => {
    const modelName = "my_model";
    const { getByTestId } = render(
      usingTestingDmnFormI18nContext(
        usingTestingAppContext(<DmnFormToolbar modelName={modelName} />, {
          data: {
            forms: [{ modelName, schema: {} }],
            baseOrigin: "http://localhost",
            basePath: "",
          },
        }).wrapper
      ).wrapper
    );
    expect(getByTestId("text-model-name")).toHaveTextContent("my_model");
  });
});
