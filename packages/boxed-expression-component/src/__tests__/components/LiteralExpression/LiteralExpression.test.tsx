/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import * as React from "react";
import { LiteralExpression } from "../../../components/LiteralExpression";
import { DataType, LogicType } from "../../../api";
import { act } from "react-dom/test-utils";

jest.useFakeTimers();
const flushPromises = () => new Promise((resolve) => process.nextTick(resolve));

describe("LiteralExpression tests", () => {
  describe("LiteralExpression Header", () => {
    test("should render expression's name, when name property is passed", () => {
      const expressionName = "expression name";
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression
            logicType={LogicType.LiteralExpression}
            name={expressionName}
            dataType={DataType.Undefined}
          />
        ).wrapper
      );
      expect(container.querySelector(".expression-name")).toBeTruthy();
      expect(container.querySelector(".expression-name")!.innerHTML).toBe(expressionName);
    });

    test("should render expression's data type, when dataType property is passed", () => {
      const dataType = DataType.Boolean;
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression logicType={LogicType.LiteralExpression} name={"expressionName"} dataType={dataType} />
        ).wrapper
      );
      expect(container.querySelector(".expression-data-type")).toBeTruthy();
      expect(container.querySelector(".expression-data-type")!.innerHTML).toBe("(" + dataType + ")");
    });

    test("should render no header section, when isHeadless property is passed", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression
            isHeadless={true}
            logicType={LogicType.LiteralExpression}
            name={"expressionName"}
            dataType={DataType.Undefined}
          />
        ).wrapper
      );
      expect(container.querySelector(".literal-expression-header")).toBeFalsy();
    });

    test("should render header section, when isHeadless property is not passed or it is false", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression
            logicType={LogicType.LiteralExpression}
            name={"expressionName"}
            dataType={DataType.Undefined}
          />
        ).wrapper
      );
      expect(container.querySelector(".literal-expression-header")).toBeTruthy();
    });

    test("should render edit expression menu, when header is clicked", async () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression
            logicType={LogicType.LiteralExpression}
            name={"expressionName"}
            dataType={DataType.Boolean}
          />
        ).wrapper
      );

      await act(async () => {
        const literalExpressionHeader = container.querySelector(
          ".literal-expression-header .expression-info"
        )! as HTMLElement;
        literalExpressionHeader.click();
        await flushPromises();
        jest.runAllTimers();
      });

      expect(document.querySelector(".selector-menu-title")).toBeTruthy();
    });
  });

  describe("LiteralExpression Body", () => {
    test("should render expression's content, when content property is passed", () => {
      const content = "content";
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression
            logicType={LogicType.LiteralExpression}
            name={"expressionName"}
            dataType={DataType.Boolean}
            content={content}
          />
        ).wrapper
      );

      expect(container.querySelector(".literal-expression-body textarea")).toBeTruthy();
      expect(container.querySelector(".literal-expression-body textarea")!.innerHTML).toBe(content);
    });

    test("should render nothing, when content property is not passed", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <LiteralExpression
            logicType={LogicType.LiteralExpression}
            name={"expressionName"}
            dataType={DataType.Boolean}
          />
        ).wrapper
      );

      expect(container.querySelector(".literal-expression-body textarea")).toBeTruthy();
      expect(container.querySelector(".literal-expression-body textarea")!.innerHTML).toBe("");
    });
  });
});
