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

import { NumValBasedErraiObject } from "../NumValBasedErraiObject";
import { ErraiObjectConstants } from "../ErraiObjectConstants";
import { ValueBasedErraiObject } from "../ValueBasedErraiObject";

describe("asErraiObject", () => {
  describe("with objectId filled", () => {
    test("with string value, should return correct a well formed Errai Object", () => {
      const input = new ValueBasedErraiObject("com.app.my", "bla", "12");

      expect(input.asErraiObject()).toStrictEqual({
        [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
        [ErraiObjectConstants.OBJECT_ID]: "12",
        [ErraiObjectConstants.VALUE]: "bla"
      });
    });

    test("with ErraiObject value, should return correct a well formed Errai Object", () => {
      const innerVal = new NumValBasedErraiObject("com.app.my", false, "2").asErraiObject();

      const input = new ValueBasedErraiObject("my.fqcn", innerVal, "13");

      expect(input.asErraiObject()).toStrictEqual({
        [ErraiObjectConstants.ENCODED_TYPE]: "my.fqcn",
        [ErraiObjectConstants.OBJECT_ID]: "13",
        [ErraiObjectConstants.VALUE]: {
          [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
          [ErraiObjectConstants.OBJECT_ID]: "2",
          [ErraiObjectConstants.NUM_VAL]: false
        }
      });
    });
  });

  describe("with objectId not filled", () => {
    test("with string value, should return correct a well formed Errai Object applying objId's default", () => {
      const input = new ValueBasedErraiObject("com.app.my", "bla");

      expect(input.asErraiObject()).toStrictEqual({
        [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
        [ErraiObjectConstants.OBJECT_ID]: "-1",
        [ErraiObjectConstants.VALUE]: "bla"
      });
    });

    test("with ErraiObject value, should return correct a well formed Errai Object applying objId's default", () => {
      const innerVal = new NumValBasedErraiObject("com.app.my", false, "2").asErraiObject();

      const input = new ValueBasedErraiObject("my.fqcn", innerVal);

      expect(input.asErraiObject()).toStrictEqual({
        [ErraiObjectConstants.ENCODED_TYPE]: "my.fqcn",
        [ErraiObjectConstants.OBJECT_ID]: "-1",
        [ErraiObjectConstants.VALUE]: {
          [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
          [ErraiObjectConstants.OBJECT_ID]: "2",
          [ErraiObjectConstants.NUM_VAL]: false
        }
      });
    });
  });
});

describe("from", () => {
  test("with string Value based errai object instance, should retrieve its data correctly", () => {
    const input = {
      [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
      [ErraiObjectConstants.OBJECT_ID]: "125",
      [ErraiObjectConstants.VALUE]: "str"
    };

    const output = ValueBasedErraiObject.from(input);

    expect(output.encodedType).toEqual("com.app.my");
    expect(output.objId).toEqual("125");
    expect(output.value).toBe("str");
  });

  test("with ErraiObject Value based errai object instance, should retrieve its data correctly", () => {
    const innerVal = new NumValBasedErraiObject("com.app.my", false, "2").asErraiObject();

    const input = {
      [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
      [ErraiObjectConstants.OBJECT_ID]: "125",
      [ErraiObjectConstants.VALUE]: innerVal
    };

    const output = ValueBasedErraiObject.from(input);

    expect(output.encodedType).toEqual("com.app.my");
    expect(output.objId).toEqual("125");
    expect(output.value).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: "com.app.my",
      [ErraiObjectConstants.OBJECT_ID]: "2",
      [ErraiObjectConstants.NUM_VAL]: false
    });
  });
});
