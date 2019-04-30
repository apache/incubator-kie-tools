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

import {
  instanceOfArray,
  instanceOfBoolean,
  instanceOfDate,
  instanceOfMap,
  instanceOfNumber,
  instanceOfSet,
  instanceOfString,
  isArray,
  isBoolean,
  isDate,
  isMap,
  isNumber,
  isSet,
  isString
} from "../TypeUtils";

describe("isString", () => {
  test("with direct assigned string, should return true", () => {
    const input = "str";

    const output = isString(input);

    expect(output).toBeTruthy();
  });

  test("with string created via constructor, should return true", () => {
    const input = String("str");

    const output = isString(input);

    expect(output).toBeTruthy();
  });

  test("with non-string input, should return false", () => {
    const input = 1;

    const output = isString(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfString", () => {
  test("with direct assigned string, should return true", () => {
    const input = "str";

    const output = instanceOfString(input);

    expect(output).toBeTruthy();
  });

  test("with string created via constructor, should return true", () => {
    const input = String("str");

    const output = instanceOfString(input);

    expect(output).toBeTruthy();
  });

  test("with non-string input, should return false", () => {
    const input = 1;

    const output = instanceOfString(input);

    expect(output).toBeFalsy();
  });
});

describe("isArray", () => {
  test("with direct assigned array, should return true", () => {
    const input = [1, 2, 3];

    const output = isArray(input);

    expect(output).toBeTruthy();
  });

  test("with array created via constructor, should return true", () => {
    const input = new Array(1, 2, 3);

    const output = isArray(input);

    expect(output).toBeTruthy();
  });

  test("with non-array input, should return false", () => {
    const input = 1;

    const output = isArray(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfArray", () => {
  test("with direct assigned array, should return true", () => {
    const input = [1, 2, 3];

    const output = instanceOfArray(input);

    expect(output).toBeTruthy();
  });

  test("with array created via constructor, should return true", () => {
    const input = new Array(1, 2, 3);

    const output = instanceOfArray(input);

    expect(output).toBeTruthy();
  });

  test("with non-array input, should return false", () => {
    const input = 1;

    const output = instanceOfArray(input);

    expect(output).toBeFalsy();
  });
});

describe("isSet", () => {
  test("with set created via constructor, should return true", () => {
    const input = new Set([1, 2, 3]);

    const output = isSet(input);

    expect(output).toBeTruthy();
  });

  test("with non-set input, should return false", () => {
    const input = 1;

    const output = isSet(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfSet", () => {
  test("with set created via constructor, should return true", () => {
    const input = new Set([1, 2, 3]);

    const output = instanceOfSet(input);

    expect(output).toBeTruthy();
  });

  test("with non-set input, should return false", () => {
    const input = 1;

    const output = instanceOfSet(input);

    expect(output).toBeFalsy();
  });
});

describe("isMap", () => {
  test("with map created via constructor, should return true", () => {
    const input = new Map([[1, 2]]);

    const output = isMap(input);

    expect(output).toBeTruthy();
  });

  test("with non-set input, should return false", () => {
    const input = 1;

    const output = isMap(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfMap", () => {
  test("with map created via constructor, should return true", () => {
    const input = new Map([[1, 2]]);

    const output = instanceOfMap(input);

    expect(output).toBeTruthy();
  });

  test("with non-set input, should return false", () => {
    const input = 1;

    const output = instanceOfMap(input);

    expect(output).toBeFalsy();
  });
});

describe("isBoolean", () => {
  test("with direct assigned boolean, should return true", () => {
    const input = false;

    const output = isBoolean(input);

    expect(output).toBeTruthy();
  });

  test("with boolean created via constructor, should return true", () => {
    const input = Boolean(false);

    const output = isBoolean(input);

    expect(output).toBeTruthy();
  });

  test("with non-boolean input, should return false", () => {
    const input = 1;

    const output = isBoolean(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfBoolean", () => {
  test("with direct assigned boolean, should return true", () => {
    const input = false;

    const output = instanceOfBoolean(input);

    expect(output).toBeTruthy();
  });

  test("with boolean created via constructor, should return true", () => {
    const input = Boolean(false);

    const output = instanceOfBoolean(input);

    expect(output).toBeTruthy();
  });

  test("with non-boolean input, should return false", () => {
    const input = 1;

    const output = instanceOfBoolean(input);

    expect(output).toBeFalsy();
  });
});

describe("isDate", () => {
  test("with date created via constructor, should return true", () => {
    const input = new Date();

    const output = isDate(input);

    expect(output).toBeTruthy();
  });

  test("with non-date input, should return false", () => {
    const input = 1;

    const output = isDate(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfDate", () => {
  test("with date created via constructor, should return true", () => {
    const input = new Date();

    const output = instanceOfDate(input);

    expect(output).toBeTruthy();
  });

  test("with non-date input, should return false", () => {
    const input = 1;

    const output = instanceOfDate(input);

    expect(output).toBeFalsy();
  });
});

describe("isNumber", () => {
  test("with direct assigned number, should return true", () => {
    const input = 1;

    const output = isNumber(input);

    expect(output).toBeTruthy();
  });

  test("with number created via constructor, should return true", () => {
    const input = Number(1);

    const output = isNumber(input);

    expect(output).toBeTruthy();
  });

  test("with non-number input, should return false", () => {
    const input = true;

    const output = isNumber(input);

    expect(output).toBeFalsy();
  });
});

describe("instanceOfNumber", () => {
  test("with direct assigned number, should return true", () => {
    const input = 1;

    const output = instanceOfNumber(input);

    expect(output).toBeTruthy();
  });

  test("with number created via constructor, should return true", () => {
    const input = Number(1);

    const output = instanceOfNumber(input);

    expect(output).toBeTruthy();
  });

  test("with non-number input, should return false", () => {
    const input = true;

    const output = instanceOfNumber(input);

    expect(output).toBeFalsy();
  });
});
