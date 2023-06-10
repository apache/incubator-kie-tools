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

import { Wrapped } from "@kie-tools-core/i18n/dist/core";
import { FormI18n } from "@kie-tools/form/dist/i18n/FormI18n";

export interface FormDmnI18n extends FormI18n {
  validation: {
    daysAndTimeError: string;
    yearsAndMonthsError: string;
  };
  dmnSchema: {
    daysAndTimePlaceholder: string;
    yearsAndMonthsPlaceholder: string;
  };
  result: {
    evaluation: {
      success: string;
      skipped: string;
      failed: string;
    };
    error: {
      title: string;
      explanation: string;
      message: Array<string | Wrapped<"jira">>;
    };
    dateTooltip: Array<string | Wrapped<"date">>;
    withoutResponse: {
      title: string;
      explanation: string;
    };
  };
}
