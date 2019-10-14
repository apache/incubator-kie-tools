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
import { Main } from "../common/Main";
import {
  KOGITO_IFRAME_CONTAINER_PR_CLASS,
  KOGITO_TOOLBAR_CONTAINER_PR_CLASS,
  KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS
} from "../../constants";
import * as dependencies__ from "../../dependencies";
import { GitHubPageType } from "../../github/GitHubPageType";

export function renderPrEditorsApp(args: { editorIndexPath: string; router: Router }) {
  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanupComponentContainers();

  ReactDOM.render(
    <Main router={args.router} editorIndexPath={args.editorIndexPath} pageType={GitHubPageType.PR}>
      <PrEditorsApp />
    </Main>,
    createAndGetMainContainer(dependencies__.common.body()),
    () => console.info("[Kogito] Mounted.")
  );
}

function cleanupComponentContainers() {
  Array.from(document.querySelectorAll(`.${KOGITO_IFRAME_CONTAINER_PR_CLASS}`)).forEach(e => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(`.${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}`)).forEach(e => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(`.${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}`)).forEach(e => {
    removeAllChildren(e);
  });
}
