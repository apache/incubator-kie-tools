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
import { Dependencies } from "./app/Dependencies";
import * as ReactDOM from "react-dom";
import { Router } from "@kogito-tooling/core-api";
import "../resources/style.css";
import { Logger } from "./Logger";
import { Globals } from "./app/components/common/Main";
import { ExternalEditorManager } from "./ExternalEditorManager";
import { ResourceContentServiceFactory } from "./app/components/common/ChromeResourceContentService";
import { addExternalEditorLinks } from "./app/components/tree/externalEditorLinkManager";

/**
 * Starts a Kogito extension.
 *
 *  @param args.name The extension name. Used to differentiate logs from other extensions.
 *  @param args.editorIndexPath The relative path to search for an "index.html" file for the editor iframe.
 *  @param args.extensionIconUrl The relative path to search for an image that will be the icon used for your extension.
 *  @param args.githubAuthTokenCookieName The name of the cookie that will hold a GitHub PAT for your extension.
 *  @param args.router The Router to be used to find resources for each language.
 *  @param args.externalEditorManager The implementation of ExternalEditorManager for your extension.
 */
export function startExtension(args: {
  name: string;
  editorIndexPath: string;
  extensionIconUrl: string;
  githubAuthTokenCookieName: string;
  router: Router;
  externalEditorManager?: ExternalEditorManager;
}) {
  const logger = new Logger(args.name);
  const resourceContentServiceFactory = new ResourceContentServiceFactory();
  const dependencies = new Dependencies();

  const runInit = () =>
    init({
      id: chrome.runtime.id,
      logger: logger,
      dependencies: dependencies,
      githubAuthTokenCookieName: args.githubAuthTokenCookieName,
      editorIndexPath: args.editorIndexPath,
      extensionIconUrl: args.extensionIconUrl,
      router: args.router,
      resourceContentServiceFactory: resourceContentServiceFactory,
      externalEditorManager: args.externalEditorManager
    });

  runAfterUriChange(logger, () => setTimeout(runInit, 0));
  setTimeout(runInit, 0);
}

function init(args: Globals) {
  args.logger.log(`---`);
  args.logger.log(`Starting GitHub extension.`);

  unmountPreviouslyRenderedFeatures(args.id, args.logger, args.dependencies);

  const fileInfo = extractFileInfoFromUrl();
  const pageType = discoverCurrentGitHubPageType();

  if (pageType === GitHubPageType.ANY) {
    args.logger.log(`This GitHub page is not supported.`);
    return;
  }

  if (pageType === GitHubPageType.EDIT) {
    renderSingleEditorApp({
      id: args.id,
      logger: args.logger,
      dependencies: args.dependencies,
      router: args.router,
      githubAuthTokenCookieName: args.githubAuthTokenCookieName,
      extensionIconUrl: args.extensionIconUrl,
      editorIndexPath: args.editorIndexPath,
      externalEditorManager: args.externalEditorManager,
      resourceContentServiceFactory: args.resourceContentServiceFactory,
      fileInfo: fileInfo
    });
  } else if (pageType === GitHubPageType.VIEW) {
    renderSingleEditorReadonlyApp({
      id: args.id,
      logger: args.logger,
      dependencies: args.dependencies,
      router: args.router,
      githubAuthTokenCookieName: args.githubAuthTokenCookieName,
      extensionIconUrl: args.extensionIconUrl,
      editorIndexPath: args.editorIndexPath,
      fileInfo: fileInfo,
      resourceContentServiceFactory: args.resourceContentServiceFactory,
      externalEditorManager: args.externalEditorManager
    });
  } else if (pageType === GitHubPageType.PR) {
    renderPrEditorsApp({
      githubAuthTokenCookieName: args.githubAuthTokenCookieName,
      id: args.id,
      logger: args.logger,
      dependencies: args.dependencies,
      router: args.router,
      extensionIconUrl: args.extensionIconUrl,
      editorIndexPath: args.editorIndexPath,
      resourceContentServiceFactory: args.resourceContentServiceFactory,
      externalEditorManager: args.externalEditorManager,
      contentPath: fileInfo.path
    });
  } else if (pageType === GitHubPageType.TREE) {
    addExternalEditorLinks({
      githubAuthTokenCookieName: args.githubAuthTokenCookieName,
      id: args.id,
      logger: args.logger,
      router: args.router,
      extensionIconUrl: args.extensionIconUrl,
      editorIndexPath: args.editorIndexPath,
      resourceContentServiceFactory: args.resourceContentServiceFactory,
      externalEditorManager: args.externalEditorManager,
      dependencies: args.dependencies
    });
    return;
  } else {
    throw new Error(`Unknown GitHubPageType ${pageType}`);
  }
}

export function extractFileInfoFromUrl() {
  const split = window.location.pathname.split("/");
  return {
    gitRef: split[4],
    repo: split[2],
    org: split[1],
    path: split.slice(5).join("/")
  };
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

function uriMatches(regex: string) {
  return !!window.location.pathname.match(new RegExp(regex));
}

export function discoverCurrentGitHubPageType() {
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

  if (uriMatches(`.*/.*/tree/.*`) || uriMatches(`/.*/.*/?$`)) {
    return GitHubPageType.TREE;
  }

  return GitHubPageType.ANY;
}

export * from "./DefaultChromeRouter";
export * from "./ExternalEditorManager";
