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
import { JavaDouble } from "../../../java-wrappers";
import { JavaDoubleMarshaller } from "../JavaDoubleMarshaller";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";
import { JavaType } from "../../../java-wrappers/JavaType";

describe("marshall", () => {
  test("with regular double, should return the same value", () => {
    const input = new JavaDouble("2.1");

    const output = new JavaDoubleMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual(2.1);
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaDoubleMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaDoubleMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with number input, should return a JavaDouble instance", () => {
    const marshaller = new JavaDoubleMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = 1.1;
    const output = marshaller.notNullUnmarshall(input, context);

    expect(output).toEqual(new JavaDouble("1.1"));
  });

  test("with ErraiObject regular input, should return a JavaDouble instance", () => {
    const marshaller = new JavaDoubleMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = 1.1;
    const marshalledInput = new NumValBasedErraiObject(JavaType.DOUBLE, input).asErraiObject();

    const output = marshaller.notNullUnmarshall(marshalledInput, context);

    expect(output).toEqual(new JavaDouble("1.1"));
  });

  test("with non double value, should throw error", () => {
    const marshaller = new JavaDoubleMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = "abc" as any;
    const marshalledInput = new NumValBasedErraiObject(JavaType.DOUBLE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with null value, should throw error", () => {
    const marshaller = new JavaDoubleMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = null as any;
    const marshalledInput = new NumValBasedErraiObject(JavaType.DOUBLE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with undefined value, should throw error", () => {
    const marshaller = new JavaDoubleMarshaller();
    const context = new UnmarshallingContext(new Map());

    const input = undefined as any;
    const marshalledInput = new NumValBasedErraiObject(JavaType.DOUBLE, input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });
});
