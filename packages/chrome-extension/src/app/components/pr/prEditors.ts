/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import * as ReactDOM from "react-dom";
import * as React from "react";
import { PrEditorsApp } from "./PrEditorsApp";
import { createAndGetMainContainer, removeAllChildren } from "../../utils";
import { Router } from "@kogito-tooling/core-api";

export function renderPrEditorsApp(args: { router: Router }) {
  cleanupComponentContainers();

  //FIXME: Use DOM mutation observers to know when to start
  setTimeout(() => {
    ReactDOM.render(React.createElement(PrEditorsApp, { router: args.router }), createAndGetMainContainer());
  }, 2000);
}

function cleanupComponentContainers() {
  Array.from(document.querySelectorAll(".kogito-iframe-container-pr")).forEach(e => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(".kogito-toolbar-container-pr")).forEach(e => {
    removeAllChildren(e);
  });
}
