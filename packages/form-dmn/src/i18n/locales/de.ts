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

import { de as de_common } from "@kie-tools/i18n-common-dictionary";
import { FormDmnI18n } from "../FormDmnI18n";
import { TranslatedDictionary, wrapped } from "@kie-tools-core/i18n/dist/core";

export const de: TranslatedDictionary<FormDmnI18n> = {
  ...de_common,
  form: {
    status: {
      autoGenerationError: {
        title: `${de_common.terms.oops}!`,
        explanation: "Formular kann wegen eines Fehlers nicht dargestellt werden.",
        checkNotificationPanel: ["Auf ", wrapped("link"), ` Fehler auf dem Benachrichtigungsfeld prüfen`],
      },
      emptyForm: {
        title: "Kein Formular",
        explanation: `Assoziiertes ${de_common.names.dmn} hat keine Eingaben.`,
      },
      validatorError: {
        title: "Beim Versuch, das Formular zu erstellen, ist ein Fehler aufgetreten",
        message: [
          `Dieses ${de_common.names.dmn}-Modell enthält ein Konstrukt, das noch nicht unterstützt wird. Bitte beziehen Sie sich auf `,
          wrapped("jira"),
          " und melden Sie ein Problem. Vergessen Sie nicht, die aktuelle Datei hochzuladen.",
        ],
      },
    },
  },
  validation: {
    xDmnAllowedValues: "gehört nicht zur Menge der zulässigen Werte",
    daysAndTimeError: "sollte dem Format P1D(Tage)T2H(Stunden)3M(inuten)1S(ekunden) entsprechen",
    yearsAndMonthsError: "sollte dem Format P1Y(Jahre)2M(onate) entsprechen",
  },
  schema: {
    selectPlaceholder: "Auswählen...",
  },
  dmnSchema: {
    daysAndTimePlaceholder: "P1DT5H oder P2D oder PT1H2M10S",
    yearsAndMonthsPlaceholder: "P1Y5M oder P2Y oder P1M",
  },
  result: {
    evaluation: {
      succeeded: "Erfolgreich evaluiert",
      skipped: "Evaluierung übersprungen",
      failed: "Evaluierung fehlgeschlagen",
    },
    error: {
      title: `${de_common.terms.oops}!`,
      explanation: "Das Ergebnis kann aufgrund eines Fehlers nicht wiedergegeben werden.",
      message: [
        `Dieses Ergebnis enthält ein Konstrukt, das noch nicht unterstützt wird. Bitte beachten Sie `,
        wrapped("jira"),
        " und melden Sie einen Fehler. Vergessen Sie nicht, die aktuelle Datei und die verwendeten Eingaben hochzuladen",
      ],
    },
    dateTooltip: ["Dieser Wert ist in UTC angegeben. Der Wert in Ihrer aktuellen Zeitzone lautet ", wrapped("date")],
    withoutResponse: {
      title: "Keine Antwort",
      explanation: "Die Antwort erscheint, nachdem die Entscheidungen evaluiert wurden.",
    },
  },
};
