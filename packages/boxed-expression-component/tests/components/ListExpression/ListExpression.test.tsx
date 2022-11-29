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
import { usingTestingBoxedExpressionI18nContext, usingTestingBoxedExpressionProviderContext } from "../test-utils";
import { ExpressionDefinitionLogicType } from "@kie-tools/boxed-expression-component/dist/api";
import * as React from "react";
import { ListExpression } from "@kie-tools/boxed-expression-component/dist/components/ListExpression";

describe("ListExpression tests", () => {
  test("should show a table without header, with one row and one column", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(<ListExpression logicType={ExpressionDefinitionLogicType.List} />)
          .wrapper
      ).wrapper
    );

    expect(container.querySelector(".list-expression")).toBeTruthy();
    expect(container.querySelector(".list-expression table")).toBeTruthy();
    expect(container.querySelector(".list-expression table thead")).toBeNull();
    expect(container.querySelectorAll(".list-expression table tbody tr")).toHaveLength(1);
    expect(container.querySelectorAll(".list-expression table tbody td.data-cell")).toHaveLength(1);
  });

  test("should have, for its default cell, as default logic type, a literal expression with empty content", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(<ListExpression logicType={ExpressionDefinitionLogicType.List} />)
          .wrapper
      ).wrapper
    );

    expect(container.querySelector(".list-expression table tbody td.data-cell .literal-expression")).toBeTruthy();
    expect(
      container.querySelector(
        ".list-expression table tbody td.data-cell .literal-expression .literal-expression-body textarea"
      )
    ).toBeEmpty();
  });

  test("should be able to render nested expressions", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        usingTestingBoxedExpressionProviderContext(
          <ListExpression
            id="id1"
            logicType={ExpressionDefinitionLogicType.List}
            items={[{ logicType: ExpressionDefinitionLogicType.List, id: "id2" }]}
          />
        ).wrapper
      ).wrapper
    );

    expect(container.querySelectorAll(".list-expression")).toHaveLength(2);
    expect(container.querySelector(".list-expression .table-component.id1 td.data-cell .list-expression")).toBeTruthy();
    expect(
      container.querySelector(
        ".list-expression .table-component tbody td.data-cell .literal-expression .literal-expression-body textarea"
      )
    ).toBeEmpty();
  });
});
