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

import { DmnBuiltInDataType, ExpressionDefinitionLogicType } from "@kie-tools/boxed-expression-component/dist/api";
import { render } from "@testing-library/react";
import { flushPromises, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextEntryExpressionCell } from "@kie-tools/boxed-expression-component/dist/components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";
import * as ReactTable from "react-table";
import { act } from "react-dom/test-utils";

jest.useFakeTimers();

describe("ContextEntryExpressionCell tests", () => {
  const name = "Expression Name";
  const dataType = DmnBuiltInDataType.Boolean;
  const emptyExpression = { name, dataType };
  const entryId = "entry-id1";
  const entryName = "entry name";
  const entryDataType = DmnBuiltInDataType.Date;

  const value = "value";
  const rowIndex = 0;
  const onRowUpdate: (rowIndex: number, updatedRow: ROWGENERICTYPE) => void = (rowIndex, updatedRow) =>
    _.identity({ rowIndex, updatedRow });

  test("should show a context entry expression cell with logic type not selected", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryExpressionCell
          data={[
            {
              entryInfo: { id: entryId, name: entryName, dataType: entryDataType },
              entryExpression: emptyExpression,
            },
          ]}
          rowIndex={0}
          columnId={"col1"}
          onRowUpdate={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".context-entry-expression-cell")).toBeTruthy();
    expect(container.querySelector(".entry-expression .logic-type-selector")).toHaveClass("logic-type-not-present");
  });

  test("should trigger onRowUpdate function when something in the context entry expression changes", async () => {
    const mockedOnRowUpdate = jest.fn(onRowUpdate);

    const { container, baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryExpressionCell
          data={[
            {
              entryInfo: { id: entryId, name: value, dataType },
              entryExpression: emptyExpression,
            },
          ]}
          rowIndex={rowIndex}
          columnId={"columnId"}
          onRowUpdate={mockedOnRowUpdate}
        />
      ).wrapper
    );

    (container.querySelector(".entry-expression")! as HTMLDivElement).click();
    await act(async () => {
      await flushPromises();
      jest.runAllTimers();
      Array.from(baseElement.querySelectorAll("button"))
        .find((el) => el.textContent === ExpressionDefinitionLogicType.LiteralExpression)!
        .click();
    });

    expect(mockedOnRowUpdate).toHaveBeenCalled();
    expect(mockedOnRowUpdate).toHaveBeenCalledWith(rowIndex, {
      entryInfo: {
        id: entryId,
        name: value,
        dataType,
      },
      entryExpression: {
        logicType: ExpressionDefinitionLogicType.LiteralExpression,
        name,
        dataType,
      },
    });
  });
});
