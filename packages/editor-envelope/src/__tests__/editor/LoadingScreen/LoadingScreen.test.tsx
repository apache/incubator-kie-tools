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
import { LoadingScreen } from "../../../editor/LoadingScreen";

describe("LoadingScreen", () => {
  test("when visible", () => {
    const { container } = render(<LoadingScreen visible={true} />);
    expect(container).toMatchSnapshot();
  });

  test("when just made not visible", () => {
    const { container } = render(<LoadingScreen visible={false} />);
    expect(container).toMatchSnapshot();
  });

  test("when not visible after fadeout delay", async () => {
    const { getByTestId, container } = render(<LoadingScreen visible={false} />);
    fireEvent.transitionEnd(getByTestId("loading-screen-div"));

    expect(container).toMatchSnapshot();
  });
});
