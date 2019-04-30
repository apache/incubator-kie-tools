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

import { MarshallingContext } from "../MarshallingContext";
import { ErraiObject } from "../model/ErraiObject";
import { ErraiObjectConstants } from "../model/ErraiObjectConstants";
import { JavaWrapper } from "../../java-wrappers/JavaWrapper";
import { NumValBasedErraiObject } from "../model/NumValBasedErraiObject";

describe("incrementAndGetObjectId", () => {
  test("with sequential calls, should return different ids", () => {
    const context = new MarshallingContext();

    const firstObjectId = context.incrementAndGetObjectId();
    const secondObjectId = context.incrementAndGetObjectId();

    expect(firstObjectId).not.toEqual(secondObjectId);
  });
});

describe("cacheObject", () => {
  let context: MarshallingContext;

  beforeEach(() => {
    context = new MarshallingContext();
  });

  test("with regular object, should record the correct ErraiObject instance", () => {
    const inputPortable = { _fqcn: "com.myapp.my.portable", foo: "bar", bar: "foo" };

    const inputErraiObject = new NumValBasedErraiObject("com.foo.bar", "123", "1").asErraiObject();

    context.cacheObject(inputPortable, inputErraiObject);

    // the only cached fields are the encodedType and objectId
    expect((context as any).objContext.get(inputPortable)).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "1"
    });
  });

  test("with object overwritten, should return the last version", () => {
    const inputPortable = { _fqcn: "com.myapp.my.portable", foo: "bar", bar: "foo" };

    const inputErraiObject = new NumValBasedErraiObject("com.foo.bar", "123", "1").asErraiObject();

    context.cacheObject(inputPortable, inputErraiObject);

    // override object
    context.cacheObject(inputPortable, {
      ...inputErraiObject,
      [ErraiObjectConstants.OBJECT_ID]: "2"
    });

    expect((context as any).objContext.get(inputPortable)).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "2"
    });
  });

  test("with java wrapper object, should use the inner value as cache key", () => {
    const innerValue = 1;
    const wrappedValue = new MyNumberWrappedType(innerValue);

    const inputErraiObject = new NumValBasedErraiObject("com.foo.bar", "123", "1").asErraiObject();

    context.cacheObject(wrappedValue, inputErraiObject);

    // use the inner value object as cache key
    expect((context as any).objContext.get(innerValue)).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "1"
    });

    // do not use the wrapped object as cache key
    expect((context as any).objContext.get(wrappedValue)).toBeUndefined();
  });
});

describe("getCached", () => {
  let context: MarshallingContext;

  beforeEach(() => {
    context = new MarshallingContext();
  });

  test("with non-existent key, should return undefined", () => {
    const input = { _fqcn: "com.myapp.my.portable", foo: "bar", bar: "foo" };
    expect(context.getCached(input)).toBeUndefined();
  });

  test("with existent key, should same object recorded previously", () => {
    const inputPortable = { _fqcn: "com.myapp.my.portable", foo: "bar", bar: "foo" };
    const inputErraiObject = {
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "1"
    } as ErraiObject;

    (context as any).objContext.set(inputPortable, inputErraiObject);

    expect(context.getCached(inputPortable)).toStrictEqual(inputErraiObject);
  });

  test("with java wrapper object, should use the inner value as cache key", () => {
    const innerValue = 1;
    const wrappedValue = new MyNumberWrappedType(innerValue);

    const inputErraiObject = {
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "1"
    } as ErraiObject;

    (context as any).objContext.set(innerValue, inputErraiObject);

    // use the inner value object as cache key
    expect(context.getCached(innerValue)).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "1"
    });

    expect(context.getCached(wrappedValue)).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.foo.bar",
      [ErraiObjectConstants.OBJECT_ID]: "1"
    });
  });
});

class MyNumberWrappedType extends JavaWrapper<number> {
  private readonly _fqcn = "com.type.wrapped.number.my";

  private readonly _value: number;

  constructor(value: number) {
    super();
    this._value = value;
  }

  public get(): number {
    return this._value;
  }

  public set(val: ((current: number) => number) | number): void {
    // not used
  }
}
