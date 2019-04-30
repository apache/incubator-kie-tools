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

import { MarshallingContext } from "../../MarshallingContext";
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
} from "../../../java-wrappers";
import { ErraiObjectConstants } from "../../model/ErraiObjectConstants";
import { MarshallerProvider } from "../../MarshallerProvider";
import { Portable } from "../../Portable";
import { JavaOptionalMarshaller } from "../JavaOptionalMarshaller";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
import { NumberUtils } from "../../../util/NumberUtils";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { JavaType } from "../../../java-wrappers/JavaType";

describe("marshall", () => {
  const encodedType = ErraiObjectConstants.ENCODED_TYPE;
  const objectId = ErraiObjectConstants.OBJECT_ID;
  const value = ErraiObjectConstants.VALUE;

  let context: MarshallingContext;

  beforeEach(() => {
    MarshallerProvider.initialize();
    context = new MarshallingContext();
  });

  test("with empty optional, should serialize normally", () => {
    const input = new JavaOptional<string>(undefined);

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toStrictEqual(new ValueBasedErraiObject(JavaType.OPTIONAL, null).asErraiObject());
  });

  test("with JavaNumber optional, should wrap element into an errai object", () => {
    const input = new JavaOptional<JavaInteger>(new JavaInteger("1"));

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toStrictEqual(
      new ValueBasedErraiObject(
        JavaType.OPTIONAL,
        new NumValBasedErraiObject(JavaType.INTEGER, 1).asErraiObject()
      ).asErraiObject()
    );
  });

  test("with JavaBoolean optional, should wrap element into an errai object", () => {
    const input = new JavaOptional<JavaBoolean>(new JavaBoolean(false));

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toStrictEqual(
      new ValueBasedErraiObject(
        JavaType.OPTIONAL,
        new NumValBasedErraiObject(JavaType.BOOLEAN, false).asErraiObject()
      ).asErraiObject()
    );
  });

  test("with JavaBigNumber optional, should serialize element normally", () => {
    const input = new JavaOptional<JavaBigInteger>(new JavaBigInteger("1"));

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toStrictEqual({
      [encodedType]: JavaType.OPTIONAL,
      [objectId]: "-1",
      [value]: {
        [encodedType]: JavaType.BIG_INTEGER,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: "1"
      }
    });
  });

  test("with custom object optional, should serialize element normally", () => {
    const input = new JavaOptional<MyPortable>(new MyPortable({ foo: "foo1", bar: "bar1" }));

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toStrictEqual({
      [encodedType]: JavaType.OPTIONAL,
      [objectId]: "-1",
      [value]: {
        [encodedType]: "com.portable.my",
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        foo: "foo1",
        bar: "bar1"
      }
    });
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaOptionalMarshaller().marshall(input, context);

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  beforeEach(() => {
    MarshallerProvider.initialize();
  });

  test("with empty optional, should return an empty optional", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<string>(undefined);
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(input);
  });

  test("with Array input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const arrayInput = new JavaOptional<string[]>(["str1", "str2"]);
    const arrayListInput = new JavaOptional<JavaArrayList<string>>(new JavaArrayList(["str1", "str2"]));

    [arrayInput, arrayListInput].forEach(input => {
      const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

      const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional(["str1", "str2"]));
    });
  });

  test("with Set input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const setInput = new JavaOptional<Set<string>>(new Set(["str1", "str2"]));
    const hashSetInput = new JavaOptional<JavaHashSet<string>>(new JavaHashSet(new Set(["str1", "str2"])));

    [setInput, hashSetInput].forEach(input => {
      const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

      const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional(new Set(["str1", "str2"])));
    });
  });

  test("with Map input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const mapInput = new JavaOptional<Map<string, string>>(new Map([["str1", "str2"]]));
    const hashMapInput = new JavaOptional<JavaHashMap<string, string>>(new JavaHashMap(new Map([["str1", "str2"]])));

    [mapInput, hashMapInput].forEach(input => {
      const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

      const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional(new Map([["str1", "str2"]])));
    });
  });

  test("with Date input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const baseDate = new Date();

    const dateInput = new JavaOptional<Date>(new Date(baseDate));
    const javaDateInput = new JavaOptional<JavaDate>(new JavaDate(baseDate));

    [dateInput, javaDateInput].forEach(input => {
      const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

      const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional<Date>(new Date(baseDate)));
    });
  });

  test("with Boolean input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const booleanInput = new JavaOptional<boolean>(false);
    const javaBooleanInput = new JavaOptional<JavaBoolean>(new JavaBoolean(false));

    [booleanInput, javaBooleanInput].forEach(input => {
      const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

      const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional<boolean>(false));
    });
  });

  test("with String input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const stringInput = new JavaOptional<string>("foo");
    const javaStringInput = new JavaOptional<JavaString>(new JavaString("foo"));

    [stringInput, javaStringInput].forEach(input => {
      const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

      const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional<string>("foo"));
    });
  });

  test("with JavaOptional input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional(new JavaOptional<string>("foo"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional(new JavaOptional<string>("foo")));
  });

  test("with JavaBigDecimal input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaBigDecimal>(new JavaBigDecimal("1.1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaBigDecimal>(new JavaBigDecimal("1.1")));
  });

  test("with JavaBigInteger should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaBigInteger>(new JavaBigInteger("1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaBigInteger>(new JavaBigInteger("1")));
  });

  test("with JavaLong input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaLong>(new JavaLong("1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaLong>(new JavaLong("1")));
  });

  test("with JavaByte input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaByte>(new JavaByte("1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaByte>(new JavaByte("1")));
  });

  test("with JavaDouble input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaDouble>(new JavaDouble("1.1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaDouble>(new JavaDouble("1.1")));
  });

  test("with JavaFloat input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaFloat>(new JavaFloat("1.1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaFloat>(new JavaFloat("1.1")));
  });

  test("with JavaInteger input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaInteger>(new JavaInteger("1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaInteger>(new JavaInteger("1")));
  });

  test("with JavaShort input, should unmarshall correctly", () => {
    const marshaller = new JavaOptionalMarshaller();

    const input = new JavaOptional<JavaShort>(new JavaShort("1"));
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new JavaOptional<JavaShort>(new JavaShort("1")));
  });

  test("with custom object optional, should unmarshall correctly", () => {
    const oracle = new Map([["com.portable.my", () => new MyPortable({} as any)]]);
    const marshaller = new JavaOptionalMarshaller();

    const pojoInput = new MyPortable({ foo: "foo1", bar: "bar1" });
    const optionalInput = new JavaOptional<MyPortable>(pojoInput);

    const marshalledInput = marshaller.notNullMarshall(optionalInput, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(oracle));

    expect(output).toEqual(new JavaOptional<MyPortable>(pojoInput));
  });

  test("with root null object, should unmarshall to null", () => {
    const input = null as any;

    const output = new JavaOptionalMarshaller().unmarshall(input, new UnmarshallingContext(new Map()));

    expect(output).toBeUndefined();
  });

  test("with root undefined object, should unmarshall to undefined", () => {
    const input = undefined as any;

    const output = new JavaOptionalMarshaller().unmarshall(input, new UnmarshallingContext(new Map()));

    expect(output).toBeUndefined();
  });
});

class MyPortable implements Portable<MyPortable> {
  private readonly _fqcn = "com.portable.my";

  public readonly foo: string;
  public readonly bar: string;

  constructor(self: { foo: string; bar: string }) {
    Object.assign(this, self);
  }
}
