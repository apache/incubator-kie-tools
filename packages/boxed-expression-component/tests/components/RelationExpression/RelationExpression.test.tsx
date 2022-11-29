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

import {
  BeeTableColumn,
  DmnBuiltInDataType,
  generateUuid,
  ExpressionDefinitionLogicType,
  BeeTableRow,
} from "@kie-tools/boxed-expression-component/dist/api";
import { render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext, usingTestingBoxedExpressionProviderContext } from "../test-utils";
import * as React from "react";
import { RelationExpression } from "@kie-tools/boxed-expression-component/dist/components/RelationExpression";

describe("RelationExpression tests", () => {
  test("should render a table element, with one default column and one default row, when no props are passed", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(
          <RelationExpression
            logicType={ExpressionDefinitionLogicType.Relation}
            name="Relation"
            dataType={DmnBuiltInDataType.Undefined}
          />
        ).wrapper
      ).wrapper
    );

    expect(container.querySelector(".relation-expression")).toBeTruthy();
    expect(container.querySelectorAll(".relation-expression table thead tr th")).toHaveLength(2);
    expect(container.querySelectorAll(".relation-expression table thead tr th")[1].innerHTML).toContain("column-1");
    expect(container.querySelectorAll(".relation-expression table tbody tr")).toHaveLength(1);
  });

  test("should render a table element, with one column, corresponding to passed prop", () => {
    const columnName = "a column";
    const columnDataType = DmnBuiltInDataType.Date;
    const column = { id: generateUuid(), name: columnName, dataType: columnDataType };

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(
          <RelationExpression
            logicType={ExpressionDefinitionLogicType.Relation}
            name="Relation"
            dataType={DmnBuiltInDataType.Undefined}
            columns={[column]}
          />
        ).wrapper
      ).wrapper
    );

    expect(container.querySelector(".relation-expression")).toBeTruthy();
    expect(container.querySelectorAll(".relation-expression table thead tr th")).toHaveLength(2);
    expect(container.querySelectorAll(".relation-expression table thead tr th")[1].innerHTML).toContain(columnName);
    expect(container.querySelectorAll(".relation-expression table thead tr th")[1].innerHTML).toContain(columnDataType);
  });

  test("should render a table element, with one row, corresponding to passed prop", () => {
    const columnName = "a column";
    const column = { id: generateUuid(), name: columnName, dataType: DmnBuiltInDataType.Date };
    const rowValue = "value";
    const row: BeeTableRow = { id: "row-id", cells: [rowValue] };

    const container = buildRelationComponent(column, row);

    expect(container.querySelector(".relation-expression")).toBeTruthy();
    expect(container.querySelectorAll(".relation-expression table tbody tr")).toHaveLength(1);
    expect(container.querySelectorAll(".relation-expression table tbody tr td")).toHaveLength(2);
    expect(container.querySelectorAll(".relation-expression table tbody tr td")[1].innerHTML).toContain(rowValue);
  });

  test("should render a table element, where there is just one cell for each column", () => {
    const columnName = "a column";
    const column = { id: generateUuid(), name: columnName, dataType: DmnBuiltInDataType.Date };
    const rowValue = "value";
    const row: BeeTableRow = { id: "row-id", cells: [rowValue, "another value", "and another one"] };

    const container = buildRelationComponent(column, row);

    expect(container.querySelector(".relation-expression")).toBeTruthy();
    expect(container.querySelectorAll(".relation-expression table tbody tr")).toHaveLength(1);
    expect(container.querySelectorAll(".relation-expression table tbody tr td")).toHaveLength(2);
  });
});

function buildRelationComponent(column: BeeTableColumn, row: BeeTableRow) {
  const { container } = render(
    usingTestingBoxedExpressionI18nContext(
      usingTestingBoxedExpressionProviderContext(
        <RelationExpression
          logicType={ExpressionDefinitionLogicType.Relation}
          name="Relation"
          dataType={DmnBuiltInDataType.Undefined}
          columns={[column]}
          rows={[row]}
        />
      ).wrapper
    ).wrapper
  );
  return container;
}
