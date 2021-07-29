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
import {
  importJavaClassesWizardI18nDictionaries,
  ImportJavaClassesWizardI18nContext,
  importJavaClassesWizardI18nDefaults,
} from "../../i18n";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";
import { ImportJavaClassesWizard } from "./ImportJavaClassesWizard";

export interface ImportJavaClassesProps {
  /** Button disabled status */
  buttonDisabledStatus: boolean;
  /** Button tooltip message */
  buttonTooltipMessage?: string;
}

export const ImportJavaClasses: React.FunctionComponent<ImportJavaClassesProps> = ({
  buttonDisabledStatus,
  buttonTooltipMessage,
}: ImportJavaClassesProps) => {
  return (
    <I18nDictionariesProvider
      defaults={importJavaClassesWizardI18nDefaults}
      dictionaries={importJavaClassesWizardI18nDictionaries}
      initialLocale={navigator.language}
      ctx={ImportJavaClassesWizardI18nContext}
    >
      <ImportJavaClassesWizard
        buttonDisabledStatus={buttonDisabledStatus}
        buttonTooltipMessage={buttonTooltipMessage}
      />
    </I18nDictionariesProvider>
  );
};
