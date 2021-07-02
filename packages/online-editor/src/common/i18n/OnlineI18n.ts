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

import { ReferenceDictionary, Wrapped } from "@kie-tooling-core/i18n/dist/core";
import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";

interface OnlineDictionary extends ReferenceDictionary {
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
      updateGistFilename: {
        title: string;
        message: string;
        yourNewUrl: string;
      };
      invalidCurrentGist: string;
      invalidGistFilename: string;
      error: string;
      unsaved: {
        title: string;
        message: string;
        closeWithoutSaving: string;
      };
    };
  };
  editorToolbar: {
    closeAndReturnHome: string;
    enterFullScreenView: string;
    saveAndDownload: string;
    sendChangesToGitHub: string;
    copySource: string;
    downloadSVG: string;
    setGitHubToken: string;
    gistIt: string;
    gistItTooltip: string;
    share: string;
    embed: string;
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
  homePage: {
    uploadFile: {
      header: string;
      body: string;
      helperText: string;
      helperInvalidText: string;
      placeholder: string;
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
    pmmlCard: {
      title: string;
      explanation: string;
      createNew: string;
    };
    trySample: string;
    chooseLocalFile: string;
  };
  guidedTour: {
    init: {
      title: string;
      learnMore: string;
      dmnRunnerIntro: string;
      takeTour: string;
      skipTour: string;
      skipTourAndUseDmnRunner: string;
    };
    end: {
      title: string;
      motivational: string;
      nextSteps: {
        title: string;
        firstStep: string;
        secondStep: string;
        thirdStep: string;
        startDmnRunner: string;
      };
      findUsefulInfo: string;
      learnDMN: string;
      courseOr: string;
      kogitoDoc: string;
      finish: string;
    };
  };
  alerts: {
    gistError: string;
    goToHomePage: string;
    errorDetails: string;
    responseError: {
      title: string;
    };
    fetchError: {
      title: string;
      possibleCauses: string;
      missingGitHubToken: string;
      cors: string;
    };
  };
  dmnRunner: {
    drawer: {
      error: {
        title: string;
        explanation: string;
        message: Array<string | Wrapped<"jira">>;
      };
    };
    modal: {
      initial: {
        runDmnModels: string;
        kieToolingExtendedServicesExplanation: string;
        notificationPanelExplanation: Array<string | Wrapped<"icon">>;
      };
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
              launchKieToolingExtendedServices: Array<string | Wrapped<"file">>;
            };
            firstTime: {
              title: string;
              openApplicationsFolder: Array<string | Wrapped<"folder">>;
              openAndCancel: Array<string | Wrapped<"file">>;
              again: string;
              openInstruction: Array<string | Wrapped<"file" | "again">>;
            };
            alreadyRanBefore: string;
            launchKieToolingExtendedServices: Array<string | Wrapped<"file">>;
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
              launchKieToolingExtendedServices: Array<string | Wrapped<"file">>;
            };
            firstTime: {
              title: string;
              openFolder: Array<string | Wrapped<"file">>;
              runAnyway: string;
            };
            alreadyRanBefore: string;
            launchKieToolingExtendedServices: Array<string | Wrapped<"file">>;
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
        fillTheForm: string;
        backToEditor: string;
      };
    };
    dropdown: {
      label: string;
      setup: string;
      open: string;
      close: string;
    };
    button: {
      available: string;
      tooltip: {
        outdated: string;
        connected: string;
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
}

export interface OnlineI18n extends OnlineDictionary, CommonI18n {}
