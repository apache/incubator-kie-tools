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
import { Router } from "@kogito-tooling/core-api";
import { GlobalContext, useGlobals } from "./GlobalContext";
import { Logger } from "../../../Logger";
import { GitHubContextProvider, useGitHubApi } from "./GitHubContext";
import * as ReactDOM from "react-dom";
import { KogitoMenu } from "./KogitoMenu";
import { Dependencies } from "../../Dependencies";
import { kogitoMenuContainer } from "../../utils";
import { ExternalEditorManager } from "../../../ExternalEditorManager";
import { ResourceContentServiceFactory } from "./ChromeResourceContentService";

export interface Globals {
  id: string;
  router: Router;
  logger: Logger;
  dependencies: Dependencies;
  githubAuthTokenCookieName: string;
  extensionIconUrl: string;
  editorIndexPath: string;
  resourceContentServiceFactory: ResourceContentServiceFactory;
  externalEditorManager?: ExternalEditorManager;
}

function KogitoMenuPortal(props: { id: string }) {
  const githubApi = useGitHubApi();
  const globals = useGlobals();

  return (
    <>
      {githubApi.userIsLoggedIn() &&
        ReactDOM.createPortal(
          <KogitoMenu />,
          kogitoMenuContainer(props.id, globals.dependencies.all.notificationIndicator()!.parentElement!)
        )}
    </>
  );
}

export const Main: React.FunctionComponent<Globals> = props => {
  return (
    <GlobalContext.Provider
      value={{
        id: props.id,
        logger: props.logger,
        dependencies: props.dependencies,
        router: props.router,
        githubAuthTokenCookieName: props.githubAuthTokenCookieName,
        extensionIconUrl: props.extensionIconUrl,
        editorIndexPath: props.editorIndexPath,
        resourceContentServiceFactory: props.resourceContentServiceFactory,
        externalEditorManager: props.externalEditorManager
      }}
    >
      <GitHubContextProvider>
        <KogitoMenuPortal id={props.id} />
        {props.children}
      </GitHubContextProvider>
    </GlobalContext.Provider>
  );
};
