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
import { UnitablesJsonSchemaBridge } from "./uniforms";
import { UnitablesI18n } from "./i18n";

export class UnitablesValidator {
  constructor(public i18n: UnitablesI18n) {}

  protected readonly ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });

  public createValidator(formSchema: object) {
    const validator = this.ajv.compile(formSchema);

    return (model: object) => {
      // AJV doesn't handle dates objects. This transformation converts Dates to their UTC format.
      validator(JSON.parse(JSON.stringify(model)));

      if (validator.errors && validator.errors.length) {
        return { details: validator.errors };
      }
      return null;
    };
  }

  public getBridge(formSchema: object) {
    return new UnitablesJsonSchemaBridge(formSchema, this.createValidator(formSchema), this.i18n);
  }
}
