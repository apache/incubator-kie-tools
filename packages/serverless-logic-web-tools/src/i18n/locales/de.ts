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
        title:
          "Fehler beim Öffnen der Datei. Sie können sie als Text bearbeiten und das Diagramm nach der Korrektur erneut öffnen.",
        action: "Als Text öffnen",
      },
      copy: "Inhalt in Zwischenablage kopiert",
      updateGist: "Gist erfolgreich aktualisiert.",
      createGist: "Gist erfolgreich erstellt.",
      errorPushingGist: "Push für das aktuelle Gist fehlgeschlagen. Push erzwungen?",
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
    share: "Teilen",
  },
  openshift: {
    configModal: {
      hostInfo: `Der mit Ihrer Instanz verbundene Hostname.`,
      namespaceInfo: `Der Namespace (Projekt), in dem Sie das Modell bereitstellen möchten.`,
      tokenInfo: `Das mit Ihrer Instanz verknüpfte Token.`,
      acceptSelfSignedCertificates: "Verifizierung des TLS Zertifikates deaktivieren.",
      acceptSelfSignedCertificatesInfo:
        "Bei Auswahl dieser Option wird die Verifizierung des TLS Zertifikates für dieses Konto deaktiviert. Dies ist nur bei Verwendung von selbst-signierten Zertifikaten in Ihrer Cluster Umgebung empfohlen.",
      validationError: "Sie müssen alle erforderlichen Felder ausfüllen, bevor Sie fortfahren können.",
      connectionError: "Verbindung abgelehnt. Bitte überprüfen Sie die angegebenen Informationen.",
      configExpiredWarning: "Token oder Konto ist abgelaufen. Bitte aktualisieren Sie Ihre Konfiguration.",
    },
    confirmModal: {
      title: "Bereitstellen",
      body: "Sind Sie sicher, dass Sie Modelle aus diesem Arbeitsbereich in Ihrer Instanz bereitstellen möchten? Diese Aktion wird einige Minuten in Anspruch nehmen und Sie müssen eine neue Bereitstellung erstellen, wenn Sie Ihre Modelle aktualisieren.",
    },
    introduction: {
      explanation: `Stellen Sie Ihre Modelle in einer Cloud-Instanz auf OpenShift bereit und teilen Sie sie mit anderen. Dieses Tool ist für ${"Entwicklung".bold()} gedacht und sollte nicht für geschäftskritische Workloads verwendet werden.`,
    },
  },
  githubTokenModal: {
    header: {
      title: `${de_common.names.github} ${de_common.names.oauth} ${de_common.terms.token}`,
      subtitle: `Richten Sie Ihr ${de_common.names.github}-Token ein, damit Sie gist erstellen und aktualisieren können.`,
    },
    footer: {
      createNewToken: "Ein neues Token erstellen",
      placeHolder: "Fügen Sie Ihr Token hier ein",
    },
    body: {
      disclaimer:
        "Das von Ihnen angegebene Token wird lokal als Browser-Cookie gespeichert und niemals an Dritte weitergegeben.",
      learnMore: `Erfahren Sie mehr über ${de_common.names.github}-Token`,
      note: `Sie sollten ein Token mit der Berechtigung ${"'gist'".bold()} bereitstellen.`,
    },
  },
  extendedServices: {
    modal: {
      wizard: {
        title: `${de_common.names.extendedServices} ${de_common.terms.setup}`,
        description: `Wählen Sie Ihre ${de_common.terms.os.full} und folgen Sie den Anweisungen zur Installation und zum Start der ${de_common.names.extendedServices}.`,
        outdatedAlert: {
          title: `${de_common.names.extendedServices} sind veraltet!`,
          message: `Es sieht so aus, als ob Sie eine inkompatible Version von ${de_common.names.extendedServices} verwenden. Folgen Sie den nachstehenden Anweisungen, um sie zu aktualisieren.`,
        },
        stoppedAlert: {
          title: `${de_common.names.extendedServices} wurde angehalten!`,
          message: `Es sieht so aus, als ob ${de_common.names.extendedServices} plötzlich beendet wurden, bitte folgen Sie diesen Anweisungen, um sie wieder zu starten.`,
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
      use: {
        title: "Alles bereit! 🎉",
        connected: `Sie sind mit den ${de_common.names.extendedServices} verbunden.`,
        backToSandbox: "Zurück zur Sandbox",
      },
    },
    button: {
      available: `Diese Funktion ist derzeit nur in ${de_common.names.chrome} verfügbar`,
    },
    dropdown: {
      label: `${de_common.names.extendedServices}`,
      setup: `${de_common.terms.setup} ${de_common.names.extendedServices}`,
      open: `${de_common.terms.open} ${de_common.names.extendedServices} Panel`,
      close: `${de_common.terms.close} ${de_common.names.extendedServices} Panel`,
      shortConnected: (port: string) => `Verbunden mit Port ${port}`,
      tooltip: {
        connected: `${de_common.names.extendedServices} ist verbunden.`,
        install: `Richten Sie ${de_common.names.extendedServices} ein, um diese Funktion zu nutzen. Zum Installieren klicken.`,
        outdated: `${de_common.names.extendedServices} ist veraltet. Klicken Sie zum Aktualisieren.`,
        disconnected: `${de_common.names.extendedServices} ist nicht verbunden.`,
      },
    },
  },
  notificationsPanel: {
    name: "Benachrichtigungs-Panel",
    tooltip: {
      retractAll: "Alle einklappen",
      expandAll: "Alles aufklappen",
    },
  },
  deployments: {
    virtualServiceRegistry: {
      dependencyWarning: "Es gibt Abhängigkeiten von fremden Arbeitsbereichen!",
      dependencyWarningTooltip:
        "Modelle in diesem Arbeitsbereich können von Bereitstellungen aus anderen Arbeitsbereichen abhängen.",
    },
  },
};
