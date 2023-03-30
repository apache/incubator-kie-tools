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

import { FormJsonSchemaBridge } from "@kie-tools/form";
import { DmnFormI18n } from "../i18n";

export const DAYS_AND_TIME_DURATION_FORMAT = "days and time duration";
export const YEARS_AND_MONTHS_DURATION_FORMAT = "years and months duration";
export const FEEL_CONTEXT = "FEEL:context";

export enum Duration {
  DaysAndTimeDuration,
  YearsAndMonthsDuration,
}

export class DmnFormJsonSchemaBridge extends FormJsonSchemaBridge {
  constructor(formSchema: object, validator: (model: object) => void, i18n: DmnFormI18n) {
    super(formSchema, validator, i18n);
    this.i18n = i18n;
  }

  public getType(name: string) {
    const { format: fieldFormat, type } = super.getField(name);
    // TODO: Luiz - create custom components
    if (fieldFormat === DAYS_AND_TIME_DURATION_FORMAT) {
      return String;
    }
    if (fieldFormat === YEARS_AND_MONTHS_DURATION_FORMAT) {
      return String;
    }
    if (type === FEEL_CONTEXT) {
      return FEEL_CONTEXT;
    }
    return super.getType(name);
  }

  public getField(name: string): Record<string, any> {
    const field = super.getField(name);

    if (field?.format === DAYS_AND_TIME_DURATION_FORMAT) {
      field.placeholder = (this.i18n as DmnFormI18n).dmnSchema.daysAndTimePlaceholder;
    }
    if (field?.format === YEARS_AND_MONTHS_DURATION_FORMAT) {
      field.placeholder = (this.i18n as DmnFormI18n).dmnSchema.yearsAndMonthsPlaceholder;
    }
    if (field?.format === "time") {
      field.placeholder = "hh:mm:ss";
    }
    if (field?.["x-dmn-type"] === FEEL_CONTEXT) {
      field.placeholder = `{ "x": <value> }`;
    }

    return field;
  }
}
