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

import { JavaDouble } from "../JavaDouble";
import { JavaType } from "../JavaType";

describe("get", () => {
  describe("with valid input", () => {
    test("positive float, should return same value as number", () => {
      const input = "12.24";

      const output = new JavaDouble(input).get();

      expect(output).toEqual(12.24);
    });

    test("negative float, should return same value as number", () => {
      const input = "-12.24";

      const output = new JavaDouble(input).get();

      expect(output).toEqual(-12.24);
    });

    test("positive integer, should return same value as number", () => {
      const input = "12";

      const output = new JavaDouble(input).get();

      expect(output).toEqual(12);
    });

    test("negative integer, should return same value as number", () => {
      const input = "-12";

      const output = new JavaDouble(input).get();

      expect(output).toEqual(-12);
    });
  });

  test("with invalid input string, should return NaN", () => {
    const input = "abc";

    const output = new JavaDouble(input).get();

    expect(output).toBeNaN();
  });

  describe("with input in the numeric bounds", () => {
    describe("minimum bound", () => {
      test("equals, should return same value as Number", () => {
        const input = `${-Number.MAX_VALUE}`;

        const output = new JavaDouble(input).get();

        expect(output).toEqual(-Number.MAX_VALUE);
      });

      test("less than, should return same value as Number", () => {
        const input = `${-Number.MAX_VALUE - 1}`;

        const output = new JavaDouble(input).get();

        expect(output).toEqual(-Number.MAX_VALUE - 1);
      });
    });

    describe("maximum bound", () => {
      test("equals, should return same value as Number", () => {
        const input = `${Number.MAX_VALUE}`;

        const output = new JavaDouble(input).get();

        expect(output).toEqual(Number.MAX_VALUE);
      });

      test("greater than, should return same value as Number", () => {
        const input = `${Number.MAX_VALUE + 1}`;

        const output = new JavaDouble(input).get();

        expect(output).toEqual(Number.MAX_VALUE + 1);
      });
    });
  });
});

describe("set", () => {
  test("with valid direct value, should set", () => {
    const input = new JavaDouble("1");
    expect(input.get()).toEqual(1);

    input.set(2);

    expect(input.get()).toEqual(2);
  });

  test("with invalid direct value, should set NaN", () => {
    const input = new JavaDouble("1");
    expect(input.get()).toEqual(1);

    input.set(Number.MAX_VALUE * 2);

    expect(input.get()).toEqual(NaN);
  });

  test("with valid value from function, should set", () => {
    const input = new JavaDouble("1");
    expect(input.get()).toEqual(1);

    input.set(cur => 2 + cur);

    expect(input.get()).toEqual(3);
  });

  test("with invalid value from function, should set NaN", () => {
    const input = new JavaDouble("2");
    expect(input.get()).toEqual(2);

    input.set(cur => Number.MAX_VALUE * cur);

    expect(input.get()).toEqual(NaN);
  });
});

describe("doubleValue", () => {
  test("should convert successfully", () => {
    const input = new JavaDouble("1.1");

    const output = input.doubleValue();

    expect(output.get()).toBe(1.1);
  });
});

describe("intValue", () => {
  test("should convert successfully", () => {
    const input = new JavaDouble("1");

    const output = input.intValue();

    expect(output.get()).toBe(1);
  });
});

describe("shortValue", () => {
  test("should convert successfully", () => {
    const input = new JavaDouble("1");

    const output = input.shortValue();

    expect(output.get()).toBe(1);
  });
});

describe("byteValue", () => {
  test("should convert successfully", () => {
    const input = new JavaDouble("1");

    const output = input.byteValue();

    expect(output.get()).toBe(1);
  });
});

describe("floatValue", () => {
  test("should convert successfully", () => {
    const input = new JavaDouble("1.1");

    const output = input.floatValue();

    expect(output.get()).toBe(1.1);
  });
});

describe("longValue", () => {
  test("should convert successfully", () => {
    const input = new JavaDouble("1");

    const output = input.longValue();

    expect(output.get().toNumber()).toBe(1);
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaDouble("1") as any)._fqcn;

    expect(fqcn).toBe(JavaType.DOUBLE);
  });
});
