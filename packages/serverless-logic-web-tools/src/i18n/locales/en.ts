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

import { wrapped } from "@kie-tools-core/i18n/dist/core";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { AppI18n } from "..";

export const en: AppI18n = {
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
      errorPushingGist: "Failed to push an update to your current Gist. Attempt to force push?",
      forcePushWarning: "WARNING: This will overwrite your Gist with the local changes!",
      invalidCurrentGist: `Your current gist ${en_common.names.url} is invalid. If you've updated its filename, it's necessary to update your ${en_common.names.url} as well.`,
      invalidGistFilename: "Invalid filename. This gist already has a file with this name.",
      error: `An error occurred trying to perform the last operation. Check if your ${en_common.names.github} token is still valid and try again later.`,
      unsaved: {
        titleLocal: "You have new changes since your last download.",
        titleGit: "You have new changes since your last push.",
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
  openshift: {
    common: {
      deployYourModel: "Deploy",
      deployInstanceInfo: "Deploy instance information",
      disclaimer:
        "When you set up the required information, you are able to deploy models on your configured instance. All the information you provide is locally stored as browser cookies and they are never shared with anyone.",
      learnMore: "Learn more",
      requiredField: "This field cannot be empty.",
      deploying: "Deploying ...",
      saving: "Saving ...",
      setupFirst: `Set up your ${en_common.names.devSandbox} to be able to deploy your models`,
    },
    configModal: {
      hostInfo: `The hostname associated with your instance.`,
      namespaceInfo: `The namespace (project) you want to deploy the model.`,
      tokenInfo: `The token associated with your instance.`,
      validationError: "You must fill out all required fields before you can proceed.",
      connectionError: "Connection refused. Please check the information provided.",
      configExpiredWarning: "Token or account expired. Please update your configuration.",
    },
    confirmModal: {
      title: "Deploy",
      body: "Are you sure you want to deploy models from this workspace to your instance? This action will take a few minutes to be completed and you will need to create a new deployment if you update your models.",
    },
    introduction: {
      explanation: `Deploy your models to a cloud instance on ${
        en_common.names.devSandbox
      } and share with others. This tool is intended for ${"development".bold()} and should not be used for business-critical workloads.`,
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
          informNamespace: `Once your instance is up and running, inform the namespace (project) in your cluster you want to have the model deployed to.`,
          inputReason: "This information is necessary for deploying your models into the right project namespace.",
          namespacePlaceholder: `The namespace (project) you want to deploy the models.`,
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
          introduction: "Now you are able to deploy models to your OpenShift instance.",
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
      createNewToken: "Create a new token",
      placeHolder: "Paste your token here",
    },
    body: {
      disclaimer: `The token you provide is locally stored as a browser cookie and is never shared with anyone.`,
      learnMore: `Learn more about ${en_common.names.github} tokens`,
      note: `You should provide a token with the ${"'gist'".bold()} permission.`,
    },
  },
  kieSandboxExtendedServices: {
    modal: {
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
        backToSandbox: "Back to Sandbox",
      },
    },
    button: {
      available: `This is only available in ${en_common.names.chrome} at the moment`,
    },
    dropdown: {
      label: `${en_common.names.kieSandboxExtendedServices}`,
      setup: `${en_common.terms.setup} ${en_common.names.kieSandboxExtendedServices}`,
      open: `${en_common.terms.open} ${en_common.names.kieSandboxExtendedServices} panel`,
      close: `${en_common.terms.close} ${en_common.names.kieSandboxExtendedServices} panel`,
      shortConnected: (port: string) => `Connected to port ${port}`,
      tooltip: {
        connected: `${en_common.names.kieSandboxExtendedServices} is connected.`,
        install: `Setup ${en_common.names.kieSandboxExtendedServices} to use this feature. Click to install.`,
        outdated: `${en_common.names.kieSandboxExtendedServices} is outdated. Click to update.`,
        disconnected: `${en_common.names.kieSandboxExtendedServices} is disconnected.`,
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
  deployments: {
    virtualServiceRegistry: {
      dependencyWarning: "Has foreign workspace dependencies!",
      dependencyWarningTooltip: "Models in this workspace may depend on deployments from other workspaces.",
    },
  },
};
