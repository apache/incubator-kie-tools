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

import "../../__mocks__/ReactWithSupervisor";
import { render } from "@testing-library/react";
import { checkEntryContent, contextEntry, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { DataType, LogicType } from "../../../api";
import * as React from "react";
import { InvocationExpression } from "../../../components/InvocationExpression";

describe("InvocationExpression tests", () => {
  test("should show a table with two levels visible header, with one row and two columns", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<InvocationExpression logicType={LogicType.Invocation} />).wrapper
    );

    expect(container.querySelector(".invocation-expression")).toBeTruthy();
    expect(container.querySelector(".invocation-expression table")).toBeTruthy();
    expect(container.querySelector(".invocation-expression table thead")).toBeTruthy();
    expect(container.querySelectorAll(".invocation-expression table thead tr")).toHaveLength(2);
    expect(container.querySelectorAll(".invocation-expression table tbody tr")).toHaveLength(1);
    expect(container.querySelectorAll(".invocation-expression table tbody td.data-cell")).toHaveLength(2);
  });

  test("should show a table with an input in the header, representing the invoked function", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<InvocationExpression logicType={LogicType.Invocation} />).wrapper
    );

    expect(
      container.querySelector(".invocation-expression table thead th .function-definition-container")
    ).toBeTruthy();
    expect(
      container.querySelector(".invocation-expression table thead th .function-definition-container")
    ).toContainHTML(
      `<input class="function-definition pf-u-text-truncate" type="text" placeholder="Enter function" value="">`
    );
  });

  test("should display the value of the passed invoked function", () => {
    const invokedFunction = "Math.max";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <InvocationExpression logicType={LogicType.Invocation} invokedFunction={invokedFunction} />
      ).wrapper
    );

    expect((container.querySelector(".invocation-expression .function-definition") as HTMLInputElement).value).toBe(
      invokedFunction
    );
  });

  test("should display a row in the table body, for each given binding entries", () => {
    const firstEntryName = "param1";
    const firstEntryDataType = DataType.Boolean;
    const firstEntry = {
      entryInfo: { name: firstEntryName, dataType: firstEntryDataType },
      entryExpression: {},
      editInfoPopoverLabel: "Edit parameter",
    };
    const secondEntryName = "param2";
    const secondEntryDataType = DataType.Any;
    const secondEntry = {
      entryInfo: { name: secondEntryName, dataType: secondEntryDataType },
      entryExpression: {},
      editInfoPopoverLabel: "Edit parameter",
    };
    const bindingEntries = [firstEntry, secondEntry];

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <InvocationExpression logicType={LogicType.Invocation} bindingEntries={bindingEntries} />
      ).wrapper
    );

    expect(container.querySelector(".invocation-expression")).toBeTruthy();
    expect(container.querySelectorAll(".invocation-expression table tbody tr")).toHaveLength(2);
    checkEntryContent(contextEntry(container, 1), firstEntry.entryInfo);
    checkEntryContent(contextEntry(container, 2), secondEntry.entryInfo);
  });
});

jest.mock("../../../api", () => ({
  ...(jest.requireActual("../../../api") as Record<string, unknown>),
  getHandlerConfiguration: jest.fn(),
}));
