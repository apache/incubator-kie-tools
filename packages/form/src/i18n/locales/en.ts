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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { wrapped } from "@kie-tools-core/i18n/dist/core";
import { FormI18n } from "../FormI18n";

export const en: FormI18n = {
  ...en_common,
  form: {
    status: {
      autoGenerationError: {
        title: `${en_common.terms.oops}!`,
        explanation: "Form cannot be rendered because of an error.",
        checkNotificationPanel: ["Check for ", wrapped("link"), ` error on the Notifications Panel`],
      },
      emptyForm: {
        title: "No Form",
        explanation: `Without inputs.`,
      },
      validatorError: {
        title: "An error occurred while trying to generate the form",
        message: [
          `The JSON schema contains a construct that is not yet supported. Please refer to `,
          wrapped("jira"),
          " and report an issue. Don't forget to upload the current file.",
        ],
      },
    },
  },
  schema: {
    selectPlaceholder: "Select...",
  },
};
