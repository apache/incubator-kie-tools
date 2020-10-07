/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { render } from "@testing-library/react";
import * as React from "react";
import { ActionSelector } from "../../../../../editor/components/Header/atoms";

describe("ActionSelector", () => {
  test("render::Collapsed", () => {
    const { getByTestId } = render(<ActionSelector />);
    expect(getByTestId("action-selector")).toMatchSnapshot();

    const element: HTMLElement = getByTestId("action-selector__toggle");
    expect(element).toBeInTheDocument();
    expect(element).toHaveTextContent("Actions");
  });

  test("render::MenuItems", () => {
    const { getAllByRole, getByTestId } = render(<ActionSelector />);

    const element: HTMLElement = getByTestId("action-selector__toggle");
    expect(element).toBeInstanceOf(HTMLButtonElement);

    (element as HTMLButtonElement).click();
    expect(getByTestId("action-selector")).toMatchSnapshot();
    const menuItems: HTMLElement[] = getAllByRole("menuitem");

    expect(menuItems.length).toBe(2);
    expect(menuItems[0]).toHaveTextContent("Create model");
    expect(menuItems[1]).toHaveTextContent("View Data Dictionary");
  });

  test("render::Toggle", () => {
    const { getAllByRole, getByTestId } = render(<ActionSelector />);

    const element: HTMLElement = getByTestId("action-selector__toggle");
    expect(element).toBeInstanceOf(HTMLButtonElement);

    (element as HTMLButtonElement).click();
    const menuItemsExpanded: HTMLElement[] = getAllByRole("menuitem");
    expect(menuItemsExpanded.length).toBe(2);

    (element as HTMLButtonElement).click();
    expect(() => getAllByRole("menuitem")).toThrowError(
      'Unable to find an accessible element with the role "menuitem"'
    );
  });
});
