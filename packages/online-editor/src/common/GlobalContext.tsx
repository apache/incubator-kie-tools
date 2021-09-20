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

import { File, newFile } from "@kie-tooling-core/editor/dist/channel";
import * as React from "react";
import { useContext, useMemo, useState } from "react";
import { routes } from "./Routes";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tooling-core/editor/dist/api";
import { useRouteMatch } from "react-router";

export type SupportedFileExtensions = "bpmn" | "bpmn2" | "dmn" | "pmml";

export interface GlobalContextType {
  file: File;
  setFile: React.Dispatch<React.SetStateAction<File>>;
  uploadedFile?: File;
  setUploadedFile: React.Dispatch<React.SetStateAction<File>>;
  externalFile?: File;
  routes: typeof routes;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  senderTabId?: string;
  isChrome: boolean;
}

export const GlobalContext = React.createContext<GlobalContextType>({} as any);

export function GlobalContextProvider(props: { externalFile?: File; senderTabId?: string; children: React.ReactNode }) {
  //FIXME: tiago: Move file to `EditorPage`.
  const match = useRouteMatch<{ extension: string }>(routes.editor.path({ extension: ":extension" }));
  const [file, setFile] = useState(() => newFile(match?.params.extension ?? "dmn"));
  const [uploadedFile, setUploadedFile] = useState<File | undefined>(undefined);
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () => ({
      targetOrigin: window.location.origin,
      mapping: new Map<SupportedFileExtensions, EnvelopeMapping>([
        ["bpmn", { resourcesPathPrefix: "gwt-editors/bpmn", envelopePath: "/bpmn-envelope.html" }],
        ["bpmn2", { resourcesPathPrefix: "gwt-editors/bpmn", envelopePath: "/bpmn-envelope.html" }],
        ["dmn", { resourcesPathPrefix: "gwt-editors/dmn", envelopePath: "/dmn-envelope.html" }],
        ["pmml", { resourcesPathPrefix: "", envelopePath: "/pmml-envelope.html" }],
      ]),
    }),
    []
  );

  return (
    <GlobalContext.Provider
      value={{
        ...props,
        editorEnvelopeLocator,
        file,
        setFile,
        uploadedFile,
        setUploadedFile,
        routes,
        isChrome: !!window.chrome,
      }}
    >
      {props.children}
    </GlobalContext.Provider>
  );
}

export function useGlobals() {
  return useContext(GlobalContext);
}
