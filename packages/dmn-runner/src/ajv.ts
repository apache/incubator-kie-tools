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

import Ajv from "ajv";
import * as metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";
export { ValidateFunction } from "ajv";
import { duration } from "moment";
import {
  DATE_AND_TIME_ENUM_REGEXP,
  DATE_AND_TIME_FEEL_REGEXP,
  DATE_ENUM_REGEXP,
  DATE_FEEL_REGEXP,
  DAYS_AND_TIME_DURATION_FORMAT,
  DAYS_AND_TIME_DURATION_REGEXP,
  RECURSION_KEYWORD,
  RECURSION_REF_KEYWORD,
  X_DMN_ALLOWED_VALUES_KEYWORD,
  X_DMN_DESCRIPTIONS_KEYWORD,
  X_DMN_TYPE_KEYWORD,
  DURATION_ENUM_REGEXP,
  DURATION_FEEL_REGEXP,
  TIME_ENUM_REGEXP,
  TIME_FEEL_REGEXP,
  YEARS_AND_MONTHS_DURATION_FORMAT,
  YEARS_AND_MONTHS_DURATION_REGEXP,
} from "./constants";

/**
 * Please notice this is enum is slightly different from DMN built in types.
 * These values are used in 'ajv' to represent corresponding DMN built-in types.
 * This enum is primarily for 'ajv' validation code, where we want to validate DMN runner values according to X_DMN_ALLOWED_VALUES.
 *
 * There is this deviation for example:
 * ajv: 'date-time'
 * dmn: 'date and time'
 */
export enum DmnAjvSchemaFormat {
  DATE = "date",
  TIME = "time",
  DATE_TIME = "date-time",
  DAYS_TIME_DURATION = "days and time duration",
  YEARS_MONTHS_DURATION = "years and months duration",
}

export class DmnRunnerAjv {
  private ajv;

  private parseRangeFromAllowedValues = (xDmnAllowedValues: string, type: DmnAjvSchemaFormat) => {
    if (
      (xDmnAllowedValues.startsWith("[") || xDmnAllowedValues.startsWith("(")) &&
      (xDmnAllowedValues.endsWith("]") || xDmnAllowedValues.endsWith(")")) &&
      xDmnAllowedValues.includes("..")
    ) {
      //It is a range
      let feelFunction: string;
      let regExp;
      switch (type) {
        case DmnAjvSchemaFormat.DATE:
          feelFunction = "date";
          regExp = DATE_FEEL_REGEXP;
          break;
        case DmnAjvSchemaFormat.TIME:
          feelFunction = "time";
          regExp = TIME_FEEL_REGEXP;
          break;
        case DmnAjvSchemaFormat.DATE_TIME:
          feelFunction = "date and time";
          regExp = DATE_AND_TIME_FEEL_REGEXP;
          break;
        case DmnAjvSchemaFormat.DAYS_TIME_DURATION:
        case DmnAjvSchemaFormat.YEARS_MONTHS_DURATION:
          feelFunction = "duration";
          regExp = DURATION_FEEL_REGEXP;
          break;
      }
      const matches = xDmnAllowedValues.match(regExp);
      if (matches) {
        const minAllowed = matches[0].replace(`${feelFunction}("`, "").replace('")', "");
        const minAllowedIncluded = xDmnAllowedValues.startsWith("[");
        const maxAllowed = matches[1].replace(`${feelFunction}("`, "").replace('")', "");
        const maxAllowedIncluded = xDmnAllowedValues.endsWith("]");
        return { minAllowed, minAllowedIncluded, maxAllowed, maxAllowedIncluded };
      }
    }
    return {};
  };

  private parseEnumerationFromAllowedValues = (xDmnAllowedValues: string, type: DmnAjvSchemaFormat) => {
    // try to check if it is enumeration
    let feelFunction: string;
    let regExp;
    switch (type) {
      case DmnAjvSchemaFormat.DATE:
        feelFunction = "date";
        regExp = DATE_ENUM_REGEXP;
        break;
      case DmnAjvSchemaFormat.TIME:
        feelFunction = "time";
        regExp = TIME_ENUM_REGEXP;
        break;
      case DmnAjvSchemaFormat.DATE_TIME:
        feelFunction = "date and time";
        regExp = DATE_AND_TIME_ENUM_REGEXP;
        break;
      case DmnAjvSchemaFormat.DAYS_TIME_DURATION:
      case DmnAjvSchemaFormat.YEARS_MONTHS_DURATION:
        feelFunction = "duration";
        regExp = DURATION_ENUM_REGEXP;
        break;
    }
    const matches = xDmnAllowedValues.match(regExp);
    if (matches) {
      return [
        ...matches.map((matchedString) =>
          matchedString.trim().replace(`${feelFunction}("`, "").replace('"),', "").replace('")', "")
        ),
      ];
    }
    return [];
  };

  constructor() {
    this.ajv = new Ajv({
      allErrors: true,
      schemaId: "auto",
      useDefaults: true,
      removeAdditional: "all",
      verbose: true,
    });
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.ajv.addKeyword(X_DMN_TYPE_KEYWORD, {});
    this.ajv.addKeyword(X_DMN_ALLOWED_VALUES_KEYWORD, {
      compile: (schema, parentSchema: { format?: DmnAjvSchemaFormat }, it) => {
        if (!parentSchema.format) {
          return (data: string) => true;
        }
        const { minAllowed, minAllowedIncluded, maxAllowed, maxAllowedIncluded } = this.parseRangeFromAllowedValues(
          schema ?? "",
          parentSchema.format
        );
        const enumeratedValues = this.parseEnumerationFromAllowedValues(schema ?? "", parentSchema.format);
        const isUnderTheMinBoundary: (value: Date | String | number, minBoundary: Date | String | number) => boolean =
          minAllowedIncluded
            ? (value, minBoundary) => value < minBoundary
            : (value, minBoundary) => value <= minBoundary;
        const isOverTheMaxBoundary: (value: Date | String | number, maxBoundary: Date | String | number) => boolean =
          maxAllowedIncluded
            ? (value, maxBoundary) => value > maxBoundary
            : (value, maxBoundary) => value >= maxBoundary;

        return (data: string) => {
          if (data.includes(".") && data.endsWith("Z")) {
            // adjusting from "2023-06-01T10:42:00.000Z" to "2023-06-01T10:42:00"
            data = data.substring(0, data.lastIndexOf("."));
          }
          if (minAllowed && maxAllowed) {
            // It is a Range constraint
            if (parentSchema.format === "time") {
              if (isUnderTheMinBoundary(data, minAllowed) || isOverTheMaxBoundary(data, maxAllowed)) {
                return false;
              }
            } else if (parentSchema.format?.includes("duration")) {
              const actualDuration = duration(data).asMilliseconds();
              const minDuration = duration(minAllowed).asMilliseconds();
              const maxDuration = duration(maxAllowed).asMilliseconds();
              if (
                isUnderTheMinBoundary(actualDuration, minDuration) ||
                isOverTheMaxBoundary(actualDuration, maxDuration)
              ) {
                return false;
              }
            } else if (parentSchema.format?.includes("date")) {
              const actualDate = new Date(data);
              if (actualDate.toString() === "Invalid Date") {
                return false;
              }
              const minAllowedDate = new Date(minAllowed);
              if (minAllowedDate && isUnderTheMinBoundary(actualDate, minAllowedDate)) {
                return false;
              }
              const maxAllowedDate = new Date(maxAllowed);
              if (maxAllowedDate && isOverTheMaxBoundary(actualDate, maxAllowedDate)) {
                return false;
              }
            }
          } else if (enumeratedValues) {
            if (parentSchema.format === "time") {
              return enumeratedValues.includes(data);
            } else if (parentSchema.format?.includes("duration")) {
              const actualDuration = duration(data).asMilliseconds();
              return enumeratedValues.some((value) => {
                const enumeratedDurationValue = duration(value).asMilliseconds();
                return enumeratedDurationValue === actualDuration;
              });
            } else if (parentSchema.format?.includes("date")) {
              const actualDate = new Date(data);
              if (actualDate.toString() === "Invalid Date") {
                return false;
              }
              return enumeratedValues.some((value) => {
                const enumeratedDateValue = new Date(value);
                return !(enumeratedDateValue < actualDate) && !(enumeratedDateValue > actualDate);
              });
            }
          }

          return true;
        };
      },
    });
    this.ajv.addKeyword(X_DMN_DESCRIPTIONS_KEYWORD, {});
    this.ajv.addKeyword(RECURSION_KEYWORD, {});
    this.ajv.addKeyword(RECURSION_REF_KEYWORD, {});
    this.ajv.addFormat(DAYS_AND_TIME_DURATION_FORMAT, {
      type: "string",
      validate: (data: string) => !!data.match(DAYS_AND_TIME_DURATION_REGEXP),
    });

    this.ajv.addFormat(YEARS_AND_MONTHS_DURATION_FORMAT, {
      type: "string",
      validate: (data: string) => !!data.match(YEARS_AND_MONTHS_DURATION_REGEXP),
    });
  }

  public getAjv(): Ajv.Ajv {
    return this.ajv;
  }
}
