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

import { ReferenceDictionary, Wrapped } from "@kie-tooling-core/i18n/dist/core";
import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";

interface DmnFormDictionary extends ReferenceDictionary {
  form: {
    status: {
      autoGenerationError: {
        title: string;
        explanation: string;
        checkNotificationPanel: Array<string | Wrapped<"link">>;
      };
      emptyForm: {
        title: string;
        explanation: string;
      };
      validatorError: {
        title: string;
        message: Array<string | Wrapped<"jira">>;
      };
    };
    validation: {
      daysAndTimeError: string;
      yearsAndMonthsError: string;
    };
    preProcessing: {
      selectPlaceholder: string;
      daysAndTimePlaceholder: string;
      yearsAndMonthsPlaceholder: string;
    };
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

export interface DmnFormI18n extends DmnFormDictionary, CommonI18n {}
