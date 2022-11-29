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
import {
  checkEntryContent,
  checkEntryLogicType,
  checkEntryStyle,
  contextEntry,
  usingTestingBoxedExpressionI18nContext,
  usingTestingBoxedExpressionProviderContext,
} from "../test-utils";
import { ContextExpression } from "@kie-tools/boxed-expression-component/dist/components/ContextExpression";
import * as React from "react";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType } from "@kie-tools/boxed-expression-component/dist/api";

describe("ContextExpression tests", () => {
  const name = "contextName";
  const dataType = DmnBuiltInDataType.Boolean;
  test("should show a table with two rows: two context entries, where last is representing the result", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(
          <ContextExpression logicType={ExpressionDefinitionLogicType.Context} name={name} dataType={dataType} />
        ).wrapper
      ).wrapper
    );

    expect(container.querySelector(".context-expression")).toBeTruthy();
    expect(container.querySelector(".context-expression table")).toBeTruthy();
    expect(container.querySelectorAll(".context-expression table tbody tr")).toHaveLength(2);
    expect(container.querySelector(".context-expression table tbody tr:first-of-type")).toContainHTML("ContextEntry-1");
    expect(container.querySelector(".context-expression table tbody tr:last-of-type")).toContainHTML("result");
  });

  test("should show a table with one row for each passed entry, plus the passed entry result", () => {
    const firstEntryId = "id1";
    const firstEntry = "first entry";
    const firstDataType = DmnBuiltInDataType.Boolean;
    const firstExpression = {
      name: "expressionName",
      dataType: DmnBuiltInDataType.Any,
      logicType: ExpressionDefinitionLogicType.LiteralExpression,
    };
    const secondEntryId = "id2";
    const secondEntry = "second entry";
    const secondDataType = DmnBuiltInDataType.Date;
    const secondExpression = { name: "anotherName", dataType: DmnBuiltInDataType.Undefined };
    const resultEntry = "result entry";
    const resultDataType = DmnBuiltInDataType.Undefined;

    const contextEntries = [
      {
        entryInfo: {
          id: firstEntryId,
          name: firstEntry,
          dataType: firstDataType,
        },
        entryExpression: firstExpression,
        editInfoPopoverLabel: "Edit entry",
      },
      {
        entryInfo: {
          id: secondEntryId,
          name: secondEntry,
          dataType: secondDataType,
        },
        entryExpression: secondExpression,
        editInfoPopoverLabel: "Edit entry",
      },
    ];

    const result = {
      name: resultEntry,
      dataType: resultDataType,
      expression: {},
    };

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(
          <ContextExpression
            logicType={ExpressionDefinitionLogicType.Context}
            name={name}
            dataType={dataType}
            contextEntries={contextEntries}
            result={result}
          />
        ).wrapper
      ).wrapper
    );

    expect(container.querySelector(".context-expression")).toBeTruthy();
    expect(container.querySelector(".context-expression table")).toBeTruthy();
    expect(container.querySelectorAll(".context-expression table tbody tr")).toHaveLength(3);

    checkEntryContent(contextEntry(container, 1), { id: firstEntryId, name: firstEntry, dataType: firstDataType });
    checkEntryContent(contextEntry(container, 2), { id: secondEntryId, name: secondEntry, dataType: secondDataType });
    checkEntryContent(contextEntry(container, 3), { name: "result", dataType: "" });

    checkEntryStyle(contextEntry(container, 1), "logic-type-selected");
    checkEntryLogicType(contextEntry(container, 1), "literal-expression");
    checkEntryStyle(contextEntry(container, 2), "logic-type-not-present");
    checkEntryStyle(contextEntry(container, 3), "logic-type-not-present");
  });
});

jest.mock("@kie-tools/boxed-expression-component/dist/api", () => ({
  ...(jest.requireActual("@kie-tools/boxed-expression-component/dist/api") as Record<string, unknown>),
  getHandlerConfiguration: jest.fn(),
}));
