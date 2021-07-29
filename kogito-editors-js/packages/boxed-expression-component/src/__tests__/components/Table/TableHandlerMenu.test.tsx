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
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { TableHandlerMenu } from "../../../components/Table";
import * as React from "react";
import { TableOperation } from "../../../api";
import * as _ from "lodash";

const menuItem = (item: string) => {
  return "[data-ouia-component-id='expression-table-handler-menu-" + item + "'] span";
};

describe("TableHandlerMenu tests", () => {
  test("should render the passed operations", () => {
    const groupName = "a group";
    const operationName = "insert left";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <TableHandlerMenu
          allowedOperations={[TableOperation.ColumnInsertLeft]}
          handlerConfiguration={[
            {
              group: groupName,
              items: [{ name: operationName, type: TableOperation.ColumnInsertLeft }],
            },
          ]}
          onOperation={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector("div.table-handler-menu")).toBeTruthy();
    expect(container.querySelector("h1.pf-c-menu__group-title")).toBeTruthy();
    expect(container.querySelector("h1.pf-c-menu__group-title")!.innerHTML).toContain(groupName);
    expect(container.querySelector(menuItem(operationName))).toBeTruthy();
    expect(container.querySelector(menuItem(operationName))!.innerHTML).toContain(operationName);
  });

  test("should execute the clicked operation", () => {
    const onOperation = (operation: TableOperation) => _.identity(operation);
    const mockedOnOperation = jest.fn(onOperation);

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <TableHandlerMenu
          allowedOperations={[TableOperation.ColumnInsertLeft]}
          handlerConfiguration={[
            {
              group: "a group",
              items: [{ name: "insert left", type: TableOperation.ColumnInsertLeft }],
            },
          ]}
          onOperation={mockedOnOperation}
        />
      ).wrapper
    );

    (container.querySelector(menuItem("insert left")) as HTMLSpanElement)!.click();
    expect(mockedOnOperation).toHaveBeenCalled();
    expect(mockedOnOperation).toHaveBeenCalledWith(TableOperation.ColumnInsertLeft);
  });
});
