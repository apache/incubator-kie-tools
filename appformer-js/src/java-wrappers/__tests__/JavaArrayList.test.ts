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

import { JavaArrayList } from "../JavaArrayList";
import { JavaType } from "../JavaType";

describe("get", () => {
  test("with populated array, returns the same array", () => {
    const input = ["foo", "bar", "foo2"];

    const output = new JavaArrayList(input).get();

    expect(output).toEqual(["foo", "bar", "foo2"]);
  });

  test("with empty array, returns the same array", () => {
    const input = [] as any[];

    const output = new JavaArrayList(input).get();

    expect(output).toEqual([]);
  });
});

describe("set", () => {
  test("with direct value, should set", () => {
    const input = new JavaArrayList(["foo", "bar"]);
    expect(input.get()).toStrictEqual(["foo", "bar"]);

    input.set(["foo"]);
    expect(input.get()).toStrictEqual(["foo"]);
  });

  test("with value from function, should set", () => {
    const input = new JavaArrayList(["foo", "bar"]);
    expect(input.get()).toStrictEqual(["foo", "bar"]);

    input.set(curr => {
      const newArr = new Array(...curr);
      newArr.push("newfoo");
      return newArr;
    });

    expect(input.get()).toStrictEqual(["foo", "bar", "newfoo"]);
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaArrayList(["1", "2"]) as any)._fqcn;

    expect(fqcn).toBe(JavaType.ARRAY_LIST);
  });
});
