/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";
import { Validator } from "@kie-tools/form/dist/Validator";
import { DmnFormI18n } from "./i18n";
import {
  DAYS_AND_TIME_DURATION_FORMAT,
  DAYS_AND_TIME_DURATION_REGEXP,
  YEARS_AND_MONTHS_DURATION_FORMAT,
  YEARS_AND_MONTHS_DURATION_REGEXP,
} from "@kie-tools/dmn-runner/dist/constants";
import { DmnFormJsonSchemaBridge } from "./uniforms";
import { ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";

export class DmnValidator extends Validator {
  private readonly SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";

  constructor(i18n: DmnFormI18n) {
    super(i18n);
    this.setupValidator();
    this.i18n = i18n;
  }

  // Add meta schema v4 and period format
  private setupValidator() {
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.ajv.addFormat(DAYS_AND_TIME_DURATION_FORMAT, {
      type: "string",
      validate: (data: string) => !!data.match(DAYS_AND_TIME_DURATION_REGEXP),
    });

    this.ajv.addFormat(YEARS_AND_MONTHS_DURATION_FORMAT, {
      type: "string",
      validate: (data: string) => !!data.match(YEARS_AND_MONTHS_DURATION_REGEXP),
    });

    this.ajv.addKeyword("recursion", {
      type: "object",
      validate: (data: object) => Object.prototype.hasOwnProperty.call(data, "recursionRoot"),
    });
  }

  // Override to add period validation
  public createValidator(jsonSchema: any) {
    const validator = this.ajv.compile(jsonSchema);

    return (model: any) => {
      // AJV doesn't handle dates objects. This transformation converts Dates to their UTC format.
      validator(JSON.parse(JSON.stringify(model)));

      if (!validator.errors?.length) {
        return null;
      }

      return {
        details: validator.errors?.map((error: any) => {
          if (error.keyword === "format") {
            if ((error.params as any).format === DAYS_AND_TIME_DURATION_FORMAT) {
              return { ...error, message: (this.i18n as DmnFormI18n).validation.daysAndTimeError };
            }
            if ((error.params as any).format === YEARS_AND_MONTHS_DURATION_FORMAT) {
              return { ...error, message: (this.i18n as DmnFormI18n).validation.yearsAndMonthsError };
            }
          }
          return error;
        }),
      };
    };
  }

  public getBridge(formSchema: ExtendedServicesDmnJsonSchema): DmnFormJsonSchemaBridge {
    const formDraft4 = { ...formSchema, $schema: this.SCHEMA_DRAFT4 };
    const validator = this.createValidator(formDraft4);
    return new DmnFormJsonSchemaBridge(formDraft4, validator, this.i18n as DmnFormI18n);
  }
}
