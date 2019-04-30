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

import { MarshallerProvider } from "../../MarshallerProvider";
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
import { JavaHashMapMarshaller } from "../JavaHashMapMarshaller";
import { MarshallingContext } from "../../MarshallingContext";
import { ErraiObjectConstants } from "../../model/ErraiObjectConstants";
import { Portable } from "../../../marshalling/Portable";
import { DefaultMarshaller } from "../DefaultMarshaller";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";
import { NumberUtils } from "../../../util/NumberUtils";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
import { JavaType } from "../../../java-wrappers/JavaType";

describe("marshall", () => {
  const encodedType = ErraiObjectConstants.ENCODED_TYPE;
  const objectId = ErraiObjectConstants.OBJECT_ID;
  const value = ErraiObjectConstants.VALUE;
  const json = ErraiObjectConstants.JSON;

  beforeEach(() => {
    MarshallerProvider.initialize();
  });

  test("with empty map, should serialize normally", () => {
    const input = new JavaHashMap(new Map());

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {}
    });
  });

  test("with string key and value, should serialize normally", () => {
    const input = new JavaHashMap(new Map([["foo1", "bar1"], ["foo2", "bar2"]]));

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        foo1: "bar1",
        foo2: "bar2"
      }
    });
  });

  test("with JavaNumber key and value, should wrap key and value into an errai object", () => {
    const input = new JavaHashMap(
      new Map([[new JavaInteger("11"), new JavaInteger("12")], [new JavaInteger("21"), new JavaInteger("22")]])
    );

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());

    const expectedKey1 = `${json + JSON.stringify(new NumValBasedErraiObject(JavaType.INTEGER, 11).asErraiObject())}`;

    const expectedKey2 = `${json + JSON.stringify(new NumValBasedErraiObject(JavaType.INTEGER, 21).asErraiObject())}`;

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        [expectedKey1]: new NumValBasedErraiObject(JavaType.INTEGER, 12).asErraiObject(),
        [expectedKey2]: new NumValBasedErraiObject(JavaType.INTEGER, 22).asErraiObject()
      }
    });
  });

  test("with JavaBoolean key and value, should wrap key and value into an errai object", () => {
    const input = new JavaHashMap(
      new Map([[new JavaBoolean(true), new JavaBoolean(false)], [new JavaBoolean(false), new JavaBoolean(true)]])
    );

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());

    const expectedKey1 = `${json + JSON.stringify(new NumValBasedErraiObject(JavaType.BOOLEAN, true).asErraiObject())}`;

    const expectedKey2 = `${json +
      JSON.stringify(new NumValBasedErraiObject(JavaType.BOOLEAN, false).asErraiObject())}`;

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        [expectedKey1]: new NumValBasedErraiObject(JavaType.BOOLEAN, false).asErraiObject(),
        [expectedKey2]: new NumValBasedErraiObject(JavaType.BOOLEAN, true).asErraiObject()
      }
    });
  });

  test("with JavaBigNumber key and value, should wrap key and value into an errai object", () => {
    const input = new JavaHashMap(
      new Map([
        [new JavaBigInteger("11"), new JavaBigInteger("12")],
        [new JavaBigInteger("21"), new JavaBigInteger("22")]
      ])
    );

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext())!;

    // need to assert the keys individually because since it's a string, can't use the regex matcher in the object id :/

    const mapKeys = Object.keys(output[value]);
    expect(mapKeys.length).toBe(2);

    const key1Str = mapKeys[0];
    const key2Str = mapKeys[1];

    mapKeys.forEach(k => {
      // complex objects as map key uses a prefix to indicate that a json must be parsed in the map key
      expect(k.startsWith(json)).toBeTruthy();
    });

    const key1Obj = JSON.parse(key1Str.replace(json, ""));
    const key2Obj = JSON.parse(key2Str.replace(json, ""));

    expect(key1Obj).toStrictEqual({
      [encodedType]: JavaType.BIG_INTEGER,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: "11"
    });

    expect(key2Obj).toStrictEqual({
      [encodedType]: JavaType.BIG_INTEGER,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: "21"
    });

    // assert map values

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        [key1Str]: {
          [encodedType]: JavaType.BIG_INTEGER,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: "12"
        },
        [key2Str]: {
          [encodedType]: JavaType.BIG_INTEGER,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: "22"
        }
      }
    });
  });

  test("with custom object key and value, should wrap key and value into an errai object", () => {
    const input = new JavaHashMap(
      new Map([
        [new DummyPojo({ foo: "bar11" }), new DummyPojo({ foo: "bar12" })],
        [new DummyPojo({ foo: "bar21" }), new DummyPojo({ foo: "bar22" })]
      ])
    );

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext())!;

    // need to assert the keys individually because since it's a string, can't use the regex matcher in the object id :/

    const mapKeys = Object.keys(output[value]);
    expect(mapKeys.length).toBe(2);

    const key1Str = mapKeys[0];
    const key2Str = mapKeys[1];

    mapKeys.forEach(k => {
      // complex objects as map key uses a prefix to indicate that a json must be parsed in the map key
      expect(k.startsWith(json)).toBeTruthy();
    });

    const key1Obj = JSON.parse(key1Str.replace(json, ""));
    const key2Obj = JSON.parse(key2Str.replace(json, ""));

    expect(key1Obj).toStrictEqual({
      [encodedType]: "com.app.my.DummyPojo",
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      foo: "bar11"
    });

    expect(key2Obj).toStrictEqual({
      [encodedType]: "com.app.my.DummyPojo",
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      foo: "bar21"
    });

    // assert map values

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        [key1Str]: {
          [encodedType]: "com.app.my.DummyPojo",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          foo: "bar12"
        },
        [key2Str]: {
          [encodedType]: "com.app.my.DummyPojo",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          foo: "bar22"
        }
      }
    });
  });

  test("with undefined key and value, should set key as null reference and value as null", () => {
    const input = new JavaHashMap(new Map([[undefined, undefined]]));

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        [ErraiObjectConstants.NULL]: null
      }
    });
  });

  test("with custom pojo containing cached key, should reuse it and don't repeat data", () => {
    const repeatedPojo = new DummyPojo({ foo: "repeatedKey" });

    const input = new ComplexPojo({
      dummy: repeatedPojo,
      map: new Map([[repeatedPojo, "value1"], [new DummyPojo({ foo: "uniqueKey" }), "value2"]])
    });

    const context = new MarshallingContext();
    const output = new DefaultMarshaller().marshall(input, context)!;

    // ===== assertions

    // 1) Assert map content

    const mapOutput = (output as any).map;
    const mapKeys = Object.keys(mapOutput[value]);
    expect(mapKeys.length).toBe(2);

    const key1Str = mapKeys[0];
    const key2Str = mapKeys[1];

    mapKeys.forEach(k => {
      // complex objects as map key uses a prefix to indicate that a json must be parsed in the map key
      expect(k.startsWith(json)).toBeTruthy();
    });

    const key1Obj = JSON.parse(key1Str.replace(json, ""));
    const key2Obj = JSON.parse(key2Str.replace(json, ""));

    // assert keys contents

    const key1ObjectId = key1Obj[objectId]; // this is the cached object's id
    expect(key1ObjectId).toMatch(NumberUtils.nonNegativeIntegerRegex);
    expect(key1Obj).toStrictEqual({
      [encodedType]: "com.app.my.DummyPojo",
      [objectId]: key1ObjectId // without object's content
    });

    expect(key2Obj).toStrictEqual({
      [encodedType]: "com.app.my.DummyPojo",
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      foo: "uniqueKey"
    });

    // assert map values contents

    expect(mapOutput).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        [key1Str]: "value1",
        [key2Str]: "value2"
      }
    });

    // 2) Assert full object content

    expect(output).toStrictEqual({
      [encodedType]: "com.app.my.ComplexPojo",
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      dummy: {
        [encodedType]: "com.app.my.DummyPojo",
        [objectId]: key1ObjectId, // same object id than the one used as map key
        foo: "repeatedKey"
      },
      map: mapOutput // already asserted
    });

    // do not cache repeated object's data
    expect(context.getCached(repeatedPojo)).toStrictEqual({
      [encodedType]: "com.app.my.DummyPojo",
      [objectId]: key1ObjectId
    });
  });

  test("with map containing repeated value, should reuse it and don't repeat data", () => {
    const repeatedValue = new DummyPojo({ foo: "repeatedValue" });
    const uniqueValue = new DummyPojo({ foo: "uniqueValue" });

    const input = new JavaHashMap(new Map([["key1", repeatedValue], ["key2", repeatedValue], ["key3", uniqueValue]]));

    const context = new MarshallingContext();
    const output = new JavaHashMapMarshaller().marshall(input, context);

    expect(output).toStrictEqual({
      [encodedType]: JavaType.HASH_MAP,
      [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [value]: {
        key1: {
          [encodedType]: "com.app.my.DummyPojo",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          foo: "repeatedValue"
        },
        key2: {
          [encodedType]: "com.app.my.DummyPojo",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex)
          // missing data
        },
        key3: {
          [encodedType]: "com.app.my.DummyPojo",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          foo: "uniqueValue"
        }
      }
    });

    // same object id
    const repeatedObjIdFirstAppearance = (output as any)[value].key1[objectId];
    const repeatedObjIdSecondAppearance = (output as any)[value].key2[objectId];
    expect(repeatedObjIdFirstAppearance).toEqual(repeatedObjIdSecondAppearance);

    // do not cache repeated object's data
    expect(context.getCached(repeatedValue)).toStrictEqual({
      [encodedType]: "com.app.my.DummyPojo",
      [objectId]: repeatedObjIdFirstAppearance
    });
  });

  test("with root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());
    expect(output).toBeNull();
  });

  test("with root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaHashMapMarshaller().marshall(input, new MarshallingContext());
    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  beforeEach(() => {
    MarshallerProvider.initialize();
  });

  test("with empty map, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap<string, string>(new Map<string, string>());

    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map());
  });

  test("with string key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const stringInput = new JavaHashMap(new Map([["foo1", "bar1"]]));
    const javaStringInput = new JavaHashMap(new Map([[new JavaString("foo1"), new JavaString("bar1")]]));

    [stringInput, javaStringInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([["foo1", "bar1"]]));
    });
  });

  test("with Array key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const arrayInput = new JavaHashMap(new Map([[["foo1"], ["bar1"]]]));
    const arrayListInput = new JavaHashMap(new Map([[new JavaArrayList(["foo1"]), new JavaArrayList(["bar1"])]]));

    [arrayInput, arrayListInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([[["foo1"], ["bar1"]]]));
    });
  });

  test("with Set key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const setInput = new JavaHashMap(new Map([[new Set(["foo1"]), new Set(["bar1"])]]));
    const hashSetInput = new JavaHashMap(
      new Map([[new JavaHashSet(new Set(["foo1"])), new JavaHashSet(new Set(["bar1"]))]])
    );

    [setInput, hashSetInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([[new Set(["foo1"]), new Set(["bar1"])]]));
    });
  });

  test("with Map key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const mapInput = new JavaHashMap(new Map([[new Map([["kfoo1", "kbar1"]]), new Map([["vfoo1", "vbar1"]])]]));
    const hashMapInput = new JavaHashMap(
      new Map([[new JavaHashMap(new Map([["kfoo1", "kbar1"]])), new JavaHashMap(new Map([["vfoo1", "vbar1"]]))]])
    );

    [mapInput, hashMapInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([[new Map([["kfoo1", "kbar1"]]), new Map([["vfoo1", "vbar1"]])]]));
    });
  });

  test("with Date key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const baseDateKey = new Date();
    const baseDateValue = new Date();

    const dateInput = new JavaHashMap(new Map([[baseDateKey, baseDateValue]]));
    const javaDateInput = new JavaHashMap(new Map([[new JavaDate(baseDateKey), new JavaDate(baseDateValue)]]));

    [dateInput, javaDateInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([[baseDateKey, baseDateValue]]));
    });
  });

  test("with Boolean key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const booleanInput = new JavaHashMap(new Map([[false, true]]));
    const javaBooleanInput = new JavaHashMap(new Map([[new JavaBoolean(false), new JavaBoolean(true)]]));

    [booleanInput, javaBooleanInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([[false, true]]));
    });
  });

  test("with String key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const stringInput = new JavaHashMap(new Map([["foo", "bar"]]));
    const javaStringInput = new JavaHashMap(new Map([[new JavaString("foo"), new JavaString("bar")]]));

    [stringInput, javaStringInput].forEach(input => {
      const marshalledInput = marshaller.marshall(input, new MarshallingContext());

      const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new Map([["foo", "bar"]]));
    });
  });

  test("with JavaOptional key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaOptional("foo"), new JavaOptional("bar")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaOptional("foo"), new JavaOptional("bar")]]));
  });

  test("with JavaBigDecimal key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaBigDecimal("1.1"), new JavaBigDecimal("1.1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaBigDecimal("1.1"), new JavaBigDecimal("1.1")]]));
  });

  test("with JavaBigInteger key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaBigInteger("1"), new JavaBigInteger("1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaBigInteger("1"), new JavaBigInteger("1")]]));
  });

  test("with JavaLong key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaLong("1"), new JavaLong("1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaLong("1"), new JavaLong("1")]]));
  });

  test("with JavaByte key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaByte("1"), new JavaByte("1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaByte("1"), new JavaByte("1")]]));
  });

  test("with JavaDouble key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaDouble("1.1"), new JavaDouble("1.1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaDouble("1.1"), new JavaDouble("1.1")]]));
  });

  test("with JavaFloat key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaFloat("1.1"), new JavaFloat("1.1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaFloat("1.1"), new JavaFloat("1.1")]]));
  });

  test("with JavaInteger key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaInteger("1"), new JavaInteger("1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaInteger("1"), new JavaInteger("1")]]));
  });

  test("with JavaShort key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaShort("1"), new JavaShort("1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaShort("1"), new JavaShort("1")]]));
  });

  test("with JavaShort key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[new JavaShort("1"), new JavaShort("1")]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[new JavaShort("1"), new JavaShort("1")]]));
  });

  test("with custom object key and value, should unmarshall correctly", () => {
    const oracle = new Map([["com.app.my.DummyPojo", () => new DummyPojo({} as any)]]);
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(
      new Map([
        [new DummyPojo({ foo: "bar11" }), new DummyPojo({ foo: "bar12" })],
        [new DummyPojo({ foo: "bar21" }), new DummyPojo({ foo: "bar22" })]
      ])
    );

    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(oracle));

    expect(output).toEqual(
      new Map([
        [new DummyPojo({ foo: "bar11" }), new DummyPojo({ foo: "bar12" })],
        [new DummyPojo({ foo: "bar21" }), new DummyPojo({ foo: "bar22" })]
      ])
    );
  });

  test("with undefined key and value, should unmarshall correctly", () => {
    const marshaller = new JavaHashMapMarshaller();

    const input = new JavaHashMap(new Map([[undefined, undefined]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext());

    const output = marshaller.unmarshall(marshalledInput!, new UnmarshallingContext(new Map()));

    expect(output).toEqual(new Map([[undefined, undefined]]));
  });

  test("with custom pojo containing cached key, should reuse cached objects and don't recreate data", () => {
    const marshaller = new DefaultMarshaller();
    const oracle: Map<string, () => Portable<any>> = new Map([
      ["com.app.my.DummyPojo", () => new DummyPojo({} as any) as any],
      ["com.app.my.ComplexPojo", () => new ComplexPojo({} as any) as any]
    ]);

    const repeatedPojo = new DummyPojo({ foo: "repeatedKey" });

    const input = new ComplexPojo({
      dummy: repeatedPojo,
      map: new Map([[repeatedPojo, "value1"], [new DummyPojo({ foo: "uniqueKey" }), "value2"]])
    });

    const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

    const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(oracle))! as ComplexPojo;

    // ===== assertions

    // successfully unmarshalled
    expect(output).toEqual(input);

    // same object for the cached one
    const repeatedPojoFromKey = output.map.keys().next().value;
    expect(output.dummy).toBe(repeatedPojoFromKey);
  });

  test("with map containing repeated value, should reuse cached objects and don't recreate data", () => {
    const marshaller = new JavaHashMapMarshaller();
    const oracle: Map<string, () => Portable<any>> = new Map([
      ["com.app.my.DummyPojo", () => new DummyPojo({} as any)]
    ]);

    const repeatedPojo = new DummyPojo({ foo: "repeatedKey" });
    const input = new JavaHashMap(new Map([["k1", repeatedPojo], ["k2", repeatedPojo]]));
    const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

    const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(oracle))!;

    // ===== assertions

    // successfully unmarshalled
    expect(output).toEqual(new Map([["k1", repeatedPojo], ["k2", repeatedPojo]]));

    // same objects for the values
    expect(output.get("k1")!).toBe(output.get("k2")!);
  });

  test("with null inside ErraiObject's value, should throw error", () => {
    const context = new UnmarshallingContext(new Map());
    const marshaller = new JavaHashMapMarshaller();

    const marshalledInput = new ValueBasedErraiObject(JavaType.HASH_MAP, null as any).asErraiObject();

    expect(() => marshaller.unmarshall(marshalledInput!, context)).toThrowError();
  });
});

class DummyPojo implements Portable<DummyPojo> {
  private readonly _fqcn = "com.app.my.DummyPojo";

  public readonly foo: string;

  constructor(self: { foo: string }) {
    Object.assign(this, self);
  }
}

class ComplexPojo implements Portable<ComplexPojo> {
  private readonly _fqcn = "com.app.my.ComplexPojo";

  public dummy: DummyPojo;
  public map: Map<DummyPojo, string>;

  constructor(self: { dummy: DummyPojo; map: Map<DummyPojo, string> }) {
    Object.assign(this, self);
  }
}
