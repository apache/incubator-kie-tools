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

import { UnmarshallingContext } from "../UnmarshallingContext";
import { Portable } from "../Portable";
import { ErraiObjectConstants } from "../model/ErraiObjectConstants";

describe("cacheObject", () => {
  test("with regular input, should cache it using the object's id as key", () => {
    const context = new UnmarshallingContext(new Map());

    const erraiObj = {
      [ErraiObjectConstants.ENCODED_TYPE]: "bla",
      [ErraiObjectConstants.OBJECT_ID]: "1",
      foo: "bar"
    };

    const portable = new MyPortable({ foo: "bar" });

    context.cacheObject(erraiObj, portable);

    const cache = (context as any)._objectsCache;
    const cachedValue = cache.get("1");

    expect(cachedValue).toStrictEqual(portable);
    expect(cache.size).toBe(1);
  });

  test("with repeated calls, should cache the last version", () => {
    const context = new UnmarshallingContext(new Map());

    const erraiObj = {
      [ErraiObjectConstants.ENCODED_TYPE]: "bla",
      [ErraiObjectConstants.OBJECT_ID]: "1",
      foo: "bar"
    };

    const portable = new MyPortable({ foo: "bar" });
    const portableV2 = new MyPortable({ foo: "bar2" });

    context.cacheObject(erraiObj, portable);
    context.cacheObject(erraiObj, portableV2);

    const cache = (context as any)._objectsCache;
    const cachedValue = cache.get("1");

    expect(cachedValue).toStrictEqual(portableV2);
    expect(cache.size).toBe(1);
  });

  test("with input without object id, should not cache it", () => {
    const context = new UnmarshallingContext(new Map());

    const erraiObj = {
      [ErraiObjectConstants.ENCODED_TYPE]: "bla",
      [ErraiObjectConstants.OBJECT_ID]: "",
      foo: "bar"
    };

    const portable = new MyPortable({ foo: "bar" });

    context.cacheObject(erraiObj, portable);

    const cache = (context as any)._objectsCache;
    const cachedValue = cache.get("1");

    expect(cachedValue).toBeUndefined();
    expect(cache.size).toBe(0);
  });
});

describe("getCached", () => {
  test("with cached input, should return it", () => {
    const context = new UnmarshallingContext(new Map());

    const erraiObj = {
      [ErraiObjectConstants.ENCODED_TYPE]: "bla",
      [ErraiObjectConstants.OBJECT_ID]: "1",
      foo: "bar"
    };

    const portable = new MyPortable({ foo: "bar" });
    context.cacheObject(erraiObj, portable);

    const cachedValue = context.getCached(erraiObj);

    expect(cachedValue).toStrictEqual(portable);
  });

  test("with input without object id, should return undefined", () => {
    const context = new UnmarshallingContext(new Map());

    const spy = jest.spyOn((context as any)._objectsCache, "get");

    const erraiObj = {
      // no object id
      [ErraiObjectConstants.ENCODED_TYPE]: "bla",
      [ErraiObjectConstants.OBJECT_ID]: "",
      foo: "bar"
    };

    context.getCached(erraiObj);

    expect(spy).not.toHaveBeenCalled();
  });

  test("with non existent input, should return undefined", () => {
    const context = new UnmarshallingContext(new Map());

    // add some dummy entry to the cache
    const erraiObj = {
      [ErraiObjectConstants.ENCODED_TYPE]: "bla",
      [ErraiObjectConstants.OBJECT_ID]: "1",
      foo: "bar"
    };

    const cachedValue = context.getCached(erraiObj);

    expect(cachedValue).toBeUndefined();
    expect((context as any)._objectsCache.size).toEqual(0);
  });
});

describe("getFactory", () => {
  test("with existent factory for fqcn, should return it", () => {
    const oracle = new Map([["com.app.my", () => new MyPortable({} as any)]]);

    const context = new UnmarshallingContext(oracle);

    const factory = context.getFactory("com.app.my");

    expect(factory).toBeDefined();
    expect(factory!()).toEqual(new MyPortable({} as any));
  });

  test("with non-existent factory for fqcn, should return it", () => {
    const oracle = new Map([["com.app.my2", () => new MyPortable({} as any)]]);

    const context = new UnmarshallingContext(oracle);

    const factory = context.getFactory("com.app.my");

    expect(factory).toBeUndefined();
  });
});

class MyPortable implements Portable<MyPortable> {
  private readonly _fqcn = "com.app.my";

  public readonly foo: string;

  constructor(self: { foo: string }) {
    Object.assign(this, self);
  }
}
