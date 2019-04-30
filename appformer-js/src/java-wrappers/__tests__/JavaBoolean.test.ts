/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { JavaBoolean } from "../JavaBoolean";
import { JavaType } from "../JavaType";

describe("get", () => {
  describe("with valid input", () => {
    test("true, should return boolean true", () => {
      const input = true;

      const output = new JavaBoolean(input).get();

      expect(output).toBeTruthy();
    });

    test("false, should return boolean false", () => {
      const input = false;

      const output = new JavaBoolean(input).get();

      expect(output).toBeFalsy();
    });
  });
});

describe("set", () => {
  test("with direct value, should set", () => {
    const input = new JavaBoolean(false);
    expect(input.get()).toBeFalsy();

    input.set(true);

    expect(input.get()).toBeTruthy();
  });

  test("with value from function, should set", () => {
    const input = new JavaBoolean(false);
    expect(input.get()).toBeFalsy();

    input.set(cur => !cur);

    expect(input.get()).toBeTruthy();
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaBoolean(true) as any)._fqcn;

    expect(fqcn).toBe(JavaType.BOOLEAN);
  });
});
