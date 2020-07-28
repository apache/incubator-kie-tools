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

import { File } from "@kogito-tooling/embedded-editor";
import * as React from "react";
import { GithubService } from "./GithubService";
import { Routes } from "./Routes";
import { EditorEnvelopeLocator } from "@kogito-tooling/editor-envelope-protocol";

export interface GlobalContextType {
  file: File;
  routes: Routes;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  readonly: boolean;
  external: boolean;
  senderTabId?: string;
  githubService: GithubService;
}

export const GlobalContext = React.createContext<GlobalContextType>({} as any);
