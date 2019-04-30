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

import { JavaLong } from "../../java-wrappers/JavaLong";
import { ErraiObject } from "../model/ErraiObject";
import { MarshallingContext } from "../MarshallingContext";
import { NullableMarshaller } from "./NullableMarshaller";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { NumValBasedErraiObject } from "../model/NumValBasedErraiObject";
import { NumberUtils } from "../../util/NumberUtils";

export class JavaLongMarshaller extends NullableMarshaller<JavaLong, ErraiObject, ErraiObject, JavaLong> {
  public notNullMarshall(input: JavaLong, ctx: MarshallingContext): ErraiObject {
    const asString = `${input.get().toString(10)}`;
    return new NumValBasedErraiObject((input as any)._fqcn, asString).asErraiObject();
  }

  public notNullUnmarshall(input: ErraiObject, ctx: UnmarshallingContext): JavaLong {
    const valueFromJson = NumValBasedErraiObject.from(input).numVal as string;

    if (!JavaLongMarshaller.isValid(valueFromJson)) {
      throw new Error(`Invalid long value ${valueFromJson}. Can't unmarshall json ${input}`);
    }

    return new JavaLong(valueFromJson);
  }

  private static isValid(jsonValue: string) {
    if (jsonValue === null || jsonValue === undefined) {
      return false;
    }

    return NumberUtils.isIntegerString(jsonValue);
  }
}
