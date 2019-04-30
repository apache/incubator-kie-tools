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
import { JavaEnum } from "../../../java-wrappers";
import { UnmarshallingContext } from "../../UnmarshallingContext";
import { JavaEnumMarshaller } from "../JavaEnumMarshaller";
import { EnumStringValueBasedErraiObject } from "../../model/EnumStringValueBasedErraiObject";

describe("marshall", () => {
  test("with regular enum, should return the same value", () => {
    const input = FooEnum.BAR;

    const output = new JavaEnumMarshaller().marshall(input, new MarshallingContext());

    expect(output).toStrictEqual(
      new EnumStringValueBasedErraiObject(FooEnum.__fqcn(), FooEnum.BAR.name).asErraiObject()
    );
  });

  test("root null object, should serialize to null", () => {
    const input = null as any;

    const output = new JavaEnumMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });

  test("root undefined object, should serialize to null", () => {
    const input = undefined as any;

    const output = new JavaEnumMarshaller().marshall(input, new MarshallingContext());

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with enum input, should return an enum instance", () => {
    const marshaller = new JavaEnumMarshaller();
    const context = new UnmarshallingContext(
      new Map([
        [
          `${FooEnum.__fqcn()}`,
          ((name: string) => {
            switch (name) {
              case "FOO":
                return FooEnum.FOO;
              case "BAR":
                return FooEnum.BAR;
              default:
                throw new Error(`Unknown value ${name} for enum AddressType!`);
            }
          }) as any
        ]
      ])
    );

    const input = new EnumStringValueBasedErraiObject(FooEnum.__fqcn(), FooEnum.FOO.name).asErraiObject();
    const output = marshaller.notNullUnmarshall(input, context);

    expect(output).toStrictEqual(FooEnum.FOO);
  });

  test("with invalid enum value, should throw error", () => {
    const marshaller = new JavaEnumMarshaller();
    const context = new UnmarshallingContext(
      new Map([
        [
          `${FooEnum.__fqcn()}`,
          ((name: string) => {
            switch (name) {
              case "FOO":
                return FooEnum.FOO;
              case "BAR":
                return FooEnum.BAR;
              default:
                throw new Error(`Unknown value ${name} for enum AddressType!`);
            }
          }) as any
        ]
      ])
    );

    const input = "abc" as any;
    const marshalledInput = new EnumStringValueBasedErraiObject(FooEnum.__fqcn(), input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with null value, should throw error", () => {
    const marshaller = new JavaEnumMarshaller();
    const context = new UnmarshallingContext(
      new Map([
        [
          `${FooEnum.__fqcn()}`,
          ((name: string) => {
            switch (name) {
              case "FOO":
                return FooEnum.FOO;
              case "BAR":
                return FooEnum.BAR;
              default:
                throw new Error(`Unknown value ${name} for enum AddressType!`);
            }
          }) as any
        ]
      ])
    );

    const input = null as any;
    const marshalledInput = new EnumStringValueBasedErraiObject(FooEnum.__fqcn(), input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });

  test("with undefined value, should throw error", () => {
    const marshaller = new JavaEnumMarshaller();
    const context = new UnmarshallingContext(
      new Map([
        [
          `${FooEnum.__fqcn()}`,
          ((name: string) => {
            switch (name) {
              case "FOO":
                return FooEnum.FOO;
              case "BAR":
                return FooEnum.BAR;
              default:
                throw new Error(`Unknown value ${name} for enum AddressType!`);
            }
          }) as any
        ]
      ])
    );

    const input = undefined as any;
    const marshalledInput = new EnumStringValueBasedErraiObject(FooEnum.__fqcn(), input).asErraiObject();

    expect(() => marshaller.notNullUnmarshall(marshalledInput, context)).toThrowError();
  });
});

class FooEnum extends JavaEnum<FooEnum> {
  public static readonly FOO: FooEnum = new FooEnum("FOO");
  public static readonly BAR: FooEnum = new FooEnum("BAR");

  protected readonly _fqcn: string = FooEnum.__fqcn();

  public static __fqcn(): string {
    return "com.app.my.AddressType";
  }

  public static values() {
    return [FooEnum.FOO, FooEnum.BAR];
  }
}
