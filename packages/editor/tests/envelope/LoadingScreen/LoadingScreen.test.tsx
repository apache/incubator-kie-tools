/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { LoadingScreen } from "@kie-tooling-core/editor/dist/envelope/LoadingScreen";
import { usingEditorEnvelopeI18nContext } from "../utils";

describe("LoadingScreen", () => {
  test("when visible", () => {
    const { container } = render(usingEditorEnvelopeI18nContext(<LoadingScreen loading={true} />).wrapper);
    expect(container).toMatchSnapshot();
  });

  test("when just made not visible", () => {
    const { container } = render(usingEditorEnvelopeI18nContext(<LoadingScreen loading={false} />).wrapper);
    expect(container).toMatchSnapshot();
  });

  test("when not visible after fadeout delay", async () => {
    const { getByTestId, container } = render(
      usingEditorEnvelopeI18nContext(<LoadingScreen loading={false} />).wrapper
    );
    fireEvent.transitionEnd(getByTestId("loading-screen-div"));

    expect(container).toMatchSnapshot();
  });
});
