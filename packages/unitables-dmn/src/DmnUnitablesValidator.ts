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

import Ajv from "ajv";
import metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";
import { DmnUnitablesI18n } from "./i18n";
import { DAYS_AND_TIME_DURATION_FORMAT, YEARS_AND_MONTHS_DURATION_FORMAT } from "@kie-tools/form-dmn/dist/uniforms";
import { DmnSchema } from "@kie-tools/form-dmn";
import { UnitablesValidator } from "@kie-tools/unitables/dist/UnitablesValidator";

const DAYS_AND_TIME = /^(-|\+)?P(?:([-+]?[0-9]*)D)?(?:T(?:([-+]?[0-9]*)H)?(?:([-+]?[0-9]*)M)?(?:([-+]?[0-9]*)S)?)?$/;
const YEARS_AND_MONTHS = /^(-|\+)?P(?:([-+]?[0-9]*)Y)?(?:([-+]?[0-9]*)M)?$/;

export class DmnUnitablesValidator extends UnitablesValidator {
  protected readonly ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });
  private readonly SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";

  constructor(i18n: DmnUnitablesI18n) {
    super(i18n);
    this.setupValidator();
  }

  private setupValidator() {
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.ajv.addFormat(DAYS_AND_TIME_DURATION_FORMAT, {
      type: "string",
      validate: (data: string) => !!data.match(DAYS_AND_TIME),
    });

    this.ajv.addFormat(YEARS_AND_MONTHS_DURATION_FORMAT, {
      type: "string",
      validate: (data: string) => !!data.match(YEARS_AND_MONTHS),
    });
  }

  public createValidator(jsonSchema: any) {
    const validator = this.ajv.compile(jsonSchema);

    return (model: any) => {
      // AJV doesn't handle dates objects. This transformation converts Dates to their UTC format.
      validator(JSON.parse(JSON.stringify(model)));

      if (validator.errors && validator.errors.length) {
        const details = validator.errors
          .filter((error: any) => error.keyword !== "required")
          .map((error: any) => {
            if (error.keyword === "format") {
              if ((error.params as any).format === DAYS_AND_TIME_DURATION_FORMAT) {
                return { ...error, message: "" };
              }
              if ((error.params as any).format === YEARS_AND_MONTHS_DURATION_FORMAT) {
                return { ...error, message: "" };
              }
            }
          });
        return { details };
      }
      return null;
    };
  }

  public getBridge(formSchema: DmnSchema): DmnUnitablesJsonSchemaBridge {
    const formDraft4 = { ...formSchema, $schema: this.SCHEMA_DRAFT4 };
    const validator = this.createValidator(formDraft4);
    return new DmnUnitablesJsonSchemaBridge(formDraft4, validator, this.i18n);
  }
}
