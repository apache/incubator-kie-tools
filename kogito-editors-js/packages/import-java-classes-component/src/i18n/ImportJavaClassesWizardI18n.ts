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

import { ReferenceDictionary } from "@kogito-tooling/i18n/dist/core";
import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";

interface ImportJavaClassesWizardDictionary extends ReferenceDictionary<ImportJavaClassesWizardDictionary | unknown> {
  modalButton: {
    text: string;
  };
  modalWizard: {
    title: string;
    description: string;
    firstStep: {
      stepName: string;
      input: {
        title: string;
        placeholder: string;
      };
      emptyState: {
        title: string;
        body: string;
      };
    };
    secondStep: {
      stepName: string;
    };
    thirdStep: {
      stepName: string;
      nextButtonText: string;
    };
  };
}

export interface ImportJavaClassesWizardI18n extends ImportJavaClassesWizardDictionary, CommonI18n {}
