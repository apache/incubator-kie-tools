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

import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import * as ReactDOM from "react-dom";
import "../static/resources/style.css";
import { ResourceContentServiceFactory } from "./app/common/ChromeResourceContentService";
import { ImageUris } from "./app/common/GlobalContext";
import { Globals } from "./app/common/Main";
import { renderServerlessWorkflowMenuApp } from "./app/renderServerlessWorkflowMenuApp";
import { Dependencies } from "./app/Dependencies";
import { RedHatConsolePageType } from "./app/openshift/RedHatConsolePageType";
import { mainContainer, runAfterUriChange } from "./app/utils";
import { Logger } from "./Logger";

export function startExtension(args: {
  name: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  imageUris: ImageUris;
}) {
  const logger = new Logger(args.name);
  const resourceContentServiceFactory = new ResourceContentServiceFactory();
  const dependencies = new Dependencies();

  const runInit = () =>
    init({
      id: chrome.runtime.id,
      logger: logger,
      dependencies: dependencies,
      editorEnvelopeLocator: args.editorEnvelopeLocator,
      resourceContentServiceFactory: resourceContentServiceFactory,
      imageUris: args.imageUris,
    });

  runAfterUriChange(logger, () => setTimeout(runInit, 0));
  setTimeout(runInit, 0);
}

function init(globals: Globals) {
  globals.logger.log(`---`);
  globals.logger.log(`Starting Chrome Extension.`);

  unmountPreviouslyRenderedFeatures(globals.id, globals.logger, globals.dependencies!);

  const pageType = discoverCurrentPageType();

  if (pageType === RedHatConsolePageType.ANY) {
    globals.logger.log(`This page is not supported: ${window.location.toString()}`);
    return;
  }

  if (pageType === RedHatConsolePageType.APPLICATION_SERVICES) {
    // reset page
    // const page = globals.dependencies.applicationServices.page();
    // if (page) {
    //   page.style.display = "block";
    // }
    renderServerlessWorkflowMenuApp({
      ...globals,
    });
    return;
  }

  throw new Error(`Unknown OpenShiftPageType ${pageType}`);
}

function unmountPreviouslyRenderedFeatures(id: string, logger: Logger, dependencies: Dependencies) {
  try {
    if (mainContainer(id, dependencies.all.body())) {
      ReactDOM.unmountComponentAtNode(mainContainer(id, dependencies.all.body())!);
      logger.log("Unmounted previous features.");
    }
  } catch (e) {
    logger.log("Ignoring exception while unmounting features.");
  }
}

function pathnameMatches(regex: string) {
  return !!window.location.pathname.match(new RegExp(regex));
}

function searchMatches(regex: string) {
  return !!window.location.search.match(new RegExp(regex));
}

export function discoverCurrentPageType() {
  if (pathnameMatches(`/application-services/*`)) {
    return RedHatConsolePageType.APPLICATION_SERVICES;
  }
  return RedHatConsolePageType.ANY;
}
