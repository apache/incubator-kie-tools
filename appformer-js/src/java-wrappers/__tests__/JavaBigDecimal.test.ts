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

import { JavaBigDecimal } from "../JavaBigDecimal";
import { BigNumber } from "bignumber.js";
import { JavaType } from "../JavaType";

describe("get", () => {
  describe("with valid input", () => {
    test("positive float, should return same value as BigNumber", () => {
      const input = "12.92";

      const output = new JavaBigDecimal(input).get();

      expect(output).toEqual(new BigNumber("12.92"));
    });

    test("negative float, should return same value as BigNumber", () => {
      const input = "-12.92";

      const output = new JavaBigDecimal(input).get();

      expect(output).toEqual(new BigNumber("-12.92"));
    });

    test("positive integer, should return same value as BigNumber", () => {
      const input = "12";

      const output = new JavaBigDecimal(input).get();

      expect(output).toEqual(new BigNumber("12"));
    });

    test("negative integer, should return same value as BigNumber", () => {
      const input = "-12";

      const output = new JavaBigDecimal(input).get();

      expect(output).toEqual(new BigNumber("-12"));
    });
  });

  test("with invalid textual string, should return NaN", () => {
    const input = "abc";

    const output = new JavaBigDecimal(input).get();

    expect(output).toEqual(new BigNumber(NaN));
  });

  describe("with input in the numeric bounds", () => {
    describe("minimum bound", () => {
      test("equals, should return same value as BigNumber", () => {
        const input = new BigNumber(-Number.MAX_VALUE);

        const output = new JavaBigDecimal(input.toString(10)).get();

        expect(output).toEqual(new BigNumber(-Number.MAX_VALUE));
      });

      test("less than, should return same value as BigNumber", () => {
        const input = new BigNumber(-Number.MAX_VALUE).minus(1, 10);

        const output = new JavaBigDecimal(input.toString(10)).get();

        expect(output).toEqual(new BigNumber(-Number.MAX_VALUE).minus(1, 10));
      });
    });

    describe("maximum bound", () => {
      test("equals, should return same value as BigNumber", () => {
        const input = new BigNumber(Number.MAX_VALUE);

        const output = new JavaBigDecimal(input.toString(10)).get();

        expect(output).toEqual(new BigNumber(Number.MAX_VALUE));
      });

      test("greater than, should return same value as BigNumber", () => {
        const input = new BigNumber(Number.MAX_VALUE).plus(1, 10);

        const output = new JavaBigDecimal(input.toString(10)).get();

        expect(output).toEqual(new BigNumber(Number.MAX_VALUE).plus(1, 10));
      });
    });
  });
});

describe("set", () => {
  test("with valid direct value, should set", () => {
    const input = new JavaBigDecimal("1.2");
    expect(input.get()).toEqual(new BigNumber("1.2"));

    input.set(new BigNumber("2.2"));

    expect(input.get()).toEqual(new BigNumber("2.2"));
  });

  test("with invalid direct value, should set NaN", () => {
    const input = new JavaBigDecimal("1.2");
    expect(input.get()).toEqual(new BigNumber("1.2"));

    input.set(new BigNumber(NaN));

    expect(input.get()).toEqual(new BigNumber(NaN));
  });

  test("with valid value from function, should set", () => {
    const input = new JavaBigDecimal("1.2");
    expect(input.get()).toEqual(new BigNumber("1.2"));

    input.set(cur => new BigNumber("2.2").plus(cur));

    expect(input.get()).toEqual(new BigNumber("3.4"));
  });

  test("with invalid value from function, should set NaN", () => {
    const input = new JavaBigDecimal("1.2");
    expect(input.get()).toEqual(new BigNumber("1.2"));

    input.set(cur => new BigNumber(NaN).plus(cur));

    expect(input.get()).toEqual(new BigNumber(NaN));
  });
});

describe("doubleValue", () => {
  test("should convert successfully", () => {
    const input = new JavaBigDecimal("1.2");

    const output = input.doubleValue();

    expect(output.get()).toBe(1.2);
  });
});

describe("intValue", () => {
  test("should convert successfully", () => {
    const input = new JavaBigDecimal("1");

    const output = input.intValue();

    expect(output.get()).toBe(1);
  });
});

describe("shortValue", () => {
  test("should convert successfully", () => {
    const input = new JavaBigDecimal("1");

    const output = input.shortValue();

    expect(output.get()).toBe(1);
  });
});

describe("byteValue", () => {
  test("should convert successfully", () => {
    const input = new JavaBigDecimal("1");

    const output = input.byteValue();

    expect(output.get()).toBe(1);
  });
});

describe("floatValue", () => {
  test("should convert successfully", () => {
    const input = new JavaBigDecimal("1.1");

    const output = input.floatValue();

    expect(output.get()).toBe(1.1);
  });
});

describe("longValue", () => {
  test("should convert successfully", () => {
    const input = new JavaBigDecimal("1");

    const output = input.longValue();

    expect(output.get().toNumber()).toBe(1);
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaBigDecimal("1") as any)._fqcn;

    expect(fqcn).toBe(JavaType.BIG_DECIMAL);
  });
});
