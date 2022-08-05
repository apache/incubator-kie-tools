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
import { GLOB_PATTERN } from "../extension";

export const EditorEnvelopeLocatorContext = React.createContext<EditorEnvelopeLocator>({} as any);

export function EditorEnvelopeLocatorContextProvider(props: { children: React.ReactNode }) {
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "swf",
          filePathGlob: GLOB_PATTERN.sw,
          resourcesPathPrefix: ".",
          envelopePath: "serverless-workflow-combined-editor-envelope.html",
        }),
        new EnvelopeMapping({
          type: "dash",
          filePathGlob: GLOB_PATTERN.dash,
          resourcesPathPrefix: "",
          envelopePath: "dashbuilder-editor-envelope.html",
        }),
        new EnvelopeMapping({
          type: "text",
          filePathGlob: GLOB_PATTERN.all,
          resourcesPathPrefix: "",
          envelopePath: "text-editor-envelope.html",
        }),
      ]),
    []
  );

  const value = useMemo(() => editorEnvelopeLocator, [editorEnvelopeLocator]);

  return <EditorEnvelopeLocatorContext.Provider value={value}>{props.children}</EditorEnvelopeLocatorContext.Provider>;
}

export function useEditorEnvelopeLocator() {
  return useContext(EditorEnvelopeLocatorContext);
}
