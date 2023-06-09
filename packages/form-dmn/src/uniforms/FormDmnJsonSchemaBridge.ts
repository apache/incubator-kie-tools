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

import { FormJsonSchemaBridge } from "@kie-tools/form/dist/uniforms/FormJsonSchemaBridge";
import { FormDmnI18n } from "../i18n";
import { DmnInputFieldProperties, ExtendedServicesDmnJsonSchema, X_DMN_TYPE } from "@kie-tools/extended-services-api";
import { DAYS_AND_TIME_DURATION_FORMAT, YEARS_AND_MONTHS_DURATION_FORMAT } from "@kie-tools/dmn-runner/dist/constants";

export enum Duration {
  DaysAndTimeDuration,
  YearsAndMonthsDuration,
}

export class FormDmnJsonSchemaBridge extends FormJsonSchemaBridge {
  schema: ExtendedServicesDmnJsonSchema;

  constructor(formSchema: ExtendedServicesDmnJsonSchema, validator: (model: object) => void, i18n: FormDmnI18n) {
    super(formSchema, validator, i18n);
    this.i18n = i18n;
  }

  public getProps(name: string, props: Record<string, any>) {
    const superProps = super.getProps(name, props);

    const spaceBetweenComponents = "5px";
    if (!superProps.padding && !superProps.properties) {
      superProps.style = { padding: spaceBetweenComponents };
    } else if (!superProps.padding && superProps.properties) {
      // nested fields should add margin instead of padding
      superProps.style = { margin: spaceBetweenComponents };
    }

    return superProps;
  }

  public getType(name: string) {
    const { format: fieldFormat, type } = super.getField(name) as DmnInputFieldProperties;
    // TODO: Luiz - create custom components
    if (fieldFormat === DAYS_AND_TIME_DURATION_FORMAT) {
      return String;
    }
    if (fieldFormat === YEARS_AND_MONTHS_DURATION_FORMAT) {
      return String;
    }
    if (type === X_DMN_TYPE.CONTEXT) {
      return X_DMN_TYPE.CONTEXT;
    }
    return super.getType(name);
  }

  public getField(name: string): DmnInputFieldProperties {
    const field = super.getField(name) as DmnInputFieldProperties;

    if (field?.format === DAYS_AND_TIME_DURATION_FORMAT) {
      field.placeholder = (this.i18n as FormDmnI18n).dmnSchema.daysAndTimePlaceholder;
    }
    if (field?.format === YEARS_AND_MONTHS_DURATION_FORMAT) {
      field.placeholder = (this.i18n as FormDmnI18n).dmnSchema.yearsAndMonthsPlaceholder;
    }
    if (field?.format === "time") {
      field.placeholder = "hh:mm:ss";
    }
    if (field?.["x-dmn-type"] === X_DMN_TYPE.CONTEXT) {
      field.placeholder = `{ "x": <value> }`;
    }

    return field;
  }
}
