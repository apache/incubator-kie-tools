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
import { JavaByte } from "../../../java-wrappers";
import { JavaByteMarshaller } from "../JavaByteMarshaller";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";
import { JavaType } from "../../../java-wrappers/JavaType";

describe("marshall", () => {
  test("with regular byte, should return same value", () => {
    const input = new JavaByte("2");

    const output = new JavaByteMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual(2);
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaByteMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaByteMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  describe("number input", () => {
    test("with byte, should return a JavaByte instance", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = 1;
      const output = marshaller.notNullUnmarshall(input, context);

      expect(output).toEqual(new JavaByte("1"));
    });

    test("with float, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = 1.2;

      expect(() => marshaller.notNullUnmarshall(input, context)).toThrowError();
    });

    test("with string, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = "abc" as any;

      expect(() => marshaller.notNullUnmarshall(input, context)).toThrowError();
    });

    test("with null, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = null as any;

      expect(() => marshaller.notNullUnmarshall(input, context)).toThrowError();
    });

    test("with undefined, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = undefined as any;

      expect(() => marshaller.notNullUnmarshall(input, context)).toThrowError();
    });
  });

  describe("ErraiObject input", () => {
    test("with byte, should return a JavaByte instance", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = 1;
      const marshalledInput = new NumValBasedErraiObject(JavaType.BYTE, input).asErraiObject();

      const output = marshaller.notNullUnmarshall(marshalledInput, context);

      expect(output).toEqual(new JavaByte("1"));
    });

    test("with float, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = 1.2;
      const marshalledInput = new NumValBasedErraiObject(JavaType.BYTE, input).asErraiObject();

      expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
    });

    test("with string, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = "abc" as any;
      const marshalledInput = new NumValBasedErraiObject(JavaType.BYTE, input).asErraiObject();

      expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
    });

    test("with null, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = null as any;
      const marshalledInput = new NumValBasedErraiObject(JavaType.BYTE, input).asErraiObject();

      expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
    });

    test("with undefined, should throw error", () => {
      const marshaller = new JavaByteMarshaller();
      const context = new UnmarshallingContext(new Map());

      const input = undefined as any;
      const marshalledInput = new NumValBasedErraiObject(JavaType.BYTE, input).asErraiObject();

      expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
    });
  });
});
