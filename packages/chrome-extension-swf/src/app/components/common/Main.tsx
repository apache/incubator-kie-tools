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
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { Logger } from "../../../Logger";
import { Dependencies } from "../../Dependencies";
import { OpenShiftProvider } from "../../openshift/OpenShiftProvider";
import { ResourceContentServiceFactory } from "./ChromeResourceContentService";
import { GlobalContext, ImageUris } from "./GlobalContext";
import { ChromeExtensionI18nContextProvider } from "../../i18n";
import { SettingsContextProvider } from "../../settings/SettingsContext";
import { MemoryRouter } from "react-router-dom";
import { NavigationContextProvider } from "../../navigation/NavigationContextProvider";

export interface Globals {
  id: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  logger: Logger;
  dependencies: Dependencies;
  resourceContentServiceFactory: ResourceContentServiceFactory;
  imageUris: ImageUris;
}

export const Main: React.FunctionComponent<Globals> = (props) => {
  return (
    <ChromeExtensionI18nContextProvider>
      <MemoryRouter>
        <NavigationContextProvider>
          <SettingsContextProvider>
            <GlobalContext.Provider
              value={{
                id: props.id,
                logger: props.logger,
                dependencies: props.dependencies,
                envelopeLocator: props.editorEnvelopeLocator,
                imageUris: props.imageUris,
                resourceContentServiceFactory: props.resourceContentServiceFactory,
              }}
            >
              <OpenShiftProvider>{props.children}</OpenShiftProvider>
            </GlobalContext.Provider>
          </SettingsContextProvider>
        </NavigationContextProvider>
      </MemoryRouter>
    </ChromeExtensionI18nContextProvider>
  );
};
