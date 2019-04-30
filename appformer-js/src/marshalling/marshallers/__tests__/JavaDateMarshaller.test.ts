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
import { JavaDate } from "../../../java-wrappers";
import { JavaDateMarshaller } from "../JavaDateMarshaller";
import { ValueBasedErraiObject } from "../../model/ValueBasedErraiObject";
import { JavaType } from "../../../java-wrappers/JavaType";
import { UnmarshallingContext } from "../../UnmarshallingContext";

describe("marshall", () => {
  test("with regular date, should should serialize it normally", () => {
    const date = new Date();

    const input = new JavaDate(date);

    const output = new JavaDateMarshaller().marshall(input, new MarshallingContext());
    expect(output).toStrictEqual(new ValueBasedErraiObject(JavaType.DATE, `${date.getTime()}`).asErraiObject());
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaDateMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaDateMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with ErraiObject regular input, should return the same Date instance", () => {
    const marshaller = new JavaDateMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = new Date();
    const expectedDate = new Date(input.getTime());

    const marshalledInput = marshaller.notNullMarshall(new JavaDate(input), new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, context);

    expect(output).toEqual(expectedDate);
  });

  test("with non numeric string value, should throw error", () => {
    const marshaller = new JavaDateMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = "abc" as any;
    const marshalledInput = new ValueBasedErraiObject(JavaType.DATE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with non string value, should throw error", () => {
    const marshaller = new JavaDateMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = true as any;
    const marshalledInput = new ValueBasedErraiObject(JavaType.DATE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with negative timestamp string value, should throw error", () => {
    const marshaller = new JavaDateMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = "-1";
    const marshalledInput = new ValueBasedErraiObject(JavaType.DATE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with null value, should throw error", () => {
    const marshaller = new JavaDateMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = null as any;
    const marshalledInput = new ValueBasedErraiObject(JavaType.DATE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with undefined value, should throw error", () => {
    const marshaller = new JavaDateMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = undefined as any;
    const marshalledInput = new ValueBasedErraiObject(JavaType.DATE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });
});
