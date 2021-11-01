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
import { DataType, LogicType } from "../../../api";
import { fireEvent, render } from "@testing-library/react";
import { flushPromises, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { act } from "react-dom/test-utils";
import * as React from "react";
import { LogicTypeSelector } from "../../../components/LogicTypeSelector";
import * as _ from "lodash";

jest.useFakeTimers();

describe("LogicTypeSelector tests", () => {
  test("should have the clear action disabled on startup", async () => {
    const expression = { name: "Test", dataType: DataType.Undefined };

    const { baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <LogicTypeSelector
          selectedExpression={expression}
          getPlacementRef={() => document.body as HTMLDivElement}
          onLogicTypeResetting={_.identity}
          onLogicTypeUpdating={_.identity}
        />
      ).wrapper
    );

    await triggerContextMenu(baseElement as HTMLElement, ".logic-type-selector");

    expect(baseElement.querySelector(".context-menu-container button")).toBeDisabled();
    expect(baseElement.querySelector(".context-menu-container button")).toHaveTextContent("Clear");
  });

  test("should have the clear action enabled, when logic type is selected", async () => {
    const expression = { name: "Test", logicType: LogicType.LiteralExpression, dataType: DataType.Undefined };

    const { baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <LogicTypeSelector
          selectedExpression={expression}
          getPlacementRef={() => document.body as HTMLDivElement}
          onLogicTypeResetting={_.identity}
          onLogicTypeUpdating={_.identity}
        />
      ).wrapper
    );

    await triggerContextMenu(baseElement as HTMLElement, ".logic-type-selector");

    expect(baseElement.querySelector(".context-menu-container button")).not.toBeDisabled();
    expect(baseElement.querySelector(".context-menu-container button")).toBeTruthy();
    expect(baseElement.querySelector(".context-menu-container button")).toHaveTextContent("Clear");
  });
});

describe("Logic type selection", () => {
  test("should show the pre-selection, when logic type prop is passed", () => {
    const expression = { name: "Test", logicType: LogicType.List, dataType: DataType.Undefined };

    const { baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <LogicTypeSelector
          selectedExpression={expression}
          getPlacementRef={() => document.body as HTMLDivElement}
          onLogicTypeResetting={_.identity}
          onLogicTypeUpdating={_.identity}
        />
      ).wrapper
    );

    expect(baseElement.querySelector(".logic-type-selector")).toBeTruthy();
    expect(baseElement.querySelector(".logic-type-selector")).toHaveClass("logic-type-selected");
  });
});

const triggerContextMenu = async (container: HTMLElement, selector: string) => {
  await act(async () => {
    const element = container.querySelector(selector)!;
    fireEvent.contextMenu(element);
    await flushPromises();
    jest.runAllTimers();
  });
};
