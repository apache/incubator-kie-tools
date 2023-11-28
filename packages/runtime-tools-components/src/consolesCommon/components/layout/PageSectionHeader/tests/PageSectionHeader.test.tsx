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

import React from "react";
import { render, screen } from "@testing-library/react";
import { PageSectionHeader } from "../PageSectionHeader";
import { BrowserRouter } from "react-router-dom";

describe("PageSectionHeader tests", () => {
  const props = {
    titleText: "Process Details",
    breadcrumbText: ["Home", "Processes"],
    breadcrumbPath: ["/", { pathname: "/ProcessInstances", state: {} }],
  };
  it("Snapshot test with default props", () => {
    const { container } = render(
      <BrowserRouter>
        <PageSectionHeader {...props} />
      </BrowserRouter>
    );
    expect(container).toMatchSnapshot();
  });
});
