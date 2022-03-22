/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import * as React from "react";
import { Logger } from "../../Logger";
import { Dependencies } from "../Dependencies";
import { ResourceContentServiceFactory } from "./ChromeResourceContentService";

export interface GlobalContextType {
  id: string;
  envelopeLocator: EditorEnvelopeLocator;
  logger: Logger;
  dependencies?: Dependencies;
  resourceContentServiceFactory: ResourceContentServiceFactory;
  imagesUriPath: string;
  resourcesUriPath: string;
}

export const GlobalContext = React.createContext<GlobalContextType>({} as any);

export function useGlobals() {
  return React.useContext(GlobalContext);
}

export function useEditorEnvelopeLocator() {
  return React.useContext(GlobalContext).envelopeLocator;
}
