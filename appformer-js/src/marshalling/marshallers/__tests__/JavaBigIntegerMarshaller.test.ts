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

import { MarshallingContext } from "../../MarshallingContext";
import { ErraiObjectConstants } from "../../model/ErraiObjectConstants";
import { JavaBigInteger } from "../../../java-wrappers";
import { JavaBigIntegerMarshaller } from "../JavaBigIntegerMarshaller";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { JavaType } from "../../../java-wrappers/JavaType";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
import { NumberUtils } from "../../../util/NumberUtils";

describe("marshall", () => {
  test("with regular big integer, should serialize it normally", () => {
    const input = new JavaBigInteger("12");

    const output = new JavaBigIntegerMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: JavaType.BIG_INTEGER,
      [ErraiObjectConstants.OBJECT_ID]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [ErraiObjectConstants.VALUE]: "12"
    });
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaBigIntegerMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaBigIntegerMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with regular input, should return a JavaBigInteger instance", () => {
    const marshaller = new JavaBigIntegerMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = new JavaBigInteger("125");
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, context);

    expect(output.get().toString(10)).toEqual(input.get().toString(10));
  });

  test("with non string value, should return a JavaBigInteger instance containing NaN", () => {
    const marshaller = new JavaBigIntegerMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_INTEGER, false, "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with non numeric string value, should throw error", () => {
    const marshaller = new JavaBigIntegerMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_INTEGER, "abc", "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with null value, should return a JavaBigInteger instance containing NaN", () => {
    const marshaller = new JavaBigIntegerMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_INTEGER, null, "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with undefined value, should return a JavaBigInteger instance containing NaN", () => {
    const marshaller = new JavaBigIntegerMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_INTEGER, undefined, "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with empty string value, should return a JavaBigInteger instance containing NaN", () => {
    const marshaller = new JavaBigIntegerMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_INTEGER, "", "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });
});
