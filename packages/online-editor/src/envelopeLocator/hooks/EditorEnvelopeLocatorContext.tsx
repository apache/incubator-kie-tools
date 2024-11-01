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
import { useContext, useMemo } from "react";
import { EditorEnvelopeLocatorFactory, GLOB_PATTERN } from "../EditorEnvelopeLocatorFactory";
import { useEnv } from "../../env/hooks/EnvContext";
import { EditorConfig } from "../EditorEnvelopeLocatorApi";
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api/EditorEnvelopeLocator";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";

// FIXME: Chaging `any` to `EditorEnvelopeLocator` breaks --env live. Please adress this as part of https://github.com/apache/incubator-kie-issues/issues/109
export const EditorEnvelopeLocatorContext = React.createContext<EditorEnvelopeLocator>({} as any);

export const LEGACY_DMN_EDITOR_EDITOR_CONFIG: EditorConfig = {
  extension: FileTypes.DMN,
  filePathGlob: GLOB_PATTERN.dmn,
  editor: {
    resourcesPathPrefix: "gwt-editors/dmn",
    path: "dmn-envelope.html",
  },
  card: {
    title: "Decision",
    description: "DMN files are used to generate decision models",
  },
};

export function EditorEnvelopeLocatorContextProvider(props: { children: React.ReactNode }) {
  const editorsConfig = useEditorsConfig();
  const value = useMemo(
    () =>
      new EditorEnvelopeLocatorFactory().create({
        targetOrigin: window.location.origin,
        editorsConfig,
      }),
    [editorsConfig]
  );

  return <EditorEnvelopeLocatorContext.Provider value={value}>{props.children}</EditorEnvelopeLocatorContext.Provider>;
}

export function useEditorEnvelopeLocator() {
  return useContext(EditorEnvelopeLocatorContext);
}

export function useEditorsConfig() {
  const { env } = useEnv();
  return useMemo<EditorConfig[]>(() => env.KIE_SANDBOX_EDITORS, [env.KIE_SANDBOX_EDITORS]);
}
