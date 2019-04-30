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
import { JavaBoolean } from "../../../java-wrappers";
import { JavaBooleanMarshaller } from "../JavaBooleanMarshaller";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { JavaType } from "../../../java-wrappers/JavaType";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";

describe("marshall", () => {
  test("with regular boolean, should return the same value", () => {
    const input = new JavaBoolean(false);

    const output = new JavaBooleanMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual(false);
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaBooleanMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaBooleanMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with boolean input, should return a boolean instance", () => {
    const marshaller = new JavaBooleanMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = true;
    const output = marshaller.notNullUnmarshall(input, context);

    expect(output).toBeTruthy();
  });

  test("with ErraiObject regular input, should return a boolean instance", () => {
    const marshaller = new JavaBooleanMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = false;
    const marshalledInput = new NumValBasedErraiObject(JavaType.BOOLEAN, input).asErraiObject();

    const output = marshaller.notNullUnmarshall(marshalledInput, context);

    expect(output).toBeFalsy();
  });

  test("with non boolean value, should throw error", () => {
    const marshaller = new JavaBooleanMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = "abc" as any;
    const marshalledInput = new NumValBasedErraiObject(JavaType.BOOLEAN, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with null value, should throw error", () => {
    const marshaller = new JavaBooleanMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = null as any;
    const marshalledInput = new NumValBasedErraiObject(JavaType.BOOLEAN, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with undefined value, should throw error", () => {
    const marshaller = new JavaBooleanMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = undefined as any;
    const marshalledInput = new NumValBasedErraiObject(JavaType.BOOLEAN, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });
});
