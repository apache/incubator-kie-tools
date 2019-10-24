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
import { ResolvedDomDependencyArray } from "../../dependencies";
import { Feature } from "../common/Feature";
import { PrInformation } from "./IsolatedPrEditor";
import { Logger } from "../../../Logger";

export function renderPrEditorsApp(args: { logger: Logger; editorIndexPath: string; router: Router }) {
  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup();

  ReactDOM.render(
    <Main
      router={args.router}
      logger={args.logger}
      editorIndexPath={args.editorIndexPath}
      commonDependencies={dependencies__.prView}
    >
      <Feature
        name={"Editors directly on PR screen"}
        dependencies={deps => ({ prInfoContainer: () => deps.all.array.pr__prInfoContainer() })}
        component={resolved => (
          <PrEditorsApp prInfo={parsePrInfo(resolved.prInfoContainer as ResolvedDomDependencyArray)} />
        )}
      />
    </Main>,
    createAndGetMainContainer({ name: "", element: dependencies__.all.body() }),
    () => args.logger.log("Mounted.")
  );
}

function parsePrInfo(prInfoContainer: ResolvedDomDependencyArray): PrInformation {
  const prInfos = prInfoContainer.element.map(e => e.textContent!);

  const targetOrganization = window.location.pathname.split("/")[1];
  const repository = window.location.pathname.split("/")[2];

  // PR is within the same organization
  if (prInfos.length < 6) {
    return {
      repository: repository,
      targetOrganization: targetOrganization,
      targetGitReference: prInfos[1],
      organization: targetOrganization,
      gitReference: prInfos[3]
    };
  }

  // PR is from a fork to an upstream
  return {
    repository: repository,
    targetOrganization: targetOrganization,
    targetGitReference: prInfos[2],
    organization: prInfos[4],
    gitReference: prInfos[5]
  };
}

function cleanup() {
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
