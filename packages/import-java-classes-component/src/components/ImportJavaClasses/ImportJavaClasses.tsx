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
import { useCallback, useState } from "react";
import {
  importJavaClassesWizardI18nDictionaries,
  ImportJavaClassesWizardI18nContext,
  importJavaClassesWizardI18nDefaults,
  useImportJavaClassesWizardI18n,
} from "../../i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { GWTLayerService, JavaCodeCompletionService } from "./services";
import { JavaClass } from "./model";

import {
  ImportJavaClassesButton,
  ImportJavaClassesWizard,
  useLanguageServerAvailable,
} from "./ImportJavaClassesWizard";

interface ImportJavaClassesProps {
  /** Service class which contains all API method to dialog with GWT layer (can be removed when Stunner editor support is discontinued ) */
  gwtLayerService?: GWTLayerService;
  /** Service class which contains all API methods to dialog with Java Code Completion Extension*/
  javaCodeCompletionService: JavaCodeCompletionService;
  /** Callback function used to load Java classes into the data type editor.*/
  loadJavaClassesInDataTypeEditor?: (javaClasses: JavaClass[]) => void;
}

const ImportJavaClassesI18nDictionariesProvider = (
  props: Omit<
    React.ComponentProps<typeof I18nDictionariesProvider>,
    "defaults" | "dictionaries" | "initialLocale" | "ctx"
  >
) => (
  <I18nDictionariesProvider
    defaults={importJavaClassesWizardI18nDefaults}
    dictionaries={importJavaClassesWizardI18nDictionaries}
    initialLocale={navigator.language}
    ctx={ImportJavaClassesWizardI18nContext}
    {...props}
  />
);

const ImportJavaClasses = ({
  javaCodeCompletionService,
  gwtLayerService,
  loadJavaClassesInDataTypeEditor,
}: ImportJavaClassesProps) => {
  const [isOpenImportJavaClassesWizard, setOpenImportJavaClassesWizard] = useState(false);
  const handleButtonClick = useCallback(() => setOpenImportJavaClassesWizard((prevState) => !prevState), []);
  const handleWizardSave = useCallback(
    (javaClasses) => {
      /* If the GWT layer service is available, it uses the `importJavaClassesInDataTypeEditor` method.
       * Otherwise, it calls the `loadJavaClassesInDataTypeEditor` callback with the provided Java classes.
       */
      if (gwtLayerService) {
        gwtLayerService?.importJavaClassesInDataTypeEditor?.(javaClasses);
      } else {
        loadJavaClassesInDataTypeEditor?.(javaClasses);
      }
    },
    [gwtLayerService, loadJavaClassesInDataTypeEditor]
  );
  return (
    <ImportJavaClassesI18nDictionariesProvider>
      <ImportJavaClassesButton
        handleButtonClick={handleButtonClick}
        javaCodeCompletionService={javaCodeCompletionService}
      />
      {isOpenImportJavaClassesWizard && (
        <ImportJavaClassesWizard
          javaCodeCompletionService={javaCodeCompletionService}
          isOpen={isOpenImportJavaClassesWizard}
          onSave={handleWizardSave}
          onClose={handleButtonClick}
        />
      )}
    </ImportJavaClassesI18nDictionariesProvider>
  );
};

export {
  ImportJavaClassesProps,
  ImportJavaClassesI18nDictionariesProvider,
  ImportJavaClasses,
  ImportJavaClassesButton,
  ImportJavaClassesWizard,
  useLanguageServerAvailable,
  useImportJavaClassesWizardI18n,
};
