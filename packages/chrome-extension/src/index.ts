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
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import "../resources/style.css";
import { Logger } from "./Logger";
import { Globals } from "./app/components/common/Main";
import { ExternalEditorManager } from "./ExternalEditorManager";
import { ResourceContentServiceFactory } from "./app/components/common/ChromeResourceContentService";
import { renderOpenRepoInExternalEditorApp } from "./app/components/openRepoInExternalEditor/openRepoInExternalEditorApp";

/**
 * Starts a Kogito extension.
 *
 *  @param args.name The extension name. Used to differentiate logs from other extensions.
 *  @param args.extensionIconUrl The relative path to search for an image that will be the icon used for your extension.
 *  @param args.githubAuthTokenCookieName The name of the cookie that will hold a GitHub PAT for your extension.
 *  @param args.editorEnvelopeLocator The file extension mapping to the provided Editors.
 *  @param args.externalEditorManager The implementation of ExternalEditorManager for your extension.
 */
export function startExtension(args: {
  name: string;
  extensionIconUrl: string;
  githubAuthTokenCookieName: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
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
      extensionIconUrl: args.extensionIconUrl,
      editorEnvelopeLocator: args.editorEnvelopeLocator,
      resourceContentServiceFactory: resourceContentServiceFactory,
      externalEditorManager: args.externalEditorManager,
    });

  runAfterUriChange(logger, () => setTimeout(runInit, 0));
  setTimeout(runInit, 0);
}

function init(globals: Globals) {
  globals.logger.log(`---`);
  globals.logger.log(`Starting GitHub extension.`);

  unmountPreviouslyRenderedFeatures(globals.id, globals.logger, globals.dependencies);

  const fileInfo = extractFileInfoFromUrl();
  const pageType = discoverCurrentGitHubPageType();

  if (pageType === GitHubPageType.ANY) {
    globals.logger.log(`This GitHub page is not supported.`);
    return;
  }

  if (pageType === GitHubPageType.EDIT) {
    renderSingleEditorApp({ ...globals, fileInfo });
  } else if (pageType === GitHubPageType.VIEW) {
    renderSingleEditorReadonlyApp({ ...globals, fileInfo });
  } else if (pageType === GitHubPageType.PR_FILES_OR_COMMITS) {
    renderPrEditorsApp({ ...globals });
  } else if (pageType === GitHubPageType.PR_HOME) {
    renderOpenRepoInExternalEditorApp({
      ...globals,
      pageType,
      className: "btn btn-sm",
      container: () => globals.dependencies.openRepoInExternalEditor.buttonContainerOnPrs()!,
    });
  } else if (pageType === GitHubPageType.CAN_OPEN_REPO_IN_EXTERNAL_EDITOR) {
    renderOpenRepoInExternalEditorApp({
      ...globals,
      pageType,
      className: "btn ml-2 d-none d-md-block",
      container: () => globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoFilesList()!,
    });
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
    path: split.slice(5).join("/"),
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

function pathnameMatches(regex: string) {
  return !!window.location.pathname.match(new RegExp(regex));
}

export function discoverCurrentGitHubPageType() {
  if (pathnameMatches(`.*/.*/edit/.*`)) {
    return GitHubPageType.EDIT;
  }

  if (pathnameMatches(`.*/.*/blob/.*`)) {
    return GitHubPageType.VIEW;
  }

  const isOrgSlashRepo = window.location.pathname.split("/").length === 3;

  if (pathnameMatches(`/.*/.*/tree/.*`) || isOrgSlashRepo) {
    return GitHubPageType.CAN_OPEN_REPO_IN_EXTERNAL_EDITOR;
  }

  if (pathnameMatches(`.*/.*/pull/[0-9]+/files.*`)) {
    return GitHubPageType.PR_FILES_OR_COMMITS;
  }

  if (pathnameMatches(`.*/.*/pull/[0-9]+/commits.*`)) {
    return GitHubPageType.PR_FILES_OR_COMMITS;
  }

  if (pathnameMatches(`.*/.*/pull/[0-9]+.*`)) {
    return GitHubPageType.PR_HOME;
  }

  return GitHubPageType.ANY;
}

export * from "./ExternalEditorManager";
