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

import { NullableMarshaller } from "./NullableMarshaller";
import { JavaEnum } from "../../java-wrappers";
import { ErraiObject } from "../model/ErraiObject";
import { MarshallingContext } from "../MarshallingContext";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { EnumStringValueBasedErraiObject } from "../model/EnumStringValueBasedErraiObject";

export class JavaEnumMarshaller<T extends JavaEnum<T>> extends NullableMarshaller<
  JavaEnum<T>,
  ErraiObject,
  ErraiObject,
  JavaEnum<T>
> {
  public notNullMarshall(input: JavaEnum<T>, ctx: MarshallingContext): ErraiObject {
    return new EnumStringValueBasedErraiObject((input as any)._fqcn, input.name).asErraiObject();
  }

  public notNullUnmarshall(input: ErraiObject, ctx: UnmarshallingContext): JavaEnum<T> {
    const valueObject = EnumStringValueBasedErraiObject.from(input);
    const factory = ctx.getFactory(valueObject.encodedType);

    // the factory method for enums receives the enum name and returns the appropriate enum value
    return (factory as any)(valueObject.enumValueName);
  }
}
