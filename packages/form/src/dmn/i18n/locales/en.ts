/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";
import { DmnFormI18n } from "../DmnFormI18n";
import { wrapped } from "@kogito-tooling/i18n/dist/core";

export const en: DmnFormI18n = {
  ...en_common,
  form: {
    validation: {
      daysAndTimeError: "should match format P1D(ays)2T(ime)",
      yearsAndMonthsError: "should match format P1Y(ers)2M(onths)",
    },
    preProcessing: {
      selectPlaceholder: "Select...",
      daysAndTimePlaceholder: "P1D5T or P2D or P1T",
      yearsAndMonthsPlaceholder: "P1Y5M or P2Y or P1M",
    },
  },
  result: {
    evaluation: {
      success: "Evaluated with success",
      skipped: "Evaluation skipped",
      failed: "Evaluation failed",
    },
    dateTooltip: ["This value is in UTC. The value in your current timezone is ", wrapped("date")],
    withoutResponse: {
      title: "No response",
      explanation: "Response appears after decisions are evaluated.",
    },
  },
};
