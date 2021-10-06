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
import { usingTestingDmnFormI18nContext } from "./testing_utils";

const onOpenOnlineEditor = jest.fn(() => null);
const onOpenSwaggerUI = jest.fn(() => null);

describe("DmnFormToolbar", () => {
  it("should truncate the filename when it is too large", () => {
    const { getByTestId } = render(
      usingTestingDmnFormI18nContext(
        <DmnFormToolbar
          filename={"a_really_really_really_really_large_filename_for_my_model.dmn"}
          onOpenOnlineEditor={onOpenOnlineEditor}
          onOpenSwaggerUI={onOpenSwaggerUI}
        />
      ).wrapper
    );
    expect(getByTestId("text-filename")).toHaveTextContent("a_really_really_really_re... .dmn");
  });

  it("should not truncate the filename when it is small enough", () => {
    const { getByTestId } = render(
      usingTestingDmnFormI18nContext(
        <DmnFormToolbar
          filename={"my_model.dmn"}
          onOpenOnlineEditor={onOpenOnlineEditor}
          onOpenSwaggerUI={onOpenSwaggerUI}
        />
      ).wrapper
    );
    expect(getByTestId("text-filename")).toHaveTextContent("my_model.dmn");
  });
});
