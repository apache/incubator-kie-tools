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
import { createRoot } from "react-dom/client";
import { createPortal } from "react-dom";
import * as React from "react";
import { PrEditorsApp } from "./PrEditorsApp";
import {
  createAndGetMainContainer,
  openRepoInExternalEditorContainer,
  removeAllChildren,
  setReactRoot,
} from "../../utils";
import { Globals, Main } from "../common/Main";
import {
  KOGITO_IFRAME_CONTAINER_PR_CLASS,
  KOGITO_OPEN_REPO_IN_EXTERNAL_EDITOR_CONTAINER_CLASS,
  KOGITO_TOOLBAR_CONTAINER_PR_CLASS,
  KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS,
} from "../../constants";
import { Dependencies } from "../../Dependencies";
import { PrInfo } from "./IsolatedPrEditor";
import { OpenInExternalEditorButton } from "../openRepoInExternalEditor/OpenInExternalEditorButton";
import { GitHubPageType } from "../../github/GitHubPageType";

export function renderPrEditorsApp(
  args: Globals & {
    className: string;
    pageType: GitHubPageType.PR_COMMITS | GitHubPageType.PR_FILES | GitHubPageType.PR_HOME;
    container: () => HTMLElement;
  }
) {
  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup(args.id);

  const container = createAndGetMainContainer(args.id, args.dependencies.all.body());
  const root = createRoot(container);
  setReactRoot(args.id, root);
  root.render(
    <Main
      id={args.id}
      editorEnvelopeLocator={args.editorEnvelopeLocator}
      dependencies={args.dependencies}
      logger={args.logger}
      githubAuthTokenCookieName={args.githubAuthTokenCookieName}
      extensionIconUrl={args.extensionIconUrl}
      resourceContentServiceFactory={args.resourceContentServiceFactory}
      externalEditorManager={args.externalEditorManager}
    >
      <PrEditorsApp prInfo={parsePrInfo(args.dependencies)} pageType={args.pageType} />
      {createPortal(
        <OpenInExternalEditorButton className={args.className} pageType={args.pageType} />,
        openRepoInExternalEditorContainer(args.id, args.container()!)
      )}
    </Main>
  );
  setTimeout(() => args.logger.log("Mounted."), 0);
}

export function parsePrInfo(dependencies: Dependencies): PrInfo {
  const prInfos = dependencies.all.array.pr__prInfoContainer()!.map((e) => e.textContent!);

  const targetOrganization = window.location.pathname.split("/")[1];
  const repository = window.location.pathname.split("/")[2];
  // PR is within the same organization
  if (prInfos.length < 6) {
    return {
      repo: repository,
      targetOrg: targetOrganization,
      targetGitRef: prInfos[1],
      org: targetOrganization,
      gitRef: prInfos[3],
    };
  }

  // PR is from a fork to an upstream
  return {
    repo: repository,
    targetOrg: targetOrganization,
    targetGitRef: prInfos[2],
    org: prInfos[4],
    gitRef: prInfos[5],
  };
}

function cleanup(id: string) {
  Array.from(document.querySelectorAll(`.${KOGITO_IFRAME_CONTAINER_PR_CLASS}.${id}`)).forEach((e) => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(`.${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}.${id}`)).forEach((e) => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(`.${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}.${id}`)).forEach((e) => {
    removeAllChildren(e);
  });

  Array.from(document.querySelectorAll(`.${KOGITO_OPEN_REPO_IN_EXTERNAL_EDITOR_CONTAINER_CLASS}.${id}`)).forEach(
    (e) => {
      removeAllChildren(e);
      e.remove();
    }
  );
}
