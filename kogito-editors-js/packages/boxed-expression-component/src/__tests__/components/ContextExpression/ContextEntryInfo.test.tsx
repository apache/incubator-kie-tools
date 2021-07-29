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

import { DataType } from "../../../api";
import { EDIT_EXPRESSION_NAME, updateElementViaPopover, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { render } from "@testing-library/react";
import { ContextEntryInfo } from "../../../components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";

jest.useFakeTimers();

describe("ContextEntryInfo tests", () => {
  const name = "Expression Name";
  const newValue = "New Value";
  const dataType = DataType.Boolean;

  test("should show a context entry info element with passed name and dataType, when rendering it", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryInfo
          name={name}
          dataType={dataType}
          editInfoPopoverLabel="Edit entry"
          onContextEntryUpdate={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".entry-info")).toBeTruthy();
    expect(container.querySelector(".entry-info .entry-definition")).toBeTruthy();
    expect(container.querySelector(".entry-info .entry-definition .entry-name")).toContainHTML(name);
    expect(container.querySelector(".entry-info .entry-definition .entry-data-type")).toContainHTML(dataType);
  });

  test("should call the onContextEntryUpdate callback when one of its prop changes", async () => {
    const onContextEntryUpdate: (name: string, dataType: DataType) => void = (name, dataType) =>
      _.identity({ name, dataType });
    const mockedOnContextEntryUpdate = jest.fn(onContextEntryUpdate);

    const { container, baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryInfo
          name={name}
          dataType={dataType}
          editInfoPopoverLabel="Edit entry"
          onContextEntryUpdate={mockedOnContextEntryUpdate}
        />
      ).wrapper
    );

    await updateElementViaPopover(
      container.querySelector(".entry-definition") as HTMLTableHeaderCellElement,
      baseElement,
      EDIT_EXPRESSION_NAME,
      newValue
    );

    expect(mockedOnContextEntryUpdate).toHaveBeenCalled();
    expect(mockedOnContextEntryUpdate).toHaveBeenCalledWith(newValue, dataType);
  });
});
