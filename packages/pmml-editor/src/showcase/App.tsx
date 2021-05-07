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

import { PMMLEditor } from "../editor";
import * as React from "react";
import { useRef, useState } from "react";
import { PMMLEmptyState } from "./EmptyState";
import { Property } from "csstype";
import { HistoryButtons, Theme } from "./HistoryButtons";
import { Notification } from "@kogito-tooling/notifications/dist/api";
import "./App.scss";

let editor: PMMLEditor;

type State = string | undefined;

export const App = () => {
  const [content, setContent] = useState<State>(undefined);

  const displayPMMLEditor = (): Property.Display => {
    return content === undefined ? "none" : "block";
  };

  const undo = (): void => {
    editor.undo().finally();
  };

  const redo = (): void => {
    editor.redo().finally();
  };

  const validate = () => {
    const notifications: Notification[] = editor.validate();
    window.alert(JSON.stringify(notifications, undefined, 2));
  };

  const container = useRef<HTMLDivElement | null>(null);

  return (
    <div>
      {content === undefined && (
        <PMMLEmptyState
          newContent={() => {
            setContent("");
            editor.setContent("New document", "").finally();
          }}
          setContent={(path: string, xml: string) => {
            setContent(xml);
            editor.setContent(path, xml).finally();
          }}
        />
      )}
      <div style={{ display: displayPMMLEditor() }}>
        <HistoryButtons
          undo={undo}
          redo={redo}
          get={() => editor.getContent()}
          setTheme={(theme) => {
            if (container.current) {
              if (theme === Theme.DARK) {
                container.current?.classList.add("vscode-dark");
              } else {
                container.current?.classList.remove("vscode-dark");
              }
            }
          }}
          validate={validate}
        />
        <div ref={container} className="editor-container">
          <PMMLEditor
            exposing={(self: PMMLEditor) => (editor = self)}
            ready={() => {
              /*NOP*/
            }}
            newEdit={() => {
              /*NOP*/
            }}
            setNotifications={() => {
              /*NOP*/
            }}
          />
        </div>
      </div>
    </div>
  );
};
