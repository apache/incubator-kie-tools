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
import { JavaDate } from "../../java-wrappers/JavaDate";
import { ErraiObject } from "../model/ErraiObject";
import { MarshallingContext } from "../MarshallingContext";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { isString } from "../../util/TypeUtils";
import { ValueBasedErraiObject } from "../model/ValueBasedErraiObject";
import { NumberUtils } from "../../util/NumberUtils";

export class JavaDateMarshaller extends NullableMarshaller<JavaDate, ErraiObject, ErraiObject, Date> {
  public notNullMarshall(input: JavaDate, ctx: MarshallingContext): ErraiObject {
    return new ValueBasedErraiObject((input as any)._fqcn, `${input.get().getTime()}`).asErraiObject();
  }

  public notNullUnmarshall(input: ErraiObject, ctx: UnmarshallingContext): Date {
    const valueFromJson = ValueBasedErraiObject.from(input).value;
    if (!JavaDateMarshaller.isValid(valueFromJson)) {
      throw new Error(`Invalid date value ${valueFromJson}. Can't unmarshall json ${input}`);
    }

    const asNumber = Number.parseInt(valueFromJson, 10);
    return new Date(asNumber);
  }

  private static isValid(input: any) {
    if (!input) {
      return false;
    }

    if (!isString(input)) {
      return false;
    }

    return NumberUtils.isNonNegativeIntegerString(input);
  }
}
