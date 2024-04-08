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

import * as React from "react";
import * as ReactDOM from "react-dom";
import { Globals, Main } from "../common/Main";
import {
  createAndGetMainContainer,
  openRepoInExternalEditorContainer,
  openRepoInExternalEditorContainerFromRepositoryHome,
  removeAllChildren,
} from "../../utils";
import { OpenInExternalEditorButton } from "./OpenInExternalEditorButton";
import { GitHubPageType } from "../../github/GitHubPageType";
import { KOGITO_OPEN_REPO_IN_EXTERNAL_EDITOR_CONTAINER_CLASS } from "../../constants";

export function renderOpenRepoInExternalEditorApp(
  args: Globals & { className: string; pageType: GitHubPageType; container: () => HTMLElement }
) {
  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup(args.id);

  ReactDOM.render(
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
      {ReactDOM.createPortal(
        <OpenInExternalEditorButton className={args.className} pageType={args.pageType} />,
        GitHubPageType.REPO_HOME === args.pageType || GitHubPageType.CAN_NOT_BE_DETERMINED_FROM_URL === args.pageType
          ? openRepoInExternalEditorContainerFromRepositoryHome(args.id, args.container())
          : openRepoInExternalEditorContainer(args.id, args.container())
      )}
    </Main>,
    createAndGetMainContainer(args.id, args.dependencies.all.body()),
    () => args.logger.log("Mounted.")
  );
}

function cleanup(id: string) {
  Array.from(document.querySelectorAll(`.${KOGITO_OPEN_REPO_IN_EXTERNAL_EDITOR_CONTAINER_CLASS}.${id}`)).forEach(
    (e) => {
      removeAllChildren(e);
      e.remove();
    }
  );
}
