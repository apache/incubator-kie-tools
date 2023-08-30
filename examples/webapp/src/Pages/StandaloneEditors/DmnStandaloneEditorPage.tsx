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
import { useEffect, useRef } from "react";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import * as DmnEditor from "@kie-tools/kie-editors-standalone/dist/dmn";

export function DmnStandaloneEditorPage() {
  const dmnEditorContainer = useRef<HTMLDivElement>(null);
  const unsavedChanges = useRef<HTMLSpanElement>(null);
  const undo = useRef<HTMLButtonElement>(null);
  const redo = useRef<HTMLButtonElement>(null);
  const download = useRef<HTMLButtonElement>(null);
  const downloadSvg = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    const editor = DmnEditor.open({
      container: dmnEditorContainer.current!,
      initialContent: Promise.resolve(""),
      readOnly: false,
    });

    undo.current?.addEventListener("click", () => {
      editor.undo();
    });

    redo.current?.addEventListener("click", () => {
      editor.redo();
    });

    download.current?.addEventListener("click", () => {
      editor.getContent().then((content) => {
        const elem = window.document.createElement("a");
        elem.href = "data:text/plain;charset=utf-8," + encodeURIComponent(content);
        elem.download = "model.dmn";
        document.body.appendChild(elem);
        elem.click();
        document.body.removeChild(elem);
        editor.markAsSaved();
      });
    });

    downloadSvg.current?.addEventListener("click", () => {
      editor.getPreview().then((svgContent) => {
        const elem = window.document.createElement("a");
        elem.href = "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svgContent!);
        elem.download = "model.svg";
        document.body.appendChild(elem);
        elem.click();
        document.body.removeChild(elem);
      });
    });

    editor.subscribeToContentChanges((isDirty) => {
      if (isDirty) {
        unsavedChanges.current!.style.display = "";
      } else {
        unsavedChanges.current!.style.display = "none";
      }
    });
  }, []);

  return (
    <Page>
      <div style={{ height: "40px", padding: "5px" }}>
        <button ref={undo}>Undo</button>
        <button ref={redo}>Redo</button>
        <button ref={download}>Download</button>
        <button ref={downloadSvg}>Download SVG</button>
        <span ref={unsavedChanges} style={{ display: "none" }}>
          File contains unsaved changes.
        </span>
      </div>
      <div ref={dmnEditorContainer} style={{ height: "calc(100% - 50px)" }} />
    </Page>
  );
}
