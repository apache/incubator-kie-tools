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

import * as React from "react";
import { useContext, useMemo } from "react";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";

export type SupportedFileExtensions = "bpmn" | "bpmn2" | "BPMN" | "BPMN2" | "dmn" | "DMN" | "pmml" | "PMML";

export const EditorEnvelopeLocatorContext = React.createContext<EditorEnvelopeLocator>({} as any);

const bpmnEnvelope = { resourcesPathPrefix: "gwt-editors/bpmn", envelopePath: "bpmn-envelope.html" };
const dmnEnvelope = { resourcesPathPrefix: "gwt-editors/dmn", envelopePath: "dmn-envelope.html" };
const pmmlEnvelope = { resourcesPathPrefix: "", envelopePath: "pmml-envelope.html" };

export function EditorEnvelopeLocatorContextProvider(props: { children: React.ReactNode }) {
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () => ({
      targetOrigin: window.location.origin,
      mapping: new Map<SupportedFileExtensions, EnvelopeMapping>([
        ["bpmn", bpmnEnvelope],
        ["bpmn2", bpmnEnvelope],
        ["BPMN", bpmnEnvelope],
        ["BPMN2", bpmnEnvelope],
        ["dmn", dmnEnvelope],
        ["DMN", dmnEnvelope],
        ["pmml", pmmlEnvelope],
        ["PMML", pmmlEnvelope],
      ]),
    }),
    []
  );

  const value = useMemo(() => editorEnvelopeLocator, [editorEnvelopeLocator]);

  return <EditorEnvelopeLocatorContext.Provider value={value}>{props.children}</EditorEnvelopeLocatorContext.Provider>;
}

export function useEditorEnvelopeLocator() {
  return useContext(EditorEnvelopeLocatorContext);
}
