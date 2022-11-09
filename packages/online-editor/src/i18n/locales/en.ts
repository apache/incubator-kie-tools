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

import { OnlineI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { en as en_unitables } from "@kie-tools/unitables/dist/i18n/locales/en";
import { wrapped } from "@kie-tools-core/i18n/dist/core";

export const en: OnlineI18n = {
  ...en_common,
  editorPage: {
    textEditorModal: {
      title: (fileName: string) => `Editing ${fileName}`,
    },
    alerts: {
      setContentError: {
        title: "Error opening file. You can edit it as text and reopen the diagram after you've fixed it.",
        action: "Open as text",
      },
      copy: "Content copied to clipboard",
      updateGist: "Gist successfully updated.",
      createGist: "Gist successfully created.",
      errorPushingGist: "Failed to Push an update to your current Gist. Attempt to Push forcefully?",
      forcePushWarning: "WARNING: This will overwrite your Gist with the local changes!",
      invalidCurrentGist: `Your current gist ${en_common.names.url} is invalid. If you've updated its filename, it's necessary to update your ${en_common.names.url} as well.`,
      invalidGistFilename: "Invalid filename. This gist already has a file with this name.",
      error: `An error occurred trying to perform the last operation. Check if your ${en_common.names.github} token is still valid and try again later.`,
      unsaved: {
        titleLocal: "You have new changes since your last download.",
        titleGit: "You have new changes since your last Push.",
        proceedAnyway: "Proceed anyway",
        message: "Your files are temporarily persisted on your browser, but may be erased before you come back.",
      },
    },
  },
  editorToolbar: {
    closeAndReturnHome: "Close and return Home",
    saveAndDownload: "Save & Download",
    sendChangesToGitHub: `Send changes to ${en_common.names.github}`,
    copySource: "Copy Source",
    downloadSVG: `${en_common.terms.download} ${en_common.names.svg}`,
    setGitHubToken: `Setup`,
    createGist: "Create Gist",
    cantCreateGistTooltip: `You can't create a Gist because you're either not logged in, or your models are in nested directories.`,
    cantUpdateGistTooltip: `You can't update your Gist because you're either not logged in, not the owner, or your models are in nested directories.`,
    share: "Share",
    embed: "Embed",
  },
  dmnDevSandbox: {
    common: {
      deployYourModel: "Deploy",
      deployInstanceInfo: "Deploy instance information",
      disclaimer:
        "When you set up the required information, you are able to deploy decision models on your configured instance. All the information you provide is locally stored as browser cookies and they are never shared with anyone.",
      learnMore: "Learn more",
      requiredField: "This field cannot be empty.",
      deploying: "Deploying ...",
      saving: "Saving ...",
      setupFirst: `Set up your ${en_common.names.dmnDevSandbox} to be able to deploy your models`,
    },
    dropdown: {
      noDeployments: "Your deployments show up here",
      connectedTo: (username: string) => `Connected to  '${username}'`,
      item: {
        upTooltip: "This deployment is up and running.",
        downTooltip: "This deployment is not running.",
        inProgressTooltip: "This deployment is in progress and it will be available shortly.",
        errorTooltip:
          "Some unexpected error happened during the deploy process. Check the logs in your instance for further information.",
        createdAt: (date: string) => `Created at ${date}`,
      },
    },
    configModal: {
      hostInfo: `The hostname associated with your instance.`,
      namespaceInfo: `The namespace (project) you want to deploy the Decision Model.`,
      tokenInfo: `The token associated with your instance.`,
      validationError: "You must fill out all required fields before you can proceed.",
      connectionError: "Connection refused. Please check the information provided.",
      configExpiredWarning: "Token or account expired. Please update your configuration.",
      useWizard: "Configure through the guided wizard instead",
    },
    confirmModal: {
      title: "Deploy",
      body: "Are you sure you want to deploy your model to your instance? This action will take a few minutes to be completed and you will need to create a new deployment if you update your model.",
    },
    alerts: {
      deployStartedError:
        "Something went wrong when creating your deployment. Please check your configuration and try again.",
      deployStartedSuccess:
        "Your deployment has been successfully started and will be available shortly. Please do not close your browser tab until this operation is completed.",
    },
    introduction: {
      explanation: `Deploy your decision models to a cloud instance on ${en_common.names.devSandbox} and share with others.`,
      disclaimer: `${
        en_common.names.dmnDevSandbox
      } is intended for ${"development".bold()} and should not be used for business-critical workloads.`,
      getStarted: "To get started, configure your instance information.",
    },
    configWizard: {
      header: {
        provider: "Provider",
      },
      steps: {
        first: {
          name: "Create your instance",
          introduction: `In order to create your ${en_common.names.shortDevSandbox} instance:`,
          goToGetStartedPage: "Go to the Get Started page",
          followSteps: `Follow the steps to launch your instance. You will be asked to log in with your ${en_common.names.redHat} account.`,
          informNamespace: `Once your instance is up and running, inform the namespace (project) in your cluster you want to have the Decision Model deployed to.`,
          inputReason:
            "This information is necessary for deploying your Decision Model into the right project namespace.",
          namespacePlaceholder: `The namespace (project) you want to deploy the Decision Model.`,
        },
        second: {
          name: "Set credentials",
          introduction: `In your ${en_common.names.shortDevSandbox} instance:`,
          accessLoginCommand: `Click on your username on the top right corner and then ${"'Copy login command'".bold()}.`,
          accessDisplayToken: `If asked, log in with ${"'DevSandbox'".bold()}, and then access the ${"'Display Token'".bold()} link.`,
          copyInformation: `In ${"'Log in with this token'".bold()} section, copy your ${"'--server'".bold()} and ${"'--token'".bold()} values, and paste them below.`,
          inputReason: "This information is necessary for establishing a connection with your instance.",
          hostPlaceholder: "Paste the --server value here",
          tokenPlaceholder: "Paste the --token value here",
        },
        final: {
          name: "Connect",
          connectionSuccess: "Connection successfully established.",
          connectionError: "Connection refused.",
          introduction: "Now you are able to deploy DMN decisions to your OpenShift instance.",
          configNote: "Your configuration will be stored as browser cookies after the operations above.",
          connectionErrorLong: `A connection with your ${en_common.names.shortDevSandbox} instance could not be established.`,
          checkInfo: "Please check the information provided and try again.",
          possibleErrorReasons: {
            introduction: "Here are some possible reasons:",
            emptyField: "One or more required information are not filled.",
            instanceExpired:
              "Instances expire in 30 days. After this period, you will need to recreate it, thus receiving a new host.",
            tokenExpired: "Tokens expire on a daily basis.",
          },
        },
      },
    },
  },
  embedModal: {
    title: "Embed",
    description:
      "Embed the editor and content in your page. Choose the options below and copy the embed code to your clipboard:",
    copy: "Copy",
    source: {
      current: {
        label: "Current content",
        description: "The embedded Editor will contain the current content, so it cannot be changed externally.",
      },
      gist: {
        tooltip: `Only available when editing a file from a ${en_common.names.github} gist.`,
        label: `${en_common.names.github} gist`,
        description:
          "The embedded Editor will fetch the content from the open gist. Changes made to this gist will be reflected in the Editor.",
      },
    },
    embedCode: "Embed code",
    copiedToClipboard: "Copied to clipboard",
  },
  githubTokenModal: {
    header: {
      title: `${en_common.names.github} ${en_common.names.oauth} ${en_common.terms.token}`,
      subtitle: `Set up your ${en_common.names.github} token so you can create and update gist.`,
    },
    footer: {
      createNewToken: "Generate new token",
      placeHolder: "Paste your token here",
    },
    body: {
      disclaimer: `The token you provide is locally stored as a browser cookie and is never shared with anyone.`,
      learnMore: `Learn more about ${en_common.names.github} tokens`,
      note: `You should provide a token with the ${"'gist'".bold()} permission.`,
    },
  },
  homePage: {
    uploadFile: {
      header: "Edit existing file",
      body: `Upload your ${en_common.names.bpmn}, ${en_common.names.dmn} or ${en_common.names.pmml} file here to start making new edits!`,
      helperText: `Upload a .${en_common.names.bpmn}, .${en_common.names.bpmn}2, .${en_common.names.dmn} or .${en_common.names.pmml} file`,
      helperInvalidText: "File extension is not supported",
      placeholder: "Drag a file or browse for it.",
    },
    openUrl: {
      validating: `Validating ${en_common.names.url}`,
      invalidGistExtension: "File type on the provided gist is not supported.",
      invalidExtension: `File type on the provided ${en_common.names.url} is not supported.`,
      invalidGist: `Enter a valid gist ${en_common.names.url}. If you're using a specific gist ${en_common.names.url} remember its name can't have whitespaces and upper-case letters.`,
      invalidUrl: `This ${en_common.names.url} is not valid (don't forget "https://"!).`,
      notFoundUrl: `This ${en_common.names.url} does not exist.`,
      corsNotAvailable: `This ${en_common.names.url} cannot be opened because it doesn't allow other websites to access it.`,
      openFromSource: "Open from source",
      description: `Paste a ${en_common.names.url} to a source code link (${en_common.names.github}, ${en_common.names.dropbox}, etc.)`,
    },
    dropdown: {
      getHub: `Get ${en_common.names.businessModeler.hub}`,
      onlineForum: "Online forum",
    },
    bpmnCard: {
      title: `Workflow (.${en_common.names.bpmn})`,
      explanation: `${en_common.names.bpmn} files are used to generate Workflows.`,
      createNew: "Create new workflow",
    },
    dmnCard: {
      title: `Decision model (.${en_common.names.dmn})`,
      explanation: `${en_common.names.dmn} files are used to generate decision models`,
      createNew: "Create new decision model",
    },
    pmmlCard: {
      title: `Scorecard model (.${en_common.names.pmml})`,
      explanation: `${en_common.names.pmml} files are used to generate scorecards`,
      createNew: "Create new Scorecard",
    },
    trySample: "Try Sample",
    chooseLocalFile: "Choose a local file",
  },
  guidedTour: {
    init: {
      title: `Welcome to the ${en_common.names.dmn} Editor`,
      learnMore: `Take this 5-minute tour to learn more about the ${en_common.names.dmn} Editor in a brief and interactive way.`,
      dmnRunnerIntro: `If you already know your way around the ${en_common.names.dmn} Editor, you can skip this tour and start executing your models with the ${en_common.names.dmnRunner}.`,
      takeTour: "Take tour",
      skipTour: "Skip tour",
      skipTourAndUseDmnRunner: `Skip tour and start ${en_common.names.dmnRunner}`,
    },
    end: {
      title: "Congratulations",
      motivational: `Now you know how each part of the ${en_common.names.dmn} Editor works, and you're empowered to go ahead and explore!`,
      nextSteps: {
        title: "As next steps, you can try to",
        firstStep: `Connect the ${"Age".bold()} input with the ${"Can drive?".bold()} decision;`,
        secondStep: `Define the decision logic in the ${"Can drive?".bold()} node to return ${"true".bold()} when ${"Age".bold()} is
              greater than ${"21".bold()}, otherwise ${"false".bold()};`,
        thirdStep: "Execute the model.",
        startDmnRunner: `Start ${en_common.names.dmnRunner}`,
      },
      findUsefulInfo: "You can find useful information in the",
      learnDMN: `Learn ${en_common.names.dmn} in 15 minutes`,
      courseOr: "course or in the",
      kogitoDoc: `${en_common.names.kogito} documentation`,
      finish: "Finish the Tour",
    },
  },
  alerts: {
    gistError: `Not able to open this Gist. If you have updated your Gist filename it can take a few seconds until the URL is available to be used.`,
    goToHomePage: "Go to Home Page",
    errorDetails: "Error details:",
    responseError: {
      title: "An error happened while fetching your file",
    },
    fetchError: {
      title: "An unexpected error happened while trying to fetch your file",
      possibleCauses: "Possible causes:",
      missingGitHubToken: `If you're trying to open a private file, make sure to set your GitHub token before. To do it use one of the Editor pages and open the "Set your GitHub token" modal under the Share dropdown.`,
      cors: "The URL to your file must allow CORS in its response, which should contain the following header:",
    },
  },
  dmnRunner: {
    drawer: {
      error: {
        title: `${en_common.terms.oops}!`,
        explanation: `The ${en_common.names.dmnRunner} drawer couldn't be rendered due to an error.`,
        message: [
          `This ${en_common.names.dmn} has a construct that is not supported. Please refer to `,
          wrapped("jira"),
          " and report an issue. Don't forget to upload the current file, and the used inputs",
        ],
      },
    },
    table: { ...en_unitables },
    modal: {
      initial: {
        runDmnModels: "Run your models and see live forms and results as you edit.",
        explanation:
          "Input nodes become interactive fields on an auto-generated form, and the results are displayed as easy-to-read cards.",
        notificationPanelExplanation: [
          `The Notifications Panel `,
          wrapped("icon"),
          `, at the bottom-right corner of the Editor, displays live Execution messages to assist modeling your decisions.`,
        ],
      },
      wizard: {
        title: `${en_common.names.kieSandboxExtendedServices} ${en_common.terms.setup}`,
        description: `Choose your ${en_common.terms.os.full} and follow the instructions to install and start the ${en_common.names.kieSandboxExtendedServices}.`,
        outdatedAlert: {
          title: `${en_common.names.kieSandboxExtendedServices} is outdated!`,
          message: `It looks like you're using an incompatible version of the ${en_common.names.kieSandboxExtendedServices}. Follow the instructions below to update.`,
        },
        stoppedAlert: {
          title: `${en_common.names.kieSandboxExtendedServices} has stopped!`,
          message: `It looks like the ${en_common.names.kieSandboxExtendedServices} has suddenly stopped, please follow these instructions to start it again.`,
        },
        macos: {
          install: {
            download: ` ${en_common.names.kieSandboxExtendedServices}.`,
            openFile: ["Open the ", wrapped("file"), " file."],
            dragFileToApplicationsFolder: ["Drag ", wrapped("file"), " to the ", wrapped("folder"), " folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.kieSandboxExtendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchKieSandboxExtendedServices: [
                `If not, start the ${en_common.names.kieSandboxExtendedServices} app by launching `,
                wrapped("file"),
                ".",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.kieSandboxExtendedServices}:`,
              openApplicationsFolder: ["Open the ", wrapped("folder"), " folder."],
              again: "again",
              openAndCancel: [
                "Right-click on ",
                wrapped("file"),
                ` select "${en_common.terms.open}" and then "${en_common.terms.cancel}".`,
              ],
              openInstruction: [
                "Right-click on ",
                wrapped("file"),
                " ",
                wrapped("again"),
                ` and then select "${en_common.terms.open}".`,
              ],
            },
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.kieSandboxExtendedServices} before:`,
            launchKieSandboxExtendedServices: ["Launch the ", wrapped("file")],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on a Terminal tab to start ${en_common.names.kieSandboxExtendedServices} on a different port:`,
            },
          },
        },
        windows: {
          install: {
            keepDownload: ` ${en_common.names.kieSandboxExtendedServices}. Note that you'll probably have to right-click the download and choose "Keep"`,
            moveTheFile: ["Move the ", wrapped("file"), " file to your preferred folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.kieSandboxExtendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchKieSandboxExtendedServices: [
                `If not, start the ${en_common.names.kieSandboxExtendedServices} by opening the `,
                wrapped("file"),
                "file.",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.kieSandboxExtendedServices}:`,
              openFolder: ["Open folder where you placed the ", wrapped("file"), " file."],
              runAnyway: `Double-click it and select "More info" then click on the "Run anyway" button.`,
            },
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.kieSandboxExtendedServices} before:`,
            launchKieSandboxExtendedServices: ["Open the ", wrapped("file"), " file."],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on the Command prompt to start ${en_common.names.kieSandboxExtendedServices} on a different port:`,
            },
          },
        },
        linux: {
          install: {
            download: ` ${en_common.names.kieSandboxExtendedServices}.`,
            installAppIndicator: "Install the AppIndicator lib for your system:",
            ubuntuDependency: [`${en_common.names.ubuntu}: `, wrapped("package")],
            fedoraDependency: [`${en_common.names.fedora}: `, wrapped("package")],
            extractContent: ["Extract the contents of ", wrapped("file"), " to your location of choice."],
            binaryExplanation: [
              `The ${en_common.names.kieSandboxExtendedServices} binary, `,
              wrapped("file"),
              ", is a single binary file, which means you can add it to your PATH or even configure it to execute when your computer starts.",
            ],
          },
          start: {
            openTerminal: "Open a Terminal window.",
            goToFolder: ["Go to the folder where you placed the ", wrapped("file"), " binary."],
            runCommand: "Run ",
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: [
                "Open a Terminal window and run the following command on the directory where you placed the ",
                wrapped("file"),
                " binary:",
              ],
            },
          },
        },
        footerWaitingToConnect: `Waiting to connect to ${en_common.names.kieSandboxExtendedServices}`,
        advancedSettings: {
          title: [
            `The default ${en_common.names.kieSandboxExtendedServices} port is `,
            wrapped("port"),
            `. If you're already using this port for another application, you can change the port used to connect with the ${en_common.names.kieSandboxExtendedServices}.`,
          ],
          label: "Port",
          helperTextInvalid: "Invalid port. Valid ports: 0 <= port <= 65353",
        },
      },
      use: {
        title: "All set! 🎉",
        connected: `You're connected to the ${en_common.names.kieSandboxExtendedServices}.`,
        fillTheForm: "Fill the Form on the Inputs column and automatically see the results on the Outputs column.",
        deployTheModel: `You can also deploy your model to the ${en_common.names.dmnDevSandbox} when you're done editing.`,
        backToEditor: "Back to Editor",
        setupDmnDevSandbox: `Set up the ${en_common.names.dmnDevSandbox}`,
        dmnDevSandboxAlreadySetup: `${en_common.names.dmnDevSandbox} already set up`,
      },
    },
    dropdown: {
      label: `${en_common.names.kieSandboxExtendedServices}`,
      setup: `${en_common.terms.setup} ${en_common.names.kieSandboxExtendedServices}`,
      open: `${en_common.terms.open} ${en_common.names.kieSandboxExtendedServices} panel`,
      close: `${en_common.terms.close} ${en_common.names.kieSandboxExtendedServices} panel`,
    },
    button: {
      available: `This is only available in ${en_common.names.chrome} at the moment`,
    },
  },
  notificationsPanel: {
    name: "Notifications Panel",
    tooltip: {
      retractAll: "Retract All",
      expandAll: "Expand All",
    },
  },
  kieSandboxExtendedServices: {
    dropdown: {
      shortConnected: (port: string) => `Connected to port ${port}`,
      tooltip: {
        connected: `${en_common.names.kieSandboxExtendedServices} is connected.`,
        install: `Setup ${en_common.names.kieSandboxExtendedServices} to use this feature. Click to install.`,
        outdated: `${en_common.names.kieSandboxExtendedServices} is outdated. Click to update.`,
        disconnected: `${en_common.names.kieSandboxExtendedServices} is disconnected.`,
      },
    },
    modal: {
      initial: {
        subHeader: `Augment the ${en_common.names.dmn} editor`,
      },
    },
  },
};
