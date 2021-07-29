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

import { fireEvent, render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import * as React from "react";
import { EditExpressionMenu } from "../../../components/EditExpressionMenu";
import { activatePopover } from "../PopoverMenu/PopoverMenu.test";
import { DataType, ExpressionProps, LogicType } from "../../../api";
import * as _ from "lodash";

jest.useFakeTimers();

describe("EditExpressionMenu tests", () => {
  test("should render Edit Expression title", async () => {
    const title = "Edit Expression";
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName="Expression Name"
            title={title}
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={(expression) => {
              console.log(expression);
            }}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector(".selector-menu-title")).toBeTruthy();
    expect(container.querySelector(".selector-menu-title")!.innerHTML).toBe(title);
  });

  test("should render custom name field label", async () => {
    const nameFieldLabel = "custom name field label";
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName="Expression Name"
            nameField={nameFieldLabel}
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={(expression) => {
              console.log(expression);
            }}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector(".expression-name label")).toBeTruthy();
    expect(container.querySelector(".expression-name label")!.innerHTML).toBe(nameFieldLabel);
  });

  test("should render custom data type field label", async () => {
    const dataTypeFieldLabel = "custom data type field label";
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName="Expression Name"
            dataTypeField={dataTypeFieldLabel}
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={(expression) => {
              console.log(expression);
            }}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector(".expression-data-type label")).toBeTruthy();
    expect(container.querySelector(".expression-data-type label")!.innerHTML).toBe(dataTypeFieldLabel);
  });

  test("should render undefined as data type, when it is not pre-selected", async () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName="Expression Name"
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={(expression) => {
              console.log(expression);
            }}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector("[id^='pf-select-toggle-id-']")).toBeTruthy();
    expect((container.querySelector("[id^='pf-select-toggle-id-']")! as HTMLInputElement).value).toBe(
      LogicType.Undefined
    );
  });

  test("should render passed data type, when it is pre-selected", async () => {
    const selectedDataType = DataType.Date;
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName="Expression Name"
            selectedDataType={selectedDataType}
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={(expression) => {
              console.log(expression);
            }}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector("[id^='pf-select-toggle-id-']")).toBeTruthy();
    expect((container.querySelector("[id^='pf-select-toggle-id-']")! as HTMLInputElement).value).toBe(selectedDataType);
  });

  test("should render passed expression name, when it is pre-selected", async () => {
    const expressionName = "a name";
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName={expressionName}
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={(expression) => {
              console.log(expression);
            }}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector("#expression-name")).toBeTruthy();
    expect((container.querySelector("#expression-name")! as HTMLInputElement).value).toBe(expressionName);
  });

  test("should trigger the onExpressionUpdate callback when the expression name is changed", async () => {
    const onExpressionUpdate = (expression: ExpressionProps) => {
      _.identity(expression);
    };
    const mockedOnExpressionUpdate = jest.fn(onExpressionUpdate);
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <EditExpressionMenu
            selectedExpressionName="init"
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            onExpressionUpdate={mockedOnExpressionUpdate}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    const input = container.querySelector("#expression-name") as HTMLInputElement;
    fireEvent.blur(input, { target: { value: "changed" } });

    expect(mockedOnExpressionUpdate).toHaveBeenCalled();
    expect(mockedOnExpressionUpdate).toHaveBeenCalledWith({
      name: "changed",
      dataType: DataType.Undefined,
    });
  });
});
