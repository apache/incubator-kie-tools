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

import { DmnValidator } from "../src/DmnValidator";
import { dmnFormI18n } from "../src/i18n";
import { DmnFormJsonSchemaBridge } from "../src/uniforms";

const schema = {
  type: "object",
  properties: {
    name: { type: "string" },
    lastName: { type: "string" },
    daysAndTimeDuration: { format: "days and time duration", type: "string" },
    yearsAndMonthsDuration: { format: "years and months duration", type: "string" },
  },
  required: ["name", "lastName"],
};

const i18n = dmnFormI18n.getCurrent();

describe("DmnValidator Tests", () => {
  it("create instance", () => {
    const dmnValidator = new DmnValidator(i18n);
    expect(dmnValidator).toBeInstanceOf(DmnValidator);
  });

  describe("create validator", () => {
    it("valid model", () => {
      const model = {
        name: "Kogito",
        lastName: "Tooling",
        daysAndTimeDuration: "P1D",
        yearsAndMonthsDuration: "P1Y",
      };

      const validator = new DmnValidator(i18n);
      const validate = validator.createValidator(schema);
      const errors = validate(model);
      expect(errors).toBeNull();
    });

    it("invalid model - constraint", () => {
      const model = {
        name: "Kogito",
        lastName: "Tooling",
        daysAndTimeDuration: "P1H",
      };

      const validator = new DmnValidator(i18n);
      const validate = validator.createValidator(schema);
      const errors = validate(model);
      expect(errors?.details[0].keyword).toEqual("format");
      expect(errors?.details[0].message).toEqual("should match format P1D(ays)T2H(ours)3M(inutes)1S(econds)");
    });

    it("invalid model - format", () => {
      const model = {
        name: "Kogito",
        lastName: "Tooling",
        yearsAndMonthsDuration: "1M",
      };

      const validator = new DmnValidator(i18n);
      const validate = validator.createValidator(schema);
      const errors = validate(model);
      expect(errors?.details[0].keyword).toEqual("format");
      expect(errors?.details[0].message).toEqual("should match format P1Y(ears)2M(onths)");
    });
  });

  it("get bridge", () => {
    const validator = new DmnValidator(i18n);
    const bridge = validator.getBridge(schema);
    expect(bridge).toBeInstanceOf(DmnFormJsonSchemaBridge);
  });
});
