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

import { GitHubPageType } from "./app/github/GitHubPageType";
import { renderSingleEditorApp } from "./app/components/single/singleEditorEdit";
import { renderSingleEditorReadonlyApp } from "./app/components/single/singleEditorView";
import { renderPrEditorsApp } from "./app/components/pr/prEditors";
import { mainContainer, runAfterUriChange } from "./app/utils";
import * as dependencies__ from "./app/dependencies";
import * as ReactDOM from "react-dom";
import { Router } from "@kogito-tooling/core-api";
import "../resources/style.css";
import { Logger } from "./Logger";

/**
 * Starts a Kogito extension.
 *
 *  @param args.name The extension name. Used to differentiate logs from other extensions.
 *  @param args.editorIndexPath The relative path to search for an "index.html" file for the editor iframe.
 *  @param args.router The Router to be used to find resources for each language.
 */
export function startExtension(args: { name: string; editorIndexPath: string; router: Router }) {
  const logger = new Logger(args.name);

  const runInit = () => init({ logger, editorIndexPath: args.editorIndexPath, router: args.router });

  runAfterUriChange(logger, () => setImmediate(runInit));
  setImmediate(runInit);
}

function init(args: { logger: Logger; editorIndexPath: string; router: Router }) {
  args.logger.log(`---`);
  args.logger.log(`Starting GitHub extension.`);

  unmountPreviouslyRenderedFeatures(args.logger);

  const pageType = discoverCurrentGitHubPageType();
  if (pageType === GitHubPageType.ANY) {
    args.logger.log(`This GitHub page is not supported.`);
    return;
  }

  if (pageType === GitHubPageType.EDIT) {
    renderSingleEditorApp({ logger: args.logger, router: args.router, editorIndexPath: args.editorIndexPath });
    return;
  }

  if (pageType === GitHubPageType.VIEW) {
    renderSingleEditorReadonlyApp({ logger: args.logger, router: args.router, editorIndexPath: args.editorIndexPath });
    return;
  }

  if (pageType === GitHubPageType.PR) {
    renderPrEditorsApp({ logger: args.logger, router: args.router, editorIndexPath: args.editorIndexPath });
    return;
  }

  throw new Error(`Unknown GitHubPageType ${pageType}`);
}

function uriMatches(regex: string) {
  return !!window.location.pathname.match(new RegExp(regex));
}

function discoverCurrentGitHubPageType() {
  if (uriMatches(`.*/.*/edit/.*`)) {
    return GitHubPageType.EDIT;
  }

  if (uriMatches(`.*/.*/blob/.*`)) {
    return GitHubPageType.VIEW;
  }

  if (uriMatches(`.*/.*/pull/[0-9]+/files.*`)) {
    return GitHubPageType.PR;
  }

  if (uriMatches(`.*/.*/pull/[0-9]+/commits.*`)) {
    return GitHubPageType.PR;
  }

  return GitHubPageType.ANY;
}

function unmountPreviouslyRenderedFeatures(logger: Logger) {
  try {
    if (mainContainer({ name: "", element: dependencies__.all.body() })) {
      ReactDOM.unmountComponentAtNode(mainContainer({ name: "", element: dependencies__.all.body() })!);
      logger.log("Unmounted previous features.");
    }
  } catch (e) {
    logger.log("Ignoring exception while unmounting features.");
  }
}
