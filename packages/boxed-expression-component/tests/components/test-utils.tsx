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
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kie-tools-core/i18n/dist/react-components";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18n,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "@kie-tools/boxed-expression-component/dist/i18n";
import { act } from "react-dom/test-utils";
import { fireEvent } from "@testing-library/react";
import {
  BoxedExpressionEditorContext,
  BoxedExpressionEditorContextProvider,
  BoxedExpressionEditorContextProviderProps,
} from "@kie-tools/boxed-expression-component/dist/components/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { BeeGwtService, DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

global.console = { ...global.console, warn: () => ({}) };

export const EDIT_EXPRESSION_NAME = "[data-ouia-component-id='edit-expression-name']";
export const EDIT_EXPRESSION_DATA_TYPE = "[data-ouia-component-id='edit-expression-data-type'] span";

export const flushPromises: () => Promise<unknown> = () => new Promise((resolve) => process.nextTick(resolve));

/**
 * The constant below contains all built-in types currently supported by the Boxed Expression Editor,
 * Plus a custom defined data-type.
 * These values are used for testing purpose, and by the showcase
 */
export const dataTypes = [
  { typeRef: "Undefined", name: "<Undefined>", isCustom: false },
  { typeRef: "Any", name: "Any", isCustom: false },
  { typeRef: "Boolean", name: "boolean", isCustom: false },
  { typeRef: "Context", name: "context", isCustom: false },
  { typeRef: "Date", name: "date", isCustom: false },
  { typeRef: "DateTime", name: "date and time", isCustom: false },
  { typeRef: "DateTimeDuration", name: "days and time duration", isCustom: false },
  { typeRef: "Number", name: "number", isCustom: false },
  { typeRef: "String", name: "string", isCustom: false },
  { typeRef: "Time", name: "time", isCustom: false },
  { typeRef: "YearsMonthsDuration", name: "years and months duration", isCustom: false },
  { typeRef: "tPerson", name: "tPerson", isCustom: true },
];

/**
 * The constant below contains an example of PMML params to be used for testing purpose, and by the showcase
 */
export const pmmlParams = [
  {
    document: "document",
    modelsFromDocument: [
      { model: "model", parametersFromModel: [{ id: "p1", name: "p-1", dataType: DmnBuiltInDataType.Number }] },
    ],
  },
  {
    document: "mining pmml",
    modelsFromDocument: [
      {
        model: "MiningModelSum",
        parametersFromModel: [
          { id: "i1", name: "input1", dataType: DmnBuiltInDataType.Any },
          { id: "i2", name: "input2", dataType: DmnBuiltInDataType.Any },
          { id: "i3", name: "input3", dataType: DmnBuiltInDataType.Any },
        ],
      },
    ],
  },
  {
    document: "regression pmml",
    modelsFromDocument: [
      {
        model: "RegressionLinear",
        parametersFromModel: [
          { id: "i1", name: "i1", dataType: DmnBuiltInDataType.Number },
          { id: "i2", name: "i2", dataType: DmnBuiltInDataType.Number },
        ],
      },
    ],
  },
];

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
    wrapper: wrapComponentInContext(
      <I18nDictionariesProvider defaults={usedCtx.defaults} dictionaries={usedCtx.dictionaries} ctx={usedCtx.ctx}>
        {usedCtx.children}
      </I18nDictionariesProvider>
    ),
  };
}

export function usingTestingBoxedExpressionProviderContext(
  children: React.ReactElement,
  ctx?: Partial<BoxedExpressionEditorContextProviderProps>
) {
  const usedCtx: BoxedExpressionEditorContextProviderProps = {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: {},
    dataTypes,
    pmmlParams,
    isRunnerTable: false,
    children,
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <BoxedExpressionEditorContextProvider
        beeGwtService={usedCtx.beeGwtService}
        decisionNodeId={usedCtx.decisionNodeId}
        expressionDefinition={usedCtx.expressionDefinition}
        dataTypes={usedCtx.dataTypes}
        pmmlParams={usedCtx.pmmlParams}
        isRunnerTable={false}
      >
        {usedCtx.children}
      </BoxedExpressionEditorContextProvider>
    ),
  };
}

export function wrapComponentInContext(component: JSX.Element, beeGwtService?: BeeGwtService): JSX.Element {
  return (
    <BoxedExpressionEditorContext.Provider
      value={{
        beeGwtService,
        decisionNodeId: "_00000000-0000-0000-0000-000000000000",
        dataTypes,
        pmmlParams,
        supervisorHash: "",
        setSupervisorHash: jest.fn,
        isContextMenuOpen: false,
        setContextMenuOpen: jest.fn,
        editorRef: { current: document.body as HTMLDivElement },
        currentlyOpenedHandlerCallback: jest.fn,
        setCurrentlyOpenedHandlerCallback: jest.fn,
      }}
    >
      {component}
    </BoxedExpressionEditorContext.Provider>
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
  await act(async () => {
    fireEvent.change(inputElement, {
      target: { value: newName },
    });
    await flushPromises();
    jest.runAllTimers();
  });
  await act(async () => {
    fireEvent.blur(inputElement);
    await flushPromises();
    jest.runAllTimers();
  });
  await act(async () => {
    fireEvent.click(baseElement);
    await flushPromises();
    jest.runAllTimers();
  });
}

export const contextEntry = (container: Element, index: number): Element | null =>
  container.querySelector(`table tbody tr:nth-of-type(${index})`);

export const checkEntryContent = (
  entry: Element | null,
  entryRecordInfo: { id?: string; name: string; dataType: string }
): void => {
  if (entryRecordInfo.id) {
    expect(entry?.querySelector(".entry-info")).toHaveClass(entryRecordInfo.id);
  }
  expect(entry).toContainHTML(entryRecordInfo.name);
  expect(entry).toContainHTML(entryRecordInfo.dataType);
};

export const checkEntryStyle = (entry: Element | null, cssClass: string): void => {
  expect(entry?.querySelector(".entry-expression")?.firstChild).toHaveClass(cssClass);
};

export const checkEntryLogicType = (entry: Element | null, cssClass: string): void => {
  expect(entry?.querySelector(".entry-expression")?.firstChild?.firstChild).toHaveClass(cssClass);
};
