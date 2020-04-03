/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { Routes } from "./common/Routes";
import { HomePage } from "./home/HomePage";
import { EditorPage } from "./editor/EditorPage";
import { NoMatchPage } from "./NoMatchPage";
import { OnlineEditorRouter } from "./common/OnlineEditorRouter";
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";
import { GlobalContext } from "./common/GlobalContext";
import { EnvelopeBusOuterMessageHandlerFactory } from "./editor/EnvelopeBusOuterMessageHandlerFactory";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly.css";
import "../static/resources/style.css";
import { File } from "./common/File";
import { DownloadHubModal } from "./home/DownloadHubModal";

interface Props {
  iframeTemplateRelativePath: string;
  file: File;
  readonly: boolean;
  external: boolean;
  senderTabId?: string;
}

export function App(props: Props) {
  const [file, setFile] = useState(props.file);
  const routes = useMemo(() => new Routes(), []);
  const envelopeBusOuterMessageHandlerFactory = useMemo(() => new EnvelopeBusOuterMessageHandlerFactory(), []);
  const onlineEditorRouter = useMemo(
    () =>
      new OnlineEditorRouter(
        new GwtEditorRoutes({
          bpmnPath: "gwt-editors/bpmn",
          dmnPath: "gwt-editors/dmn",
          scesimPath: "gwt-editors/scesim"
        })
      ),
    []
  );

  const onFileOpened = useCallback(fileOpened => {
    setFile(fileOpened);
  }, []);

  const onFileNameChanged = useCallback(
    (fileName: string) => {
      setFile({
        fileName: fileName,
        getFileContents: file.getFileContents
      });
    },
    [file]
  );

  return (
    <GlobalContext.Provider
      value={{
        router: onlineEditorRouter,
        routes: routes,
        envelopeBusOuterMessageHandlerFactory: envelopeBusOuterMessageHandlerFactory,
        iframeTemplateRelativePath: props.iframeTemplateRelativePath,
        file: file,
        readonly: props.readonly,
        external: props.external,
        senderTabId: props.senderTabId
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
