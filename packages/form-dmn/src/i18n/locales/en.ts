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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { FormDmnI18n } from "../FormDmnI18n";
import { wrapped } from "@kie-tools-core/i18n/dist/core";

export const en: FormDmnI18n = {
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
        explanation: `Associated ${en_common.names.dmn} doesn't have any inputs.`,
      },
      validatorError: {
        title: "An error occurred while trying to generate the form",
        message: [
          `This ${en_common.names.dmn} model contains a construct that is not yet supported. Please refer to `,
          wrapped("jira"),
          " and report an issue. Don't forget to upload the current file.",
        ],
      },
    },
  },
  validation: {
    daysAndTimeError: "should match format P1D(ays)T2H(ours)3M(inutes)1S(econds)",
    yearsAndMonthsError: "should match format P1Y(ears)2M(onths)",
  },
  schema: {
    selectPlaceholder: "Select...",
  },
  dmnSchema: {
    daysAndTimePlaceholder: "P1DT5H or P2D or PT1H2M10S",
    yearsAndMonthsPlaceholder: "P1Y5M or P2Y or P1M",
  },
  result: {
    evaluation: {
      success: "Evaluated with success",
      skipped: "Evaluation skipped",
      failed: "Evaluation failed",
    },
    error: {
      title: `${en_common.terms.oops}!`,
      explanation: "Result cannot be rendered because of an error.",
      message: [
        `This result contains a construct that is not yet supported. Please refer to `,
        wrapped("jira"),
        " and report an issue. Don't forget to upload the current file, and the used inputs",
      ],
    },
    dateTooltip: ["This value is in UTC. The value in your current timezone is ", wrapped("date")],
    withoutResponse: {
      title: "No response",
      explanation: "Response appears after decisions are evaluated.",
    },
  },
};
