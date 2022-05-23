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

import { ReferenceDictionary, Wrapped } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface AppDictionary extends ReferenceDictionary {
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
      updateGist: string;
      createGist: string;
      errorPushingGist: string;
      forcePushWarning: string;
      invalidCurrentGist: string;
      invalidGistFilename: string;
      error: string;
      unsaved: {
        message: string;
        titleLocal: string;
        titleGit: string;
        proceedAnyway: string;
      };
    };
  };
  editorToolbar: {
    closeAndReturnHome: string;
    saveAndDownload: string;
    sendChangesToGitHub: string;
    copySource: string;
    downloadSVG: string;
    setGitHubToken: string;
    createGist: string;
    cantCreateGistTooltip: string;
    cantUpdateGistTooltip: string;
    share: string;
    embed: string;
  };
  openshift: {
    common: {
      deployYourModel: string;
      deployInstanceInfo: string;
      disclaimer: string;
      learnMore: string;
      requiredField: string;
      deploying: string;
      saving: string;
      setupFirst: string;
    };
    configWizard: {
      header: {
        provider: string;
      };
      steps: {
        first: {
          name: string;
          introduction: string;
          goToGetStartedPage: string;
          followSteps: string;
          informNamespace: string;
          inputReason: string;
          namespacePlaceholder: string;
        };
        second: {
          name: string;
          introduction: string;
          accessLoginCommand: string;
          accessDisplayToken: string;
          copyInformation: string;
          inputReason: string;
          hostPlaceholder: string;
          tokenPlaceholder: string;
        };
        final: {
          name: string;
          connectionError: string;
          connectionSuccess: string;
          introduction: string;
          configNote: string;
          connectionErrorLong: string;
          checkInfo: string;
          possibleErrorReasons: {
            introduction: string;
            emptyField: string;
            tokenExpired: string;
            instanceExpired: string;
          };
        };
      };
    };
    configModal: {
      hostInfo: string;
      namespaceInfo: string;
      tokenInfo: string;
      validationError: string;
      connectionError: string;
      configExpiredWarning: string;
      useWizard: string;
    };
  };
  embedModal: {
    title: string;
    description: string;
    copy: string;
    source: {
      current: {
        label: string;
        description: string;
      };
      gist: {
        tooltip: string;
        label: string;
        description: string;
      };
    };
    embedCode: string;
    copiedToClipboard: string;
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
  notificationsPanel: {
    name: string;
    tooltip: {
      retractAll: string;
      expandAll: string;
    };
  };
  kieSandboxExtendedServices: {
    dropdown: {
      shortConnected: (port: string) => string;
      tooltip: {
        connected: string;
        install: string;
        outdated: string;
        disconnected: string;
      };
    };
    modal: {
      initial: {
        subHeader: string;
      };
    };
  };
}

export interface AppI18n extends AppDictionary, CommonI18n {}
