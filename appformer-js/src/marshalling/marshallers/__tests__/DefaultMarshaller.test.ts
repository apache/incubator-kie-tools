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
  JavaEnum,
  JavaFloat,
  JavaHashMap,
  JavaHashSet,
  JavaInteger,
  JavaLong,
  JavaOptional,
  JavaShort,
  JavaString
} from "../../../java-wrappers";
import { DefaultMarshaller } from "../DefaultMarshaller";
import { MarshallingContext } from "../../MarshallingContext";
import { ErraiObjectConstants } from "../../model/ErraiObjectConstants";
import { MarshallerProvider } from "../../MarshallerProvider";
import { Portable } from "../../Portable";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
import { JavaType } from "../../../java-wrappers/JavaType";
import { NumberUtils } from "../../../util/NumberUtils";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { EnumStringValueBasedErraiObject } from "../../model/EnumStringValueBasedErraiObject";

beforeEach(() => {
  MarshallerProvider.initialize();
});

describe("marshall", () => {
  const objectId = ErraiObjectConstants.OBJECT_ID;
  const encodedType = ErraiObjectConstants.ENCODED_TYPE;
  const value = ErraiObjectConstants.VALUE;
  const enumStringValue = ErraiObjectConstants.ENUM_STRING_VALUE;

  describe("pojo marshalling", () => {
    test("custom pojo, should return serialize it normally", () => {
      const input = new User({
        name: "my name",
        sendSpam: false,
        age: new JavaInteger("10"),
        address: new Address({
          line1: "address line 1",
          type: AddressType.WORK
        }),
        bestFriend: new User({
          name: "my name 2",
          sendSpam: true,
          address: new Address({
            line1: "address 2 line 1"
          })
        })
      });

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: "com.app.my.Pojo",
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        name: "my name",
        age: 10,
        sendSpam: false,
        address: {
          [encodedType]: "com.app.my.Address",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          line1: "address line 1",
          type: new EnumStringValueBasedErraiObject("com.app.my.AddressType", AddressType.WORK.name).asErraiObject()
        },
        bestFriend: {
          [encodedType]: "com.app.my.Pojo",
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          name: "my name 2",
          sendSpam: true,
          age: null,
          address: {
            [encodedType]: "com.app.my.Address",
            [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
            line1: "address 2 line 1",
            type: null
          },
          bestFriend: null
        }
      });
    });

    test("custom pojo with function, should serialize it normally and ignore the function", () => {
      const input = {
        _fqcn: "com.app.my.Pojo",
        foo: "bar",
        doSomething: () => {
          return "hey!";
        }
      };

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: "com.app.my.Pojo",
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        foo: "bar"
      });
    });

    test("custom pojo without fqcn, should throw error", () => {
      const input = {
        foo: "bar"
      };

      const context = new MarshallingContext();
      const marshaller = new DefaultMarshaller();

      expect(() => marshaller.marshall(input, context)).toThrowError();
    });

    test("custom pojo with a pojo without fqcn as property, should throw error", () => {
      const input = {
        _fqcn: "com.app.my.Pojo",
        name: "my name",
        childPojo: {
          foo: "bar"
        }
      };

      const context = new MarshallingContext();
      const marshaller = new DefaultMarshaller();

      expect(() => marshaller.marshall(input, context)).toThrowError();
    });

    test("custom pojo with java types, should serialize it normally", () => {
      const date = new Date();

      const input = new JavaTypesPojo({
        bigDecimal: new JavaBigDecimal("1.1"),
        bigInteger: new JavaBigInteger("2"),
        boolean: false,
        byte: new JavaByte("3"),
        double: new JavaDouble("1.2"),
        float: new JavaFloat("1.3"),
        integer: new JavaInteger("4"),
        long: new JavaLong("5"),
        short: new JavaShort("6"),
        string: "str",
        date: new Date(date),
        optional: new JavaOptional<string>("optstr")
      });

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: "com.app.my.Pojo",
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        bigDecimal: {
          [encodedType]: JavaType.BIG_DECIMAL,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: "1.1"
        },
        bigInteger: {
          [encodedType]: JavaType.BIG_INTEGER,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: "2"
        },
        boolean: false,
        byte: 3,
        double: 1.2,
        float: 1.3,
        integer: 4,
        long: new NumValBasedErraiObject(JavaType.LONG, "5").asErraiObject(),
        short: 6,
        string: "str",
        date: new ValueBasedErraiObject(JavaType.DATE, `${date.getTime()}`).asErraiObject(),
        optional: new ValueBasedErraiObject(JavaType.OPTIONAL, "optstr").asErraiObject()
      });
    });

    test("custom pojo with collection type, should serialize it normally", () => {
      const input = {
        _fqcn: "com.app.my.Pojo",
        list: ["1", "2", "3"],
        set: new Set(["3", "2", "1"]),
        map: new Map([["k1", "v1"], ["k2", "v2"]])
      };

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: "com.app.my.Pojo",
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        list: {
          [encodedType]: JavaType.ARRAY_LIST,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: ["1", "2", "3"]
        },
        set: {
          [encodedType]: JavaType.HASH_SET,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: ["3", "2", "1"]
        },
        map: {
          [encodedType]: JavaType.HASH_MAP,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: {
            k1: "v1",
            k2: "v2"
          }
        }
      });
    });

    test("custom pojo with enum type, should serialize it normally", () => {
      const input = {
        _fqcn: "com.app.my.Pojo",
        str: "foo",
        enum: AddressType.HOME
      };

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: "com.app.my.Pojo",
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        str: "foo",
        enum: new EnumStringValueBasedErraiObject("com.app.my.AddressType", AddressType.HOME.name).asErraiObject()
      });
    });
  });

  describe("object caching", () => {
    test("custom pojo with repeated pojo objects, should cache the object and don't repeat data", () => {
      // === scenario

      // repeatedNode appears two times in the hierarchy, all other nodes are unique
      const repeatedNode = new Node({ data: "repeatedNode" });
      const input = new Node({
        data: "root",
        right: new Node({ data: "rightNode", left: repeatedNode, right: new Node({ data: "rightLeaf" }) }),
        left: repeatedNode
      });

      // === test

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      // === assertion
      // expects all nodes to contain their data and a unique objectId, except for the deepest left node.
      // the deepest left node should contain only its encodedType and objectId, which needs to be the same as the root's left node

      const rootObjId = output![objectId];
      expect(output![encodedType]).toEqual("com.app.my.Node");
      expect((output as any).data).toEqual("root");

      const firstLeftLeaf = (output as any).left;
      const firstLeftLeafObjId = firstLeftLeaf[objectId];
      expect(firstLeftLeaf.data).toEqual("repeatedNode");

      const rightNodeOut = (output as any).right;
      const rightNodeObjId = rightNodeOut[objectId];
      expect(rightNodeOut.data).toEqual("rightNode");

      const repeatedLeftLeaf = (rightNodeOut as any).left;
      const repeatedLeftLeafObjId = repeatedLeftLeaf[objectId];
      expect(repeatedLeftLeaf.data).toBeUndefined();

      const rightLeafOut = (rightNodeOut as any).right;
      const rightLeafObjId = rightLeafOut[objectId];
      expect(rightLeafOut.data).toEqual("rightLeaf");

      expect(firstLeftLeafObjId).toEqual(repeatedLeftLeafObjId); // reuse same id

      const allObjIds = [rootObjId, firstLeftLeafObjId, rightNodeObjId, repeatedLeftLeafObjId, rightLeafObjId];
      expect(allObjIds.forEach(id => expect(id).toMatch(NumberUtils.nonNegativeIntegerRegex)));

      // all ids unique (excluding the reused one)
      const uniqueObjIds = new Set(allObjIds);
      expect(uniqueObjIds).toStrictEqual(new Set([rootObjId, firstLeftLeafObjId, rightNodeObjId, rightLeafObjId]));
    });

    test("custom pojo with repeated JavaBigDecimal objects, should not cache it and not reuse data", () => {
      const repeatedValue = new JavaBigDecimal("1.1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaBigDecimal("1.2") }),
        right: new Node({ data: repeatedValue })
      });

      // === test
      const context = new MarshallingContext();
      const output = new DefaultMarshaller().marshall(input, context);

      // === assertions

      const rootObjId = output![objectId];
      const rootDataObjId = (output as any).data[objectId];
      expect((output as any).data[value]).toEqual("1.1");

      const leftObjId = (output as any).left[objectId];
      const leftDataObjId = (output as any).left.data[objectId];
      expect((output as any).left.data[value]).toEqual("1.2");

      const rightObjId = (output as any).right[objectId];
      const rightDataObjId = (output as any).right.data[objectId];
      expect((output as any).right.data[value]).toEqual("1.1");

      const allObjectIds = [rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId, rightDataObjId];

      allObjectIds.forEach(id => expect(id).toBeDefined());

      // create new object ids even for same obj references
      expect(new Set(allObjectIds)).toStrictEqual(new Set(allObjectIds));

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaBigInteger objects, should not cache it", () => {
      const repeatedValue = new JavaBigInteger("1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaBigInteger("2") }),
        right: new Node({ data: repeatedValue })
      });

      // === test
      const context = new MarshallingContext();
      const output = new DefaultMarshaller().marshall(input, context);

      // === assertions

      const rootObjId = output![objectId];
      const rootDataObjId = (output as any).data[objectId];
      expect((output as any).data[value]).toEqual("1");

      const leftObjId = (output as any).left[objectId];
      const leftDataObjId = (output as any).left.data[objectId];
      expect((output as any).left.data[value]).toEqual("2");

      const rightObjId = (output as any).right[objectId];
      const rightDataObjId = (output as any).right.data[objectId];
      expect((output as any).right.data[value]).toEqual("1");

      const allObjectIds = [rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId, rightDataObjId];

      allObjectIds.forEach(id => expect(id).toBeDefined());

      // create new object ids even for same obj references
      expect(new Set(allObjectIds)).toStrictEqual(new Set(allObjectIds));

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaBoolean objects, should not cache it", () => {
      const repeatedValue = new JavaBoolean(false);

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaBoolean(true) }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because Boolean types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaByte objects, should not cache it", () => {
      const repeatedValue = new JavaByte("1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaByte("2") }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because Byte types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated array objects, should cache the object and don't repeat data", () => {
      const repeatedValue = ["a", "b", "c"];

      const arrayInput = new Node({
        data: repeatedValue,
        left: new Node({ data: ["d", "e"] }),
        right: new Node({ data: repeatedValue })
      });

      const javaArrayListInput = new Node({
        data: new JavaArrayList(repeatedValue),
        left: new Node({ data: new JavaArrayList(["d", "e"]) }),
        right: new Node({ data: new JavaArrayList(repeatedValue) })
      });

      [arrayInput, javaArrayListInput].forEach(input => {
        const context = new MarshallingContext();
        const output = new DefaultMarshaller().marshall(input, context);

        // === assertions

        const rootObjId = output![objectId];
        const rootDataObjId = (output as any).data[objectId];
        expect((output as any).data).toStrictEqual({
          [encodedType]: JavaType.ARRAY_LIST,
          [objectId]: expect.anything(),
          [value]: ["a", "b", "c"]
        });

        const leftObjId = (output as any).left[objectId];
        const leftDataObjId = (output as any).left.data[objectId];
        expect((output as any).left.data).toStrictEqual({
          [encodedType]: JavaType.ARRAY_LIST,
          [objectId]: expect.anything(),
          [value]: ["d", "e"]
        });

        const rightObjId = (output as any).right[objectId];
        const rightDataObjId = (output as any).right.data[objectId];
        expect((output as any).right.data).toStrictEqual({
          [encodedType]: JavaType.ARRAY_LIST,
          [objectId]: expect.anything()
          // missing value since it is cached
        });

        const allObjectIds = [rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId, rightDataObjId];

        allObjectIds.forEach(id => expect(id).toBeDefined());

        // all ids are unique except for the right data id, that was reused
        expect(new Set(allObjectIds)).toStrictEqual(
          new Set([rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId])
        );

        // do not cache repeated object
        const cached = context.getCached(repeatedValue);
        expect(ValueBasedErraiObject.from(cached!)).toStrictEqual(
          new ValueBasedErraiObject(JavaType.ARRAY_LIST, undefined, rootDataObjId)
        );
      });
    });

    test("custom pojo with repeated set objects, should cache the object and don't repeat data", () => {
      const repeatedValue = new Set(["a", "b", "c"]);

      const setInput = new Node({
        data: repeatedValue,
        left: new Node({ data: new Set(["d", "e"]) }),
        right: new Node({ data: repeatedValue })
      });

      const javaHashSetInput = new Node({
        data: new JavaHashSet(repeatedValue),
        left: new Node({ data: new JavaHashSet(new Set(["d", "e"])) }),
        right: new Node({ data: new JavaHashSet(repeatedValue) })
      });

      [setInput, javaHashSetInput].forEach(input => {
        const context = new MarshallingContext();
        const output = new DefaultMarshaller().marshall(input, context);

        // === assertions

        const rootObjId = output![objectId];
        const rootDataObjId = (output as any).data[objectId];
        expect((output as any).data).toStrictEqual({
          [encodedType]: JavaType.HASH_SET,
          [objectId]: expect.anything(),
          [value]: ["a", "b", "c"]
        });

        const leftObjId = (output as any).left[objectId];
        const leftDataObjId = (output as any).left.data[objectId];
        expect((output as any).left.data).toStrictEqual({
          [encodedType]: JavaType.HASH_SET,
          [objectId]: expect.anything(),
          [value]: ["d", "e"]
        });

        const rightObjId = (output as any).right[objectId];
        const rightDataObjId = (output as any).right.data[objectId];
        expect((output as any).right.data).toStrictEqual({
          [encodedType]: JavaType.HASH_SET,
          [objectId]: expect.anything()
          // missing value since it is cached
        });

        const allObjectIds = [rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId, rightDataObjId];

        allObjectIds.forEach(id => expect(id).toBeDefined());

        // all ids are unique except for the right data id, that was reused
        expect(new Set(allObjectIds)).toStrictEqual(
          new Set([rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId])
        );

        // do not cache repeated object
        const cached = context.getCached(repeatedValue);
        expect(ValueBasedErraiObject.from(cached!)).toStrictEqual(
          new ValueBasedErraiObject(JavaType.HASH_SET, undefined, rootDataObjId)
        );
      });
    });

    test("custom pojo with repeated map objects, should cache the object and don't repeat data", () => {
      const repeatedMap = new Map([["k1", "v1"]]);

      const mapInput = new Node({
        data: repeatedMap,
        left: new Node({ data: new Map([["k2", "v2"]]) }),
        right: new Node({ data: repeatedMap })
      });

      const javaHashMapInput = new Node({
        data: new JavaHashMap(repeatedMap),
        left: new Node({ data: new JavaHashMap(new Map([["k2", "v2"]])) }),
        right: new Node({ data: new JavaHashMap(repeatedMap) })
      });

      [mapInput, javaHashMapInput].forEach(input => {
        const context = new MarshallingContext();
        const output = new DefaultMarshaller().marshall(input, context);

        // === assertions

        const rootObjId = output![objectId];
        const rootDataObjId = (output as any).data[objectId];
        expect((output as any).data).toStrictEqual({
          [encodedType]: JavaType.HASH_MAP,
          [objectId]: expect.anything(),
          [value]: {
            k1: "v1"
          }
        });

        const leftObjId = (output as any).left[objectId];
        const leftDataObjId = (output as any).left.data[objectId];
        expect((output as any).left.data).toStrictEqual({
          [encodedType]: JavaType.HASH_MAP,
          [objectId]: expect.anything(),
          [value]: {
            k2: "v2"
          }
        });

        const rightObjId = (output as any).right[objectId];
        const rightDataObjId = (output as any).right.data[objectId];
        expect((output as any).right.data).toStrictEqual({
          [encodedType]: JavaType.HASH_MAP,
          [objectId]: expect.anything()
          // missing value since it is cached
        });

        const allObjectIds = [rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId, rightDataObjId];

        allObjectIds.forEach(id => expect(id).toBeDefined());

        // all ids are unique except for the right data id, that was reused
        expect(new Set(allObjectIds)).toStrictEqual(
          new Set([rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId])
        );

        // do not cache repeated object's data
        const cached = context.getCached(repeatedMap);
        expect(ValueBasedErraiObject.from(cached!)).toStrictEqual(
          new ValueBasedErraiObject(JavaType.HASH_MAP, undefined, rootDataObjId)
        );
      });
    });

    test("custom pojo with repeated JavaDouble objects, should not cache it", () => {
      const repeatedValue = new JavaDouble("1.1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaDouble("1.2") }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because Double types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaFloat objects, should not cache it", () => {
      const repeatedValue = new JavaFloat("1.1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaFloat("1.2") }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because Float types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaInteger objects, should not cache it", () => {
      const repeatedValue = new JavaInteger("1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaInteger("2") }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because Integer types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaLong objects, should not cache it", () => {
      const repeatedValue = new JavaLong("1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaLong("2") }),
        right: new Node({ data: repeatedValue })
      });

      // === test
      const context = new MarshallingContext();
      const output = new DefaultMarshaller().marshall(input, context);

      // === assertions

      const rootObjId = output![objectId];
      const rootData = NumValBasedErraiObject.from((output as any).data);
      expect(rootData.numVal).toEqual("1");

      const leftObj = NumValBasedErraiObject.from((output as any).left);
      const leftData = NumValBasedErraiObject.from((output as any).left.data);
      expect(leftData.numVal).toEqual("2");

      const rightObj = NumValBasedErraiObject.from((output as any).right);
      const rightData = NumValBasedErraiObject.from((output as any).right.data);
      expect(rightData.numVal).toEqual("1");

      const allObjectIds = [rootObjId, rootData.objId, leftObj.objId, leftData.objId, rightObj.objId, rightData.objId];

      allObjectIds.forEach(id => expect(id).toBeDefined());

      // create new object ids even for same obj references
      expect(new Set(allObjectIds)).toStrictEqual(new Set(allObjectIds));

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaShort objects, should not cache it", () => {
      const repeatedValue = new JavaShort("1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaShort("2") }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because Short types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaString objects, should not cache it", () => {
      const repeatedValue = new JavaString("str1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaString("str2") }),
        right: new Node({ data: repeatedValue })
      });

      const context = new MarshallingContext();
      new DefaultMarshaller().marshall(input, context);

      // in this test we're not interested in the output structure, because String types are not wrapped into an
      // ErraiObject, so, it doesn't even have an objectId assigned.

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaOptional objects, should not cache it", () => {
      const repeatedValue = new JavaOptional<string>("str1");

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: new JavaOptional<string>("str2") }),
        right: new Node({ data: repeatedValue })
      });

      // === test
      const context = new MarshallingContext();
      const output = new DefaultMarshaller().marshall(input, context);

      // === assertions

      const rootObjId = output![objectId];
      const rootDataObjId = (output as any).data[objectId];
      expect((output as any).data[value]).toStrictEqual("str1");

      const leftObjId = (output as any).left[objectId];
      const leftDataObjId = (output as any).left.data[objectId];
      expect((output as any).left.data[value]).toEqual("str2");

      const rightObjId = (output as any).right[objectId];
      const rightDataObjId = (output as any).right.data[objectId];
      expect((output as any).right.data[value]).toEqual("str1");

      const allObjectIds = [rootObjId, rootDataObjId, leftObjId, leftDataObjId, rightObjId, rightDataObjId];

      allObjectIds.forEach(id => expect(id).toBeDefined());

      // optional objects always use the same object id (its value doesn't matter)
      expect(new Set(allObjectIds)).toStrictEqual(new Set([rootObjId, rootDataObjId, leftObjId, rightObjId]));
      expect(rootDataObjId).toEqual(leftDataObjId);
      expect(rootDataObjId).toEqual(rightDataObjId);

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });

    test("custom pojo with repeated JavaEnum objects, should not cache it", () => {
      const repeatedValue = AddressType.WORK;

      const input = new Node({
        data: repeatedValue,
        left: new Node({ data: AddressType.HOME }),
        right: new Node({ data: repeatedValue })
      });

      // === test
      const context = new MarshallingContext();
      const output = new DefaultMarshaller().marshall(input, context);

      // === assertions

      const rootObjId = output![objectId];
      const rootDataObjId = (output as any).data[objectId];
      expect((output as any).data[enumStringValue]).toStrictEqual(AddressType.WORK.name);

      const leftObjId = (output as any).left[objectId];
      const leftDataObjId = (output as any).left.data[objectId];
      expect((output as any).left.data[enumStringValue]).toEqual(AddressType.HOME.name);

      const rightObjId = (output as any).right[objectId];
      const rightDataObjId = (output as any).right.data[objectId];
      expect((output as any).right.data[enumStringValue]).toEqual(AddressType.WORK.name);

      // every Node object has an unique id
      expect(new Set([rootObjId, leftObjId, rightObjId])).toStrictEqual(new Set([rootObjId, leftObjId, rightObjId]));

      // every enum field doesn't have an object id defined
      [rootDataObjId, rightDataObjId, leftDataObjId].forEach(id => expect(id).toBeUndefined());

      // do not cache repeated object
      expect(context.getCached(repeatedValue)).toBeUndefined();
    });
  });

  describe("non-pojo root types", () => {
    test("root null object, should serialize to null", () => {
      const input = null as any;

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toBeNull();
    });

    test("root undefined object, should serialize to null", () => {
      const input = undefined as any;

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toBeNull();
    });

    test("root JavaBigDecimal object, should serialize it normally", () => {
      const input = new JavaBigDecimal("1.2");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: JavaType.BIG_DECIMAL,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: "1.2"
      });
    });

    test("root JavaBigInteger object, should serialize it normally", () => {
      const input = new JavaBigInteger("1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual({
        [encodedType]: JavaType.BIG_INTEGER,
        [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
        [value]: "1"
      });
    });

    test("root JavaByte object, should serialize it to byte raw value", () => {
      const input = new JavaByte("1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toEqual(1);
    });

    test("root JavaDouble object, should serialize it to double raw value", () => {
      const input = new JavaDouble("1.1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toEqual(1.1);
    });

    test("root JavaFloat object, should serialize it to float raw value", () => {
      const input = new JavaFloat("1.1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toEqual(1.1);
    });

    test("root JavaInteger object, should serialize it to integer raw value", () => {
      const input = new JavaInteger("1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toEqual(1);
    });

    test("root JavaLong object, should serialize it normally", () => {
      const input = new JavaLong("1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual(new NumValBasedErraiObject(JavaType.LONG, "1").asErraiObject());
    });

    test("root JavaShort object, should serialize it normally to short raw value", () => {
      const input = new JavaShort("1");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toEqual(1);
    });

    test("root JavaOptional object, should serialize it normally", () => {
      const input = new JavaOptional<string>("str");

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual(new ValueBasedErraiObject(JavaType.OPTIONAL, "str").asErraiObject());
    });

    test("root JavaEnum object, should serialize it normally", () => {
      const input = AddressType.WORK;

      const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

      expect(output).toStrictEqual(
        new EnumStringValueBasedErraiObject(AddressType.__fqcn(), AddressType.WORK.name).asErraiObject()
      );
    });

    test("root string object, should serialize it to string raw value", () => {
      const stringInput = "str";
      const javaStringInput = new JavaString("str");

      [stringInput, javaStringInput].forEach(input => {
        const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

        expect(output).toEqual("str");
      });
    });

    test("root date object, should serialize it normally", () => {
      const date = new Date();
      const javaDate = new JavaDate(date);

      [date, javaDate].forEach(input => {
        const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

        expect(output).toStrictEqual(new ValueBasedErraiObject(JavaType.DATE, `${date.getTime()}`).asErraiObject());
      });
    });

    test("root boolean object, should serialize it to boolean raw value", () => {
      const booleanInput = false;
      const javaBooleanInput = new JavaBoolean(false);

      [booleanInput, javaBooleanInput].forEach(input => {
        const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

        expect(output).toEqual(false);
      });
    });

    test("root number object, should throw error", () => {
      const input = 125.1;

      const marshaller = new DefaultMarshaller();
      const ctx = new MarshallingContext();

      expect(() => marshaller.marshall(input, ctx)).toThrowError();
    });

    test("root array object, should serialize it normally", () => {
      const arrayInput = ["1", "2", "3"];
      const javaArrayListInput = new JavaArrayList(["1", "2", "3"]);

      [arrayInput, javaArrayListInput].forEach(input => {
        const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

        expect(output).toStrictEqual({
          [encodedType]: JavaType.ARRAY_LIST,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: ["1", "2", "3"]
        });
      });
    });

    test("root set object, should serialize it normally", () => {
      const setInput = new Set(["1", "2", "3"]);
      const javaHashSetInput = new JavaHashSet(new Set(["1", "2", "3"]));

      [setInput, javaHashSetInput].forEach(input => {
        const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

        expect(output).toStrictEqual({
          [encodedType]: JavaType.HASH_SET,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: ["1", "2", "3"]
        });
      });
    });

    test("root map object, should serialize it normally", () => {
      const mapInput = new Map([["k1", "v1"], ["k2", "v2"]]);
      const javaHashMapInput = new JavaHashMap(new Map([["k1", "v1"], ["k2", "v2"]]));

      [mapInput, javaHashMapInput].forEach(input => {
        const output = new DefaultMarshaller().marshall(input, new MarshallingContext());

        expect(output).toStrictEqual({
          [encodedType]: JavaType.HASH_MAP,
          [objectId]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
          [value]: {
            k1: "v1",
            k2: "v2"
          }
        });
      });
    });
  });

  class Node implements Portable<Node> {
    private readonly _fqcn = "com.app.my.Node";

    public readonly data?: any = undefined;
    public readonly left?: Node = undefined;
    public readonly right?: Node = undefined;

    constructor(self: { data?: any; left?: Node; right?: Node }) {
      Object.assign(this, self);
    }
  }
});

describe("unmarshall", () => {
  describe("non-pojo root types", () => {
    test("root null object, should unmarshall to undefined", () => {
      const marshaller = new DefaultMarshaller();

      const input = null as any;
      const marshalledInput = marshaller.marshall(input, new MarshallingContext()) as any;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toBeUndefined();
    });

    test("root undefined object, should unmarshall to null", () => {
      const marshaller = new DefaultMarshaller();

      const input = undefined as any;
      const marshalledInput = marshaller.marshall(input, new MarshallingContext()) as any;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toBeUndefined();
    });

    test("root JavaBigDecimal object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = new JavaBigDecimal("1.1");
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaBigDecimal("1.1"));
    });

    test("root JavaBigInteger object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = new JavaBigInteger("1");
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaBigInteger("1"));
    });

    test("root JavaByte object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1;
      const marshalledInput = new NumValBasedErraiObject(JavaType.BYTE, input).asErraiObject();

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaByte("1"));
    });

    test("root JavaDouble object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1.1;
      const marshalledInput = new NumValBasedErraiObject(JavaType.DOUBLE, input).asErraiObject();

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaDouble("1.1"));
    });

    test("root JavaFloat object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1.1;
      const marshalledInput = new NumValBasedErraiObject(JavaType.FLOAT, input).asErraiObject();

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaFloat("1.1"));
    });

    test("root JavaInteger object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1;
      const marshalledInput = new NumValBasedErraiObject(JavaType.INTEGER, input).asErraiObject();

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaInteger("1"));
    });

    test("root JavaLong object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = new JavaLong("1");
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaLong("1"));
    });

    test("root JavaShort object, should unmarshall it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1;
      const marshalledInput = new NumValBasedErraiObject(JavaType.SHORT, input).asErraiObject();

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaShort("1"));
    });

    test("root JavaOptional object, should serialize it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = new JavaOptional("1");
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

      expect(output).toEqual(new JavaOptional("1"));
    });

    test("root JavaEnum object, should serialize it normally", () => {
      const marshaller = new DefaultMarshaller();

      const input = AddressType.WORK;
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const factory = new Map([
        [
          "com.app.my.AddressType",
          ((name: string) => {
            switch (name) {
              case "HOME":
                return AddressType.HOME;
              case "WORK":
                return AddressType.WORK;
              default:
                throw new Error(`Unknown value ${name} for enum AddressType!`);
            }
          }) as any
        ]
      ]);

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(factory));

      expect(output).toStrictEqual(AddressType.WORK);
    });

    test("root string object, should unmarshall it to native string", () => {
      const marshaller = new DefaultMarshaller();

      const stringInput = "foo";
      const javaStringInput = new JavaString("foo");

      [stringInput, javaStringInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

        expect(output).toEqual("foo");
      });
    });

    test("root date object, should unmarshall it to native date", () => {
      const marshaller = new DefaultMarshaller();

      const baseDate = new Date();

      const dateInput = new Date(baseDate);
      const javaDateInput = new JavaDate(new Date(baseDate));

      [dateInput, javaDateInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

        expect(output).toEqual(baseDate);
      });
    });

    test("root boolean object, should unmarshall it to native boolean", () => {
      const marshaller = new DefaultMarshaller();

      const booleanInput = false;
      const javaBooleanInput = new JavaBoolean(false);

      [booleanInput, javaBooleanInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

        expect(output).toEqual(false);
      });
    });

    test("root array object, should unmarshall it to native array", () => {
      const marshaller = new DefaultMarshaller();

      const arrayInput = ["foo"];
      const javaArrayInput = new JavaArrayList(["foo"]);

      [arrayInput, javaArrayInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

        expect(output).toEqual(["foo"]);
      });
    });

    test("root set object, should unmarshall it to native set", () => {
      const marshaller = new DefaultMarshaller();

      const setInput = new Set(["foo"]);
      const javaSetInput = new JavaHashSet(new Set(["foo"]));

      [setInput, javaSetInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

        expect(output).toEqual(new Set(["foo"]));
      });
    });

    test("root map object, should unmarshall it native map", () => {
      const marshaller = new DefaultMarshaller();

      const mapInput = new Map([["foo", "bar"]]);
      const javaMapInput = new JavaHashMap(new Map([["foo", "bar"]]));

      [mapInput, javaMapInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(new Map()));

        expect(output).toEqual(new Map([["foo", "bar"]]));
      });
    });

    test("root integer number object, should throw error", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1;

      expect(() => marshaller.unmarshall(input as any, new UnmarshallingContext(new Map()))).toThrowError();
    });

    test("root float number object, should throw error", () => {
      const marshaller = new DefaultMarshaller();

      const input = 1.1;

      expect(() => marshaller.unmarshall(input as any, new UnmarshallingContext(new Map()))).toThrowError();
    });
  });

  describe("pojo root types", () => {
    test("with custom pojo, should unmarshall correctly", () => {
      const oracle = new Map([
        ["com.app.my.Pojo", () => new User({ age: new JavaInteger("0") }) as any],
        ["com.app.my.Address", () => new Address({} as any) as any],
        [
          "com.app.my.AddressType",
          ((name: string) => {
            switch (name) {
              case "HOME":
                return AddressType.HOME;
              case "WORK":
                return AddressType.WORK;
              default:
                throw new Error(`Unknown value ${name} for enum AddressType!`);
            }
          }) as any
        ]
      ]);

      const marshaller = new DefaultMarshaller();

      const input = new User({
        name: "my name",
        sendSpam: false,
        age: new JavaInteger("10"),
        address: new Address({
          line1: "address line 1",
          type: AddressType.HOME
        }),
        bestFriend: new User({
          name: "my name 2",
          sendSpam: true,
          address: new Address({
            line1: "address 2 line 1"
          })
        })
      });

      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(oracle));

      expect(output).toEqual(input);
    });

    test("with custom pojo with function, should unmarshall to a correct object containing the function", () => {
      const oracle = new Map([["com.app.my.PojoWithFunction", () => new PojoWithFunction({})]]);
      const marshaller = new DefaultMarshaller();

      const input = new PojoWithFunction({ foo: "bar" });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(oracle)) as PojoWithFunction;

      expect(output).toEqual(input);

      // generates a full functional object of the correct type
      expect(output.whatToSay()).toEqual("Hello, bar!");
    });

    test("with custom pojo with java types, should unmarshall normally", () => {
      const marshaller = new DefaultMarshaller();
      const oracle = new Map([
        [
          "com.app.my.Pojo",
          () =>
            new JavaTypesPojo({
              bigDecimal: new JavaBigDecimal("0"),
              bigInteger: new JavaBigInteger("0"),
              byte: new JavaByte("0"),
              double: new JavaDouble("0"),
              float: new JavaFloat("0"),
              integer: new JavaInteger("0"),
              short: new JavaShort("0")
            })
        ]
      ]);

      const input = new JavaTypesPojo({
        bigDecimal: new JavaBigDecimal("1.1"),
        bigInteger: new JavaBigInteger("2"),
        boolean: false,
        byte: new JavaByte("3"),
        double: new JavaDouble("1.2"),
        float: new JavaFloat("1.3"),
        integer: new JavaInteger("4"),
        long: new JavaLong("5"),
        short: new JavaShort("6"),
        string: "str",
        date: new Date(),
        optional: new JavaOptional<string>("optstr")
      });

      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(oracle));

      expect(output).toEqual(input);
    });

    test("with custom pojo with java collection types, should unmarshall normally", () => {
      const marshaller = new DefaultMarshaller();
      const oracle = new Map([["com.app.my.JavaCollectionTypesPojo", () => new JavaCollectionTypesPojo({})]]);

      const input = new JavaCollectionTypesPojo({
        list: ["1", "2"],
        set: new Set<string>(["3", "2"]),
        map: new Map([["k1", "v1"], ["k2", "v2"]])
      });

      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, new UnmarshallingContext(oracle));

      expect(output).toEqual(input);
    });

    test("with custom pojo without fqcn, should throw error", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.Pojo", () => new User({ age: new JavaInteger("0") })]])
      );

      const input = new User({});
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      // remove its fqcn
      delete marshalledInput[ErraiObjectConstants.ENCODED_TYPE];

      expect(() => marshaller.unmarshall(marshalledInput, unmarshallContext)).toThrowError();
    });

    test("with custom pojo without factory, should throw error", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(new Map());

      const input = new User({});
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      expect(() => marshaller.unmarshall(marshalledInput, unmarshallContext)).toThrowError();
    });

    test("with custom pojo with property without fqcn, should throw error", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          ["com.app.my.Pojo", () => new User({ age: new JavaInteger("0") }) as any],
          ["com.app.my.Address", () => new Address({} as any) as any]
        ])
      );

      const input = new User({ name: "foo", address: new Address({ line1: "bla" }) });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      // remove its fqcn
      delete (marshalledInput as any).address[ErraiObjectConstants.ENCODED_TYPE];

      expect(() => marshaller.unmarshall(marshalledInput, unmarshallContext)).toThrowError();
    });

    class PojoWithFunction implements Portable<PojoWithFunction> {
      private readonly _fqcn = "com.app.my.PojoWithFunction";

      public readonly foo?: string = undefined;

      constructor(self: { foo?: string }) {
        Object.assign(this, self);
      }

      public whatToSay() {
        return `Hello, ${this.foo}!`;
      }
    }

    class JavaCollectionTypesPojo implements Portable<JavaCollectionTypesPojo> {
      private readonly _fqcn = "com.app.my.JavaCollectionTypesPojo";

      public readonly list?: string[] = undefined;
      public readonly set?: Set<string> = undefined;
      public readonly map?: Map<string, string> = undefined;

      constructor(self: { list?: string[]; set?: Set<string>; map?: Map<string, string> }) {
        Object.assign(this, self);
      }
    }
  });

  describe("object caching", () => {
    test("custom pojo with repeated pojo objects, should cache the object and reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          ["com.app.my.Address", () => new Address({}) as any],
          ["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]
        ])
      );

      const repeatedObject = new Address({ line1: "bla address" });
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<Address>;

      expect(output).toEqual(input);
      expect(output.field1!).toBe(output.field2!);
    });

    test("custom pojo with repeated JavaBigDecimal objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedObject = new JavaBigDecimal("1.1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaBigDecimal>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaBigInteger objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedObject = new JavaBigInteger("1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaBigInteger>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaByte objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          [
            "com.app.my.RepeatedFieldsPojo",
            () => new RepeatedFieldsPojo({ field1: new JavaByte("0"), field2: new JavaByte("0") }) as any
          ]
        ])
      );

      const repeatedObject = new JavaByte("1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaByte>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaDouble objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          [
            "com.app.my.RepeatedFieldsPojo",
            () => new RepeatedFieldsPojo({ field1: new JavaDouble("0"), field2: new JavaDouble("0") }) as any
          ]
        ])
      );

      const repeatedObject = new JavaDouble("1.1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaDouble>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaFloat objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          [
            "com.app.my.RepeatedFieldsPojo",
            () => new RepeatedFieldsPojo({ field1: new JavaFloat("0"), field2: new JavaFloat("0") }) as any
          ]
        ])
      );

      const repeatedObject = new JavaFloat("1.1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaFloat>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaInteger objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          [
            "com.app.my.RepeatedFieldsPojo",
            () => new RepeatedFieldsPojo({ field1: new JavaInteger("0"), field2: new JavaInteger("0") }) as any
          ]
        ])
      );

      const repeatedObject = new JavaInteger("1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaInteger>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaLong objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedObject = new JavaLong("1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaLong>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaShort objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          [
            "com.app.my.RepeatedFieldsPojo",
            () => new RepeatedFieldsPojo({ field1: new JavaShort("0"), field2: new JavaShort("0") }) as any
          ]
        ])
      );

      const repeatedObject = new JavaShort("1");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<JavaShort>;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaOptional objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedObject = new JavaOptional("foo");
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<
        JavaOptional<string>
      >;

      expect(output).toEqual(input);
      expect(output.field1!).not.toBe(output.field2!);
    });

    test("custom pojo with repeated JavaEnum objects, should not cache it and don't reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([
          ["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any],
          [
            "com.app.my.AddressType",
            ((name: string) => {
              switch (name) {
                case "HOME":
                  return AddressType.HOME;
                case "WORK":
                  return AddressType.WORK;
                default:
                  throw new Error(`Unknown value ${name} for enum AddressType!`);
              }
            }) as any
          ]
        ])
      );

      const repeatedObject = AddressType.HOME;
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<AddressType>;

      expect(output).toEqual(input);
      expect(output.field1!).toBe(output.field2!);
    });

    test("custom pojo with repeated array objects, should cache the object and reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedInlineArray = ["foo", "bar"];
      const inlineArrayInput = new RepeatedFieldsPojo({ field1: repeatedInlineArray, field2: repeatedInlineArray });

      const repeatedNewArray = new Array("foo", "bar");
      const newArrayInput = new RepeatedFieldsPojo({ field1: repeatedNewArray, field2: repeatedNewArray });

      [inlineArrayInput, newArrayInput].forEach(input => {
        const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

        const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<string[]>;

        expect(output).toEqual(input);
        expect(output.field1!).toBe(output.field2!);
      });
    });

    test("custom pojo with repeated set objects, should cache the object and reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedObject = new Set(["foo", "bar"]);
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<Set<string>>;

      expect(output).toEqual(input);
      expect(output.field1!).toBe(output.field2!);
    });

    test("custom pojo with repeated map objects, should cache the object and reuse data", () => {
      const marshaller = new DefaultMarshaller();
      const unmarshallContext = new UnmarshallingContext(
        new Map([["com.app.my.RepeatedFieldsPojo", () => new RepeatedFieldsPojo({}) as any]])
      );

      const repeatedObject = new Map([["foo1", "bar1"], ["foo2", "bar2"]]);
      const input = new RepeatedFieldsPojo({ field1: repeatedObject, field2: repeatedObject });
      const marshalledInput = marshaller.marshall(input, new MarshallingContext())!;

      const output = marshaller.unmarshall(marshalledInput, unmarshallContext) as RepeatedFieldsPojo<
        Map<string, string>
      >;

      expect(output).toEqual(input);
      expect(output.field1!).toBe(output.field2!);
    });

    class RepeatedFieldsPojo<T> implements Portable<RepeatedFieldsPojo<T>> {
      private readonly _fqcn = "com.app.my.RepeatedFieldsPojo";

      public field1?: T = undefined;
      public field2?: T = undefined;

      constructor(self: { field1?: T; field2?: T }) {
        Object.assign(this, self);
      }
    }
  });
});

class User implements Portable<User> {
  private readonly _fqcn = "com.app.my.Pojo";

  public readonly name?: string = undefined;
  public readonly sendSpam?: boolean = undefined;
  public readonly age?: JavaInteger = undefined;
  public readonly address?: Address = undefined;
  public readonly bestFriend?: User = undefined;

  constructor(self: { name?: string; sendSpam?: boolean; age?: JavaInteger; address?: Address; bestFriend?: User }) {
    Object.assign(this, self);
  }
}

class Address implements Portable<Address> {
  private readonly _fqcn = "com.app.my.Address";

  public line1?: string = undefined;
  public type?: AddressType = undefined;
  constructor(self: { line1?: string; type?: AddressType }) {
    Object.assign(this, self);
  }
}

class AddressType extends JavaEnum<AddressType> {
  public static readonly HOME: AddressType = new AddressType("HOME");
  public static readonly WORK: AddressType = new AddressType("WORK");

  protected readonly _fqcn: string = AddressType.__fqcn();

  public static __fqcn(): string {
    return "com.app.my.AddressType";
  }

  public static values() {
    return [AddressType.HOME, AddressType.WORK];
  }
}

class JavaTypesPojo implements Portable<JavaTypesPojo> {
  private readonly _fqcn = "com.app.my.Pojo";

  public readonly bigDecimal?: JavaBigDecimal = undefined;
  public readonly bigInteger?: JavaBigInteger = undefined;
  public readonly boolean?: boolean = undefined;
  public readonly byte?: JavaByte = undefined;
  public readonly double?: JavaDouble = undefined;
  public readonly float?: JavaFloat = undefined;
  public readonly integer?: JavaInteger = undefined;
  public readonly long?: JavaLong = undefined;
  public readonly short?: JavaShort = undefined;
  public readonly string?: string = undefined;
  public readonly date?: Date = undefined;
  public readonly optional?: JavaOptional<string> = undefined;

  constructor(self: {
    bigDecimal?: JavaBigDecimal;
    bigInteger?: JavaBigInteger;
    boolean?: boolean;
    byte?: JavaByte;
    double?: JavaDouble;
    float?: JavaFloat;
    integer?: JavaInteger;
    long?: JavaLong;
    short?: JavaShort;
    string?: string;
    date?: Date;
    optional?: JavaOptional<string>;
  }) {
    Object.assign(this, self);
  }
}
