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

import { DAYS_AND_TIME_DURATION_FORMAT, YEARS_AND_MONTHS_DURATION_FORMAT } from "@kie-tools/dmn-runner/dist/constants";
import { UnitablesJsonSchemaBridge } from "@kie-tools/unitables/dist/uniforms";
import { DmnInputFieldProperties, ExtendedServicesDmnJsonSchema, X_DMN_TYPE } from "@kie-tools/extended-services-api";

export class DmnUnitablesJsonSchemaBridge extends UnitablesJsonSchemaBridge {
  schema: ExtendedServicesDmnJsonSchema;

  public getField(name: string) {
    const field = super.getField(name) as DmnInputFieldProperties;
    if (field.format === DAYS_AND_TIME_DURATION_FORMAT) {
      field.placeholder = "P1DT5H or P2D or PT1H2M10S";
    }
    if (field.format === YEARS_AND_MONTHS_DURATION_FORMAT) {
      field.placeholder = "P1Y5M or P2Y or P1M";
    }
    if (field["x-dmn-type"] === X_DMN_TYPE.CONTEXT) {
      field.placeholder = `{ "x": <value> }`;
    }
    return field;
  }

  public getFieldDataType(field: DmnInputFieldProperties) {
    return super.getFieldDataType(field);
  }
}
