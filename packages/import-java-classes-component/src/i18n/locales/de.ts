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

import { ImportJavaClassesWizardI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const de: ImportJavaClassesWizardI18n = {
  ...en_common,
  modalButton: {
    text: "Java-Klassen importieren",
    disabledMessage:
      "Nicht verfügbar, bitte prüfen Sie, ob die Erweiterung 'Language Support for Java by Red Hat' korrekt installiert ist und die @KieActivator-Annotation vorhanden ist",
    errorMessage: "Es ist ein Fehler aufgetreten. Bitte überprüfen Sie Ihre WebView Developer Tools",
  },
  modalWizard: {
    title: "Java-Klassen importieren",
    description:
      "Konvertiert Ihre Java-Assets in DMN-Typen. Dies ist eine einmalige Importaktion: Wenn die Java-Klasse aktualisiert wird, müssen Sie sie erneut importieren.",
    firstStep: {
      stepName: "Java-Klassen auswählen",
      input: {
        placeholder: "Geben Sie hier den Klassennamen ein",
        title: "Suche:",
        tooltip: "Geben Sie mindestens 3 Zeichen ein, um die Suche zu starten",
      },
      emptyState: {
        title: "Keine Java-Klassen gefunden oder ausgewählt",
        body: "Geben Sie den Namen der Java-Klasse oder einen Teil des Namens ein, um die Klasse zu finden, die Sie importieren möchten.",
      },
    },
    secondStep: {
      stepName: "Felder auswählen",
    },
    thirdStep: {
      stepName: "Überprüfung",
      nextButtonText: "Import",
    },
    fieldTable: {
      fetchButtonLabel: "Abrufen",
    },
  },
};
