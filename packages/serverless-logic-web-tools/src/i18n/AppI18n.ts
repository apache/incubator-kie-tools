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
  };
  openshift: {
    introduction: {
      explanation: string;
    };
    configModal: {
      hostInfo: string;
      namespaceInfo: string;
      tokenInfo: string;
      validationError: string;
      connectionError: string;
      configExpiredWarning: string;
    };
    confirmModal: {
      title: string;
      body: string;
    };
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
  extendedServices: {
    modal: {
      wizard: {
        title: string;
        description: string;
        outdatedAlert: {
          title: string;
          message: string;
        };
        stoppedAlert: {
          title: string;
          message: string;
        };
        macos: {
          install: {
            download: string;
            openFile: Array<string | Wrapped<"file">>;
            dragFileToApplicationsFolder: Array<string | Wrapped<"file" | "folder">>;
          };
          start: {
            stopped: {
              startInstruction: string;
              launchExtendedServices: Array<string | Wrapped<"file">>;
            };
            firstTime: {
              title: string;
              openApplicationsFolder: Array<string | Wrapped<"folder">>;
              openAndCancel: Array<string | Wrapped<"file">>;
              again: string;
              openInstruction: Array<string | Wrapped<"file" | "again">>;
            };
            alreadyRanBefore: string;
            launchExtendedServices: Array<string | Wrapped<"file">>;
            advanced: {
              title: string;
              runFollowingCommand: string;
            };
          };
        };
        windows: {
          install: {
            keepDownload: string;
            moveTheFile: Array<string | Wrapped<"file">>;
          };
          start: {
            stopped: {
              startInstruction: string;
              launchExtendedServices: Array<string | Wrapped<"file">>;
            };
            firstTime: {
              title: string;
              openFolder: Array<string | Wrapped<"file">>;
              runAnyway: string;
            };
            alreadyRanBefore: string;
            launchExtendedServices: Array<string | Wrapped<"file">>;
            advanced: {
              title: string;
              runFollowingCommand: string;
            };
          };
        };
        linux: {
          install: {
            download: string;
            installAppIndicator: string;
            ubuntuDependency: Array<string | Wrapped<"package">>;
            fedoraDependency: Array<string | Wrapped<"package">>;
            extractContent: Array<string | Wrapped<"file">>;
            binaryExplanation: Array<string | Wrapped<"file">>;
          };
          start: {
            openTerminal: string;
            goToFolder: Array<string | Wrapped<"file">>;
            runCommand: string;
            advanced: {
              title: string;
              runFollowingCommand: Array<string | Wrapped<"file">>;
            };
          };
        };
        footerWaitingToConnect: string;
        advancedSettings: {
          title: Array<string | Wrapped<"port">>;
          label: string;
          helperTextInvalid: string;
        };
      };
      use: {
        title: string;
        connected: string;
        backToSandbox: string;
      };
    };
    button: {
      available: string;
    };
    dropdown: {
      label: string;
      setup: string;
      open: string;
      close: string;
      shortConnected: (port: string) => string;
      tooltip: {
        connected: string;
        install: string;
        outdated: string;
        disconnected: string;
      };
    };
  };
  notificationsPanel: {
    name: string;
    tooltip: {
      retractAll: string;
      expandAll: string;
    };
  };
  deployments: {
    virtualServiceRegistry: {
      dependencyWarning: string;
      dependencyWarningTooltip: string;
    };
  };
}

export interface AppI18n extends AppDictionary, CommonI18n {}
