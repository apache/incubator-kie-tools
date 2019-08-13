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

import { ChromeRouter } from "./app/ChromeRouter";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { ChromeExtensionApp } from "./app/Components";
import { GwtEditorRoutes } from "appformer-js-gwt-editors-common";

function init() {
  const githubEditor = document.querySelector(".js-code-editor") as HTMLElement;
  if (!githubEditor) {
    console.debug("Not GitHub edit page.");
    return;
  }

  const splitLocationHref = window.location.href.split(".");
  const openFileExtension = splitLocationHref[splitLocationHref.length - 1];

  const gwtEditorRoutes = new GwtEditorRoutes({
    dmnLocation: `editors/dmn`,
    bpmnLocation: `editors/bpmn`
  });

  const router = new ChromeRouter(gwtEditorRoutes);

  if (!router.getLanguageData(openFileExtension)) {
    console.info(`No enhanced editor available for "${openFileExtension}" format.`);
    return;
  }

  document.body.appendChild(document.createElement("div")).setAttribute("id", "kogito-container");

  ReactDOM.render(
    <ChromeExtensionApp openFileExtension={openFileExtension} router={router} githubEditor={githubEditor} />,
    document.getElementById("kogito-container")
  );
}

init();
