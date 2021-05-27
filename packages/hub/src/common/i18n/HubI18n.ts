/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

interface HubDictionary extends ReferenceDictionary {
  alert: {
    launching: {
      title: string;
      try: string;
      directory: string;
    };
  };
  vscode: {
    title: string;
    description: string;
    installed: string;
    uninstalling: string;
  };
  chromeExtension: {
    title: string;
    description: string;
    modal: {
      title: string;
      chromeRequirement: string;
      chromeDownload: string;
      here: string;
      alreadyHaveChrome: string;
      firstStep: {
        firstPart: string;
        secondPart: string;
      };
      secondStep: string;
      thirdStep: string;
      done: {
        firstPart: string;
        secondPart: string;
      };
    };
  };
  desktop: {
    title: string;
    description: string;
  };
  online: {
    title: string;
    description: string;
  };
  noUpdates: string;
}

export interface HubI18n extends HubDictionary, CommonI18n {}
