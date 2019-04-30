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

import { ErraiObjectConstants } from "../ErraiObjectConstants";
import { EnumStringValueBasedErraiObject } from "../EnumStringValueBasedErraiObject";

describe("asErraiObject", () => {
  test("with correct inputs, should return correct a well formed Errai Object", () => {
    const input = new EnumStringValueBasedErraiObject("com.app.my", "foo");

    expect(input.asErraiObject()).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
      [ErraiObjectConstants.ENUM_STRING_VALUE]: "foo"
    });
  });
});

describe("from", () => {
  test("with well formed errai object instance, should retrieve its data correctly", () => {
    const input = {
      [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
      [ErraiObjectConstants.ENUM_STRING_VALUE]: "foo"
    };

    const output = EnumStringValueBasedErraiObject.from(input);

    expect(output.encodedType).toEqual("com.app.my");
    expect(output.enumValueName).toEqual("foo");
  });
});
