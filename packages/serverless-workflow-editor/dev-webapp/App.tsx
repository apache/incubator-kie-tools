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

import { ServerlessWorkflowEditor } from "../src";
import * as React from "react";
import { useRef, useState } from "react";
import { ServerlessWorkflowEmptyState } from "./EmptyState";
import type { Property } from "csstype";
import { HistoryButtons, Theme } from "./HistoryButtons";
import "./App.scss";
import { EditorApi } from "@kie-tools-core/editor/dist/api";

type State = string | undefined;

export const App = () => {
  const [content, setContent] = useState<State>(undefined);
  const editor = useRef<EditorApi>();

  const displayServerlessWorkflowEditor = (): Property.Display => {
    return content === undefined ? "none" : "block";
  };

  const undo = (): void => {
    editor.current!.undo().finally();
  };

  const redo = (): void => {
    editor.current!.redo().finally();
  };

  const validate = () => {
    editor.current!.validate().then((notifications) => {
      window.alert(JSON.stringify(notifications, undefined, 2));
    });
  };

  const container = useRef<HTMLDivElement | null>(null);

  return (
    <div>
      {content === undefined && (
        <ServerlessWorkflowEmptyState
          newContent={(type: string) => {
            setContent("");
            editor.current!.setContent(`new-document.sw.${type}`, "").finally();
          }}
          setContent={(path: string, content: string) => {
            setContent(content);
            editor.current!.setContent(path, content).finally();
          }}
        />
      )}
      <div style={{ display: displayServerlessWorkflowEditor() }}>
        <HistoryButtons
          undo={undo}
          redo={redo}
          get={() => editor.current!.getContent()}
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
          <ServerlessWorkflowEditor
            ref={editor}
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
