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
import * as React from "react";
import { FunctionKindSelector } from "@kie-tools/boxed-expression-component/dist/components/FunctionExpression";
import { FunctionExpressionDefinitionKind } from "@kie-tools/boxed-expression-component/dist/api";
import * as _ from "lodash";

jest.useFakeTimers();

describe("FunctionKindSelector tests", () => {
  test("should render passed Function Kind property", async () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <FunctionKindSelector
          selectedFunctionKind={FunctionExpressionDefinitionKind.Feel}
          onFunctionKindSelect={_.identity}
        />
      ).wrapper
    );

    await activateSelector(container as HTMLElement, ".selected-function-kind");

    expect(container.querySelector(".selected-function-kind")).toBeTruthy();
    expect(container.querySelector(".selected-function-kind")).toContainHTML("F");
  });

  test("should trigger the Function Kind which the user has selected", async () => {
    const onFunctionKindSelect = (functionKind: FunctionExpressionDefinitionKind) => _.identity(functionKind);
    const mockedFunctionKindSelect = jest.fn(onFunctionKindSelect);

    const { container, baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <FunctionKindSelector
          selectedFunctionKind={FunctionExpressionDefinitionKind.Feel}
          onFunctionKindSelect={mockedFunctionKindSelect}
        />
      ).wrapper
    );

    await activateSelector(container as HTMLElement, ".selected-function-kind");
    await activateSelector(baseElement as HTMLElement, "[data-ouia-component-id='Java'] > button");

    expect(mockedFunctionKindSelect).toHaveBeenCalled();
    expect(mockedFunctionKindSelect).toHaveBeenCalledWith(FunctionExpressionDefinitionKind.Java);
  });
});
