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
  masthead: {
    disclaimer: {
      title: "Nur für die Entwicklung",
      description: `Diese Bereitstellung ist für die Verwendung während der ${"Entwicklung".bold()} vorgesehen, daher sollten die Benutzer die
        Dienste nicht in der Produktion oder für irgendeine Art von geschäftskritischen Arbeitslasten verwenden.`,
    },
  },
  page: {
    error: {
      title: `${de_common.terms.oops}!`,
      explanation: "Die Seite konnte aufgrund eines Fehlers nicht gerendert werden.",
      referToJira: ["Bitte lesen Sie ", wrapped("jira"), " und melden Sie ein Problem."],
    },
  },
};
