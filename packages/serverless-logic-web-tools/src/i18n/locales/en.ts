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
  },
  openshift: {
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
      explanation: `Deploy your models to a cloud instance on OpenShift and share with others. This tool is intended for ${"development".bold()} and should not be used for business-critical workloads.`,
    },
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
  extendedServices: {
    modal: {
      wizard: {
        title: `${en_common.names.extendedServices} ${en_common.terms.setup}`,
        description: `Choose your ${en_common.terms.os.full} and follow the instructions to install and start the ${en_common.names.extendedServices}.`,
        outdatedAlert: {
          title: `${en_common.names.extendedServices} is outdated!`,
          message: `It looks like you're using an incompatible version of the ${en_common.names.extendedServices}. Follow the instructions below to update.`,
        },
        stoppedAlert: {
          title: `${en_common.names.extendedServices} has stopped!`,
          message: `It looks like the ${en_common.names.extendedServices} has suddenly stopped, please follow these instructions to start it again.`,
        },
        macos: {
          install: {
            download: ` ${en_common.names.extendedServices}.`,
            openFile: ["Open the ", wrapped("file"), " file."],
            dragFileToApplicationsFolder: ["Drag ", wrapped("file"), " to the ", wrapped("folder"), " folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.extendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchExtendedServices: [
                `If not, start the ${en_common.names.extendedServices} app by launching `,
                wrapped("file"),
                ".",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.extendedServices}:`,
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
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.extendedServices} before:`,
            launchExtendedServices: ["Launch the ", wrapped("file")],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on a Terminal tab to start ${en_common.names.extendedServices} on a different port:`,
            },
          },
        },
        windows: {
          install: {
            keepDownload: ` ${en_common.names.extendedServices}. Note that you'll probably have to right-click the download and choose "Keep"`,
            moveTheFile: ["Move the ", wrapped("file"), " file to your preferred folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.extendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchExtendedServices: [
                `If not, start the ${en_common.names.extendedServices} by opening the `,
                wrapped("file"),
                "file.",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.extendedServices}:`,
              openFolder: ["Open folder where you placed the ", wrapped("file"), " file."],
              runAnyway: `Double-click it and select "More info" then click on the "Run anyway" button.`,
            },
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.extendedServices} before:`,
            launchExtendedServices: ["Open the ", wrapped("file"), " file."],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on the Command prompt to start ${en_common.names.extendedServices} on a different port:`,
            },
          },
        },
        linux: {
          install: {
            download: ` ${en_common.names.extendedServices}.`,
            installAppIndicator: "Install the AppIndicator lib for your system:",
            ubuntuDependency: [`${en_common.names.ubuntu}: `, wrapped("package")],
            fedoraDependency: [`${en_common.names.fedora}: `, wrapped("package")],
            extractContent: ["Extract the contents of ", wrapped("file"), " to your location of choice."],
            binaryExplanation: [
              `The ${en_common.names.extendedServices} binary, `,
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
        footerWaitingToConnect: `Waiting to connect to ${en_common.names.extendedServices}`,
        advancedSettings: {
          title: [
            `The default ${en_common.names.extendedServices} port is `,
            wrapped("port"),
            `. If you're already using this port for another application, you can change the port used to connect with the ${en_common.names.extendedServices}.`,
          ],
          label: "Port",
          helperTextInvalid: "Invalid port. Valid ports: 0 <= port <= 65353",
        },
      },
      use: {
        title: "All set! 🎉",
        connected: `You're connected to the ${en_common.names.extendedServices}.`,
        backToSandbox: "Back to Sandbox",
      },
    },
    button: {
      available: `This is only available in ${en_common.names.chrome} at the moment`,
    },
    dropdown: {
      label: `${en_common.names.extendedServices}`,
      setup: `${en_common.terms.setup} ${en_common.names.extendedServices}`,
      open: `${en_common.terms.open} ${en_common.names.extendedServices} panel`,
      close: `${en_common.terms.close} ${en_common.names.extendedServices} panel`,
      shortConnected: (port: string) => `Connected to port ${port}`,
      tooltip: {
        connected: `${en_common.names.extendedServices} is connected.`,
        install: `Setup ${en_common.names.extendedServices} to use this feature. Click to install.`,
        outdated: `${en_common.names.extendedServices} is outdated. Click to update.`,
        disconnected: `${en_common.names.extendedServices} is disconnected.`,
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
