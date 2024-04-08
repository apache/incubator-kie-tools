/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { GitHubPageType } from "./app/github/GitHubPageType";
import { renderSingleEditorApp } from "./app/components/single/singleEditorEdit";
import { FileInfo, iframeContainer, renderSingleEditorReadonlyApp } from "./app/components/single/singleEditorView";
import { renderPrEditorsApp } from "./app/components/pr/prEditors";
import { mainContainer, runAfterUriChange } from "./app/utils";
import { Dependencies } from "./app/Dependencies";
import * as ReactDOM from "react-dom";
import { EditorEnvelopeLocator, KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import "../resources/style.css";
import { Logger } from "./Logger";
import { Globals } from "./app/components/common/Main";
import { ExternalEditorManager } from "./ExternalEditorManager";
import { ResourceContentServiceFactory } from "./app/components/common/ChromeResourceContentService";
import { renderOpenRepoInExternalEditorApp } from "./app/components/openRepoInExternalEditor/openRepoInExternalEditorApp";
import { StateControl } from "@kie-tools-core/editor/dist/channel";

/**
 * Starts a Kogito extension.
 *
 *  @param args.name The extension name. Used to differentiate logs from other extensions.
 *  @param args.extensionIconUrl The relative path to search for an image that will be the icon used for your extension.
 *  @param args.githubAuthTokenCookieName The name of the cookie that will hold a GitHub PAT for your extension.
 *  @param args.editorEnvelopeLocator The file extension mapping to the provided Editors.
 *  @param args.externalEditorManager The implementation of ExternalEditorManager for your extension.
 *  @param args.customChannelApiImpl Optional channelApi implementation.
 */
export function startExtension(args: {
  name: string;
  extensionIconUrl: string;
  githubAuthTokenCookieName: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  externalEditorManager?: ExternalEditorManager;
  getCustomChannelApiImpl?: (
    pageType: GitHubPageType,
    fileInfo: FileInfo,
    stateControl: StateControl
  ) => KogitoEditorChannelApi | undefined;
}) {
  const logger = new Logger(args.name);
  const resourceContentServiceFactory = new ResourceContentServiceFactory();
  const dependencies = new Dependencies();

  const runInit = () => {
    const pageType = discoverCurrentGitHubPageType();
    const fileInfo = extractFileInfoFromUrl();
    const stateControl = new StateControl();

    init({
      id: chrome.runtime.id,
      logger: logger,
      dependencies: dependencies,
      githubAuthTokenCookieName: args.githubAuthTokenCookieName,
      extensionIconUrl: args.extensionIconUrl,
      editorEnvelopeLocator: args.editorEnvelopeLocator,
      resourceContentServiceFactory: resourceContentServiceFactory,
      externalEditorManager: args.externalEditorManager,
      stateControl,
      customChannelApiImpl: args.getCustomChannelApiImpl?.(pageType, fileInfo, stateControl),
    });
  };

  runAfterUriChange(logger, () => setTimeout(runInit, 0));
  setTimeout(runInit, 0);
}

function init(globals: Globals) {
  globals.logger.log(`---`);
  globals.logger.log(`Starting GitHub extension.`);

  unmountPreviouslyRenderedFeatures(globals.id, globals.logger, globals.dependencies, globals.editorEnvelopeLocator);

  const fileInfo = extractFileInfoFromUrl();
  const pageType = discoverCurrentGitHubPageType();

  if (!globals.dependencies.all.octiconMarkGitHub() || pageType === GitHubPageType.NOT_SUPPORTED) {
    globals.logger.warn(
      `This is not supported GitHub web page. '${window.location.origin}${window.location.pathname}'`
    );
    return;
  }

  if (pageType === GitHubPageType.EDIT) {
    renderSingleEditorApp({ ...globals, fileInfo });
  } else if (pageType === GitHubPageType.VIEW) {
    if (!globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoFilesList()) {
      globals.logger.warn(
        "The extension stopped working for this asset view. Please be sure you explore the asset with the latest GitHub instance."
      );
    } else {
      renderSingleEditorReadonlyApp({
        ...globals,
        pageType,
        className: "btn ml-2 d-none d-md-block",
        container: () => globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoFilesList()!,
        fileInfo,
      });
    }
  } else if (
    pageType === GitHubPageType.PR_HOME ||
    pageType === GitHubPageType.PR_FILES ||
    pageType === GitHubPageType.PR_COMMITS
  ) {
    if (!globals.dependencies.openRepoInExternalEditor.buttonContainerOnPrs()) {
      globals.logger.warn(
        "The extension stopped working for this pull request view. Please be sure you explore the pull request on the latest GitHub instance."
      );
    } else {
      renderPrEditorsApp({
        ...globals,
        pageType,
        className: "btn btn-sm",
        container: () => globals.dependencies.openRepoInExternalEditor.buttonContainerOnPrs()!,
      });
    }
  } else if (pageType === GitHubPageType.REPO_HOME) {
    if (!globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoHome()) {
      globals.logger.warn(
        "The extension stopped working for this repository view. Please be sure you explore the repository on the latest GitHub instance."
      );
    } else {
      renderOpenRepoInExternalEditorApp({
        ...globals,
        pageType,
        className: "btn btn-sm",
        container: () => globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoHome()!,
      });
    }
  } else if (pageType === GitHubPageType.CAN_NOT_BE_DETERMINED_FROM_URL) {
    // if user uses [GITHUBINSTANCE_BASE_URL]/[ORG]/[REPO] we can not determine it from the url, but it may be still valid scenario for this chrome-extension
    if (!globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoHome()) {
      globals.logger.warn(
        `The extension stopped working for this view '${window.location.origin}${window.location.pathname}'. Please be sure you explore repository, asset or pull request on the latest GitHub instance.`
      );
    } else {
      // presence of the 'buttonContainerOnRepoHome' locator should mean, user is really on the repository home screen view
      renderOpenRepoInExternalEditorApp({
        ...globals,
        pageType,
        className: "btn btn-sm",
        container: () => globals.dependencies.openRepoInExternalEditor.buttonContainerOnRepoHome()!,
      });
    }
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

function unmountPreviouslyRenderedFeatures(
  id: string,
  logger: Logger,
  dependencies: Dependencies,
  editorEnvelopeLocator: EditorEnvelopeLocator
) {
  try {
    if (mainContainer(id, dependencies.all.body())) {
      ReactDOM.unmountComponentAtNode(mainContainer(id, dependencies.all.body())!);
      logger.log("Unmounted previous features.");
    }
    switchHiddenCss(id, dependencies, editorEnvelopeLocator);
  } catch (e) {
    logger.log("Ignoring exception while unmounting features.");
  }
}

function pathnameMatches(regex: string) {
  return !!window.location.pathname.match(new RegExp(regex));
}

function switchHiddenCss(id: string, dependencies: Dependencies, editorEnvelopeLocator: EditorEnvelopeLocator) {
  if (!editorEnvelopeLocator.getEnvelopeMapping(window.location.pathname)) {
    dependencies.singleView.githubTextEditorToReplaceElement()?.classList.remove("hidden");
    iframeContainer(id, dependencies)?.classList.add("hidden");
  } else {
    dependencies.singleView.githubTextEditorToReplaceElement()!.classList.add("hidden");
    iframeContainer(id, dependencies)?.classList.remove("hidden");
  }
}

export function discoverCurrentGitHubPageType() {
  if (pathnameMatches(`.*/.*/edit/.*`)) {
    return GitHubPageType.EDIT;
  }

  if (pathnameMatches(`.*/.*/blob/.*`)) {
    return GitHubPageType.VIEW;
  }

  const isOrgSlashRepoSlashTreeSlashName =
    window.location.pathname.split("/tree/").length === 2 && !window.location.pathname.split("/tree/")[1].includes("/");

  if (isOrgSlashRepoSlashTreeSlashName) {
    return GitHubPageType.REPO_HOME;
  }

  if (pathnameMatches(`.*/.*/pull/[0-9]+/files.*`)) {
    return GitHubPageType.PR_FILES;
  }

  if (pathnameMatches(`.*/.*/pull/[0-9]+/commits.*`)) {
    return GitHubPageType.PR_COMMITS;
  }

  if (pathnameMatches(`^.*/.*/pull/[0-9]+$`)) {
    return GitHubPageType.PR_HOME;
  }

  if (["/tree/", "/pull/"].some((pathnamePart) => window.location.pathname.includes(pathnamePart))) {
    // if pathanme containing one of these substrings and didn't match previous `if` statements, then it is not supperted by our extension
    // .../tree/main/some/folders/only
    // .../pull/1/checks
    return GitHubPageType.NOT_SUPPORTED;
  }

  return GitHubPageType.CAN_NOT_BE_DETERMINED_FROM_URL;
}

export * from "./ExternalEditorManager";
