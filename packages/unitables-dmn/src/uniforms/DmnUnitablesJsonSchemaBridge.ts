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

import {
  FEEL_CONTEXT,
  DAYS_AND_TIME_DURATION_FORMAT,
  YEARS_AND_MONTHS_DURATION_FORMAT,
} from "@kie-tools/form-dmn/dist/uniforms";
import { UnitablesJsonSchemaBridge } from "@kie-tools/unitables/dist/uniforms";

export class DmnUnitablesJsonSchemaBridge extends UnitablesJsonSchemaBridge {
  public getField(name: string) {
    const field = super.getField(name);
    if (field.format === DAYS_AND_TIME_DURATION_FORMAT) {
      field.placeholder = "P1DT5H or P2D or PT1H2M10S";
    }
    if (field.format === YEARS_AND_MONTHS_DURATION_FORMAT) {
      field.placeholder = "P1Y5M or P2Y or P1M";
    }
    if (field["x-dmn-type"] === FEEL_CONTEXT) {
      field.placeholder = `{ "x": <value> }`;
    }
    return field;
  }

  public getBoxedFieldType(field: Record<string, any>): string {
    let extractedType = (field["x-dmn-type"] ?? "").split("FEEL:").pop();
    if ((extractedType?.length ?? 0) > 1) {
      extractedType = (field["x-dmn-type"] ?? "").split(":").pop()?.split("}").join("").trim();
    }
    return extractedType;
  }
}
