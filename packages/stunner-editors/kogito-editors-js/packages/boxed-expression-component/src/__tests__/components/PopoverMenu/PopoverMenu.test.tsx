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
import { PopoverMenu } from "../../../components/PopoverMenu";
import { act } from "react-dom/test-utils";

jest.useFakeTimers();
const flushPromises = () => new Promise((resolve) => process.nextTick(resolve));

describe("PopoverMenu tests", () => {
  test("should render PopoverMenu component", async () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <PopoverMenu
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            body={null}
            title="title"
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container).toMatchSnapshot();
  });

  test("should render popover menu title, when title props is passed", async () => {
    const title = "title";
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <div>
          <div id="container">Popover</div>
          <PopoverMenu
            arrowPlacement={() => document.getElementById("container")!}
            appendTo={() => document.getElementById("container")!}
            body={null}
            title={title}
          />
        </div>
      ).wrapper
    );

    await activatePopover(container as HTMLElement);

    expect(container.querySelector(".selector-menu-title")).toBeTruthy();
    expect(container.querySelector(".selector-menu-title")!.innerHTML).toBe(title);
  });
});

export async function activatePopover(container: HTMLElement): Promise<void> {
  await act(async () => {
    const popoverContainer = container.querySelector("#container")! as HTMLElement;
    popoverContainer.click();
    await flushPromises();
    jest.runAllTimers();
  });
}
