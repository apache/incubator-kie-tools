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
import {createAndGetMainContainer, loopUntil, removeAllChildren} from "../../utils";
import { Router } from "@kogito-tooling/core-api";
import { Main } from "../common/Main";

export function renderPrEditorsApp(args: { router: Router }) {
  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanupComponentContainers();

  loopUntil(githubPageLooksReady, { interval: 100, timeout: 5000 }).then(() => {
    ReactDOM.render(
      <Main router={args.router}>
        <PrEditorsApp />
      </Main>,
      createAndGetMainContainer()
    );
  });
}

function githubPageLooksReady() {
  return document.querySelectorAll(".js-file-content").length > 0;
}

function cleanupComponentContainers() {
  Array.from(document.querySelectorAll(".kogito-iframe-container-pr")).forEach(e => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(".kogito-toolbar-container-pr")).forEach(e => {
    removeAllChildren(e);
  });
}
