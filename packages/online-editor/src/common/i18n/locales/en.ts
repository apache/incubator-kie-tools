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
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";
import { wrapped } from "@kie-tooling-core/i18n/dist/core";

export const en: OnlineI18n = {
  ...en_common,
  downloadHubModal: {
    beforeDownload: {
      title: `The ${en_common.names.businessModeler.hub} allows you to access`,
      vscodeDescription: `Installs ${en_common.names.vscode} extension and gives you a convenient way to launch ${en_common.names.vscode} ready to work with ${en_common.names.kogito}.`,
      githubChromeExtension: {
        title: `${en_common.names.github}  ${en_common.names.chrome} Extension`,
        description: `Provides detailed instructions on how to install ${en_common.names.kogito} ${en_common.names.github} Extension for ${en_common.names.chrome}.`,
      },
      desktop: {
        title: `${en_common.names.desktop} ${en_common.names.app}`,
        description: `Installs the ${en_common.names.businessModeler.desktop} ${en_common.names.app} for use locally and offline.`,
      },
      businessModeler: {
        title: `${en_common.names.businessModeler.name} Preview`,
        description: "Provides a quick link to access the website in the same hub.",
      },
    },
    afterDownload: {
      title: `Thank you for downloading ${en_common.names.businessModeler.hub}!`,
      message: "If the download does not begin automatically,",
      link: "click here",
    },
  },
  editorFullScreenToolbar: "Exit full screen",
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
      updateGist: "Your gist was updated.",
      updateGistFilename: {
        title: "Your gist and its filename were updated!",
        message: `Your gists filename was updated, and it can take a few seconds until the new ${en_common.names.url} is available.`,
        yourNewUrl: `Your new ${en_common.names.url}`,
      },
      invalidCurrentGist: `Your current gist ${en_common.names.url} is invalid. If you've updated its filename, it's necessary to update your ${en_common.names.url} as well.`,
      invalidGistFilename: "Invalid filename. This gist already has a file with this name.",
      error: `An error occurred trying to perform the last operation. Check if your ${en_common.names.github} token is still valid and try again later.`,
      unsaved: {
        title: "Unsaved changes will be lost",
        message: "Click Save to download your progress before closing.",
        closeWithoutSaving: "Close without saving",
      },
    },
  },
  editorToolbar: {
    closeAndReturnHome: "Close and return Home",
    enterFullScreenView: "Enter full screen view",
    saveAndDownload: "Save & Download",
    sendChangesToGitHub: `Send changes to ${en_common.names.github}`,
    copySource: "Copy Source",
    downloadSVG: `${en_common.terms.download} ${en_common.names.svg}`,
    setGitHubToken: `Set up your ${en_common.names.github} token`,
    gistIt: "Gist it!",
    gistItTooltip: `Set up your ${en_common.names.github} token to be able to create and update gists!`,
    share: "Share",
    embed: "Embed",
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
      createNewToken: "Create a new token",
      placeHolder: "Paste your token here",
    },
    body: {
      disclaimer: `By authenticating with your ${en_common.names.oauth} Token we are able to create gists so you can share your diagrams with your colleagues. The token you provide is locally stored as browser cookies and it is never shared with anyone.`,
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
    header: {
      title: `Asset Editor for ${en_common.names.kogito} and Process Automation`,
      welcomeText: `Welcome to ${en_common.names.businessModeler.name}! These simple ${en_common.names.bpmn}, ${en_common.names.dmn} and ${en_common.names.pmml} editors are here to allow you to collaborate quickly\n and to help introduce you to the new tools and capabilities of Process Automation. Feel free to get in touch\n in the forum or review the documentation for more information.`,
    },
    bpmnCard: {
      title: `Workflow (.${en_common.names.bpmn})`,
      explanation: `${en_common.names.bpmn} files are used to generate business processes.`,
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
    modal: {
      initial: {
        runDmnModels: `Run your ${en_common.names.dmn} models with the ${en_common.names.kieToolingExtendedServices} and see live forms and results as you edit.`,
        kieToolingExtendedServicesExplanation: `With its validation and execution capabilities, ${en_common.names.kieToolingExtendedServices} helps you create assertive DMN decisions. Input nodes become interactive fields on an auto-generated form, and the results are displayed as easy-to-read cards.`,
        notificationPanelExplanation: [
          `The Notifications Panel `,
          wrapped("icon"),
          `, at the right-bottom side of the Editor, displays live Execution messages to assist during the modeling stage of your decisions.`,
        ],
      },
      wizard: {
        title: `${en_common.names.kieToolingExtendedServices} ${en_common.terms.setup}`,
        description: `Choose your ${en_common.terms.os.full} and follow the instructions to install and start the ${en_common.names.kieToolingExtendedServices}.`,
        outdatedAlert: {
          title: `${en_common.names.kieToolingExtendedServices} is outdated!`,
          message: `It looks like you're using an incompatible version of the ${en_common.names.kieToolingExtendedServices}. Follow the instructions below to update.`,
        },
        stoppedAlert: {
          title: `${en_common.names.kieToolingExtendedServices} has stopped!`,
          message: `It looks like the ${en_common.names.kieToolingExtendedServices} has suddenly stopped, please follow these instructions to start it again.`,
        },
        macos: {
          install: {
            download: ` ${en_common.names.kieToolingExtendedServices}.`,
            openFile: ["Open the ", wrapped("file"), " file."],
            dragFileToApplicationsFolder: ["Drag ", wrapped("file"), " to the ", wrapped("folder"), " folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.kieToolingExtendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchKieToolingExtendedServices: [
                `If not, start the ${en_common.names.kieToolingExtendedServices} app by launching `,
                wrapped("file"),
                ".",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.kieToolingExtendedServices}:`,
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
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.kieToolingExtendedServices} before:`,
            launchKieToolingExtendedServices: ["Launch the ", wrapped("file")],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on a Terminal tab to start ${en_common.names.kieToolingExtendedServices} on a different port:`,
            },
          },
        },
        windows: {
          install: {
            keepDownload: ` ${en_common.names.kieToolingExtendedServices}. Note that you'll probably have to right-click the download and choose "Keep"`,
            moveTheFile: ["Move the ", wrapped("file"), " file to your preferred folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.kieToolingExtendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchKieToolingExtendedServices: [
                `If not, start the ${en_common.names.kieToolingExtendedServices} by opening the `,
                wrapped("file"),
                "file.",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.kieToolingExtendedServices}:`,
              openFolder: ["Open folder where you placed the ", wrapped("file"), " file."],
              runAnyway: `Double-click it and select "More info" then click on the "Run anyway" button.`,
            },
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.kieToolingExtendedServices} before:`,
            launchKieToolingExtendedServices: ["Open the ", wrapped("file"), " file."],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on the Command prompt to start ${en_common.names.kieToolingExtendedServices} on a different port:`,
            },
          },
        },
        linux: {
          install: {
            download: ` ${en_common.names.kieToolingExtendedServices}.`,
            installAppIndicator: "Install the AppIndicator lib for your system:",
            ubuntuDependency: [`${en_common.names.ubuntu}: `, wrapped("package")],
            fedoraDependency: [`${en_common.names.fedora}: `, wrapped("package")],
            extractContent: ["Extract the contents of ", wrapped("file"), " to your location of choice."],
            binaryExplanation: [
              `The ${en_common.names.kieToolingExtendedServices} binary, `,
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
        footerWaitingToConnect: `Waiting to connect to ${en_common.names.kieToolingExtendedServices}`,
        advancedSettings: {
          title: [
            `The default ${en_common.names.kieToolingExtendedServices} port is `,
            wrapped("port"),
            `. If you're already using this port for another application, you can change the port used to connect with the ${en_common.names.kieToolingExtendedServices}.`,
          ],
          label: "Port",
          helperTextInvalid: "Invalid port. Valid ports: 0 <= port <= 65353",
        },
      },
      use: {
        title: "All set! 🎉",
        connected: `You're connected to the ${en_common.names.kieToolingExtendedServices}.`,
        fillTheForm: "Fill the Form on the Inputs column and automatically see the results on the Outputs column.",
        backToEditor: "Back to Editor",
      },
    },
    dropdown: {
      label: `${en_common.names.kieToolingExtendedServices}`,
      setup: `${en_common.terms.setup} ${en_common.names.kieToolingExtendedServices}`,
      open: `${en_common.terms.open} ${en_common.names.kieToolingExtendedServices} panel`,
      close: `${en_common.terms.close} ${en_common.names.kieToolingExtendedServices} panel`,
    },
    button: {
      available: `This is only available in ${en_common.names.chrome} at the moment`,
      tooltip: {
        outdated: `The ${en_common.names.kieToolingExtendedServices} is outdated`,
        connected: `The ${en_common.names.kieToolingExtendedServices} is connected`,
        disconnected: `The ${en_common.names.kieToolingExtendedServices} is disconnected`,
      },
    },
  },
  notificationsPanel: {
    name: "Notifications Panel",
    tooltip: {
      retractAll: "Retract All",
      expandAll: "Expand All",
    },
  },
};
