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

import { unmountComponentAtNode, render as reactRender } from "react-dom";
import * as React from "react";

let contextMock = {};
let componentContainer: HTMLElement | null = null;

jest.mock("react", () => {
  const ActualReact = require.requireActual("react");
  return {
    ...ActualReact,
    useContext: () => contextMock
  };
});

export const useContextMock = (ctx: any) => {
  contextMock = ctx;
};

export const setupContainer = () => {
  componentContainer = document.createElement("div");
  document.body.appendChild(componentContainer);
};

export const teardownContainer = () => {
  if (componentContainer) {
    unmountComponentAtNode(componentContainer);
    componentContainer.remove();
  }
  componentContainer = null;
};

export const render = (component: React.SFCElement<any>) => {
  if (componentContainer) {
    reactRender(component, componentContainer);
  } else {
    throw new Error("[Guided Tour] Test error: 'setupContainer' must be called on 'beforeEach'.");
  }
};

export const renderedComponent = () => {
  return componentContainer;
};

export const triggerClick = (selector: string) => {
  const element = document.querySelector(selector);
  if (element) {
    element.dispatchEvent(new MouseEvent("click", { bubbles: true }));
  } else {
    throw new Error("[Guided Tour] Test error: clickable element could not be found.");
  }
};
