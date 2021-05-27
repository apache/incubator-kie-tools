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

interface DesktopDictionary extends ReferenceDictionary {
  app: {
    title: string;
  };
  editorPage: {
    textEditorModal: {
      title: (fileName: string) => string;
    };
    alerts: {
      setContentError: {
        action: string;
        title: string;
      };
      copy: string;
      unsaved: {
        title: string;
        message: string;
        closeWithoutSaving: string;
      };
      saved: string;
      previewSaved: string;
    };
  };
  filesPage: {
    alerts: {
      errorFetchingFile: string;
      unexpectedErrorFetchingFile: string;
    };
    errorDetails: string;
    files: {
      title: string;
      bpmn: {
        blank: string;
        sample: string;
      };
      dmn: {
        blank: string;
        sample: string;
      };
    };
    openUrl: {
      initial: string;
      invalidExtension: string;
      invalidUrl: string;
      notFoundUrl: string;
      openFromSource: string;
      description: string;
    };
    recent: {
      title: string;
      noFilesYet: string;
    };
  };
  homePage: {
    learnMore: string;
  };
  learnMorePage: {
    readMore: string;
    bpmn: {
      title: string;
      explanation: string;
      create: string;
    };
    dmn: {
      title: string;
      explanation: string;
      learn: string;
      create: string;
    };
    about: string;
    editorsExplanation: string;
    getChromeExtension: string;
    getVsCodeExtension: string;
    redHatOpenSource: string;
    kogitoWebsite: string;
  };
}

export interface DesktopI18n extends DesktopDictionary, CommonI18n {}
