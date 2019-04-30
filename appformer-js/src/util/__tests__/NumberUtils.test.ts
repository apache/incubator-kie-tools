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

import { NumberUtils } from "../NumberUtils";

describe("isNonNegativeIntegerString", () => {
  test("with non negative integer string, should return true", () => {
    const input = "1";

    const output = NumberUtils.isNonNegativeIntegerString(input);

    expect(output).toBeTruthy();
  });

  test("with negative integer string, should return false", () => {
    const input = "-1";

    const output = NumberUtils.isNonNegativeIntegerString(input);

    expect(output).toBeFalsy();
  });

  test("with non negative float string, should return false", () => {
    const input = "1.1";

    const output = NumberUtils.isNonNegativeIntegerString(input);

    expect(output).toBeFalsy();
  });

  test("with non numeric string, should return false", () => {
    const input = "abc";

    const output = NumberUtils.isNonNegativeIntegerString(input);

    expect(output).toBeFalsy();
  });
});

describe("isIntegerString", () => {
  test("with non negative integer string, should return true", () => {
    const input = "1";

    const output = NumberUtils.isIntegerString(input);

    expect(output).toBeTruthy();
  });

  test("with negative integer string, should return true", () => {
    const input = "-1";

    const output = NumberUtils.isIntegerString(input);

    expect(output).toBeTruthy();
  });

  test("with non negative float string, should return false", () => {
    const input = "1.1";

    const output = NumberUtils.isIntegerString(input);

    expect(output).toBeFalsy();
  });

  test("with negative float string, should return false", () => {
    const input = "-1.1";

    const output = NumberUtils.isIntegerString(input);

    expect(output).toBeFalsy();
  });

  test("with non numeric string, should return false", () => {
    const input = "abc";

    const output = NumberUtils.isIntegerString(input);

    expect(output).toBeFalsy();
  });
});

describe("isFloatString", () => {
  test("with non negative integer string, should return true", () => {
    const input = "1";

    const output = NumberUtils.isFloatString(input);

    expect(output).toBeTruthy();
  });

  test("with negative integer string, should return true", () => {
    const input = "-1";

    const output = NumberUtils.isFloatString(input);

    expect(output).toBeTruthy();
  });

  test("with non negative float string, should return true", () => {
    const input = "1.1";

    const output = NumberUtils.isFloatString(input);

    expect(output).toBeTruthy();
  });

  test("with negative float string, should return true", () => {
    const input = "-1.1";

    const output = NumberUtils.isFloatString(input);

    expect(output).toBeTruthy();
  });

  test("with non numeric string, should return false", () => {
    const input = "abc";

    const output = NumberUtils.isFloatString(input);

    expect(output).toBeFalsy();
  });
});
