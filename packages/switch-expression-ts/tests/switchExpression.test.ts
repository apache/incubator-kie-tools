/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { describe, it, expect } from "@jest/globals";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";

type UnionString3Values = "one" | "two" | "three";
type UnionStringSubset = "one" | "three";
enum SomeKind {
  ONE = "one",
  TWO = "two",
  THREE = "three",
}
const someKindSubsetValues = [SomeKind.ONE, SomeKind.THREE] as const;
type SomeKindSubset = (typeof someKindSubsetValues)[number];
type UnionMixed3Values = 1 | "two" | SomeKind.THREE;

describe("switchExpression tests", () => {
  it("test default value when key did not match", () => {
    const value: { type: UnionString3Values } = { type: "two" };
    expect(
      switchExpression(value.type, {
        one: "value1",
        default: "value_default",
      })
    ).toBe("value_default");
  });
  it("test default value when key matched", () => {
    const value: { type: UnionString3Values } = { type: "one" };
    expect(
      switchExpression(value.type, {
        one: "value1",
        default: "value_default",
      })
    ).toBe("value1");
  });
  it("test switchStatement varying types", () => {
    const value: { type: UnionMixed3Values } = { type: SomeKind.THREE };
    expect(
      switchExpression(value.type, {
        1: "value1",
        two: "value2",
        [SomeKind.THREE]: "value3",
      })
    ).toBe("value3");
  });
  it("test restrict case options by explicit cast", () => {
    const value: { type: UnionString3Values } = { type: "three" };
    expect(
      switchExpression(value.type as UnionStringSubset, {
        one: "value1",
        three: "value3",
      })
    ).toBe("value3");
  });
  it("test restrict case options by explicit binding and cast", () => {
    const value: { type: SomeKind } = { type: SomeKind.THREE };
    expect(
      switchExpression<SomeKindSubset, string>(value.type as SomeKindSubset, {
        one: "value1",
        three: "value3",
      })
    ).toBe("value3");
  });
  it("test undefined as a valid result", () => {
    const value: "a" | "b" | "c" = "c";
    const res: string | undefined = switchExpression(value, {
      a: "a",
      c: undefined,
    });

    expect(res).toBe(undefined);
  });
});
