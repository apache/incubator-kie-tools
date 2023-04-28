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
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

const DEFAULT_COLUMN_MIN_WIDTH = 150;
const DEFAULT_DATE_TIME_CELL_WDITH = 188;
const DEFAULT_DATE_CELL_WIDTH = 170;
const DEFAULT_TIME_CELL_WIDTH = 150;

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

  public getFieldDataType(field: Record<string, any>) {
    const xDmnType: string | undefined = field["x-dmn-type"];

    let type: string | undefined;
    if (!xDmnType) {
      type = field.type;
    } else {
      const splitedXDmnType: string[] | undefined = xDmnType.split(":");
      if (!splitedXDmnType) {
        type = undefined;
      } else if (splitedXDmnType.length > 2) {
        type = splitedXDmnType[2].split("}")?.[0]?.trim();
      } else {
        type = splitedXDmnType[1];
      }
    }

    switch (type) {
      case "<Undefined>":
        return { dataType: DmnBuiltInDataType.Undefined, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "Any":
        return { dataType: DmnBuiltInDataType.Any, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "boolean":
        return { dataType: DmnBuiltInDataType.Boolean, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "context":
        return { dataType: DmnBuiltInDataType.Context, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "date":
        return { dataType: DmnBuiltInDataType.Date, width: DEFAULT_DATE_CELL_WIDTH };
      case "date and time":
        return { dataType: DmnBuiltInDataType.DateTime, width: DEFAULT_DATE_TIME_CELL_WDITH };
      case "days and time duration":
        return { dataType: DmnBuiltInDataType.DateTimeDuration, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "number":
        return { dataType: DmnBuiltInDataType.Number, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "string":
        return { dataType: DmnBuiltInDataType.String, width: DEFAULT_COLUMN_MIN_WIDTH };
      case "time":
        return { dataType: DmnBuiltInDataType.Time, width: DEFAULT_TIME_CELL_WIDTH };
      case "years and months duration":
        return { dataType: DmnBuiltInDataType.YearsMonthsDuration, width: DEFAULT_COLUMN_MIN_WIDTH };
      default:
        return {
          dataType: (type as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined,
          width: DEFAULT_COLUMN_MIN_WIDTH,
        };
    }
  }
}
