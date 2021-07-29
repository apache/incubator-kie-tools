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
import { EDIT_EXPRESSION_NAME, updateElementViaPopover, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextEntryInfoCell } from "../../../components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";
import { DataType } from "../../../api";
import { DataRecord } from "react-table";

jest.useFakeTimers();

describe("ContextEntryInfoCell tests", () => {
  const name = "Expression Name";
  const newValue = "New Value";
  const dataType = DataType.Boolean;
  const emptyExpression = { name, dataType };
  const onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void = (rowIndex, updatedRow) =>
    _.identity({ rowIndex, updatedRow });

  test("should show a context entry info cell element with passed name and dataType, when rendering it", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryInfoCell
          data={[
            { entryInfo: { name, dataType }, entryExpression: emptyExpression, editInfoPopoverLabel: "Edit entry" },
          ]}
          row={{ index: 0 }}
          column={{ id: "col1" }}
          onRowUpdate={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".context-entry-info-cell")).toBeTruthy();
    expect(container.querySelector(".context-entry-info-cell .entry-info")).toBeTruthy();
    expect(container.querySelector(".context-entry-info-cell .entry-info .entry-definition")).toBeTruthy();
    expect(container.querySelector(".context-entry-info-cell .entry-info .entry-definition .entry-name")).toContainHTML(
      name
    );
    expect(
      container.querySelector(".context-entry-info-cell .entry-info .entry-definition .entry-data-type")
    ).toContainHTML(dataType);
  });

  test("should trigger onRowUpdate function when something in the context entry info changes", async () => {
    const mockedOnRowUpdate = jest.fn(onRowUpdate);

    const { container, baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryInfoCell
          data={[
            { entryInfo: { name, dataType }, entryExpression: emptyExpression, editInfoPopoverLabel: "Edit entry" },
          ]}
          row={{ index: 0 }}
          column={{ id: "col1" }}
          onRowUpdate={mockedOnRowUpdate}
        />
      ).wrapper
    );

    await updateElementViaPopover(
      container.querySelector(".entry-definition") as HTMLTableHeaderCellElement,
      baseElement,
      EDIT_EXPRESSION_NAME,
      newValue
    );

    expect(mockedOnRowUpdate).toHaveBeenCalled();
    expect(mockedOnRowUpdate).toHaveBeenCalledWith(0, {
      entryInfo: {
        name: newValue,
        dataType,
      },
      entryExpression: emptyExpression,
      editInfoPopoverLabel: "Edit entry",
    });
  });
});
