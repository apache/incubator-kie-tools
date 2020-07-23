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

import { TranslationBundle } from "@kogito-tooling/i18n";
import { TermsBundle } from "@kogito-tooling/i18n-terms";

interface OnlineBundle extends TranslationBundle<OnlineBundle> {
  downloadHubModal: {
    beforeDownload: {
      title: string;
      vscodeDescription: string;
      githubChromeDescription: string;
      desktopDescription: string;
      businessModelerDescription: string;
      operationSystem: string;
    };
    afterDownload: {
      title: string;
    };
  };
}

export interface OnlineI18n extends TranslationBundle<OnlineBundle & TermsBundle> {}
