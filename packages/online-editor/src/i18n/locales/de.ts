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
import { de as de_common } from "@kie-tools/i18n-common-dictionary";
import { de as de_unitables } from "@kie-tools/unitables/dist/i18n/locales/de";
import { wrapped } from "@kie-tools-core/i18n/dist/core";

export const de: OnlineI18n = {
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
      updateSnippet: "Snippet erfolgreich aktualisiert.",
      createSnippet: "Snippet erfolgreich erstellt.",
      errorPushingSnippet: "Push für das aktuelle Snippet fehlgeschlagen. Push erzwungen?",
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
    error: {
      title: `${de_common.terms.oops}!`,
      explanation: `Der ${de_common.names.dmnRunner} konnte aufgrund eines Fehlers nicht gerendert werden.`,
      message: [
        `Dieses ${de_common.names.dmn} hat ein Konstrukt, das nicht unterstützt wird. Bitte beziehen Sie sich auf `,
        wrapped("jira"),
        " und melden Sie einen Fehler. Vergessen Sie nicht, die aktuelle Datei und die verwendeten Eingaben hochzuladen",
      ],
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
  accelerators: {
    commitMessage: (appName: string, acceleratorName: string) => `${appName}: Wende ${acceleratorName} Accelerator an`,
    loadingAlert: (acceleratorName: string) => `Wende ${acceleratorName} Accelerator an...`,
    successAlert: (acceleratorName: string) => `Erfolgreich ${acceleratorName} Accelerator angewendet`,
    failAlert: (acceleratorName: string) => `Anwenden von ${acceleratorName} Accelerator fehlgeschlagen`,
    acceleratorDescription:
      "Ein Accelerator ist eine Vorlage. Wenn Sie ihn anwenden, werden Ihre aktuellen Dateien gemäß den Spezifikationen des Accelerators verschoben und eine neue Übergabe für sie erstellt.",
    acceleratorDetails: "Dieser Accelerator ist unter folgender Adresse zu erreichen",
    dmnFilesMove: "Entscheidungen (.dmn) werden verschoben nach:",
    dmnFilesLocation: "Entscheidungen (.dmn) wurden verschoben nach:",
    pmmlFilesMove: "Scorekarten (.pmml) werden verschoben nach:",
    pmmlFilesLocation: "Scorekarten (.pmml) wurden verschoben nach:",
    bpmnFilesMove: "Workflows (.bpmn, .bpmn2) werden verschoben nach:",
    bpmnFilesLocation: "Workflows (.bpmn, .bpmn2) wurden verschoben nach:",
    otherFilesMove: "Andere Dateien werden verschoben nach:",
    otherFilesLocation: "Andere Dateien wurden verschoben nach:",
    applyAccelerator: "Accelerator anwenden",
    appliedAt: "Der Accelerator wurde angewendet auf:",
    applyDisclaimer:
      "Diese Aktion ist unwiderruflich. Alle Änderungen, die nach der Anwendung eines Accelerators vorgenommen werden, können dazu führen, dass sich Ihre Dateien in anderen Verzeichnissen befinden.",
  },
  devDeployments: {
    common: {
      deployYourModel: "Bereitstellen",
      deployInstanceInfo: "Bereitstellen von Instanzinformationen",
      disclaimer:
        "Wenn Sie die erforderlichen Informationen eingegeben haben, können Sie Dev-Deployments auf Ihrer konfigurierten Instanz erstellen. Alle Informationen, die Sie angeben, werden lokal im Browser gespeichert und niemals an Dritte weitergegeben.",
      learnMore: "Mehr erfahren",
      requiredField: "Dieses Feld darf nicht leer sein.",
      deploying: "Bereitstellen ...",
      deleting: "Löschen ...",
      saving: "Speichern ...",
      setupFirst: `Richten Sie Ihre ${de_common.names.devDeployments} ein, um Ihre Modelle bereitstellen zu können`,
    },
    dropdown: {
      noDeployments: "Ihre Deployments werden hier angezeigt",
      connectedTo: (username: string) => `Verbunden mit  '${username}'`,
      connectedToAction: "Ändern...",
      deleteDeployments: "Alles löschen",
      item: {
        upTooltip: "Diese Bereitstellung ist bereits in Betrieb.",
        downTooltip: "Diese Bereitstellung wird nicht ausgeführt.",
        inProgressTooltip: "Diese Bereitstellung ist in Arbeit und wird in Kürze verfügbar sein.",
        errorTooltip:
          "Während des Bereitstellungsprozesses ist ein unerwarteter Fehler aufgetreten. Prüfen Sie die Protokolle in Ihrer Instanz für weitere Informationen.",
        createdAt: (date: string) => `Erstellt am ${date}`,
      },
    },
    configModal: {
      hostInfo: `Der mit Ihrer Instanz verbundene Hostname.`,
      namespaceInfo: `Der Namespace (Projekt), in dem Sie Ihre Dev-Bereitstellungen durchführen möchten.`,
      tokenInfo: `Das mit Ihrer Instanz verbundene Token.`,
      validationError: "Sie müssen alle erforderlichen Felder ausfüllen, bevor Sie fortfahren können.",
      connectionError: "Verbindung abgelehnt. Bitte überprüfen Sie die angegebenen Details.",
      missingPermissions:
        "Es fehlen die erforderlichen Berechtigungen für Dev Deployments (Deployments, Services, Ingresses). Überprüfen Sie Ihre Benutzerberechtigungen und versuchen Sie es erneut.",
      namespaceNotFound: (namespace: string) => `Der Namespace ${namespace} wurde in Ihrem Cluster nicht gefunden.`,
      configExpiredWarning: "Token oder Konto ist abgelaufen. Bitte aktualisieren Sie Ihre Konfiguration.",
      useOpenShiftWizard: "Konfigurieren Sie eine neue Developer Sandbox für Red Hat OpenShift mit Hilfe des geführten Assistenten",
      useKubernetesWizard: "Konfigurieren Sie einen neuen lokalen Kubernetes-Cluster mithilfe des geführten Assistenten",
    },
    deployConfirmModal: {
      title: "Bereitstellen",
      body: "Dieser Vorgang kann einige Minuten in Anspruch nehmen, und Sie müssen eine neue Bereitstellung erstellen, wenn Sie Ihr Modell aktualisieren, da Dev-Deployments unveränderbar sind.",
    },
    deleteConfirmModal: {
      title: "Dev deployment(s) löschen",
      body: "Sind Sie sicher, dass Sie Ihre Dev deployment(s) löschen möchten?",
    },
    alerts: {
      deployStartedError:
        "Bei der Erstellung Ihres Dev Deployments ist etwas schief gelaufen. Bitte überprüfen Sie Ihre Konfiguration und versuchen Sie es erneut.",
      deployStartedSuccess: "Ihre Dev Deployment wurde erfolgreich gestartet und wird in Kürze verfügbar sein.",
      deleteError: "Dev Deployment(s) konnte(n) nicht gelöscht werden. Bitte versuchen Sie es erneut über die OpenShift-Konsole oder CLI.",
      deleteSuccess: "Dev deployment(s) erfolgreich gelöscht.",
    },
    introduction: {
      explanation: `Erstellen Sie Dev Deployments in der Cloud und teilen Sie sie mit anderen.`,
      disclaimer: `${
        de_common.names.devDeployments
      } ist für ${"Entwicklung".bold()} gedacht und sollte nicht für geschäftskritische Workloads verwendet werden.`,
      getStarted: "Um loszulegen, konfigurieren Sie Ihre Instanzinformationen.",
    },
    openShiftConfigWizard: {
      header: {
        provider: "Anbieter",
      },
      steps: {
        first: {
          name: "Instanz erstellen",
          introduction: `Um Ihre ${de_common.names.shortDevSandbox} Instanz zu erstellen :`,
          goToGetStartedPage: "Gehen Sie zur Seite Erste Schritte",
          followSteps: `Folgen Sie den Schritten, um Ihre Instanz zu starten. Sie werden aufgefordert, sich mit Ihrem ${de_common.names.redHat}-Konto anzumelden.`,
          informNamespace: `Sobald Ihre Instanz in Betrieb ist, teilen Sie den Namespace (Projekt) mit, in dem Sie Ihre Dev Deployments erstellen möchten.`,
          inputReason:
            "Diese Informationen sind notwendig, um Ihre Dev Deployments im richtigen Namespace (Projekt) zu erstellen.",
          namespacePlaceholder: `Der Namespace (Projekt), in dem Sie Ihre Dev Deployments erstellen möchten.`,
        },
        second: {
          name: "Anmeldeinformationen eingeben",
          introduction: `In Ihrer ${de_common.names.shortDevSandbox} Instanz:`,
          accessLoginCommand: `Klicken Sie auf Ihren Benutzernamen in der oberen rechten Ecke und dann auf ${"'Copy login command'".bold()}.`,
          accessDisplayToken: `Wenn Sie dazu aufgefordert werden, melden Sie sich mit ${"'DevSandbox'".bold()} an, und rufen Sie dann den Link ${"'Display Token'".bold()} auf.`,
          copyInformation: `Kopieren Sie im Abschnitt ${"'Log in with this token'".bold()} die Werte ${"'--server'".bold()} und ${"'--token'".bold()}, und fügen Sie sie unten ein.`,
          inputReason: "Diese Informationen sind notwendig, um eine Verbindung mit Ihrer Instanz herzustellen.",
          hostPlaceholder: "Fügen Sie hier den Wert für --server ein",
          tokenPlaceholder: "Fügen Sie den Wert von --token hier ein",
        },
        final: {
          name: "Verbinden",
          connectionSuccess: "Verbindung erfolgreich hergestellt.",
          connectionError: "Verbindung abgelehnt.",
          introduction: "Jetzt können Sie Dev Deployments auf dieser OpenShift-Instanz erstellen.",
          configNote: "Das von Ihnen angegebene Token wird lokal in diesem Browser gespeichert und niemals an Dritte weitergegeben.",
          connectionErrorLong: `Eine Verbindung mit Ihrer ${de_common.names.shortDevSandbox} Instanz konnte nicht hergestellt werden.`,
          checkInfo: "Bitte überprüfen Sie die angegebenen Informationen und versuchen Sie es erneut.",
          possibleErrorReasons: {
            introduction: "Hier sind einige mögliche Ursachen:",
            emptyField: "Eine oder mehrere erforderliche Informationen sind nicht ausgefüllt.",
            instanceExpired:
              "Die Instanzen laufen nach 30 Tagen ab. Nach Ablauf dieser Frist müssen Sie die Instanz neu erstellen und erhalten somit einen neuen Host.",
            tokenExpired: "Token laufen täglich ab.",
          },
        },
      },
    },
    kubernetesConfigWizard: {
      header: {
        provider: "Anbieter",
      },
      fields: {
        namespace: "Namespace",
        namespaceInfo: "Der Namespace im Cluster, in dem Ihre Dev Deployments erstellt werden sollen.",
        kubernetesApiServerUrl: "Kubernetes API Server URL",
        kubernetesApiServerUrlInfo: "Der Hostname, der mit dem Kubernetes-API-Server Ihres Clusters verknüpft ist.",
        tokenInfo: "Das Token, das mit Ihrem Dienstkonto verbunden ist.",
      },
      steps: {
        first: {
          name: "Create your Kubernetes cluster",
          introduction:
            "In order to create your local Kubernetes cluster first select the flavor you would like and follow the steps:",
          installFlavor: (flavor: string) => `Download and install ${flavor}.`,
          installKubectl: "Install Kubectl if you don't have it already.",
          runCommandsTerminal: "For this step, run the commands in a terminal.",
          createCluster: "Create your cluster:",
          installIngress: "Install the Ingress Controller and wait for it to be ready:",
          installKieSandboxYaml:
            "Install a proxy for the Kubernetes API Server and create the required Service Accounts:",
        },
        second: {
          name: "Set connection info",
          introduction:
            "With your cluster up and running, it should be available in the host prefilled below, and should have a Namespace created.",
          disclaimer:
            "Only change the values below if you have a custom Kubernetes installation, but beware that things might not go as expected.",
          hostInputReason: "This information is necessary for establishing a connection with your Kubernetes cluster.",
          namespaceInputReason:
            "This information is necessary for creating your Dev deployments in the correct Namespace.",
          namespacePlaceholder: "The Namespace where you want to create your Dev deployments.",
          hostPlaceholder: "The Kubernetes API Server URL",
        },
        third: {
          name: "Authenticate",
          introduction:
            "The Kubernetes API requires an authentication token for all requests. In this step we will get the authentication token for the Service Account we created before.",
          getToken: "Run the command below in your terminal to get the authentication token then copy it:",
          tokenPlaceholder: "Paste the token value here",
          tokenInputReason: "The token is necessary to authenticate requests to the Kubernetes API Server",
        },
        final: {
          name: "Connect",
          connectionSuccess: "Connection successfully established.",
          connectionError: "Connection refused.",
          introduction: "Now you are able to create Dev deployments on this OpenShift instance.",
          configNote: "The token you provide is locally stored in this browser and is never shared with anyone.",
          connectionErrorLong: `A connection with your Kubernetes cluster could not be established.`,
          checkInfo: "Please check the information provided and try again.",
          possibleErrorReasons: {
            introduction: "Here are some possible reasons:",
            emptyField: "One or more required information are not filled.",
            clusterNotCreatedCorrectly: "Your Kubernetes cluster might not have been created correctly.",
            tokenExpired: "Tokens might be expired, try creating a new one.",
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
        alert: `You have new changes to push. Embedding as a ${de_common.names.github} gist won't show your latest changes.`,
        tooltip: `Only available when editing a file from a ${de_common.names.github} gist.`,
        label: `${de_common.names.github} gist`,
        description:
          "The embedded Editor will fetch the content from the open gist. Changes made to this gist will be reflected in the Editor.",
      },
    },
    embedCode: "Embed code",
    copiedToClipboard: "Copied to clipboard",
  },
  connectToGitModal: {
    github: {
      header: {
        title: `${de_common.names.github} ${de_common.names.oauth} ${de_common.terms.token}`,
        subtitle: `Set up your ${de_common.names.github} token so you can interact with GitHub.`,
      },
      footer: {
        createNewToken: "Generate new token",
        placeHolder: "Paste your token here",
      },
      body: {
        learnMore: `Learn more about ${de_common.names.github} tokens`,
        note: `You should provide a token with the ${"'gist'".bold()} permission.`,
      },
      validation: {
        scopes: {
          helper: "Your token must include the 'repo' and 'gist' scopes.",
        },
      },
      form: {
        token: {
          label: "Personal Access Token (classic)",
          placeHolder: "Paste your GitHub token here",
        },
      },
    },
    bitbucket: {
      header: {
        title: `${de_common.names.bitbucket} ${de_common.names.oauth} ${de_common.terms.token}`,
        subtitle: `Set up your ${de_common.names.bitbucket} App Password so you can interact with Bitbucket.`,
      },
      footer: {
        createNewToken: "Generate new App Passord",
        placeHolder: "Paste your App Password here",
      },
      body: {
        learnMore: `Learn more about ${de_common.names.bitbucket} App Passwords`,
        note: `You should provide a token with the ${"'snippet'".bold()} permission.`,
      },
      validation: {
        scopes: {
          helper: "Your token must include the 'account', 'repository' and 'snippet' scopes.",
        },
      },
      form: {
        username: {
          label: "Bitbucket username",
          placeHolder: "Paste your Bitbucket username here",
        },
        token: {
          label: "Bitbucket App Password",
          placeHolder: "Paste your Bitbucket App Password here",
        },
      },
    },
    auth: {
      disclaimer: `The token you provide is locally stored in this browser and is never shared with anyone.`,
      error: {
        alreadyLoggedIn: "You're already logged in with this Token.",
        oauthScopes: (requiredScopes: string) =>
          `Make sure your Token includes the necessary OAuth2 scopes: ${requiredScopes}`,
      },
    },
    navigation: {
      continue: "Continue",
      seeConnectedAccounts: "See connected accounts",
    },
    status: {
      loading: "Loading...",
    },
  },
  commitModal: {
    title: "Input custom commit message",
    description: "Write a brief summary of the changes made to the workspace, ideally up to 72 characters.",
    commit: "Commit",
    emptyMessageValidation: "Commit message cannot be empty",
    placeholder: "Commit message",
  },
  homePage: {
    uploadFile: {
      header: "Edit existing file",
      body: `Upload your ${de_common.names.bpmn}, ${de_common.names.dmn} or ${de_common.names.pmml} file here to start making new edits!`,
      helperText: `Upload a .${de_common.names.bpmn}, .${de_common.names.bpmn}2, .${de_common.names.dmn} or .${de_common.names.pmml} file`,
      helperInvalidText: "File extension is not supported",
      placeholder: "Drag a file or browse for it.",
    },
    openUrl: {
      validating: `Validating ${de_common.names.url}`,
      invalidGistExtension: "File type on the provided gist is not supported.",
      invalidExtension: `File type on the provided ${de_common.names.url} is not supported.`,
      invalidGist: `Enter a valid gist ${de_common.names.url}. If you're using a specific gist ${de_common.names.url} remember its name can't have whitespaces and upper-case letters.`,
      invalidUrl: `This ${de_common.names.url} is not valid (don't forget "https://"!).`,
      notFoundUrl: `This ${de_common.names.url} does not exist.`,
      corsNotAvailable: `This ${de_common.names.url} cannot be opened because it doesn't allow other websites to access it.`,
      openFromSource: "Open from source",
      description: `Paste a ${de_common.names.url} to a source code link (${de_common.names.github}, ${de_common.names.dropbox}, etc.)`,
    },
    dropdown: {
      onlineForum: "Online forum",
    },
    bpmnCard: {
      title: `Workflow (.${de_common.names.bpmn})`,
      explanation: `${de_common.names.bpmn} files are used to generate Workflows.`,
      createNew: "Create new workflow",
    },
    dmnCard: {
      title: `Decision model (.${de_common.names.dmn})`,
      explanation: `${de_common.names.dmn} files are used to generate decision models`,
      createNew: "Create new decision model",
    },
    pmmlCard: {
      title: `Scorecard model (.${de_common.names.pmml})`,
      explanation: `${de_common.names.pmml} files are used to generate scorecards`,
      createNew: "Create new Scorecard",
    },
    trySample: "Try Sample",
    chooseLocalFile: "Choose a local file",
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
    error: {
      title: `${de_common.terms.oops}!`,
      explanation: `The ${de_common.names.dmnRunner} couldn't be rendered due to an error.`,
      message: [
        `This ${de_common.names.dmn} has a construct that is not supported. Please refer to `,
        wrapped("jira"),
        " and report an issue. Don't forget to upload the current file, and the used inputs",
      ],
    },
    table: { ...de_unitables },
    modal: {
      initial: {
        runDmnModels: "Run your models and see live forms and results as you edit.",
        explanation:
          "Input nodes become interactive fields on an auto-generated form, and the results are displayed as easy-to-read cards.",
        notificationPanelExplanation: [
          `The Problems panel `,
          wrapped("icon"),
          `, at the bottom-right corner of the Editor, displays live Execution messages to assist modeling your decisions.`,
        ],
      },
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
    },
    dropdown: {
      label: `${de_common.names.extendedServices}`,
      setup: `${de_common.terms.setup} ${de_common.names.extendedServices}`,
      open: `${de_common.terms.open} ${de_common.names.extendedServices} panel`,
      close: `${de_common.terms.close} ${de_common.names.extendedServices} panel`,
    },
    button: {
      available: `This is only available in ${de_common.names.chrome} at the moment`,
    },
  },
  notificationsPanel: {
    name: "Problems panel",
    tooltip: {
      retractAll: "Retract All",
      expandAll: "Expand All",
    },
  },
  extendedServices: {
    dropdown: {
      shortConnected: (port: string) => `Connected to port ${port}`,
      tooltip: {
        connected: `${de_common.names.extendedServices} is connected.`,
        install: `Setup ${de_common.names.extendedServices} to use this feature. Click to install.`,
        outdated: `${de_common.names.extendedServices} is outdated. Click to update.`,
        disconnected: `${de_common.names.extendedServices} is disconnected.`,
      },
    },
    modal: {
      initial: {
        subHeader: `Augment the ${de_common.names.dmn} editor`,
      },
    },
  },
  createGitRepositoryModal: {
    form: {
      buttonCreate: "Create",
      nameField: {
        label: "Name",
        hint: "Invalid name. Only letters, numbers, dashes (-), dots (.), and underscores (_) are allowed.",
      },
      visibility: {
        public: {
          label: "Public",
          description: "Anyone on the internet can see this repository. You choose who can commit.",
        },
        private: {
          label: "Private",
          description: "You choose who can see and commit to this repository.",
        },
      },
    },
    bitbucket: {
      repository: `${de_common.names.bitbucket} repository`,
      createRepository: `Create ${de_common.names.bitbucket} repository`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${de_common.names.bitbucket} repository.`,
      error: {
        formAlert: (error: string) => `Error creating ${de_common.names.bitbucket} repository. ${error}`,
      },
      form: {
        select: {
          label: "Pick a workspace under which the new repository will be created.",
          description: "Pick either a personal or shared workspace.",
        },
      },
    },
    github: {
      repository: `${de_common.names.github} repository`,
      createRepository: `Create ${de_common.names.github} repository`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${de_common.names.github} repository.`,
      error: {
        formAlert: (error: string) => `Error creating ${de_common.names.github} repository. ${error}`,
      },
      form: {
        select: {
          label: "The new repository will be created under the following scope",
          description: "Pick either your user account or a GitHub organization.",
        },
      },
    },
  },
  createGistOrSnippetModal: {
    form: {
      buttonCreate: "Create",
      visibility: {
        public: {
          label: "Public",
          description: "Anyone on the internet can see this repository. You choose who can commit.",
        },
        private: {
          label: "Private",
          description: "You choose who can see and commit to this repository.",
        },
      },
    },
    bitbucket: {
      gistOrSnippet: `${de_common.names.bitbucket} Snippet`,
      create: `Create ${de_common.names.bitbucket} Snippet`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${de_common.names.bitbucket} Snippet.`,
      error: {
        formAlert: (error: string) => `Error creating ${de_common.names.bitbucket} Snippet. ${error}`,
      },
      form: {
        select: {
          label: "Pick a workspace under which the new Snippet will be created.",
          description: "Pick either a personal or shared workspace.",
        },
      },
    },
    github: {
      gistOrSnippet: `${de_common.names.github} Gist`,
      create: `Create ${de_common.names.github} Gist`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${de_common.names.github} Gist.`,
      error: {
        formAlert: (error: string) => `Error creating ${de_common.names.github} Gist. ${error}`,
      },
      form: {
        select: {
          label: "The Gist will be created under the following user.",
          description: "Currently GitHub does not allow to create Gists in GitHub organizations.",
        },
      },
    },
  },
  loadOrganizationsSelect: {
    bitbucket: {
      user: "Bitbucket user",
      organizations: "Bitbucket workspaces",
    },
    github: {
      user: "GitHub user",
      organizations: "GitHub organizations",
    },
  },
  gitStatusIndicatorActions: {
    revert: {
      title: "Revert",
      warning: "This action is permanent",
      description: "Are you sure you want to revert local changes to:",
      confirmButtonText: "Yes, revert permanently",
    },
    revertAll: {
      title: "Revert all changes",
      warning: "This action is permanent",
      description: "Are you sure? The following files will be reverted to the last commit:",
      confirmButtonText: "Yes, revert permanently",
    },
  },
};
