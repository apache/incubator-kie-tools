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
import {
  importJavaClassesWizardI18nDictionaries,
  ImportJavaClassesWizardI18nContext,
  importJavaClassesWizardI18nDefaults,
} from "../../i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { ImportJavaClassesWizard } from "./ImportJavaClassesWizard";
import { GWTLayerService, JavaCodeCompletionService } from "./services";

export interface ImportJavaClassesProps {
  /** Service class which contains all API method to dialog with GWT layer */
  gwtLayerService: GWTLayerService;
  /** Service class which contains all API methods to dialog with Java Code Completion Extension*/
  javaCodeCompletionService: JavaCodeCompletionService;
}

export const ImportJavaClasses = ({ gwtLayerService, javaCodeCompletionService }: ImportJavaClassesProps) => {
  return (
    <I18nDictionariesProvider
      defaults={importJavaClassesWizardI18nDefaults}
      dictionaries={importJavaClassesWizardI18nDictionaries}
      initialLocale={navigator.language}
      ctx={ImportJavaClassesWizardI18nContext}
    >
      <ImportJavaClassesWizard
        gwtLayerService={gwtLayerService}
        javaCodeCompletionService={javaCodeCompletionService}
      />
    </I18nDictionariesProvider>
  );
};
