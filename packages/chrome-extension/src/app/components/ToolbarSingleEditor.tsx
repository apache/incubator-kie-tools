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
import { useContext } from "react";
import { IsolatedEditorContext } from "./IsolatedEditorContext";

export function ToolbarSingleEditor(props: { readonly: boolean }) {
  const [isolatedEditorState, setIsolatedEditorState] = useContext(IsolatedEditorContext);

  const goFullScreen = (e: any) => {
    e.preventDefault();
    setIsolatedEditorState({ ...isolatedEditorState, fullscreen: true });
  };

  const seeAsSource = (e: any) => {
    e.preventDefault();
    setIsolatedEditorState({ ...isolatedEditorState, textMode: true });
  };

  const seeAsDiagram = (e: any) => {
    e.preventDefault();
    setIsolatedEditorState({ ...isolatedEditorState, textMode: false });
  };

  return (
    <>
      <div>
        {!isolatedEditorState.textMode && (
          <button
            disabled={!isolatedEditorState.textModeEnabled}
            className={"btn btn-sm kogito-button"}
            onClick={seeAsSource}
          >
            See as source
          </button>
        )}
        {isolatedEditorState.textMode && (
          <button className={"btn btn-sm kogito-button"} onClick={seeAsDiagram}>
            See as diagram
          </button>
        )}
        {!isolatedEditorState.textMode && (
          <button className={"btn btn-sm kogito-button"} onClick={goFullScreen}>
            Full screen
          </button>
        )}
      </div>
      {props.readonly &&
        !isolatedEditorState.textMode && (
          <>
            {/* TODO: Add "info" icon with hint explaining how to edit the file */}
            <h4>🔸️ This is a readonly visualization</h4>
          </>
        )}
    </>
  );
}
