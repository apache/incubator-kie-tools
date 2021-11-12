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

import * as React from "react";
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kogito-tooling/i18n/dist/react-components";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18n,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { act } from "react-dom/test-utils";
import { fireEvent } from "@testing-library/react";
import { resetId } from "react-id-generator";
import { BoxedExpressionGlobalContext } from "../../context";
import { DataType } from "../../api";
import { BoxedExpressionProvider, BoxedExpressionProviderProps } from "../../components";

global.console = { ...global.console, warn: jest.fn() };

export const EDIT_EXPRESSION_NAME = "[data-ouia-component-id='edit-expression-name']";
export const EDIT_EXPRESSION_DATA_TYPE = "[data-ouia-component-id='edit-expression-data-type'] input";

export const flushPromises: () => Promise<unknown> = () => new Promise((resolve) => process.nextTick(resolve));

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export function usingTestingBoxedExpressionI18nContext(
  children: React.ReactElement,
  ctx?: Partial<I18nDictionariesProviderProps<BoxedExpressionEditorI18n>>
) {
  const usedCtx: I18nDictionariesProviderProps<BoxedExpressionEditorI18n> = {
    defaults: boxedExpressionEditorI18nDefaults,
    dictionaries: boxedExpressionEditorDictionaries,
    ctx: BoxedExpressionEditorI18nContext,
    children,
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <I18nDictionariesProvider defaults={usedCtx.defaults} dictionaries={usedCtx.dictionaries} ctx={usedCtx.ctx}>
        {usedCtx.children}
      </I18nDictionariesProvider>
    ),
  };
}

export function usingTestingBoxedExpressionProviderContext(
  children: React.ReactElement,
  ctx?: Partial<BoxedExpressionProviderProps>
) {
  const usedCtx: BoxedExpressionProviderProps = {
    expressionDefinition: {},
    pmmlParams: [
      {
        document: "document",
        modelsFromDocument: [{ model: "model", parametersFromModel: [{ name: "p-1", dataType: DataType.Number }] }],
      },
    ],
    isRunnerTable: false,
    children,
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <BoxedExpressionProvider
        expressionDefinition={usedCtx.expressionDefinition}
        pmmlParams={usedCtx.pmmlParams}
        isRunnerTable={false}
      >
        {usedCtx.children}
      </BoxedExpressionProvider>
    ),
  };
}

export function wrapComponentInContext(component: JSX.Element): JSX.Element {
  return (
    <BoxedExpressionGlobalContext.Provider
      value={{
        pmmlParams: [
          {
            document: "document",
            modelsFromDocument: [{ model: "model", parametersFromModel: [{ name: "p-1", dataType: DataType.Number }] }],
          },
        ],
        supervisorHash: "",
        setSupervisorHash: jest.fn,
        boxedExpressionEditorRef: { current: document.body as HTMLDivElement },
        currentlyOpenedHandlerCallback: jest.fn,
        setCurrentlyOpenedHandlerCallback: jest.fn,
      }}
    >
      {component}
    </BoxedExpressionGlobalContext.Provider>
  );
}

export async function activateSelector(container: HTMLElement, query: string): Promise<void> {
  await act(async () => {
    const selector = container.querySelector(query)! as HTMLElement;
    selector.click();
    await flushPromises();
    jest.runAllTimers();
  });
}

export async function activateNameAndDataTypePopover(element: HTMLElement): Promise<void> {
  await act(async () => {
    element.click();
    await flushPromises();
    jest.runAllTimers();
  });
}

export async function updateElementViaPopover(
  /** Element that when clicked will trigger the popover */
  triggerPoint: HTMLElement,
  /** Base container used as reference for the test */
  baseElement: Element,
  /** Selector used for get the input element */
  inputSelector: string,
  /** New value passed to the input element */
  newName: string
): Promise<void> {
  await activateNameAndDataTypePopover(triggerPoint);
  const inputElement = baseElement.querySelector(inputSelector)! as HTMLInputElement;
  inputElement.value = newName;
  fireEvent.change(inputElement, {
    target: { value: newName },
  });
  fireEvent.blur(inputElement);
}

export const contextEntry = (container: Element, index: number): Element | null =>
  container.querySelector(`table tbody tr:nth-of-type(${index})`);

export const checkEntryContent = (entry: Element | null, entryRecordInfo: { name: string; dataType: string }): void => {
  expect(entry).toContainHTML(entryRecordInfo.name);
  expect(entry).toContainHTML(entryRecordInfo.dataType);
};

export const checkEntryStyle = (entry: Element | null, cssClass: string): void => {
  expect(entry?.querySelector(".entry-expression")?.firstChild).toHaveClass(cssClass);
};

export const checkEntryLogicType = (entry: Element | null, cssClass: string): void => {
  expect(entry?.querySelector(".entry-expression")?.firstChild?.firstChild).toHaveClass(cssClass);
};

beforeEach(() => {
  resetId();
});
