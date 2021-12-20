/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { getEnumKeyByEnumValue } from "../../api";

describe("EnumUtils tests", () => {
  test("given an enum value, it should return its key", () => {
    enum Enum {
      OneKey = "val",
      AnotherKey = "anotherVal",
    }

    expect(getEnumKeyByEnumValue(Enum, "val")).toBe("OneKey");
  });

  test("given an enum, where multiple keys have the same value, it should return one of its keys", () => {
    enum Enum {
      AnotherKey = "val",
      OneKey = "val",
    }

    expect(getEnumKeyByEnumValue(Enum, "val")).toBeOneOf(["AnotherKey", "OneKey"]);
  });

  test("given an enum, where no key has the passed value, it should return null", () => {
    enum Enum {
      AnotherKey = "val",
      OneKey = "another",
    }

    expect(getEnumKeyByEnumValue(Enum, "not present")).toBeNull();
  });
});
