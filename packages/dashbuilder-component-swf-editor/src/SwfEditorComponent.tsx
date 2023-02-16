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
import React, { useEffect, useState, useCallback } from "react";

import { SwfStandaloneEditor } from "./SwfStandaloneEditor";
import { ComponentController } from "@kie-tools/dashbuilder-component-api";

const CONTENT_PARAM = "content";

const MISSING_PARAM_MSG =
  "You must provide either a YAML/JSON content or URL containing YAML/JSON using the swf-editor external component 'content' parameter.";

const isUrl = (param: string) => {
  return param && (param.trim().startsWith("http") || param.trim().startsWith("file:"));
};

const validateParams = (params: Map<string, string>) => {
  const content = params.get(CONTENT_PARAM);
  if (!content) {
    return MISSING_PARAM_MSG;
  }
};

interface AppState {
  content: string;
  errorMessage?: string;
}

interface Props {
  controller: ComponentController;
}

export function SwfEditorComponent(props: Props) {
  const [appState, setAppState] = useState<AppState>({ content: "" });

  const initialiseWithContent = useCallback(
    (params: Map<string, any>) => {
      const validationMessage = validateParams(params);
      if (validationMessage) {
        props.controller.requireConfigurationFix(validationMessage);
        setAppState((previousState) => ({
          ...previousState,
          errorMessage: validationMessage,
        }));
        return;
      }
      props.controller.configurationOk();

      const content = params.get(CONTENT_PARAM);

      if (!isUrl(content)) {
        setAppState((previousState) => ({
          ...previousState,
          content: content,
        }));
      } else if (isUrl(content)) {
        fetch(content)
          .then((r) => r.text())
          .then((urlContent) =>
            setAppState((previousState) => ({
              ...previousState,
              content: urlContent,
            }))
          )
          .catch((e) =>
            setAppState((previousState) => ({
              ...previousState,
              content: "",
              errorMessage: e,
            }))
          );
      }
    },
    [appState]
  );

  useEffect(() => props.controller.setOnInit(initialiseWithContent), [appState.content]);

  return <>{appState?.errorMessage ? <em>{appState.errorMessage}</em> : <SwfStandaloneEditor {...appState} />};</>;
}
