/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { EditorType, EmbeddedEditorRouter, File, StateControl } from "@kogito-tooling/embedded-editor";
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly.css";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GithubService } from "./common/GithubService";
import { GlobalContext } from "./common/GlobalContext";
import { Routes } from "./common/Routes";
import { extractFileExtension } from "./common/utils";
import { EditorPage } from "./editor/EditorPage";
import { DownloadHubModal } from "./home/DownloadHubModal";
import { HomePage } from "./home/HomePage";
import { NoMatchPage } from "./NoMatchPage";
import "../static/resources/style.css";

interface Props {
  file: File;
  readonly: boolean;
  external: boolean;
  senderTabId?: string;
  githubService: GithubService;
}

export function App(props: Props) {
  const [file, setFile] = useState(props.file);
  const routes = useMemo(() => new Routes(), []);
  const router: EmbeddedEditorRouter = useMemo(
    () =>
      new EmbeddedEditorRouter(
        new GwtEditorRoutes({
          dmnPath: "gwt-editors/dmn",
          bpmnPath: "gwt-editors/bpmn",
          scesimPath: "gwt-editors/scesim"
        })
      ),
    []
  );
  const stateControl = useMemo(() => new StateControl(), []);

  const onFileOpened = useCallback(fileOpened => {
    setFile(fileOpened);
  }, []);

  const onFileNameChanged = useCallback(
    (fileName: string) => {
      setFile({
        isReadOnly: false,
        editorType: extractFileExtension(fileName) as EditorType,
        fileName: fileName,
        getFileContents: file.getFileContents
      });
    },
    [file]
  );

  return (
    <GlobalContext.Provider
      value={{
        file: file,
        routes: routes,
        router: router,
        readonly: props.readonly,
        external: props.external,
        senderTabId: props.senderTabId,
        githubService: props.githubService,
        stateControl: stateControl
      }}
    >
      <HashRouter>
        <Switch>
          <Route path={routes.editor.url({ type: ":type" })}>
            <EditorPage onFileNameChanged={onFileNameChanged} />
          </Route>
          <Route exact={true} path={routes.home.url({})}>
            <HomePage onFileOpened={onFileOpened} />
          </Route>
          <Route exact={true} path={routes.downloadHub.url({})}>
            <HomePage onFileOpened={onFileOpened} />
            <DownloadHubModal />
          </Route>
          <Route component={NoMatchPage} />
        </Switch>
      </HashRouter>
    </GlobalContext.Provider>
  );
}
