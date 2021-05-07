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

interface ChromeExtensionDictionary extends ReferenceDictionary {
  openIn: (text: string) => string;
  seeAsDiagram: string;
  tree: {
    openInOnlineEditor: string;
  };
  single: {
    exitFullScreen: string;
    editorToolbar: {
      fixAndSeeAsDiagram: string;
      errorOpeningFile: string;
      seeAsSource: string;
      copyLinkTo: (text: string) => string;
      linkCopied: string;
      readOnly: string;
    };
  };
  pr: {
    isolated: {
      viewOriginal: string;
    };
    toolbar: {
      closeDiagram: string;
      original: string;
      changes: string;
    };
  };
  common: {
    menu: {
      createToken: string;
      placeYourToken: string;
      tokenInfo: {
        title: string;
        disclaimer: string;
        explanation: string;
        whichPermissionUserGive: string;
        permission: string;
      };
    };
  };
}

export interface ChromeExtensionI18n extends ChromeExtensionDictionary, CommonI18n {}
