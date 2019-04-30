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

import { JavaWrapperUtils } from "../JavaWrapperUtils";
import { JavaArrayList } from "../JavaArrayList";
import { JavaHashSet } from "../JavaHashSet";
import { JavaHashMap } from "../JavaHashMap";
import { JavaBoolean } from "../JavaBoolean";
import { JavaString } from "../JavaString";
import { JavaDate } from "../JavaDate";
import { JavaType } from "../JavaType";
import { JavaEnum } from "../JavaEnum";

describe("needsWrapping", () => {
  test("with array object, should return true", () => {
    const input = ["foo1", "foo2", "foo3"];
    expect(JavaWrapperUtils.needsWrapping(input)).toBeTruthy();
  });

  test("with set object, should return true", () => {
    const input = new Set(["foo1", "foo2", "foo3"]);
    expect(JavaWrapperUtils.needsWrapping(input)).toBeTruthy();
  });

  test("with map object, should return true", () => {
    const input = new Map([["foo1", "bar1"], ["foo2", "bar2"]]);
    expect(JavaWrapperUtils.needsWrapping(input)).toBeTruthy();
  });

  test("with boolean object, should return true", () => {
    const input = false;
    expect(JavaWrapperUtils.needsWrapping(input)).toBeTruthy();
  });

  test("with string object, should return true", () => {
    const input = "foo";
    expect(JavaWrapperUtils.needsWrapping(input)).toBeTruthy();
  });

  test("with date object, should return true", () => {
    const input = new Date();
    expect(JavaWrapperUtils.needsWrapping(input)).toBeTruthy();
  });

  test("with custom object, should return false", () => {
    const input = {
      foo: "bar1",
      foo2: "bar2"
    };
    expect(JavaWrapperUtils.needsWrapping(input)).toBeFalsy();
  });

  test("with enum type, should return false", () => {
    const input = FooEnum.FOO;
    expect(JavaWrapperUtils.needsWrapping(input)).toBeFalsy();
  });
});

describe("wrapIfNeeded", () => {
  test("with array object, should return a JavaArray instance", () => {
    const input = ["foo1", "foo2", "foo3"];
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toEqual(new JavaArrayList(["foo1", "foo2", "foo3"]));
  });

  test("with set object, should return a JavaHashSet instance", () => {
    const input = new Set(["foo1", "foo2", "foo3"]);
    const output = new JavaHashSet(new Set(["foo1", "foo2", "foo3"]));
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toEqual(output);
  });

  test("with map object, should return a JavaHashMap instance", () => {
    const input = new Map([["foo1", "bar1"], ["foo2", "bar2"]]);
    const output = new JavaHashMap(new Map([["foo1", "bar1"], ["foo2", "bar2"]]));
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toEqual(output);
  });

  test("with boolean object, should return a JavaBoolean instance", () => {
    const input = false;
    const output = new JavaBoolean(false);
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toEqual(output);
  });

  test("with string object, should return a JavaString instance", () => {
    const input = "foo";
    const output = new JavaString("foo");
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toEqual(output);
  });

  test("with date object, should return a JavaDate instance", () => {
    const input = new Date();
    const output = new JavaDate(input);
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toEqual(output);
  });

  test("with custom object, should return same object", () => {
    const input = {
      foo: "bar1",
      foo2: "bar2"
    };
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toStrictEqual(input);
  });

  test("with enum value, should return same value", () => {
    const input = FooEnum.BAR;
    expect(JavaWrapperUtils.wrapIfNeeded(input)).toStrictEqual(input);
  });
});

describe("isJavaType", () => {
  test("with Java Byte's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.BYTE)).toBeTruthy();
  });

  test("with Java Double's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.DOUBLE)).toBeTruthy();
  });

  test("with Java Float's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.FLOAT)).toBeTruthy();
  });

  test("with Java Integer's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.INTEGER)).toBeTruthy();
  });

  test("with Java Long's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.LONG)).toBeTruthy();
  });

  test("with Java Short's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.SHORT)).toBeTruthy();
  });

  test("with Java Boolean's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.BOOLEAN)).toBeTruthy();
  });

  test("with Java String's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.STRING)).toBeTruthy();
  });

  test("with Java BigDecimal's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.BIG_DECIMAL)).toBeTruthy();
  });

  test("with Java BigInteger's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.BIG_INTEGER)).toBeTruthy();
  });

  test("with Java ArrayList's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.ARRAY_LIST)).toBeTruthy();
  });

  test("with Java HashSet's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.HASH_SET)).toBeTruthy();
  });

  test("with Java HashMap's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.HASH_MAP)).toBeTruthy();
  });

  test("with Java Date's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.DATE)).toBeTruthy();
  });

  test("with non Java type fqcn, should return false", () => {
    expect(JavaWrapperUtils.isJavaType("foo")).toBeFalsy();
  });

  test("with Java Optional's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.OPTIONAL)).toBeTruthy();
  });

  test("with Java Enum's fqcn, should return true", () => {
    expect(JavaWrapperUtils.isJavaType(JavaType.ENUM)).toBeTruthy();
  });
});

describe("isEnum", () => {
  test("with Enum input, should return true", () => {
    const input = FooEnum.BAR;
    expect(JavaWrapperUtils.isEnum(input)).toBeTruthy();
  });

  test("with non enum input, should return false", () => {
    const input = {
      foo: "bar"
    };
    expect(JavaWrapperUtils.isEnum(input)).toBeFalsy();
  });
});

class FooEnum extends JavaEnum<FooEnum> {
  public static readonly FOO: FooEnum = new FooEnum("FOO");
  public static readonly BAR: FooEnum = new FooEnum("BAR");

  protected readonly _fqcn: string = FooEnum.__fqcn();

  public static __fqcn(): string {
    return "com.app.my.AddressType";
  }

  public static values() {
    return [FooEnum.FOO, FooEnum.BAR];
  }
}
