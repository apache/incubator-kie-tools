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
import { JavaString } from "../../../java-wrappers";
import { JavaStringMarshaller } from "../JavaStringMarshaller";
import { UnmarshallingContext } from "../../UnmarshallingContext";

describe("marshall", () => {
  test("with regular string, should return the same value", () => {
    const input = new JavaString("str");

    const output = new JavaStringMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual("str");
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaStringMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaStringMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with string value, should return same string", () => {
    const marshaller = new JavaStringMarshaller();

    const input = new JavaString("foo");

    const marshalledInput = marshaller.notNullMarshall(input, new MarshallingContext());

    const output = marshaller.notNullUnmarshall(marshalledInput, new UnmarshallingContext(new Map()));

    expect(output).toEqual("foo");
  });

  test("with JavaString value, should return inner string", () => {
    const marshaller = new JavaStringMarshaller();

    const input = new JavaString("foo");

    const output = marshaller.notNullUnmarshall(input, new UnmarshallingContext(new Map()));

    expect(output).toEqual("foo");
  });
});
