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
  JavaArrayList,
  JavaBigDecimal,
  JavaBigInteger,
  JavaBoolean,
  JavaByte,
  JavaDate,
  JavaDouble,
  JavaFloat,
  JavaHashMap,
  JavaHashSet,
  JavaInteger,
  JavaLong,
  JavaOptional,
  JavaShort,
  JavaString
} from "../../java-wrappers";
import { MarshallerProvider } from "../MarshallerProvider";
import { JavaBigIntegerMarshaller } from "../marshallers/JavaBigIntegerMarshaller";
import { JavaHashMapMarshaller } from "../marshallers/JavaHashMapMarshaller";
import { JavaByteMarshaller } from "../marshallers/JavaByteMarshaller";
import { JavaBigDecimalMarshaller } from "../marshallers/JavaBigDecimalMarshaller";
import { JavaStringMarshaller } from "../marshallers/JavaStringMarshaller";
import { JavaBooleanMarshaller } from "../marshallers/JavaBooleanMarshaller";
import { JavaShortMarshaller } from "../marshallers/JavaShortMarshaller";
import { JavaLongMarshaller } from "../marshallers/JavaLongMarshaller";
import { JavaIntegerMarshaller } from "../marshallers/JavaIntegerMarshaller";
import { JavaFloatMarshaller } from "../marshallers/JavaFloatMarshaller";
import { JavaDoubleMarshaller } from "../marshallers/JavaDoubleMarshaller";
import { DefaultMarshaller } from "../marshallers/DefaultMarshaller";
import { JavaWrapperUtils } from "../../java-wrappers/JavaWrapperUtils";
import { JavaType } from "../../java-wrappers/JavaType";
import * as JavaCollectionMarshaller from "../marshallers/JavaCollectionMarshaller";
import { JavaDateMarshaller } from "../marshallers/JavaDateMarshaller";
import { JavaOptionalMarshaller } from "../marshallers/JavaOptionalMarshaller";

describe("getForObject", () => {
  test("without initialize, should return Error", () => {
    const input = new JavaString("foo");
    expect(() => MarshallerProvider.getForObject(input)).toThrowError();
  });

  describe("properly initialized", () => {
    beforeEach(() => {
      MarshallerProvider.initialize();
    });

    afterEach(() => {
      // force reinitialization
      (MarshallerProvider as any).initialized = false;
    });

    test("with JavaByte instance, should return JavaByteMarshaller instance", () => {
      const input = new JavaByte("1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaByteMarshaller());
    });

    test("with JavaDouble instance, should return JavaDoubleMarshaller instance", () => {
      const input = new JavaDouble("1.1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaDoubleMarshaller());
    });

    test("with JavaFloat instance, should return JavaFloatMarshaller instance", () => {
      const input = new JavaFloat("1.1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaFloatMarshaller());
    });

    test("with JavaInteger instance, should return JavaIntegerMarshaller instance", () => {
      const input = new JavaInteger("1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaIntegerMarshaller());
    });

    test("with JavaLong instance, should return JavaLongMarshaller instance", () => {
      const input = new JavaLong("1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaLongMarshaller());
    });

    test("with JavaShort instance, should return JavaShortMarshaller instance", () => {
      const input = new JavaShort("1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaShortMarshaller());
    });

    test("with JavaBoolean instance, should return JavaBooleanMarshaller instance", () => {
      const input = new JavaBoolean(false);
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaBooleanMarshaller());
    });

    test("with JavaString instance, should return JavaStringMarshaller instance", () => {
      const input = new JavaString("foo");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaStringMarshaller());
    });

    test("with JavaDate instance, should return JavaDateMarshaller instance", () => {
      const input = new JavaDate(new Date());
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaDateMarshaller());
    });

    test("with JavaBigDecimal instance, should return JavaBigDecimalMarshaller instance", () => {
      const input = new JavaBigDecimal("1.1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaBigDecimalMarshaller());
    });

    test("with JavaBigInteger instance, should return JavaBigIntegerMarshaller instance", () => {
      const input = new JavaBigInteger("1");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaBigIntegerMarshaller());
    });

    test("with JavaArrayList instance, should return JavaArrayListMarshaller instance", () => {
      const input = new JavaArrayList([1, 2, 3]);
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaCollectionMarshaller.JavaArrayListMarshaller());
    });

    test("with JavaHashSet instance, should return JavaHashSetMarshaller instance", () => {
      const input = new JavaHashSet(new Set([1, 2, 3]));
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaCollectionMarshaller.JavaHashSetMarshaller());
    });

    test("with JavaHashMap instance, should return JavaHashMapMarshaller instance", () => {
      const input = new JavaHashMap(new Map([["foo", "bar"]]));
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaHashMapMarshaller());
    });

    test("with JavaOptional instance, should return JavaOptionalMarshaller instance", () => {
      const input = new JavaOptional<string>("str");
      expect(MarshallerProvider.getForObject(input)).toEqual(new JavaOptionalMarshaller());
    });

    test("with input without fqcn, should return default marshaller", () => {
      const input = {
        foo: "bar",
        bar: "foo"
      };

      expect(MarshallerProvider.getForObject(input)).toEqual(new DefaultMarshaller());
    });

    test("with input with a custom fqcn, should return default marshaller", () => {
      const fqcn = "com.myapp.custom.pojo";
      const input = {
        _fqcn: fqcn,
        foo: "bar",
        bar: "foo"
      };

      // it is a custom pojo (i.e. no pre-defined marshaller)
      expect(JavaWrapperUtils.isJavaType(fqcn)).toBeFalsy();

      expect(MarshallerProvider.getForObject(input)).toEqual(new DefaultMarshaller());
    });

    test("with a Java type without marshaller, should throw error", () => {
      // the only scenario it throws errors is when a Java-wrapped type has no marshaller associated.

      // little trick to mess with internal state
      const marshallers = (MarshallerProvider as any).marshallersByJavaType;
      marshallers.delete(JavaType.STRING);

      const input = new JavaString("foo");

      expect(() => MarshallerProvider.getForObject(input)).toThrowError();
    });
  });
});

describe("getForFqcn", () => {
  test("without initialize, should return Error", () => {
    expect(() => MarshallerProvider.getForFqcn("anything")).toThrowError();
  });

  describe("properly initialized", () => {
    beforeEach(() => {
      MarshallerProvider.initialize();
    });

    afterEach(() => {
      // force reinitialization
      (MarshallerProvider as any).initialized = false;
    });

    test("with Java's Byte fqcn, should return JavaByteMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.BYTE)).toEqual(new JavaByteMarshaller());
    });

    test("with Java's Double fqcn, should return JavaDoubleMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.DOUBLE)).toEqual(new JavaDoubleMarshaller());
    });

    test("with Java's Float fqcn, should return JavaFloatMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.FLOAT)).toEqual(new JavaFloatMarshaller());
    });

    test("with Java's Integer fqcn, should return JavaIntegerMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.INTEGER)).toEqual(new JavaIntegerMarshaller());
    });

    test("with Java's Long fqcn, should return JavaLongMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.LONG)).toEqual(new JavaLongMarshaller());
    });

    test("with Java's Short fqcn, should return JavaShortMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.SHORT)).toEqual(new JavaShortMarshaller());
    });

    test("with Java's Boolean fqcn, should return JavaBooleanMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.BOOLEAN)).toEqual(new JavaBooleanMarshaller());
    });

    test("with Java's String fqcn, should return JavaStringMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.STRING)).toEqual(new JavaStringMarshaller());
    });

    test("with Java's Date fqcn, should return JavaDateMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.DATE)).toEqual(new JavaDateMarshaller());
    });

    test("with Java's BigDecimal fqcn, should return JavaBigDecimalMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.BIG_DECIMAL)).toEqual(new JavaBigDecimalMarshaller());
    });

    test("with Java's BigInteger fqcn, should return JavaBigIntegerMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.BIG_INTEGER)).toEqual(new JavaBigIntegerMarshaller());
    });

    test("with Java's ArrayList fqcn, should return JavaArrayListMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.ARRAY_LIST)).toEqual(
        new JavaCollectionMarshaller.JavaArrayListMarshaller()
      );
    });

    test("with Java's HashSet fqcn, should return JavaHashSetMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.HASH_SET)).toEqual(
        new JavaCollectionMarshaller.JavaHashSetMarshaller()
      );
    });

    test("with Java's HashMap fqcn, should return JavaHashMapMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.HASH_MAP)).toEqual(new JavaHashMapMarshaller());
    });

    test("with Java's Optional fqcn, should return JavaOptionalMarshaller instance", () => {
      expect(MarshallerProvider.getForFqcn(JavaType.OPTIONAL)).toEqual(new JavaOptionalMarshaller());
    });

    test("with null fqcn, should return default marshaller", () => {
      const fqcn = null as any;

      expect(MarshallerProvider.getForFqcn(fqcn)).toEqual(new DefaultMarshaller());
    });

    test("with undefined fqcn, should return default marshaller", () => {
      const fqcn = undefined as any;

      expect(MarshallerProvider.getForFqcn(fqcn)).toEqual(new DefaultMarshaller());
    });

    test("with empty string, should return default marshaller", () => {
      const fqcn = "";

      expect(MarshallerProvider.getForFqcn(fqcn)).toEqual(new DefaultMarshaller());
    });

    test("with custom fqcn, should return default marshaller", () => {
      const fqcn = "com.myapp.custom.pojo";

      // it is a custom pojo (i.e. no pre-defined marshaller)
      expect(JavaWrapperUtils.isJavaType(fqcn)).toBeFalsy();

      expect(MarshallerProvider.getForFqcn(fqcn)).toEqual(new DefaultMarshaller());
    });
  });
});

describe("consistency validations", () => {
  beforeEach(() => {
    (MarshallerProvider as any).initialized = false; // force reinitialization
    MarshallerProvider.initialize();
  });

  test("all Java types should have a marshaller associated", () => {
    // this test is important to avoid developers to forget to add marshallers to new JavaTypes

    const marshallers = (MarshallerProvider as any).marshallersByJavaType;
    Object.keys(JavaType)
      .map((k: keyof typeof JavaType) => JavaType[k])
      .forEach(javaType => expect(marshallers.get(javaType)).toBeDefined());
  });
});

describe("getForEnum", () => {
  beforeEach(() => {
    MarshallerProvider.initialize();
  });

  afterEach(() => {
    // force reinitialization
    (MarshallerProvider as any).initialized = false;
  });

  test("should return exactly the same enum marshaller object", () => {
    const enumMarshaller = (MarshallerProvider as any).marshallersByJavaType.get(JavaType.ENUM);
    expect(MarshallerProvider.getForEnum()).toStrictEqual(enumMarshaller);
  });
});
