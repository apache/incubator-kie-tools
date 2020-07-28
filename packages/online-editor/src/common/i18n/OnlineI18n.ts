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

import { Dictionary } from "@kogito-tooling/i18n";
import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";

interface OnlineDictionary extends Dictionary<OnlineDictionary> {
  downloadHubModal: {
    beforeDownload: {
      title: string;
      vscodeDescription: string;
      githubChromeExtension: {
        title: string;
        description: string;
      };
      desktop: {
        title: string;
        description: string;
      };
      businessModeler: {
        title: string;
        description: string;
      };
    };
    afterDownload: {
      title: string;
      message: string;
      link: string;
    };
  };
  editorFullScreenToolbar: string;
  editorPage: {
    alerts: {
      copy: string;
      unsaved: {
        title: string;
        message: string;
        closeWithoutSaving: string;
      };
    };
  };
  editorToolbar: {
    sendChangesToGitHub: string;
    copySource: string;
    gistIt: string;
    fileActions: string;
  };
  githubTokenModal: {
    header: {
      title: string;
      subtitle: string;
    };
    footer: {
      createNewToken: string;
      placeHolder: string;
    };
    body: {
      disclaimer: string;
      learnMore: string;
      note: string;
    };
  };
  homePage: {
    uploadFile: {
      dndZone: {
        invalidFile: string;
        waitingFile: string;
      };
      fileInput: string;
    };
    openUrl: {
      validating: string;
      invalidGistExtension: string;
      invalidExtension: string;
      invalidGist: string;
      invalidUrl: string;
      notFoundUrl: string;
      corsNotAvailable: string;
      openFromSource: string;
      description: string;
    };
    dropdown: {
      getHub: string;
      onlineForum: string;
    };
    header: {
      title: string;
      welcomeText: string;
    };
    bpmnCard: {
      title: string;
      explanation: string;
      createNew: string;
    };
    dmnCard: {
      title: string;
      explanation: string;
      createNew: string;
    };
    trySample: string;
    editExistingFile: string;
    chooseLocalFile: string;
  };
}

export interface OnlineI18n extends OnlineDictionary, CommonI18n {}
