/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
        title:
          "Fehler beim Öffnen der Datei. Sie können sie als Text bearbeiten und das Diagramm nach der Korrektur erneut öffnen.",
        action: "Als Text öffnen",
      },
      copy: "Inhalt in Zwischenablage kopiert",
      updateGist: "Gist erfolgreich aktualisiert.",
      createGist: "Gist erfolgreich erstellt.",
      errorPushingGist: "Push für das aktuelle Gist fehlgeschlagen. Push erzwingen?",
      updateSnippet: "Snippet erfolgreich aktualisiert.",
      createSnippet: "Snippet erfolgreich erstellt.",
      errorPushingSnippet: "Push für das aktuelle Snippet fehlgeschlagen. Push erzwingen?",
      forcePushWarning: "WARNUNG: Dadurch wird Ihre Gist mit den lokalen Änderungen überschrieben!",
      invalidCurrentGist: `Das aktuelle Gist ${de_common.names.url} ist ungültig. Wenn Sie den Dateinamen aktualisiert haben, müssen Sie auch die ${de_common.names.url} aktualisieren.`,
      invalidGistFilename: "Ungültiger Dateiname. Dieses Gist hat bereits eine Datei mit diesem Namen.",
      error: `Beim Versuch, den letzten Vorgang auszuführen, ist ein Fehler aufgetreten. Überprüfen Sie, ob Ihr Authentifizierungstoken noch gültig ist und versuchen Sie es später erneut.`,
      unsaved: {
        titleLocal: "Sie haben neue Änderungen seit Ihrem letzten Download.",
        titleGit: "Es gibt neue Änderungen seit Ihrem letzten Push.",
        proceedAnyway: "Trotzdem fortfahren",
        message:
          "Die Dateien bleiben vorübergehend im Browser gespeichert, werden aber möglicherweise gelöscht, bevor Sie zurückkehren.",
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
      insecurelyDisableTlsCertificateValidation: "Unsichere Deaktivierung der TLS Zertifikat Validierung",
      insecurelyDisableTlsCertificateValidationInfo:
        "Bei Auswahl dieser Option wird die Verifizierung des TLS Zertifikates für dieses Konto deaktiviert. Dies ist eine Alternative dazu, sich nicht mit den Einschränkungen des Browsers auseinandersetzen zu müssen wenn sich Ihr Cluster hinter einem HTTPS-Endpunkt mit einem selbstsignierten Zertifikat befindet. Bitte beachten Sie, dass die Verwendung von selbstsignierten Zertifikaten eine abgeschwächte Form von Sicherheit darstellt. Wenden Sie sich an Ihren Cluster Administrator, um ein vertrauenswürdiges Zertifikat zu verwenden. Weitere Informationen finden Sie unter <a href='https://cwe.mitre.org/data/definitions/295.html' target='_blank'>https://cwe.mitre.org/data/definitions/295.html</a>.",
      validationError: "Sie müssen alle erforderlichen Felder ausfüllen, bevor Sie fortfahren können.",
      connectionError: "Verbindung abgelehnt. Bitte überprüfen Sie die angegebenen Details.",
      missingPermissions:
        "Es fehlen die erforderlichen Berechtigungen für Dev Deployments (Deployments, Services, Ingresses). Überprüfen Sie Ihre Benutzerberechtigungen und versuchen Sie es erneut.",
      namespaceNotFound: (namespace: string) => `Der Namespace ${namespace} wurde in Ihrem Cluster nicht gefunden.`,
      configExpiredWarning: "Token oder Konto ist abgelaufen. Bitte aktualisieren Sie Ihre Konfiguration.",
      useOpenShiftWizard:
        "Konfigurieren Sie eine neue Developer Sandbox für Red Hat OpenShift mit Hilfe des geführten Assistenten",
      useKubernetesWizard:
        "Konfigurieren Sie einen neuen lokalen Kubernetes-Cluster mithilfe des geführten Assistenten",
    },
    deployConfirmModal: {
      title: "Bereitstellen",
      body: "Dieser Vorgang kann einige Minuten in Anspruch nehmen, und Sie müssen eine neue Bereitstellung erstellen, wenn Sie Ihr Modell aktualisieren, da Dev-Deployments unveränderbar sind.",
    },
    deleteConfirmModal: {
      title: "Dev Deployment(s) löschen",
      body: "Sind Sie sicher, dass Sie Ihre Dev Deployment(s) löschen möchten?",
    },
    alerts: {
      deployStartedError:
        "Bei der Erstellung Ihres Dev Deployments ist etwas schief gelaufen. Bitte überprüfen Sie Ihre Konfiguration und versuchen Sie es erneut.",
      deployStartedSuccess: "Ihre Dev Deployment wurde erfolgreich gestartet und wird in Kürze verfügbar sein.",
      deleteError:
        "Dev Deployment(s) konnte(n) nicht gelöscht werden. Bitte versuchen Sie es erneut über die OpenShift-Konsole oder CLI.",
      deleteSuccess: "Dev Deployment(s) erfolgreich gelöscht.",
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
          configNote:
            "Das von Ihnen angegebene Token wird lokal in diesem Browser gespeichert und niemals an Dritte weitergegeben.",
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
          name: "Erstellen eines Kubernetes-Clusters",
          introduction:
            "Um Ihren lokalen Kubernetes-Cluster zu erstellen, wählen Sie zunächst die gewünschte Distribution aus und folgen Sie den Schritten:",
          installFlavor: (flavor: string) => `Herunterladen und Installieren von ${flavor}.`,
          installKubectl: "Installieren Sie Kubectl, falls Sie es noch nicht eingerichtet haben.",
          runCommandsTerminal: "Für diesen Schritt führen Sie die folgenden Befehle in einem Terminal aus.",
          createCluster: "Erstellen Sie Ihren Cluster:",
          installIngress: "Installieren Sie den Ingress Controller und warten Sie, bis er bereit ist.:",
          installKieSandboxYaml:
            "Installieren Sie einen Proxy für den Kubernetes-API-Server und erstellen Sie die erforderlichen Servicekonten:",
        },
        second: {
          name: "Verbindungsinformationen einstellen",
          introduction:
            "Wenn Ihr Cluster eingerichtet ist und läuft, sollte er auf dem unten vorausgefüllten Host verfügbar sein und einen Namespace haben.",
          disclaimer:
            "Ändern Sie die folgenden Werte nur, wenn Sie eine benutzerdefinierte Kubernetes-Installation haben, aber beachten Sie, dass die Dinge möglicherweise nicht wie erwartet funktionieren.",
          hostInputReason:
            "Diese Informationen sind erforderlich, um eine Verbindung mit Ihrem Kubernetes-Cluster herzustellen.",
          namespaceInputReason:
            "Diese Informationen sind notwendig, um Ihre Dev Deployments im richtigen Namespace zu erstellen.",
          namespacePlaceholder: "Der Namespace, in dem Sie Ihre Dev Deployments erstellen möchten.",
          hostPlaceholder: "Die URL des Kubernetes-API-Servers",
        },
        third: {
          name: "Authentifizierung",
          introduction:
            "Die Kubernetes-API erfordert ein Authentifizierungs-Token für alle Anfragen. In diesem Schritt erhalten wir das Authentifizierungs-Token für das zuvor erstellte Servicekonto.",
          getToken:
            "Führen Sie den folgenden Befehl in Ihrem Terminal aus, um das Authentifizierungs-Token zu erhalten, und kopieren Sie es:",
          tokenPlaceholder: "Fügen Sie das Token hier ein",
          tokenInputReason:
            "Das Token ist für die Authentifizierung von Anfragen an den Kubernetes-API-Server erforderlich.",
        },
        final: {
          name: "Verbinden",
          connectionSuccess: "Verbindung erfolgreich hergestellt.",
          connectionError: "Verbindung abgelehnt.",
          introduction: "Jetzt können Sie Dev Deployments auf dieser Kubernetes-Instanz erstellen.",
          configNote:
            "Das von Ihnen angegebene Token wird lokal in diesem Browser gespeichert und niemals an Dritte weitergegeben..",
          connectionErrorLong: `Eine Verbindung mit Ihrem Kubernetes-Cluster konnte nicht hergestellt werden.`,
          checkInfo: "Bitte überprüfen Sie die angegebenen Informationen und versuchen Sie es erneut.",
          possibleErrorReasons: {
            introduction: "Hier sind einige mögliche Ursachen:",
            emptyField: "Eine oder mehrere erforderliche Angaben sind nicht ausgefüllt.",
            clusterNotCreatedCorrectly: "Ihr Kubernetes-Cluster wurde möglicherweise nicht korrekt erstellt.",
            tokenExpired: "Die Token sind möglicherweise abgelaufen. Versuchen Sie, ein Neues zu erstellen..",
          },
        },
      },
    },
  },
  embedModal: {
    title: "Integrieren",
    description:
      "Binden Sie den Editor und den Inhalt in Ihre Seite ein. Wählen Sie die unten stehenden Optionen und kopieren Sie den Code in Ihre Zwischenablage:",
    copy: "Kopieren",
    source: {
      current: {
        label: "Aktueller Inhalt",
        description:
          "Der eingebettete Editor enthält den aktuellen Inhalt, so dass er nicht von außen geändert werden kann.",
      },
      gist: {
        alert: `Sie haben neue Änderungen zu pushen. Das Einbetten als ${de_common.names.github} gist wird Ihre letzten Änderungen nicht zeigen.`,
        tooltip: `Nur verfügbar, wenn eine Datei aus einem ${de_common.names.github} gist bearbeitet wird.`,
        label: `${de_common.names.github} gist`,
        description:
          "Der eingebettete Editor holt den Inhalt aus dem geöffneten Gist. Änderungen, die an diesem Gist vorgenommen werden, werden in den Editor übernommen.",
      },
    },
    embedCode: "Code einbinden",
    copiedToClipboard: "In Zwischenablage kopiert",
  },
  connectToGitModal: {
    github: {
      header: {
        title: `${de_common.names.github} ${de_common.names.oauth} ${de_common.terms.token}`,
        subtitle: `Richten Sie Ihr ${de_common.names.github}-Token ein, damit Sie mit GitHub interagieren können.`,
      },
      footer: {
        createNewToken: "Neues Token generieren",
        placeHolder: "Fügen Sie Ihren Token hier ein",
      },
      body: {
        learnMore: `Erfahren Sie mehr über ${de_common.names.github}-Tokens`,
        note: `Sie sollten ein Token mit der Berechtigung ${"'gist'".bold()} bereitstellen.`,
      },
      validation: {
        scopes: {
          helper: "Ihr Token muss die Scopes 'repo' und 'gist' enthalten.",
        },
      },
      form: {
        token: {
          label: "Personal Access Token (classic)",
          placeHolder: "Fügen Sie hier Ihr GitHub-Token ein",
        },
      },
    },
    bitbucket: {
      header: {
        title: `${de_common.names.bitbucket} ${de_common.names.oauth} ${de_common.terms.token}`,
        subtitle: `Richten Sie Ihr ${de_common.names.bitbucket} App Password ein, damit Sie mit Bitbucket interagieren können.`,
      },
      footer: {
        createNewToken: "Neue App Password generieren",
        placeHolder: "Fügen Sie Ihr App-Passwort hier ein",
      },
      body: {
        learnMore: `Erfahren Sie mehr über ${de_common.names.bitbucket} App Passwords`,
        note: `Sie sollten ein Token mit der Berechtigung ${"'snippet'".bold()} bereitstellen.`,
      },
      validation: {
        scopes: {
          helper: "Ihr Token muss die Scopes 'account', 'repository' und 'snippet' enthalten.",
        },
      },
      form: {
        username: {
          label: "Bitbucket Benutzername",
          placeHolder: "Fügen Sie hier Ihren Bitbucket-Benutzernamen ein",
        },
        token: {
          label: "Bitbucket App Password",
          placeHolder: "Fügen Sie Ihr Bitbucket App Passwort hier ein",
        },
      },
    },
    gitlab: {
      header: {
        title: `${de_common.names.gitlab} ${de_common.names.oauth} ${de_common.terms.token}`,
        subtitle: `Richten Sie Ihr ${de_common.names.gitlab}-Token ein, damit Sie mit ${de_common.names.gitlab} interagieren können.`,
      },
      footer: {
        createNewToken: "Neues Token generieren",
        placeHolder: "Fügen Sie hier Ihren Token ein",
      },
      body: {
        learnMore: `Erfahren Sie mehr über ${de_common.names.gitlab}-Token`,
        note: `Sie sollten ein Token mit der Berechtigung ${"'api' 'read_user' 'read_repository' 'write_repository'"} bereitstellen.`,
      },
      validation: {
        scopes: {
          helper: "Ihr Token muss die Bereiche 'api' 'read_user' 'read_repository' 'write_repository' enthalten.",
        },
      },
      form: {
        token: {
          label: "Persönliches Zugriffstoken",
          placeHolder: `Fügen Sie Ihr ${de_common.names.gitlab} Token ein.`,
        },
      },
    },
    auth: {
      disclaimer: `Das von Ihnen angegebene Token wird lokal in diesem Browser gespeichert und niemals an Dritte weitergegeben..`,
      error: {
        alreadyLoggedIn: "Sie sind bereits mit diesem Token eingeloggt.",
        oauthScopes: (requiredScopes: string) =>
          `Stellen Sie sicher, dass Ihr Token die erforderlichen OAuth2-Scopes enthält: ${requiredScopes}`,
      },
    },
    navigation: {
      continue: "Fortfahren",
      seeConnectedAccounts: "Siehe verknüpfte Konten",
    },
    status: {
      loading: "Laden...",
    },
    insecurelyDisableTlsCertificateValidation: "Unsichere Deaktivierung der TLS Zertifikat Validierung",
    insecurelyDisableTlsCertificateValidationInfo:
      "Checking this option will insecurely disable TLS certificate verification for this account. Only check this option if you trust the Git provider and it's behind an HTTPS endpoint with a self-signed certificate. Please be advised that the use of self-signed certificates is a weaker form of security, so consider contacting your Git provider to use a trusted certificate. For more information, refer to <a href='https://cwe.mitre.org/data/definitions/295.html' target='_blank'>https://cwe.mitre.org/data/definitions/295.html</a>.",
  },
  commitModal: {
    title: "Benutzerdefinierte Commit-Nachricht eingeben",
    description:
      "Schreiben Sie eine kurze Zusammenfassung der am Arbeitsbereich vorgenommenen Änderungen, idealerweise bis zu 72 Zeichen.",
    commit: "Commit",
    emptyMessageValidation: "Commit-Nachricht kann nicht leer sein",
    placeholder: "Commit-Nachricht",
  },
  homePage: {
    uploadFile: {
      header: "Vorhandene Datei bearbeiten",
      body: `Laden Sie Ihre ${de_common.names.bpmn}-, ${de_common.names.dmn}- oder ${de_common.names.pmml}-Datei hier hoch, um neue Änderungen vorzunehmen!`,
      helperText: `Hochladen einer .${de_common.names.bpmn}-, .${de_common.names.bpmn}2-, .${de_common.names.dmn}- oder .${de_common.names.pmml}-Datei`,
      helperInvalidText: "Dateierweiterung wird nicht unterstützt",
      placeholder: "Ziehen Sie eine Datei oder suchen Sie sie.",
    },
    openUrl: {
      validating: `Validiere ${de_common.names.url}`,
      invalidGistExtension: "Der Dateityp der angegebenen Gist wird nicht unterstützt.",
      invalidExtension: `Der Dateityp der angegebenen ${de_common.names.url} wird nicht unterstützt.`,
      invalidGist: `Geben Sie eine gültige Gist ${de_common.names.url} ein. Wenn Sie eine bestimmte Gist ${de_common.names.url} verwenden, denken Sie daran, dass deren Name keine Leerzeichen und Großbuchstaben enthalten darf.`,
      invalidUrl: `Diese ${de_common.names.url} ist nicht gültig (vergessen Sie nicht "https://"!).`,
      notFoundUrl: `Diese ${de_common.names.url} existiert nicht.`,
      corsNotAvailable: `Diese ${de_common.names.url} kann nicht geöffnet werden, weil sie anderen Websites den Zugriff darauf verwehrt.`,
      openFromSource: "Von der Quelle öffnen",
      description: `Einfügen einer ${de_common.names.url} zu einem Quellcode-Link (${de_common.names.github}, ${de_common.names.dropbox}, usw.)`,
    },
    dropdown: {
      onlineForum: "Online-Forum",
    },
    bpmnCard: {
      title: `Workflow (.${de_common.names.bpmn})`,
      explanation: `${de_common.names.bpmn}-Dateien werden zur Erstellung von Workflows verwendet.`,
      createNew: "Neuen Workflow erstellen",
    },
    dmnCard: {
      title: `Entscheidungsmodell (.${de_common.names.dmn})`,
      explanation: `${de_common.names.dmn}-Dateien werden zur Erstellung von Entscheidungsmodellen verwendet.`,
      createNew: "Neues Entscheidungsmodell erstellen",
    },
    pmmlCard: {
      title: `Scorecard-Modell (.${de_common.names.pmml})`,
      explanation: `${de_common.names.pmml}-Dateien werden zur Erstellung von Scorecards verwendet`,
      createNew: "Neue Scorecard erstellen",
    },
    trySample: "Beispiel ausprobieren",
    chooseLocalFile: "Eine lokale Datei auswählen",
  },
  alerts: {
    gistError: `Dieser Gist kann nicht geöffnet werden. Wenn Sie Ihren Gist-Dateinamen aktualisiert haben, kann es ein paar Sekunden dauern, bis die URL zur Verwendung zur Verfügung steht.`,
    goToHomePage: "Gehe zur Startseite",
    errorDetails: "Fehlerdetails:",
    responseError: {
      title: "Beim Abrufen Ihrer Datei ist ein Fehler aufgetreten",
    },
    fetchError: {
      title: "Ein unerwarteter Fehler trat auf, während Sie versuchten, Ihre Datei zu holen",
      possibleCauses: "Mögliche Ursachen:",
      missingGitHubToken: `Wenn Sie versuchen, eine private Datei zu öffnen, stellen Sie sicher, dass Sie zuvor Ihr GitHub-Token festlegen. Verwenden Sie dazu eine der Editor-Seiten und öffnen Sie das Modal "GitHub-Token festlegen" unter dem Dropdown-Menü "Teilen"`,
      cors: "Die URL zu Ihrer Datei muss CORS in ihrer Antwort zulassen, die den folgenden Header enthalten sollte:",
    },
  },
  dmnRunner: {
    error: {
      title: `${de_common.terms.oops}!`,
      explanation: `Der ${de_common.names.dmnRunner} konnte aufgrund eines Fehlers nicht dargestellt werden.`,
      message: [
        `Dieser ${de_common.names.dmn} hat ein Konstrukt, das nicht unterstützt wird. Bitte beachten Sie `,
        wrapped("jira"),
        " und melden Sie ein Problem. Vergessen Sie nicht, die aktuelle Datei und die verwendeten Eingaben hochzuladen",
      ],
    },
    table: { ...de_unitables },
    modal: {
      initial: {
        runDmnModels: "Führen Sie Ihre Modelle aus und sehen Sie die Ergebnisse live, während Sie sie bearbeiten.",
        explanation:
          "Eingabeknoten werden zu interaktiven Feldern in einem automatisch generierten Formular, und die Ergebnisse werden als leicht lesbare Karten angezeigt.",
        notificationPanelExplanation: [
          `Das Problem-Panel`,
          wrapped("icon"),
          `, in der unteren rechten Ecke des Editors, zeigt Live-Auswertungsmeldungen an, um Sie bei der Modellierung Ihrer Entscheidungen zu unterstützen.`,
        ],
      },
      wizard: {
        title: `${de_common.names.extendedServices} ${de_common.terms.setup}`,
        description: `Wählen Sie Ihre ${de_common.terms.os.full} und folgen Sie den Anweisungen zur Installation und zum Start der ${de_common.names.extendedServices}.`,
        outdatedAlert: {
          title: `${de_common.names.extendedServices} ist veraltet!`,
          message: `Es sieht so aus, als ob Sie eine inkompatible Version von ${de_common.names.extendedServices} verwenden. Folgen Sie den nachstehenden Anweisungen, um sie zu aktualisieren.`,
        },
        stoppedAlert: {
          title: `${de_common.names.extendedServices} wurde angehalten!`,
          message: `Es sieht so aus, als ob ${de_common.names.extendedServices} plötzlich beendet wurde, bitte folgen Sie diesen Anweisungen, um es wieder zu starten.`,
        },
        disabled: {
          title: `${de_common.names.extendedServices}`,
          alert: `Sie sind nicht mit ${de_common.names.extendedServices} verbunden.`,
          message: `Beachten Sie, dass einige Funktionen wie der ${de_common.names.dmnRunner}, ohne ${de_common.names.extendedServices} nicht verfügbar sind.`,
          helper: `Stellen Sie sicher, dass ${de_common.names.extendedServices} ausgeführt wird, und überprüfen Sie dann die Host- und Porteinstellungen.`,
        },
        macos: {
          install: {
            download: ` ${de_common.names.extendedServices}.`,
            openFile: [wrapped("file"), "-Datei öffnen."],
            dragFileToApplicationsFolder: ["Ziehen Sie ", wrapped("file"), " in den ", wrapped("folder"), " Ordner."],
          },
          start: {
            stopped: {
              startInstruction: `Wenn Sie das Symbol ${de_common.names.extendedServices} in Ihrer Systemleiste sehen, klicken Sie einfach darauf und wählen Sie "${de_common.terms.start}".`,
              launchExtendedServices: [
                `Wenn nicht, starten Sie die ${de_common.names.extendedServices} Anwendung, indem Sie `,
                wrapped("file"),
                " ausführen.",
              ],
            },
            firstTime: {
              title: `Wenn Sie gerade ${de_common.names.extendedServices} installiert haben:`,
              openApplicationsFolder: ["Öffnen Sie den ", wrapped("folder"), " Ordner."],
              again: "Nochmals",
              openAndCancel: [
                "Rechtsklick auf ",
                wrapped("file"),
                ` wählen Sie "${de_common.terms.open}" und dann "${de_common.terms.cancel}".`,
              ],
              openInstruction: [
                "Rechtsklick auf ",
                wrapped("file"),
                " ",
                wrapped("again"),
                ` und wählen Sie dann "${de_common.terms.open}".`,
              ],
            },
            alreadyRanBefore: `Wenn Sie die ${de_common.names.extendedServices} bereits installiert und ausgeführt haben:`,
            launchExtendedServices: ["Starten Sie die ", wrapped("file")],
            advanced: {
              title: "Erweiterte Einstellungen",
              runFollowingCommand: `Führen Sie den folgenden Befehl auf einer Terminal-Registerkarte aus, um ${de_common.names.extendedServices} auf einem anderen Port zu starten:`,
            },
          },
        },
        windows: {
          install: {
            keepDownload: ` ${de_common.names.extendedServices}. Beachten Sie, dass Sie wahrscheinlich mit der rechten Maustaste auf den Download klicken und "Behalten" wählen müssen`,
            moveTheFile: ["Verschieben Sie die ", wrapped("file"), " Datei in Ihren bevorzugten Ordner."],
          },
          start: {
            stopped: {
              startInstruction: `Wenn Sie das Symbol ${de_common.names.extendedServices} in Ihrer Systemleiste sehen, klicken Sie einfach darauf und wählen Sie "${de_common.terms.start}".`,
              launchExtendedServices: [
                `Wenn nicht, starten Sie die ${de_common.names.extendedServices}, indem Sie die `,
                wrapped("file"),
                " öffnen.",
              ],
            },
            firstTime: {
              title: `Wenn Sie gerade ${de_common.names.extendedServices} installiert haben:`,
              openFolder: ["Öffnen Sie den Ordner, in dem Sie die ", wrapped("file"), " Datei abgelegt haben."],
              runAnyway: `Doppelklicken Sie auf die Datei und wählen Sie "Mehr Informationen" und klicken Sie dann auf die Schaltfläche "Trotzdem ausführen".`,
            },
            alreadyRanBefore: `Wenn Sie die ${de_common.names.extendedServices} bereits installiert und vorher ausgeführt haben:`,
            launchExtendedServices: ["Öffnen Sie die ", wrapped("file"), " Datei."],
            advanced: {
              title: "Erweiterte Einstellungen",
              runFollowingCommand: `Führen Sie den folgenden Befehl in der Eingabeaufforderung aus, um ${de_common.names.extendedServices} auf einem anderen Port zu starten:`,
            },
          },
        },
        linux: {
          install: {
            download: ` ${de_common.names.extendedServices}.`,
            installAppIndicator: "Installieren Sie die AppIndicator-Bibliothek für Ihr System:",
            ubuntuDependency: [`${de_common.names.ubuntu}: `, wrapped("package")],
            fedoraDependency: [`${de_common.names.fedora}: `, wrapped("package")],
            extractContent: ["Extrahieren Sie den Inhalt von ", wrapped("file"), " an einen Ort Ihrer Wahl."],
            binaryExplanation: [
              `Die ${de_common.names.extendedServices} Datei, `,
              wrapped("file"),
              ", ist eine einzelne ausführbare Datei, was bedeutet, dass Sie sie zu Ihrem PATH hinzufügen oder sogar so konfigurieren können, dass sie beim Start Ihres Computers ausgeführt wird.",
            ],
          },
          start: {
            openTerminal: " Öffnen Sie ein Terminal-Fenster.",
            goToFolder: [
              "Wechseln Sie zu dem Ordner, in dem Sie die ausführbare Datei ",
              wrapped("file"),
              " abgelegt haben.",
            ],
            runCommand: "Ausführen",
            advanced: {
              title: "Erweiterte Einstellungen",
              runFollowingCommand: [
                "Öffnen Sie ein Terminalfenster und führen Sie den folgenden Befehl in dem Verzeichnis aus, in dem Sie die ausführbare Datei ",
                wrapped("file"),
                " abgelegt haben:",
              ],
            },
          },
        },
        footerWaitingToConnect: `Warten auf die Verbindung zu ${de_common.names.extendedServices}`,
        advancedSettings: {
          title: [
            `Der Standard-Port von ${de_common.names.extendedServices} ist `,
            wrapped("port"),
            `. Wenn Sie diesen Port bereits für eine andere Anwendung verwenden, können Sie den Port, der für die Verbindung mit ${de_common.names.extendedServices} verwendet wird, ändern.`,
          ],
          label: " Port",
          helperTextInvalid: "Ungültiger Port. Gültige Ports: 0 <= port <= 65353",
        },
      },
    },
    dropdown: {
      label: `${de_common.names.extendedServices}`,
      setup: `${de_common.terms.setup} ${de_common.names.extendedServices}`,
      open: `${de_common.terms.open} ${de_common.names.extendedServices} Panel`,
      close: `${de_common.terms.close} ${de_common.names.extendedServices} Panel`,
    },
    button: {
      available: `Diese Funktion ist derzeit nur in ${de_common.names.chrome} verfügbar`,
    },
  },
  notificationsPanel: {
    name: " Problem-Panel",
    tooltip: {
      retractAll: "Alle einklappen",
      expandAll: "Alles aufklappen",
    },
  },
  extendedServices: {
    dropdown: {
      shortConnected: (port: string) => `Verbunden mit port ${port}`,
      tooltip: {
        connected: `${de_common.names.extendedServices} ist verbunden.`,
        install: `Richten Sie ${de_common.names.extendedServices} ein, um diese Funktion zu nutzen. Zum Installieren klicken.`,
        outdated: `${de_common.names.extendedServices} ist veraltet. Klicken Sie zum Aktualisieren.`,
        disconnected: `${de_common.names.extendedServices} ist nicht verbunden.`,
      },
    },
    modal: {
      initial: {
        subHeader: `Erweitern Sie den ${de_common.names.dmn} Editor`,
      },
    },
  },
  createGitRepositoryModal: {
    form: {
      buttonCreate: "Erstellen",
      nameField: {
        label: "Name",
        hint: "Ungültiger Name. Nur Buchstaben, Zahlen, Bindestriche (-), Punkte (.) und Unterstriche (_) sind erlaubt.",
      },
      visibility: {
        public: {
          label: "Öffentlich",
          description: "Jeder im Internet kann dieses Repository sehen. Sie bestimmen, wer Commits durchführen kann.",
        },
        private: {
          label: "Privat",
          description: "Sie wählen aus, wer dieses Repository sehen und an es committen kann.",
        },
      },
    },
    bitbucket: {
      repository: `${de_common.names.bitbucket} repository`,
      createRepository: `Erstelle ${de_common.names.bitbucket} repository`,
      description: (workspace: string) =>
        `Der Inhalt von '${workspace}' wird in das neue ${de_common.names.bitbucket} Repository übernommen.`,
      error: {
        formAlert: (error: string) => `Fehler beim Erstellen des ${de_common.names.bitbucket} Repository. ${error}`,
      },
      form: {
        select: {
          label: "Wählen Sie einen Arbeitsbereich aus, unter dem das neue Repository erstellt werden soll.",
          description: "Wählen Sie entweder einen persönlichen oder einen gemeinsamen Arbeitsbereich.",
        },
      },
    },
    github: {
      repository: `${de_common.names.github} repository`,
      createRepository: `Erstelle ${de_common.names.github} repository`,
      description: (workspace: string) =>
        `Der Inhalt von '${workspace}' wird in das neue ${de_common.names.github} repository übernommen.`,
      error: {
        formAlert: (error: string) => `Fehler beim Erstellen des Repositorys ${de_common.names.github}. ${error}`,
      },
      form: {
        select: {
          label: "Das neue Repository wird unter dem folgenden Scope erstellt",
          description: "Wählen Sie entweder Ihr Benutzerkonto oder eine GitHub-Organisation.",
        },
      },
    },
    gitlab: {
      repository: `${de_common.names.gitlab} repository`,
      createRepository: `Erstelle ${de_common.names.gitlab} repository`,
      description: (workspace: string) =>
        `Der Inhalt von '${workspace}' wird in das neue ${de_common.names.gitlab} repository übernommen.`,
      error: {
        formAlert: (error: string) => `Fehler beim Erstellen des Repositorys ${de_common.names.gitlab}. ${error}`,
      },
      form: {
        select: {
          label: "Das neue Repository wird unter dem folgenden Scope erstellt",
          description: `Wählen Sie entweder Ihr Benutzerkonto oder eine ${de_common.names.gitlab} Gruppe.`,
        },
      },
    },
  },
  createGistOrSnippetModal: {
    form: {
      buttonCreate: "Erstellen",
      visibility: {
        public: {
          label: "Öffentlich",
          description: "Jeder im Internet kann dieses Repository sehen. Sie bestimmen, wer Commits durchführen kann.",
        },
        private: {
          label: "Privat",
          description: "Sie wählen aus, wer dieses Repository sehen und an es committen kann.",
        },
      },
    },
    bitbucket: {
      gistOrSnippet: `${de_common.names.bitbucket} Snippet`,
      create: `Erstelle ${de_common.names.bitbucket} Snippet`,
      description: (workspace: string) =>
        `Der Inhalt des '${workspace}' wird in dem neuen ${de_common.names.bitbucket} Snippet verfügbar gemacht.`,
      error: {
        formAlert: (error: string) => `Fehler beim Erstellen von ${de_common.names.bitbucket} Snippet. ${error}`,
      },
      form: {
        select: {
          label: "Wählen Sie einen Arbeitsbereich aus, unter dem das neue Snippet erstellt werden soll.",
          description: "Wählen Sie entweder einen persönlichen oder einen gemeinsamen Arbeitsbereich.",
        },
      },
    },
    github: {
      gistOrSnippet: `${de_common.names.github} Gist`,
      create: `Erstelle ${de_common.names.github} Gist`,
      description: (workspace: string) =>
        `Der Inhalt des '${workspace}' wird in dem neuen ${de_common.names.github} Gist verfügbar gemacht.`,
      error: {
        formAlert: (error: string) => `Fehler beim Erstellen von ${de_common.names.github} Gist. ${error}`,
      },
      form: {
        select: {
          label: " Der Gist wird unter dem folgenden Benutzer erstellt.",
          description: "Derzeit erlaubt GitHub nicht, Gists in GitHub-Organisationen zu erstellen.",
        },
      },
    },
    gitlab: {
      gistOrSnippet: `${de_common.names.gitlab} Snippet`,
      create: `Erstelle ${de_common.names.gitlab} Snippet`,
      description: (workspace: string) =>
        `Der Inhalt des '${workspace}' wird in dem neuen ${de_common.names.gitlab} Snippet.`,
      error: {
        formAlert: (error: string) => `Fehler beim Erstellen von ${de_common.names.gitlab} Snippet. ${error}`,
      },
      form: {
        select: {
          label: "Wählen Sie einen Arbeitsbereich aus, unter dem das neue Snippet erstellt wird.",
          description: "Wählen Sie entweder ein persönliches oder ein gemeinsames Projekt aus.",
        },
      },
    },
  },
  loadOrganizationsSelect: {
    bitbucket: {
      user: "Bitbucket-Benutzer",
      organizations: "Bitbucket-Workspaces",
    },
    github: {
      user: "GitHub-Benutzer",
      organizations: "GitHub Organisationen",
    },
    gitlab: {
      user: `${de_common.names.gitlab} user`,
      organizations: `${de_common.names.gitlab} Gruppen`,
    },
  },
  gitStatusIndicatorActions: {
    revert: {
      title: "Rückgängig machen",
      warning: "Diese Aktion ist permanent",
      description: "Sind Sie sicher, dass Sie lokale Änderungen rückgängig machen wollen an: ",
      confirmButtonText: "Ja, dauerhaft rückgängig machen",
    },
    revertAll: {
      title: "Alle Änderungen rückgängig machen",
      warning: "Diese Aktion ist permanent",
      description: "Sind Sie sicher? Die folgenden Dateien werden auf den letzten Commit zurückgesetzt:",
      confirmButtonText: "Ja, dauerhaft rückgängig machen",
    },
  },
};
