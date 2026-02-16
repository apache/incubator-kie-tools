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

import { FormI18n } from "@kie-tools/uniforms-patternfly-form-wrapper/dist/i18n/FormI18n";

export interface FormDmnI18n extends FormI18n {
  validation: {
    xDmnAllowedValues: string;
    xDmnTypeConstraint: string;
    daysAndTimeError: string;
    yearsAndMonthsError: string;
  };
  dmnSchema: {
    daysAndTimePlaceholder: string;
    yearsAndMonthsPlaceholder: string;
  };
  result: {
    evaluation: {
      succeeded: string;
      skipped: string;
      failed: string;
    };
    error: {
      title: string;
      explanation: string;
      message: string;
    };
    dateTooltip: string;
    withoutResponse: {
      title: string;
      explanation: string;
    };
    recursiveStructureNotSupported: string;
    openExpression: (name: string) => string;
  };
}
