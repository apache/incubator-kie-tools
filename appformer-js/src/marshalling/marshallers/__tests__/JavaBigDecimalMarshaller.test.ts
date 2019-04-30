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

import { JavaBigDecimal } from "../../../java-wrappers";
import { JavaBigDecimalMarshaller } from "../JavaBigDecimalMarshaller";
import { MarshallingContext } from "../../MarshallingContext";
import { ErraiObjectConstants } from "../../model/ErraiObjectConstants";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { JavaType } from "../../../java-wrappers/JavaType";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
import { NumberUtils } from "../../../util/NumberUtils";

describe("marshall", () => {
  test("with regular big decimal, should serialize it normally", () => {
    const input = new JavaBigDecimal("12.12");

    const output = new JavaBigDecimalMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual({
      [ErraiObjectConstants.ENCODED_TYPE]: JavaType.BIG_DECIMAL,
      [ErraiObjectConstants.OBJECT_ID]: expect.stringMatching(NumberUtils.nonNegativeIntegerRegex),
      [ErraiObjectConstants.VALUE]: "12.12"
    });
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaBigDecimalMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaBigDecimalMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with regular input, should return a JavaBigDecimal instance", () => {
    const marshaller = new JavaBigDecimalMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = new JavaBigDecimal("125.2");
    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, context);

    expect(output.get().toString(10)).toEqual(input.get().toString(10));
  });

  test("with non string value, should throw error", () => {
    const marshaller = new JavaBigDecimalMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_DECIMAL, false, "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with non numeric string value, should throw error", () => {
    const marshaller = new JavaBigDecimalMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_DECIMAL, "abc", "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with null value, should return a JavaBigDecimal instance containing NaN", () => {
    const marshaller = new JavaBigDecimalMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_DECIMAL, null, "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with undefined value, should return a JavaBigDecimal instance containing NaN", () => {
    const marshaller = new JavaBigDecimalMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_DECIMAL, undefined, "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with empty string value, should return a JavaBigDecimal instance containing NaN", () => {
    const marshaller = new JavaBigDecimalMarshaller();
    const context = new UnmarshallingContext(new Map());

    const marshalledInput = new ValueBasedErraiObject(JavaType.BIG_DECIMAL, "", "1").asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });
});
