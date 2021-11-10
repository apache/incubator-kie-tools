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

import * as React from "react";
import * as ReactDOM from "react-dom";
import { Globals, Main } from "../common/Main";
import { createAndGetMainContainer, openRepoInExternalEditorContainer } from "../../utils";
import { OpenInExternalEditorButton } from "./OpenInExternalEditorButton";
import { GitHubPageType } from "../../github/GitHubPageType";

export function renderOpenRepoInExternalEditorApp(
  args: Globals & { className: string; pageType: GitHubPageType; container: () => HTMLElement }
) {
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
        openRepoInExternalEditorContainer(args.id, args.container())
      )}
    </Main>,
    createAndGetMainContainer(args.id, args.dependencies.all.body()),
    () => args.logger.log("Mounted.")
  );
}
