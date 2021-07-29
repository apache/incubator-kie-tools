/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { render } from "@testing-library/react";
import { activateSelector, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { LogicType } from "../../../api";
import * as React from "react";
import { PMMLLiteralExpression } from "../../../components/LiteralExpression";

jest.useFakeTimers();

describe("PMMLLiteralExpression tests", () => {
  test("should show noOptionsLabel when no options are available", async () => {
    const noOptionsLabel = "no options label";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <PMMLLiteralExpression
          logicType={LogicType.PMMLLiteralExpression}
          getOptions={() => []}
          noOptionsLabel={noOptionsLabel}
        />
      ).wrapper
    );

    expect(container.querySelector(".pmml-literal-expression")).toBeTruthy();
    expect(container.querySelector(".pmml-literal-expression button")).toContainHTML(noOptionsLabel);
  });

  test("should show noOptionsLabel when selected option is not present in the options list", async () => {
    const noOptionsLabel = "no options label";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <PMMLLiteralExpression
          logicType={LogicType.PMMLLiteralExpression}
          getOptions={() => ["a", "b", "c"]}
          selected="selected"
          noOptionsLabel={noOptionsLabel}
        />
      ).wrapper
    );

    expect(container.querySelector(".pmml-literal-expression")).toBeTruthy();
    expect(container.querySelector(".pmml-literal-expression button")).toContainHTML(noOptionsLabel);
  });

  test("should show selected option when it is passed and it is present in the options list", async () => {
    const selectedOption = "selected";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <PMMLLiteralExpression
          logicType={LogicType.PMMLLiteralExpression}
          getOptions={() => ["a", "b", "c", selectedOption]}
          selected={selectedOption}
          noOptionsLabel={"no options"}
        />
      ).wrapper
    );

    expect(container.querySelector(".pmml-literal-expression")).toBeTruthy();
    expect(container.querySelector(".pmml-literal-expression button")).toContainHTML(selectedOption);
  });

  test("should change the selected option when the user manually select it", async () => {
    const changedOption = "changed";
    const selectedOption = "selected";

    const { baseElement, container } = render(
      usingTestingBoxedExpressionI18nContext(
        <PMMLLiteralExpression
          logicType={LogicType.PMMLLiteralExpression}
          getOptions={() => [changedOption, "a", "b", "c", selectedOption]}
          selected={selectedOption}
          noOptionsLabel={"no options"}
        />
      ).wrapper
    );

    await activateSelector(container as HTMLElement, ".pmml-literal-expression button");
    (baseElement.querySelector(`[data-ouia-component-id='${changedOption}']`) as HTMLButtonElement).click();

    expect(container.querySelector(".pmml-literal-expression")).toBeTruthy();
    expect(container.querySelector(".pmml-literal-expression button")).toContainHTML(changedOption);
  });
});
