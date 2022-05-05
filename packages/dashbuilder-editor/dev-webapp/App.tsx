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

import { DashbuilderEditor } from "../src";
import * as React from "react";
import { useRef } from "react";
import "./App.scss";
import { ChannelType, EditorApi, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { isEmpty } from "underscore";

type State = string | undefined;

const IMPORT_PARAM = "import";
const PREVIEW_PARAM = "preview";
const DEFAULT_DASHBOARD_NAME = "new-dashboard.yml";

export const App = () => {
  const editor = useRef<EditorApi>();

  const urlQueryParams = new URLSearchParams(window.location.search);
  const importUrl = urlQueryParams.get(IMPORT_PARAM) || "";

  const urlHashParams = new URLSearchParams(window.location.hash.replace(/#/, "?"));
  const showPreviewParam = urlHashParams.get(PREVIEW_PARAM) === "true";

  React.useEffect(() => {
    if (importUrl !== null && !isEmpty(importUrl)) {
      fetch(importUrl)
        .then((req) => req.text())
        .then((data) => editor.current!.setContent(DEFAULT_DASHBOARD_NAME, data).finally())
        .catch((e) => editor.current!.setContent(DEFAULT_DASHBOARD_NAME, "").finally());
    } else {
      editor.current!.setContent(DEFAULT_DASHBOARD_NAME, "").finally();
    }
  }, []);

  const container = useRef<HTMLDivElement | null>(null);

  return (
    <Page>
      <PageSection padding={{ default: "noPadding" }} isFilled={true} hasOverflowScroll={false}>
        <div ref={container} className="editor-container">
          <DashbuilderEditor
            channelType={ChannelType.ONLINE}
            showEditor={showPreviewParam}
            ref={editor}
            onShowPreviewChange={(v) => (window.location.hash = `${PREVIEW_PARAM}=${v}`)}
            onReady={() => {
              /*NOP*/
            }}
            onNewEdit={() => {
              /*NOP*/
            }}
            setNotifications={() => {
              /*NOP*/
            }}
            onStateControlCommandUpdate={(command) => {
              if (command === StateControlCommand.UNDO) {
                editor.current?.undo();
              } else if (command === StateControlCommand.REDO) {
                editor.current?.redo();
              } else {
                console.log("Nothing to do.");
              }
            }}
          />
        </div>
      </PageSection>
    </Page>
  );
};
