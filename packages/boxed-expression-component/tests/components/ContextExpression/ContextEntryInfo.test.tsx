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

import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { EDIT_EXPRESSION_NAME, updateElementViaPopover, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { render } from "@testing-library/react";
import { ContextEntryInfo } from "@kie-tools/boxed-expression-component/dist/components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";
import { ExpressionDefinition } from "../../../src/api";

jest.useFakeTimers();

describe("ContextEntryInfo tests", () => {
  const id = "id1";
  const name = "Expression Name";
  const dataType = DmnBuiltInDataType.Boolean;

  test("should show a context entry info element with passed name and dataType, when rendering it", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryInfo
          id={id}
          name={name}
          dataType={dataType}
          editInfoPopoverLabel="Edit entry"
          onContextEntryUpdate={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".entry-info")).toBeTruthy();
    expect(container.querySelector(".entry-info")).toHaveClass(id);
    expect(container.querySelector(".entry-info .entry-definition")).toBeTruthy();
    expect(container.querySelector(".entry-info .entry-definition .entry-name")).toContainHTML(name);
    expect(container.querySelector(".entry-info .entry-definition .entry-data-type")).toContainHTML(dataType);
  });

  test("should call the onContextEntryUpdate callback when one of its prop changes", async () => {
    const onContextEntryUpdate: (args: Pick<ExpressionDefinition, "name" | "dataType">) => void = (args) =>
      _.identity(args);
    const mockedOnContextEntryUpdate = jest.fn(onContextEntryUpdate);

    const { container, baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryInfo
          id={id}
          name={name}
          dataType={dataType}
          editInfoPopoverLabel="Edit entry"
          onContextEntryUpdate={mockedOnContextEntryUpdate}
        />
      ).wrapper
    );

    const newName = "New Value";
    await updateElementViaPopover(
      container.querySelector(".entry-definition") as HTMLTableCellElement,
      baseElement,
      EDIT_EXPRESSION_NAME,
      newName
    );

    expect(mockedOnContextEntryUpdate).toHaveBeenCalled();
    expect(mockedOnContextEntryUpdate).toHaveBeenCalledWith({ name: newName, dataType });
  });
});
