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

import { Validator } from "../src";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { formI18n } from "../dist";

const schema = {
  type: "object",
  properties: {
    name: { type: "string" },
    lastName: { type: "string" },
    birthDate: { format: "date-time", type: "string" },
    contributors: { type: "number", minimum: 1 },
  },
  required: ["name", "lastName"],
};

const i18n = formI18n.getCurrent();

describe("Validator Tests", () => {
  it("create instance", () => {
    const validator = new Validator(i18n);
    expect(validator).toBeInstanceOf(Validator);
  });

  describe("create validator", () => {
    it("valid model", () => {
      const model = {
        name: "Kogito",
        lastName: "Tooling",
        birthDate: new Date(),
        contributors: 10,
      };

      const validator = new Validator(i18n);
      const validate = validator.createValidator(schema);
      const errors = validate(model);
      expect(errors).toBeNull();
    });

    it("invalid model - constraint", () => {
      const model = {
        name: "Kogito",
        lastName: "Tooling",
        birthDate: new Date(),
        contributors: 0,
      };

      const validator = new Validator(i18n);
      const validate = validator.createValidator(schema);
      const errors = validate(model);
      expect(errors?.details[0].keyword).toEqual("minimum");
      expect(errors?.details[0].message).toEqual("should be >= 1");
    });

    it("invalid model - format", () => {
      const model = {
        name: "Kogito",
        lastName: "Tooling",
        birthDate: "2000-10-10",
        contributors: 10,
      };

      const validator = new Validator(i18n);
      const validate = validator.createValidator(schema);
      const errors = validate(model);
      expect(errors?.details[0].keyword).toEqual("format");
      expect(errors?.details[0].message).toEqual(`should match format "date-time"`);
    });
  });

  it("get bridge", () => {
    const validator = new Validator(i18n);
    const bridge = validator.getBridge(schema);
    expect(bridge).toBeInstanceOf(JSONSchemaBridge);
  });
});
