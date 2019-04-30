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
import { JavaFloat } from "../../java-wrappers/JavaFloat";
import { MarshallingContext } from "../MarshallingContext";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { instanceOfNumber } from "../../util/TypeUtils";
import { ErraiObject } from "../model/ErraiObject";
import { NumValBasedErraiObject } from "../model/NumValBasedErraiObject";

export class JavaFloatMarshaller extends NullableMarshaller<JavaFloat, number, ErraiObject | number, JavaFloat> {
  public notNullMarshall(input: JavaFloat, ctx: MarshallingContext): number {
    return input.get();
  }

  public notNullUnmarshall(input: ErraiObject | number, ctx: UnmarshallingContext): JavaFloat {
    if (instanceOfNumber(input)) {
      return new JavaFloat(`${input}`);
    }

    const valueFromJson = NumValBasedErraiObject.from(input).numVal;
    if (!JavaFloatMarshaller.isValid(valueFromJson)) {
      throw new Error(`Invalid float value ${valueFromJson}. Can't unmarshall json ${input}`);
    }

    return new JavaFloat(`${valueFromJson}`);
  }

  private static isValid(valueFromJson: any): boolean {
    if (valueFromJson === null || valueFromJson === undefined) {
      return false;
    }

    return instanceOfNumber(valueFromJson);
  }
}
