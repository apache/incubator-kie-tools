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

import { ChromeExtensionI18n } from "..";

export const de: ChromeExtensionI18n = {
  openIn: (name: string) => `Öffnen in ${name}`,
  seeAsDiagram: "Siehe als Diagramm",
  fullScreen: "Vollbild",
  reset: "Zurücksetzen",
  note: "Anmerkung",
  single: {
    exitFullScreen: `Vollbild beenden`,
    editorToolbar: {
      fixAndSeeAsDiagram: "Korrigieren Sie Ihre Datei und versuchen Sie, sie erneut zu öffnen.",
      errorOpeningFile: `Der Editor für diese Datei kann nicht geöffnet werden.`,
      seeAsSource: "Als Quelltext sehen",
      copyLinkTo: (name: string) => `Link kopieren auf ${name}`,
      linkCopied: "Link in Zwischenablage kopiert",
      readOnly: "Dies ist eine schreibgeschützte Visualisierung.",
    },
  },
  pr: {
    isolated: {
      viewOriginal: "Originaldatei anzeigen",
    },
    toolbar: {
      closeDiagram: "Diagramm schließen",
      original: "Original",
      changes: "Änderungen",
    },
  },
  common: {
    menu: {
      createToken: "Token erstellen",
      placeYourToken: "Token hier eingeben...",
      tokenInfo: {
        title: "Token werden nur lokal als Cookies gespeichert.",
        disclaimer: "Wir speichern Ihren Token nicht und geben ihn auch nicht an Dritte weiter.",
        explanation: `Wir verwenden Ihr GitHub OAuth Token, um eine bessere Erfahrung bei der Verwendung benutzerdefinierter Editoren zu ermöglichen. Die offizielle GitHub-API verfügt über einen Drosselungsmechanismus mit einem recht niedrigen Schwellenwert für nicht authentifizierte Anfragen.`,
        whichPermissionUserGive: `Durch die Authentifizierung mit Ihrem OAuth Token können wir Verzögerungen beim Abrufen kürzlich aktualisierter Dateien vermeiden und auch Funktionen bereitstellen, die aus Ihren Repositories gelesen werden müssen, wie z. B. Work Item Definitions in BPMN-Diagrammen.`,
        permission: `${"Für öffentliche Repositories sind keine besonderen Berechtigungen erforderlich".bold()}. Sie können sogar ein Token generieren, ohne ein Kästchen anzukreuzen. Für private Repositories sollten Sie jedoch ein Token mit der Berechtigung ${"'repo'".bold()} bereitstellen.`,
      },
    },
  },
};
