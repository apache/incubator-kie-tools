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
import { de as de_common } from "@kie-tools/i18n-common-dictionary";
import { AppI18n } from "..";

export const de: AppI18n = {
  ...de_common,
  editorPage: {
    textEditorModal: {
      title: (fileName: string) => `Editiere ${fileName}`,
    },
    alerts: {
      setContentError: {
        title: "Fehler beim Öffnen der Datei. Sie können sie als Text bearbeiten und das Diagramm nach der Korrektur erneut öffnen.",
        action: "Als Text öffnen",
      },
      copy: "Content copied to clipboard",
      updateGist: "Gist successfully updated.",
      createGist: "Gist successfully created.",
      errorPushingGist: "Push für das aktuelle Gist fehlgeschlagen. Push erzwungen?",
      forcePushWarning: "WARNUNG: Dadurch wird Ihre Gist mit den lokalen Änderungen überschrieben!",
      invalidCurrentGist: `Das aktuelle Gist ${de_common.names.url} ist ungültig. Wenn Sie den Dateinamen aktualisiert haben, müssen Sie auch die ${de_common.names.url} aktualisieren.`,
      invalidGistFilename: "Ungültiger Dateiname. Dieses Gist hat bereits eine Datei mit diesem Namen.",
      error: `Beim Versuch, den letzten Vorgang auszuführen, ist ein Fehler aufgetreten. Überprüfen Sie, ob Ihr Authentifizierungstoken noch gültig ist und versuchen Sie es später erneut.`,
      unsaved: {
        titleLocal: "Sie haben neue Änderungen seit Ihrem letzten Download.",
        titleGit: "Es gibt neue Änderungen seit Ihrem letzten Push.",
        proceedAnyway: "Trotzdem fortfahren",
        message: "Die Dateien bleiben vorübergehend im Browser gespeichert, werden aber möglicherweise gelöscht, bevor Sie zurückkehren.",
      },
    },
  },
  editorToolbar: {
    closeAndReturnHome: "Schließen und zurück zur Startseite",
    saveAndDownload: "Speichern & Herunterladen",
    sendChangesToGitHub: `Änderungen senden an ${de_common.names.github}`,
    copySource: "Quellcode kopieren",
    downloadSVG: `${de_common.terms.download} ${de_common.names.svg}`,
    setGitHubToken: `Einrichten`,
    createGist: "Gist erstellen",
    cantCreateGistTooltip: `Sie können keinen Gist erstellen, weil Sie entweder nicht eingeloggt sind oder Ihre Modelle in verschachtelten Verzeichnissen liegen.`,
    cantUpdateGistTooltip: `Sie können Ihren Gist nicht aktualisieren, weil Sie entweder nicht eingeloggt sind, nicht der Besitzer sind oder Ihre Modelle in verschachtelten Verzeichnissen liegen.`,
    createSnippet: "Snippet erstellen",
    cantCreateSnippetTooltip: `Sie können kein Snippet erstellen, weil Sie entweder nicht eingeloggt sind oder Ihre Modelle in verschachtelten Verzeichnissen liegen.`,
    cantUpdateSnippetTooltip: `Sie können Ihr Snippet nicht aktualisieren, weil Sie entweder nicht eingeloggt sind, nicht der Eigentümer sind oder Ihre Modelle in verschachtelten Verzeichnissen liegen.`,
    share: "Teilen",
    embed: "Einbetten",
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
      title: `${de_common.names.github} ${de_common.names.oauth} ${de_common.terms.token}`,
      subtitle: `Set up your ${de_common.names.github} token so you can create and update gist.`,
    },
    footer: {
      createNewToken: "Create a new token",
      placeHolder: "Paste your token here",
    },
    body: {
      disclaimer: `The token you provide is locally stored as a browser cookie and is never shared with anyone.`,
      learnMore: `Learn more about ${de_common.names.github} tokens`,
      note: `You should provide a token with the ${"'gist'".bold()} permission.`,
    },
  },
  extendedServices: {
    modal: {
      wizard: {
        title: `${de_common.names.extendedServices} ${de_common.terms.setup}`,
        description: `Choose your ${de_common.terms.os.full} and follow the instructions to install and start the ${de_common.names.extendedServices}.`,
        outdatedAlert: {
          title: `${de_common.names.extendedServices} is outdated!`,
          message: `It looks like you're using an incompatible version of the ${de_common.names.extendedServices}. Follow the instructions below to update.`,
        },
        stoppedAlert: {
          title: `${de_common.names.extendedServices} has stopped!`,
          message: `It looks like the ${de_common.names.extendedServices} has suddenly stopped, please follow these instructions to start it again.`,
        },
        macos: {
          install: {
            download: ` ${de_common.names.extendedServices}.`,
            openFile: ["Open the ", wrapped("file"), " file."],
            dragFileToApplicationsFolder: ["Drag ", wrapped("file"), " to the ", wrapped("folder"), " folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${de_common.names.extendedServices} icon on your system bar, simply click it and select "${de_common.terms.start}".`,
              launchExtendedServices: [
                `If not, start the ${de_common.names.extendedServices} app by launching `,
                wrapped("file"),
                ".",
              ],
            },
            firstTime: {
              title: `If you just installed ${de_common.names.extendedServices}:`,
              openApplicationsFolder: ["Open the ", wrapped("folder"), " folder."],
              again: "again",
              openAndCancel: [
                "Right-click on ",
                wrapped("file"),
                ` select "${de_common.terms.open}" and then "${de_common.terms.cancel}".`,
              ],
              openInstruction: [
                "Right-click on ",
                wrapped("file"),
                " ",
                wrapped("again"),
                ` and then select "${de_common.terms.open}".`,
              ],
            },
            alreadyRanBefore: `If you already installed and ran the ${de_common.names.extendedServices} before:`,
            launchExtendedServices: ["Launch the ", wrapped("file")],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on a Terminal tab to start ${de_common.names.extendedServices} on a different port:`,
            },
          },
        },
        windows: {
          install: {
            keepDownload: ` ${de_common.names.extendedServices}. Note that you'll probably have to right-click the download and choose "Keep"`,
            moveTheFile: ["Move the ", wrapped("file"), " file to your preferred folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${de_common.names.extendedServices} icon on your system bar, simply click it and select "${de_common.terms.start}".`,
              launchExtendedServices: [
                `If not, start the ${de_common.names.extendedServices} by opening the `,
                wrapped("file"),
                "file.",
              ],
            },
            firstTime: {
              title: `If you just installed ${de_common.names.extendedServices}:`,
              openFolder: ["Open folder where you placed the ", wrapped("file"), " file."],
              runAnyway: `Double-click it and select "More info" then click on the "Run anyway" button.`,
            },
            alreadyRanBefore: `If you already installed and ran the ${de_common.names.extendedServices} before:`,
            launchExtendedServices: ["Open the ", wrapped("file"), " file."],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on the Command prompt to start ${de_common.names.extendedServices} on a different port:`,
            },
          },
        },
        linux: {
          install: {
            download: ` ${de_common.names.extendedServices}.`,
            installAppIndicator: "Install the AppIndicator lib for your system:",
            ubuntuDependency: [`${de_common.names.ubuntu}: `, wrapped("package")],
            fedoraDependency: [`${de_common.names.fedora}: `, wrapped("package")],
            extractContent: ["Extract the contents of ", wrapped("file"), " to your location of choice."],
            binaryExplanation: [
              `The ${de_common.names.extendedServices} binary, `,
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
        footerWaitingToConnect: `Waiting to connect to ${de_common.names.extendedServices}`,
        advancedSettings: {
          title: [
            `The default ${de_common.names.extendedServices} port is `,
            wrapped("port"),
            `. If you're already using this port for another application, you can change the port used to connect with the ${de_common.names.extendedServices}.`,
          ],
          label: "Port",
          helperTextInvalid: "Invalid port. Valid ports: 0 <= port <= 65353",
        },
      },
      use: {
        title: "All set! 🎉",
        connected: `You're connected to the ${de_common.names.extendedServices}.`,
        backToSandbox: "Back to Sandbox",
      },
    },
    button: {
      available: `This is only available in ${de_common.names.chrome} at the moment`,
    },
    dropdown: {
      label: `${de_common.names.extendedServices}`,
      setup: `${de_common.terms.setup} ${de_common.names.extendedServices}`,
      open: `${de_common.terms.open} ${de_common.names.extendedServices} panel`,
      close: `${de_common.terms.close} ${de_common.names.extendedServices} panel`,
      shortConnected: (port: string) => `Connected to port ${port}`,
      tooltip: {
        connected: `${de_common.names.extendedServices} is connected.`,
        install: `Setup ${de_common.names.extendedServices} to use this feature. Click to install.`,
        outdated: `${de_common.names.extendedServices} is outdated. Click to update.`,
        disconnected: `${de_common.names.extendedServices} is disconnected.`,
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
