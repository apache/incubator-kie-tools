/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { DmnFormI18n } from "./i18n";
import { Validator } from "../core";
import { DmnFormJsonSchemaBridge } from "./uniforms";

export const DAYS_AND_TIME =
  /^(-|\+)?P(?:([-+]?[0-9]*)D)?(?:T(?:([-+]?[0-9]*)H)?(?:([-+]?[0-9]*)M)?(?:([-+]?[0-9]*)S)?)?$/;
export const YEARS_AND_MONTHS = /^(-|\+)?P(?:([-+]?[0-9]*)Y)?(?:([-+]?[0-9]*)M)?$/;

export class DmnValidator extends Validator {
  private readonly SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";

  constructor(private readonly i18n: DmnFormI18n) {
    super();
    this.setupValidator();
  }

  // Add meta schema v4 and period format
  private setupValidator() {
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.ajv.addFormat("days and time duration", {
      type: "string",
      validate: (data: string) => !!data.match(DAYS_AND_TIME),
    });

    this.ajv.addFormat("years and months duration", {
      type: "string",
      validate: (data: string) => !!data.match(YEARS_AND_MONTHS),
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
            if ((error.params as any).format === "days and time duration") {
              return { ...error, message: this.i18n.form.validation.daysAndTimeError };
            }
            if ((error.params as any).format === "years and months duration") {
              return { ...error, message: this.i18n.form.validation.yearsAndMonthsError };
            }
          }
          return error;
        }),
      };
    };
  }

  public getBridge(formSchema: any): DmnFormJsonSchemaBridge {
    const formDraft4 = { ...formSchema, $schema: this.SCHEMA_DRAFT4 };
    const validator = this.createValidator(formDraft4);
    return new DmnFormJsonSchemaBridge(formDraft4, validator);
  }
}
