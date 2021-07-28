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
import { render } from "@testing-library/react";
import * as React from "react";
import { ModelCardIcon } from "@kogito-tooling/pmml-editor/dist/editor/components/LandingPage/atoms";

describe("ModelCardIcon", () => {
  test("render::Undefined", () => {
    const { getByTestId } = render(<ModelCardIcon type={undefined} />);
    const element: HTMLElement = getByTestId("model-card__icon");
    expect(element).toBeInTheDocument();
    expect(element instanceof HTMLImageElement);
    expect((element as HTMLImageElement).src).toContain("card-icon-default.svg");
  });

  test("render::Scorecard", () => {
    const { getByTestId } = render(<ModelCardIcon type={"Scorecard"} />);
    const element: HTMLElement = getByTestId("model-card__icon");
    expect(element).toBeInTheDocument();
    expect(element instanceof HTMLImageElement);
    expect((element as HTMLImageElement).src).toContain("card-icon-scorecard.svg");
  });
});
