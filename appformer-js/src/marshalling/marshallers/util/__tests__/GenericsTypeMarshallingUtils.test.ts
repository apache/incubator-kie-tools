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
} from "../../../../java-wrappers";
import { GenericsTypeMarshallingUtils } from "../GenericsTypeMarshallingUtils";
import { MarshallingContext } from "../../../MarshallingContext";
import { JavaArrayListMarshaller, JavaHashSetMarshaller } from "../../JavaCollectionMarshaller";
import { ErraiObjectConstants } from "../../../model/ErraiObjectConstants";
import { MarshallerProvider } from "../../../MarshallerProvider";
import { JavaBigDecimalMarshaller } from "../../JavaBigDecimalMarshaller";
import { JavaBigIntegerMarshaller } from "../../JavaBigIntegerMarshaller";
import { JavaHashMapMarshaller } from "../../JavaHashMapMarshaller";
import { JavaLongMarshaller } from "../../JavaLongMarshaller";
import { JavaStringMarshaller } from "../../JavaStringMarshaller";
import { JavaDateMarshaller } from "../../JavaDateMarshaller";
import { DefaultMarshaller } from "../../DefaultMarshaller";
import { Portable } from "../../../Portable";
import { JavaOptionalMarshaller } from "../../JavaOptionalMarshaller";
import { NumValBasedErraiObject } from "../../../model/NumValBasedErraiObject";
import { JavaType } from "../../../../java-wrappers/JavaType";

describe("marshallGenericsTypeElement", () => {
  const objectId = ErraiObjectConstants.OBJECT_ID;

  beforeEach(() => {
    MarshallerProvider.initialize();
  });

  test("with array input, should marshall with regular marshalling", () => {
    const baseArray = ["str1", "str2"];

    const arrayInput = {
      input: baseArray,
      inputAsJavaArrayList: new JavaArrayList(baseArray)
    };

    const javaArrayListInput = {
      input: new JavaArrayList(baseArray),
      inputAsJavaArrayList: new JavaArrayList(baseArray)
    };

    [arrayInput, javaArrayListInput].forEach(({ input, inputAsJavaArrayList }) => {
      const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
      const expected = new JavaArrayListMarshaller().marshall(inputAsJavaArrayList, new MarshallingContext())!;

      // don't care about the ids
      delete output[objectId];
      delete expected[objectId];

      expect(output).toStrictEqual(expected);
    });
  });

  test("with Set input, should marshall with regular marshalling", () => {
    const baseSet = new Set(["str1", "str2"]);

    const setInput = {
      input: baseSet,
      inputAsJavaHashSet: new JavaHashSet(baseSet)
    };

    const javaHashSetInput = {
      input: new JavaHashSet(baseSet),
      inputAsJavaHashSet: new JavaHashSet(baseSet)
    };

    [setInput, javaHashSetInput].forEach(({ input, inputAsJavaHashSet }) => {
      const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
      const expected = new JavaHashSetMarshaller().marshall(inputAsJavaHashSet, new MarshallingContext())!;

      // don't care about the ids
      delete output[objectId];
      delete expected[objectId];

      expect(output).toStrictEqual(expected);
    });
  });

  test("with JavaBigDecimal input, should marshall with regular marshalling", () => {
    const input = new JavaBigDecimal("1.1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
    const expected = new JavaBigDecimalMarshaller().marshall(input, new MarshallingContext())!;

    // don't care about the ids
    delete output[objectId];
    delete expected[objectId];

    expect(output).toStrictEqual(expected);
  });

  test("with JavaBigInteger input, should marshall with regular marshalling", () => {
    const input = new JavaBigInteger("1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
    const expected = new JavaBigIntegerMarshaller().marshall(input, new MarshallingContext())!;

    // don't care about the ids
    delete output[objectId];
    delete expected[objectId];

    expect(output).toStrictEqual(expected);
  });

  test("with map input, should marshall with regular marshalling", () => {
    const baseMap = new Map([["foo1", "bar1"], ["foo2", "bar2"]]);

    const mapInput = { input: baseMap, inputAsJavaHashMap: new JavaHashMap(baseMap) };

    const javaHashMapInput = {
      input: new JavaHashMap(baseMap),
      inputAsJavaHashMap: new JavaHashMap(baseMap)
    };

    [mapInput, javaHashMapInput].forEach(({ input, inputAsJavaHashMap }) => {
      const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
      const expected = new JavaHashMapMarshaller().marshall(inputAsJavaHashMap, new MarshallingContext())!;

      // don't care about the ids
      delete output[objectId];
      delete expected[objectId];

      expect(output).toStrictEqual(expected);
    });
  });

  test("with JavaLong input, should marshall with regular marshalling", () => {
    const input = new JavaLong("1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
    const expected = new JavaLongMarshaller().marshall(input, new MarshallingContext())!;

    // don't care about the ids
    delete output[objectId];
    delete expected[objectId];

    expect(output).toStrictEqual(expected);
  });

  test("with string input, should marshall with regular marshalling", () => {
    const stringInput = {
      input: "str",
      inputAsJavaString: new JavaString("str")
    };

    const javaStringInput = {
      input: new JavaString("str"),
      inputAsJavaString: new JavaString("str")
    };

    [stringInput, javaStringInput].forEach(({ input, inputAsJavaString }) => {
      const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
      const expected = new JavaStringMarshaller().marshall(inputAsJavaString, new MarshallingContext())!;

      expect(output).toStrictEqual(expected);
    });
  });

  test("with date input, should marshall with regular marshalling", () => {
    const baseDate = new Date();

    const dateInput = {
      input: baseDate,
      inputAsJavaDate: new JavaDate(baseDate)
    };

    const javaDateInput = {
      input: new JavaDate(baseDate),
      inputAsJavaDate: new JavaDate(baseDate)
    };

    [dateInput, javaDateInput].forEach(({ input, inputAsJavaDate }) => {
      const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
      const expected = new JavaDateMarshaller().marshall(inputAsJavaDate, new MarshallingContext())!;

      // don't care about the ids
      delete output[objectId];
      delete expected[objectId];

      expect(output).toStrictEqual(expected);
    });
  });

  test("with JavaOptional input, should marshall with regular marshalling", () => {
    const input = new JavaOptional<string>("str");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
    const expected = new JavaOptionalMarshaller().marshall(input, new MarshallingContext())!;

    // don't care about the ids
    delete output[objectId];
    delete expected[objectId];

    expect(output).toStrictEqual(expected);
  });

  test("with custom portable input, should marshall with regular marshalling", () => {
    const input = new Pojo("bar");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
    const expected = new DefaultMarshaller().marshall(input, new MarshallingContext())!;

    // don't care about the ids
    delete output[objectId];
    delete expected[objectId];

    expect(output).toStrictEqual(expected);
  });

  test("with boolean input, should return input wrapped as an ErraiObject", () => {
    const booleanInput = false;
    const javaBooleanInput = new JavaBoolean(false);

    [booleanInput, javaBooleanInput].forEach(input => {
      const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());

      expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.BOOLEAN, false).asErraiObject());
    });
  });

  test("with JavaByte input, should return input wrapped as an ErraiObject", () => {
    const input = new JavaByte("1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());
    expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.BYTE, 1).asErraiObject());
  });

  test("with JavaDouble input, should return input wrapped as an ErraiObject", () => {
    const input = new JavaDouble("1.1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());

    expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.DOUBLE, 1.1).asErraiObject());
  });

  test("with JavaFloat input, should return input wrapped as an ErraiObject", () => {
    const input = new JavaFloat("1.1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());

    expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.FLOAT, 1.1).asErraiObject());
  });

  test("with JavaInteger input, should return input wrapped as an ErraiObject", () => {
    const input = new JavaInteger("1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());

    expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.INTEGER, 1).asErraiObject());
  });

  test("with JavaShort input, should return input wrapped as an ErraiObject", () => {
    const input = new JavaShort("1");

    const output = GenericsTypeMarshallingUtils.marshallGenericsTypeElement(input, new MarshallingContext());

    expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.SHORT, 1).asErraiObject());
  });

  class Pojo implements Portable<Pojo> {
    private readonly _fqcn = "com.app.my.Pojo";

    public foo: string;

    constructor(foo: string) {
      this.foo = foo;
    }
  }
});
