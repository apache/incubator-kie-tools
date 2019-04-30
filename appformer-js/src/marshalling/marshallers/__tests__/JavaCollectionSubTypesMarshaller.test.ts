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

import { JavaArrayListMarshaller, JavaHashSetMarshaller } from "../JavaCollectionMarshaller";
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
import { MarshallingContext } from "../../MarshallingContext";
import { ErraiObjectConstants } from "../../model/ErraiObjectConstants";
import { MarshallerProvider } from "../../MarshallerProvider";
import { JavaBigIntegerMarshaller } from "../JavaBigIntegerMarshaller";
import { Portable } from "../../Portable";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";
import { NumberUtils } from "../../../util/NumberUtils";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
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

  test("with empty collection, should serialize normally", () => {
    const arrayListScenario = () => {
      const input = new JavaArrayList([]);
      return { fqcn: JavaType.ARRAY_LIST, output: new JavaArrayListMarshaller().marshall(input, context) };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set([]));
      return { fqcn: JavaType.HASH_SET, output: new JavaHashSetMarshaller().marshall(input, context) };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      expect(output).toStrictEqual({
        [encodedType]: fqcn,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: []
      });
    });
  });

  test("with JavaNumber collection, should wrap every element into an errai object", () => {
    const numberArray = [new JavaInteger("1"), new JavaInteger("2"), new JavaInteger("3")];

    const arrayListScenario = () => {
      const input = new JavaArrayList(numberArray);
      return { fqcn: JavaType.ARRAY_LIST, output: new JavaArrayListMarshaller().marshall(input, context) };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set(numberArray));
      return { fqcn: JavaType.HASH_SET, output: new JavaHashSetMarshaller().marshall(input, context) };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      expect(output).toStrictEqual({
        [encodedType]: fqcn,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: [
          new NumValBasedErraiObject(JavaType.INTEGER, 1).asErraiObject(),
          new NumValBasedErraiObject(JavaType.INTEGER, 2).asErraiObject(),
          new NumValBasedErraiObject(JavaType.INTEGER, 3).asErraiObject()
        ]
      });
    });
  });

  test("with JavaBoolean collection, should wrap every element into an errai object", () => {
    const booleanArray = [new JavaBoolean(true), new JavaBoolean(false)];

    const arrayListScenario = () => {
      const input = new JavaArrayList(booleanArray);
      return { fqcn: JavaType.ARRAY_LIST, output: new JavaArrayListMarshaller().marshall(input, context) };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set(booleanArray));
      return { fqcn: JavaType.HASH_SET, output: new JavaHashSetMarshaller().marshall(input, context) };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      expect(output).toStrictEqual({
        [encodedType]: fqcn,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: [
          new NumValBasedErraiObject(JavaType.BOOLEAN, true).asErraiObject(),
          new NumValBasedErraiObject(JavaType.BOOLEAN, false).asErraiObject()
        ]
      });
    });
  });

  test("with JavaBigNumber collection, should serialize every element normally", () => {
    const bigIntegerMarshaller = new JavaBigIntegerMarshaller();

    const bigNumberArray = [new JavaBigInteger("1"), new JavaBigInteger("2"), new JavaBigInteger("3")];

    const arrayListScenario = () => {
      const input = new JavaArrayList(bigNumberArray);
      return { fqcn: JavaType.ARRAY_LIST, output: new JavaArrayListMarshaller().marshall(input, context) };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set(bigNumberArray));
      return { fqcn: JavaType.HASH_SET, output: new JavaHashSetMarshaller().marshall(input, context) };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      expect(output).toStrictEqual({
        [encodedType]: fqcn,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: [
          {
            ...(bigIntegerMarshaller.marshall(new JavaBigInteger("1"), context) as any),
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex)
          },
          {
            ...(bigIntegerMarshaller.marshall(new JavaBigInteger("2"), context) as any),
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex)
          },
          {
            ...(bigIntegerMarshaller.marshall(new JavaBigInteger("3"), context) as any),
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex)
          }
        ]
      });
    });
  });

  test("with custom object collection, should serialize every element normally", () => {
    const portableArray = [
      new MyPortable({ foo: "foo1", bar: "bar1" }),
      new MyPortable({ foo: "foo2", bar: "bar2" }),
      new MyPortable({ foo: "foo3", bar: "bar3" })
    ];

    const arrayListScenario = () => {
      const input = new JavaArrayList(portableArray);
      return {
        fqcn: JavaType.ARRAY_LIST,
        output: new JavaArrayListMarshaller().marshall(input, new MarshallingContext())
      };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set(portableArray));
      return {
        fqcn: JavaType.HASH_SET,
        output: new JavaHashSetMarshaller().marshall(input, new MarshallingContext())
      };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      expect(output).toStrictEqual({
        [encodedType]: fqcn,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: [
          {
            [encodedType]: "com.portable.my",
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
            foo: "foo1",
            bar: "bar1"
          },
          {
            [encodedType]: "com.portable.my",
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
            foo: "foo2",
            bar: "bar2"
          },
          {
            [encodedType]: "com.portable.my",
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
            foo: "foo3",
            bar: "bar3"
          }
        ]
      });
    });
  });

  test("with collection containing null elements, should serialize every element normally", () => {
    const arrayListScenario = () => {
      const input = new JavaArrayList([null]);
      return {
        fqcn: JavaType.ARRAY_LIST,
        output: new JavaArrayListMarshaller().marshall(input, new MarshallingContext())
      };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set([null]));
      return {
        fqcn: JavaType.HASH_SET,
        output: new JavaHashSetMarshaller().marshall(input, new MarshallingContext())
      };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      expect(output).toStrictEqual({
        [encodedType]: fqcn,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: [null]
      });
    });
  });

  test("with custom pojo array containing repeated elements, should cache inner objects and don't repeat data", () => {
    const repeatedValue = new Node({ data: "foo1", left: undefined, right: undefined });

    const portableArray = [repeatedValue, new Node({ data: "foo2", left: undefined, right: repeatedValue })];

    const arrayListScenario = () => {
      const input = new JavaArrayList(portableArray);
      return {
        fqcn: JavaType.ARRAY_LIST,
        output: new JavaArrayListMarshaller().marshall(input, new MarshallingContext())
      };
    };

    const hashSetScenario = () => {
      const input = new JavaHashSet(new Set(portableArray));
      return {
        fqcn: JavaType.HASH_SET,
        output: new JavaHashSetMarshaller().marshall(input, new MarshallingContext())
      };
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const { fqcn, output } = outputFunc();

      const rootObjId = output![objectId];
      expect(output![encodedType]).toEqual(fqcn);
      expect(rootObjId).toMatch(NumberUtils.nonNegativeIntegerRegex);

      const rootObjValue = output![value] as any[];

      const foo2Objects = rootObjValue.filter(obj => obj.data === "foo2");
      expect(foo2Objects.length).toEqual(1);
      const uniqueObjId = foo2Objects[0][objectId];
      expect(uniqueObjId).toMatch(NumberUtils.nonNegativeIntegerRegex);

      const repeatedObjects = rootObjValue.filter(obj => obj.data !== "foo2");
      expect(repeatedObjects.length).toEqual(1);
      const repeatedObjId = repeatedObjects[0][objectId];
      expect(repeatedObjId).toMatch(NumberUtils.nonNegativeIntegerRegex);

      expect(rootObjValue).toEqual([
        { [encodedType]: "com.app.my.Node", [objectId]: repeatedObjId, data: "foo1", left: null, right: null },
        {
          [encodedType]: "com.app.my.Node",
          [objectId]: uniqueObjId,
          data: "foo2",
          right: { [encodedType]: "com.app.my.Node", [objectId]: repeatedObjId },
          left: null
        }
      ]);
    });
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const arrayListScenario = () => {
      return new JavaArrayListMarshaller().marshall(input, new MarshallingContext());
    };

    const hashSetScenario = () => {
      return new JavaHashSetMarshaller().marshall(input, new MarshallingContext());
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const output = outputFunc();
      expect(output).toBeNull();
    });
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const arrayListScenario = () => {
      return new JavaArrayListMarshaller().marshall(input, new MarshallingContext());
    };

    const hashSetScenario = () => {
      return new JavaHashSetMarshaller().marshall(input, new MarshallingContext());
    };

    [arrayListScenario, hashSetScenario].forEach(outputFunc => {
      const output = outputFunc();
      expect(output).toBeNull();
    });
  });
});

describe("unmarshall", () => {
  beforeEach(() => {
    MarshallerProvider.initialize();
  });

  test("with empty collection, should unmarshall to empty collection", () => {
    const arrayInput = {
      input: new JavaArrayList([]),
      marshaller: new JavaArrayListMarshaller(),
      expected: []
    };

    const setInput = {
      input: new JavaHashSet(new Set([])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with Array collection, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([["foo"]]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [["foo"]]
    };

    const setInput = {
      input: new JavaHashSet(new Set([["foo"]])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([["foo"]])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaArrayList collection, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaArrayList(["foo"])]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [["foo"]]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaArrayList(["foo"])])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([["foo"]])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with Set input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new Set(["foo"])]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new Set(["foo"])]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new Set(["foo"])])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new Set(["foo"])])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with HashSet input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaHashSet(new Set(["foo"]))]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new Set(["foo"])]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaHashSet(new Set(["foo"]))])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new Set(["foo"])])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with Map input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new Map([["foo", "bar"]])]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new Map([["foo", "bar"]])]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new Map([["foo", "bar"]])])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new Map([["foo", "bar"]])])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaHashMap input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaHashMap(new Map([["foo", "bar"]]))]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new Map([["foo", "bar"]])]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaHashMap(new Map([["foo", "bar"]]))])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new Map([["foo", "bar"]])])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with Date input, should unmarshall correctly", () => {
    const baseDate = new Date();

    const arrayInput = {
      input: new JavaArrayList([new Date(baseDate)]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new Date(baseDate)]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new Date(baseDate)])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new Date(baseDate)])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaDate input, should unmarshall correctly", () => {
    const baseDate = new Date();

    const arrayInput = {
      input: new JavaArrayList([new JavaDate(new Date(baseDate))]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new Date(baseDate)]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaDate(new Date(baseDate))])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new Date(baseDate)])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with Boolean input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([false]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [false]
    };

    const setInput = {
      input: new JavaHashSet(new Set([false])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([false])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaBoolean input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaBoolean(false)]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [false]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaBoolean(false)])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([false])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with String input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList(["foo"]),
      marshaller: new JavaArrayListMarshaller(),
      expected: ["foo"]
    };

    const setInput = {
      input: new JavaHashSet(new Set(["foo"])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set(["foo"])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaString input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaString("foo")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: ["foo"]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaString("foo")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set(["foo"])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaOptional input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaOptional("foo")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaOptional("foo")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaOptional("foo")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaOptional("foo")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaBigDecimal input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaBigDecimal("1.1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaBigDecimal("1.1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaBigDecimal("1.1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaBigDecimal("1.1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaBigInteger should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaBigInteger("1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaBigInteger("1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaBigInteger("1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaBigInteger("1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaLong input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaLong("1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaLong("1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaLong("1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaLong("1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaByte input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaByte("1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaByte("1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaByte("1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaByte("1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaDouble input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaDouble("1.1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaDouble("1.1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaDouble("1.1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaDouble("1.1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaFloat input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaFloat("1.1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaFloat("1.1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaFloat("1.1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaFloat("1.1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaInteger input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaInteger("1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaInteger("1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaInteger("1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaInteger("1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with JavaShort input, should unmarshall correctly", () => {
    const arrayInput = {
      input: new JavaArrayList([new JavaShort("1")]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new JavaShort("1")]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new JavaShort("1")])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new JavaShort("1")])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(new Map()));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with custom object optional, should unmarshall correctly", () => {
    const oracle = new Map([["com.portable.my", () => new MyPortable({} as any)]]);

    const arrayInput = {
      input: new JavaArrayList([new MyPortable({ foo: "bar", bar: "foo" })]),
      marshaller: new JavaArrayListMarshaller(),
      expected: [new MyPortable({ foo: "bar", bar: "foo" })]
    };

    const setInput = {
      input: new JavaHashSet(new Set([new MyPortable({ foo: "bar", bar: "foo" })])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set([new MyPortable({ foo: "bar", bar: "foo" })])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(oracle));

      expect(output).toEqual(scenario.expected);
    });
  });

  test("with root null object, should unmarshall to undefined", () => {
    [new JavaArrayListMarshaller(), new JavaHashSetMarshaller()].forEach(marshaller => {
      const output = marshaller.unmarshall(null as any, new UnmarshallingContext(new Map()));

      expect(output).toBeUndefined();
    });
  });

  test("with root undefined object, should unmarshall to undefined", () => {
    [new JavaArrayListMarshaller(), new JavaHashSetMarshaller()].forEach(marshaller => {
      const output = marshaller.unmarshall(undefined as any, new UnmarshallingContext(new Map()));

      expect(output).toBeUndefined();
    });
  });

  test("with undefined value inside ErraiObject, should throw error", () => {
    const arrayInput = {
      input: new ValueBasedErraiObject(JavaType.ARRAY_LIST, undefined).asErraiObject(),
      marshaller: new JavaArrayListMarshaller()
    };

    const setInput = {
      input: new ValueBasedErraiObject(JavaType.HASH_SET, undefined).asErraiObject(),
      marshaller: new JavaHashSetMarshaller()
    };

    [arrayInput, setInput].forEach(scenario => {
      expect(() => scenario.marshaller.unmarshall(scenario.input, new UnmarshallingContext(new Map()))).toThrowError();
    });
  });

  test("with null value inside ErraiObject, should throw error", () => {
    const arrayInput = {
      input: new ValueBasedErraiObject(JavaType.ARRAY_LIST, null).asErraiObject(),
      marshaller: new JavaArrayListMarshaller()
    };

    const setInput = {
      input: new ValueBasedErraiObject(JavaType.HASH_SET, null).asErraiObject(),
      marshaller: new JavaHashSetMarshaller()
    };

    [arrayInput, setInput].forEach(scenario => {
      expect(() => scenario.marshaller.unmarshall(scenario.input, new UnmarshallingContext(new Map()))).toThrowError();
    });
  });

  test("with non array value inside ErraiObject, should throw error", () => {
    const arrayInput = {
      input: new ValueBasedErraiObject(JavaType.ARRAY_LIST, false).asErraiObject(),
      marshaller: new JavaArrayListMarshaller()
    };

    const setInput = {
      input: new ValueBasedErraiObject(JavaType.HASH_SET, false).asErraiObject(),
      marshaller: new JavaHashSetMarshaller()
    };

    [arrayInput, setInput].forEach(scenario => {
      expect(() => scenario.marshaller.unmarshall(scenario.input, new UnmarshallingContext(new Map()))).toThrowError();
    });
  });

  test("with custom pojo containing repeated elements, should reuse cached objects and don't recreate data", () => {
    const oracle = new Map([["com.app.my.Node", () => new Node({} as any)]]);

    const repeatedValue = new Node({ data: "foo1" });
    const portableArray = [repeatedValue, new Node({ data: "foo2", right: repeatedValue })];

    const arrayInput = {
      input: new JavaArrayList(portableArray),
      marshaller: new JavaArrayListMarshaller(),
      expected: portableArray
    };

    const setInput = {
      input: new JavaHashSet(new Set(portableArray)),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set(portableArray)
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const output = scenario.marshaller.unmarshall(input, new UnmarshallingContext(oracle));

      // compares value equality
      expect(output).toEqual(scenario.expected);

      // check if the repeated object was reused from cache
      const asArray = Array.from(scenario.expected);

      const repeatedNode = asArray[0];
      const uniqueNode = asArray[1];

      expect(repeatedNode).toBe(uniqueNode.right!);
    });
  });

  test("with repeated collection unmarshalling, should reuse cached collection and don't recreate it", () => {
    const arrayInput = {
      input: new JavaArrayList(["list"]),
      marshaller: new JavaArrayListMarshaller(),
      expected: ["list"]
    };

    const setInput = {
      input: new JavaHashSet(new Set(["set"])),
      marshaller: new JavaHashSetMarshaller(),
      expected: new Set(["set"])
    };

    [arrayInput, setInput].forEach(scenario => {
      const input = (scenario.marshaller as any).marshall(scenario.input as any, new MarshallingContext());

      const context = new UnmarshallingContext(new Map());

      const output = scenario.marshaller.unmarshall(input, context);
      const repeatedOutput = scenario.marshaller.unmarshall(input, context);

      expect(output).toEqual(scenario.expected);
      expect(output).toBe(repeatedOutput!);
    });
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

class Node implements Portable<Node> {
  private readonly _fqcn = "com.app.my.Node";

  public readonly data: any;
  public readonly left?: Node;
  public readonly right?: Node;

  constructor(self: { data: any; left?: Node; right?: Node }) {
    Object.assign(this, self);
  }
}
