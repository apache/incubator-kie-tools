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
import { wrapped } from "@kie-tools-core/i18n/dist/core";
import { FormI18n } from "../FormI18n";

export const de: FormI18n = {
  ...de_common,
  form: {
    status: {
      autoGenerationError: {
        title: `${de_common.terms.oops}!`,
        explanation: "Das Formular kann aufgrund eines Fehlers nicht dargestellt werden.",
        checkNotificationPanel: ["Auf ", wrapped("link"), ` Fehler im Benachrichtigungsfeld prüfen`],
      },
      emptyForm: {
        title: "Kein Formular",
        explanation: `Ohne Eingänge.`,
      },
      validatorError: {
        title: "Beim Versuch, das Formular zu erstellen, ist ein Fehler aufgetreten",
        message: [
          `Das JSON-Schema enthält ein Konstrukt, das noch nicht unterstützt wird. Bitte beachten Sie `,
          wrapped("jira"),
          " und melden Sie ein Problem. Vergessen Sie nicht, die aktuelle Datei hochzuladen.",
        ],
      },
    },
  },
  schema: {
    selectPlaceholder: "Auswählen...",
  },
};
