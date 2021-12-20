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
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextEntryExpression } from "../../../components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";

describe("ContextEntryExpression tests", () => {
  const name = "Expression Name";
  const dataType = DataType.Boolean;
  const emptyExpression = { name, dataType };

  test("should show a context entry element with logic type not selected, when rendering it with an empty expression", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryExpression expression={emptyExpression} onUpdatingRecursiveExpression={_.identity} />
      ).wrapper
    );

    expect(container.querySelector(".entry-expression")).toBeTruthy();
    expect(container.querySelector(".entry-expression .logic-type-selector")).toHaveClass("logic-type-not-present");
  });

  test("should show a context entry element with selected logic type, when rendering it with an expression", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryExpression
          expression={{ ...emptyExpression, logicType: LogicType.LiteralExpression }}
          onUpdatingRecursiveExpression={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".entry-expression")).toBeTruthy();
    expect(container.querySelector(".entry-expression .logic-type-selector")).toHaveClass("logic-type-selected");
  });

  test("should show a context entry element with logic type not selected, when rendering it with an empty expression", () => {
    const content = <div id="content">content</div>;
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryExpression expression={emptyExpression} onUpdatingRecursiveExpression={_.identity}>
          {content}
        </ContextEntryExpression>
      ).wrapper
    );

    expect(container.querySelector(".entry-expression")).toBeTruthy();
    expect(container.querySelector(".entry-expression .logic-type-selector")).toHaveClass("logic-type-not-present");
  });
});
