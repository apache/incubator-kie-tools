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

import { NumberWrapper } from "../../../java-wrappers/NumberWrapper";
import { JavaBoolean } from "../../../java-wrappers";
import { Portable } from "../../../marshalling/Portable";
import { ErraiObject } from "../../model/ErraiObject";
import { MarshallingContext } from "../../MarshallingContext";
import { MarshallerProvider } from "../../MarshallerProvider";
import { JavaWrapperUtils } from "../../../java-wrappers/JavaWrapperUtils";
import { NumValBasedErraiObject } from "../../model/NumValBasedErraiObject";

export class GenericsTypeMarshallingUtils {
  private static shouldWrapAsGenericsType(value: Portable<any>) {
    return value instanceof NumberWrapper || value instanceof JavaBoolean;
  }

  private static wrapGenericsTypeElement(value: Portable<any>, marshalledValue: any): ErraiObject {
    // This is mandatory in order to comply with errai-marshalling protocol.
    // When marshalling numeric or boolean values, we use its raw value, without any ErraiObject envelope.
    // But, when the value is a generic type, we always wrap it inside an ErraiObject
    return new NumValBasedErraiObject((value as any)._fqcn, marshalledValue).asErraiObject();
  }

  public static marshallGenericsTypeElement(value: any, ctx: MarshallingContext): ErraiObject {
    // apply automatic native types -> java types conversion
    const enhancedInput = JavaWrapperUtils.wrapIfNeeded(value);

    const marshaller = MarshallerProvider.getForObject(enhancedInput);
    const marshalledValue = marshaller.marshall(enhancedInput, ctx);

    if (this.shouldWrapAsGenericsType(enhancedInput)) {
      return this.wrapGenericsTypeElement(enhancedInput, marshalledValue)!;
    }

    return marshalledValue;
  }
}
