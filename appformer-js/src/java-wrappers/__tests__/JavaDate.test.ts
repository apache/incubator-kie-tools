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

import { JavaDate } from "../JavaDate";
import { JavaType } from "../JavaType";

describe("get", () => {
  test("with date, should return same value as Date", () => {
    const input = new Date();

    const output = new JavaDate(input).get();

    expect(output).toEqual(input);
  });
});

describe("set", () => {
  test("with direct value, should set", () => {
    const firstDate = new Date();

    const input = new JavaDate(firstDate);
    expect(input.get()).toEqual(firstDate);

    const secondDate = new Date();
    input.set(secondDate);

    expect(input.get()).toEqual(secondDate);
  });

  test("with value from function, should set", () => {
    const firstDate = new Date();

    const input = new JavaDate(firstDate);
    expect(input.get()).toEqual(firstDate);

    input.set(cur => {
      const newDate = new Date(cur.getUTCMilliseconds());
      newDate.setHours(cur.getHours() + 1);
      return newDate;
    });

    const expectedDate = new Date(firstDate.getUTCMilliseconds());
    expectedDate.setHours(firstDate.getHours() + 1);
    expect(input.get()).toEqual(expectedDate);
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaDate(new Date()) as any)._fqcn;

    expect(fqcn).toBe(JavaType.DATE);
  });
});
