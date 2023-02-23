/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useEffect, useRef, useState, useCallback } from "react";
import * as SwfEditor from "@kie-tools/serverless-workflow-standalone-editor/dist/swf";

export interface SwfEditorProps {
  content: string;
}
export function isContentJson(content: string) {
  try {
    const json = JSON.parse(content);
    if (json && typeof json === "object") {
      return true;
    }
  } catch (e) {
    return false;
  }
  return false;
}

export function SwfStandaloneEditor(props: SwfEditorProps) {
  const swfEditorContainer = useRef<HTMLDivElement>(null);
  const initEditor = useCallback((content: string) => {
    SwfEditor.open({
      container: swfEditorContainer.current!,
      initialContent: Promise.resolve(content),
      readOnly: true,
      languageType: isContentJson(content) ? "json" : "yaml",
      swfPreviewOptions: { editorMode: "diagram" },
    });
  }, []);
  const [editor, setEditor] = useState<any>();

  useEffect(() => {
    if (props.content) {
      setEditor(initEditor(props.content));
    }
  }, [props.content]);

  return <div ref={swfEditorContainer} style={{ height: "calc(100% - 50px)" }} />;
}
