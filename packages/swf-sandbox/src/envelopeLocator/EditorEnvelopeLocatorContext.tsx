/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import * as React from "react";
import { useContext, useMemo } from "react";

export type SupportedFileExtensions =
  | "sw.json"
  | "sw.yaml"
  | "sw.yml"
  | "yard.json"
  | "yard.yaml"
  | "yard.yml"
  | "dash.yml"
  | "dash.yaml"; //FIXME db?

export const EditorEnvelopeLocatorContext = React.createContext<EditorEnvelopeLocator>({} as any);

export function EditorEnvelopeLocatorContextProvider(props: { children: React.ReactNode }) {
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping("sw", "**/*.sw.+(json|yml|yaml)", "", "swf-editor-envelope.html"),
        new EnvelopeMapping("dash", "**/*.dash.+(yml|yaml)", "", "dashbuilder-editor-envelope.html"),
        new EnvelopeMapping("text", "**/*", "", "text-editor-envelope.html"),
      ]),
    []
  );

  const value = useMemo(() => editorEnvelopeLocator, [editorEnvelopeLocator]);

  return <EditorEnvelopeLocatorContext.Provider value={value}>{props.children}</EditorEnvelopeLocatorContext.Provider>;
}

export function useEditorEnvelopeLocator() {
  return useContext(EditorEnvelopeLocatorContext);
}
