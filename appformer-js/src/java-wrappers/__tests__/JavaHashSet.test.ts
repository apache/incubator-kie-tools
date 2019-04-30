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

import { JavaHashSet } from "../JavaHashSet";
import { JavaType } from "../JavaType";

describe("get", () => {
  test("with populated set, returns the same set", () => {
    const input = new Set(["foo", "bar", "foo2"]);

    const output = new JavaHashSet(input).get();

    expect(output).toEqual(new Set(["foo", "bar", "foo2"]));
  });

  test("with empty set, returns the same set", () => {
    const input = new Set();

    const output = new JavaHashSet(input).get();

    expect(output).toEqual(new Set());
  });
});

describe("set", () => {
  test("with direct value, should set", () => {
    const input = new JavaHashSet(new Set(["k1", "v1"]));
    expect(input.get()).toEqual(new Set(["k1", "v1"]));

    input.set(new Set(["k2", "v2"]));

    expect(input.get()).toEqual(new Set(["k2", "v2"]));
  });

  test("with value from function, should set", () => {
    const input = new JavaHashSet(new Set(["k1", "v1"]));
    expect(input.get()).toEqual(new Set(["k1", "v1"]));

    input.set(cur => {
      const newSet = new Set(cur);
      newSet.add("k2");
      return newSet;
    });

    expect(input.get()).toEqual(new Set(["k1", "v1", "k2"]));
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaHashSet(new Set(["1", "2"])) as any)._fqcn;

    expect(fqcn).toBe(JavaType.HASH_SET);
  });
});
