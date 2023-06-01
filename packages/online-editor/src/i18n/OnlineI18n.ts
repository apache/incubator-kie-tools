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

import { ReferenceDictionary, Wrapped } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";
import { DmnUnitablesI18n } from "@kie-tools/unitables-dmn/dist/i18n";
import { GistEnabledAuthProviderType, SupportedGitAuthProviders } from "../authProviders/AuthProvidersApi";
import { SupportedActions } from "../workspace/components/GitStatusIndicatorActions";

interface OnlineDictionary extends ReferenceDictionary {
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
      updateSnippet: string;
      createGist: string;
      createSnippet: string;
      errorPushingGist: string;
      errorPushingSnippet: string;
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
    error: {
      title: string;
      explanation: string;
      message: Array<string | Wrapped<"jira">>;
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
    createSnippet: string;
    cantCreateSnippetTooltip: string;
    cantUpdateSnippetTooltip: string;
    share: string;
    embed: string;
  };
  accelerators: {
    commitMessage: (appName: string, acceleratorName: string) => string;
    loadingAlert: (acceleratorName: string) => string;
    successAlert: (acceleratorName: string) => string;
    failAlert: (acceleratorName: string) => string;
    acceleratorDescription: string;
    acceleratorDetails: string;
    dmnFilesMove: string;
    dmnFilesLocation: string;
    pmmlFilesMove: string;
    pmmlFilesLocation: string;
    bpmnFilesMove: string;
    bpmnFilesLocation: string;
    otherFilesMove: string;
    otherFilesLocation: string;
    applyAccelerator: string;
    appliedAt: string;
    applyDisclaimer: string;
  };
  devDeployments: {
    common: {
      deployYourModel: string;
      deployInstanceInfo: string;
      disclaimer: string;
      learnMore: string;
      requiredField: string;
      deploying: string;
      deleting: string;
      saving: string;
      setupFirst: string;
    };
    dropdown: {
      noDeployments: string;
      connectedTo: (username: string) => string;
      connectedToAction: string;
      deleteDeployments: string;
      item: {
        upTooltip: string;
        downTooltip: string;
        inProgressTooltip: string;
        errorTooltip: string;
        createdAt: (date: string) => string;
      };
    };
    introduction: {
      explanation: string;
      disclaimer: string;
      getStarted: string;
    };
    configModal: {
      hostInfo: string;
      namespaceInfo: string;
      tokenInfo: string;
      validationError: string;
      connectionError: string;
      missingPermissions: string;
      namespaceNotFound: (namespace: string) => string;
      configExpiredWarning: string;
      useOpenShiftWizard: string;
      useKubernetesWizard: string;
    };
    deployConfirmModal: {
      title: string;
      body: string;
    };
    deleteConfirmModal: {
      title: string;
      body: string;
    };
    alerts: {
      deployStartedError: string;
      deployStartedSuccess: string;
      deleteError: string;
      deleteSuccess: string;
    };
    openShiftConfigWizard: {
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
    kubernetesConfigWizard: {
      header: {
        provider: string;
      };
      fields: {
        namespace: string;
        namespaceInfo: string;
        kubernetesApiServerUrl: string;
        kubernetesApiServerUrlInfo: string;
        tokenInfo: string;
      };
      steps: {
        first: {
          name: string;
          introduction: string;
          installFlavor: (flavor: string) => string;
          installKubectl: string;
          runCommandsTerminal: string;
          createCluster: string;
          installIngress: string;
          installKieSandboxYaml: string;
        };
        second: {
          name: string;
          introduction: string;
          disclaimer: string;
          hostInputReason: string;
          namespaceInputReason: string;
          namespacePlaceholder: string;
          hostPlaceholder: string;
        };
        third: {
          name: string;
          introduction: string;
          getToken: string;
          tokenPlaceholder: string;
          tokenInputReason: string;
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
            clusterNotCreatedCorrectly: string;
            tokenExpired: string;
          };
        };
      };
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
        alert: string;
        tooltip: string;
        label: string;
        description: string;
      };
    };
    embedCode: string;
    copiedToClipboard: string;
  };
  connectToGitModal: {
    [key in SupportedGitAuthProviders]: {
      header: {
        title: string;
        subtitle: string;
      };
      footer: {
        createNewToken: string;
        placeHolder: string;
      };
      body: {
        learnMore: string;
        note: string;
      };
      validation: {
        scopes: {
          helper: string;
        };
      };
      form: {
        username?: {
          label: string;
          placeHolder: string;
        };
        token: {
          label: string;
          placeHolder: string;
        };
      };
    };
  } & {
    auth: {
      disclaimer: string;
      error: {
        alreadyLoggedIn: string;
        oauthScopes: (scopes: string) => string;
      };
    };
    status: {
      loading: string;
    };
    navigation: {
      seeConnectedAccounts: string;
      continue: string;
    };
  };
  commitModal: {
    title: string;
    description: string;
    commit: string;
    emptyMessageValidation: string;
    placeholder: string;
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
      onlineForum: string;
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
    error: {
      title: string;
      explanation: string;
      message: Array<string | Wrapped<"jira">>;
    };
    table: DmnUnitablesI18n;
    modal: {
      initial: {
        runDmnModels: string;
        explanation: string;
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
    };
    dropdown: {
      label: string;
      setup: string;
      open: string;
      close: string;
    };
    button: {
      available: string;
    };
  };
  notificationsPanel: {
    name: string;
    tooltip: {
      retractAll: string;
      expandAll: string;
    };
  };
  extendedServices: {
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
  createGitRepositoryModal: {
    [key in SupportedGitAuthProviders]: {
      repository: string;
      createRepository: string;
      description: (workspace: string) => string;
      error: {
        formAlert: (error: string) => string;
      };
      form: {
        select: {
          label: string;
          description: string;
        };
      };
    };
  } & {
    form: {
      buttonCreate: string;
      nameField: {
        label: string;
        hint: string;
      };
      visibility: {
        public: {
          label: string;
          description: string;
        };
        private: {
          label: string;
          description: string;
        };
      };
    };
  };
  createGistOrSnippetModal: {
    [key in GistEnabledAuthProviderType]: {
      gistOrSnippet: string;
      create: string;
      description: (workspace: string) => string;
      error: {
        formAlert: (error: string) => string;
      };
      form: {
        select: {
          label: string;
          description: string;
        };
      };
    };
  } & {
    form: {
      buttonCreate: string;
      visibility: {
        public: {
          label: string;
          description: string;
        };
        private: {
          label: string;
          description: string;
        };
      };
    };
  };
  loadOrganizationsSelect: {
    [key in SupportedGitAuthProviders]: {
      user: string;
      organizations: string;
    };
  };
  gitStatusIndicatorActions: {
    [key in SupportedActions]: {
      title: string;
      warning: string;
      description: string;
      confirmButtonText: string;
    };
  };
}

export interface OnlineI18n extends OnlineDictionary, CommonI18n {}
