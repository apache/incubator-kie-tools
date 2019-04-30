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
import { JavaBoolean } from "../../java-wrappers/JavaBoolean";
import { MarshallingContext } from "../MarshallingContext";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { ErraiObject } from "../model/ErraiObject";
import { instanceOfBoolean } from "../../util/TypeUtils";
import { NumValBasedErraiObject } from "../model/NumValBasedErraiObject";

export class JavaBooleanMarshaller extends NullableMarshaller<JavaBoolean, boolean, ErraiObject | boolean, boolean> {
  public notNullMarshall(input: JavaBoolean, ctx: MarshallingContext): boolean {
    return input.get();
  }

  public notNullUnmarshall(input: ErraiObject | boolean, ctx: UnmarshallingContext): boolean {
    if (instanceOfBoolean(input)) {
      return input;
    }

    const valueFromJson = NumValBasedErraiObject.from(input).numVal;

    if (!JavaBooleanMarshaller.isValid(valueFromJson)) {
      throw new Error(`Invalid boolean value ${valueFromJson}. Can't unmarshall json ${input}`);
    }

    return valueFromJson as boolean;
  }

  private static isValid(valueFromJson: any): boolean {
    if (valueFromJson === null || valueFromJson === undefined) {
      return false;
    }

    return instanceOfBoolean(valueFromJson);
  }
}
