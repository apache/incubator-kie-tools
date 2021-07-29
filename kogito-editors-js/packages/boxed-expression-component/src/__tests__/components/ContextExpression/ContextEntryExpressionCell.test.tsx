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

import { DataType, LogicType } from "../../../api";
import { render } from "@testing-library/react";
import { flushPromises, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextEntryExpressionCell } from "../../../components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";
import { DataRecord } from "react-table";
import { act } from "react-dom/test-utils";

jest.useFakeTimers();

describe("ContextEntryExpressionCell tests", () => {
  const name = "Expression Name";
  const dataType = DataType.Boolean;
  const emptyExpression = { name, dataType };
  const entryName = "entry name";
  const entryDataType = DataType.Date;

  const value = "value";
  const rowIndex = 0;
  const columnId = "col1";
  const onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void = (rowIndex, updatedRow) =>
    _.identity({ rowIndex, updatedRow });

  test("should show a context entry expression cell with logic type not selected", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryExpressionCell
          data={[
            {
              entryInfo: { name: entryName, dataType: entryDataType },
              entryExpression: emptyExpression,
              editInfoPopoverLabel: "Edit entry",
            },
          ]}
          row={{ index: 0 }}
          column={{ id: "col1" }}
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
              entryInfo: { name: value, dataType },
              entryExpression: emptyExpression,
              editInfoPopoverLabel: "Edit entry",
            },
          ]}
          row={{ index: rowIndex }}
          column={{ id: columnId }}
          onRowUpdate={mockedOnRowUpdate}
        />
      ).wrapper
    );

    (container.querySelector(".entry-expression")! as HTMLDivElement).click();
    await act(async () => {
      await flushPromises();
      jest.runAllTimers();
      Array.from(baseElement.querySelectorAll("button"))
        .find((el) => el.textContent === LogicType.LiteralExpression)!
        .click();
    });

    expect(mockedOnRowUpdate).toHaveBeenCalled();
    expect(mockedOnRowUpdate).toHaveBeenCalledWith(rowIndex, {
      entryInfo: {
        name: value,
        dataType,
      },
      entryExpression: {
        uid: "id1",
        logicType: LogicType.LiteralExpression,
        name,
        dataType,
        onUpdatingNameAndDataType: undefined,
        onUpdatingRecursiveExpression: expect.any(Function),
      },
      editInfoPopoverLabel: "Edit entry",
    });
  });
});
