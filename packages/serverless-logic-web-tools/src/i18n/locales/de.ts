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
      insecurelyDisableTlsCertificateValidation: "Unsichere Deaktivierung der TLS Zertifikat Validierung",
      insecurelyDisableTlsCertificateValidationInfo:
        "Bei Auswahl dieser Option wird die Verifizierung des TLS Zertifikates für dieses Konto deaktiviert. Dies ist eine Alternative dazu, sich nicht mit den Einschränkungen des Browsers auseinandersetzen zu müssen wenn sich Ihr Cluster hinter einem HTTPS-Endpunkt mit einem selbstsignierten Zertifikat befindet. Bitte beachten Sie, dass die Verwendung von selbstsignierten Zertifikaten eine abgeschwächte Form von Sicherheit darstellt. Wenden Sie sich an Ihren Cluster Administrator, um ein vertrauenswürdiges Zertifikat zu verwenden. Weitere Informationen finden Sie unter <a href='https://cwe.mitre.org/data/definitions/295.html' target='_blank'>https://cwe.mitre.org/data/definitions/295.html</a>.",
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
