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

import { JavaString } from "../../java-wrappers/JavaString";
import { MarshallingContext } from "../MarshallingContext";
import { NullableMarshaller } from "./NullableMarshaller";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { instanceOfString } from "../../util/TypeUtils";

export class JavaStringMarshaller extends NullableMarshaller<JavaString, string, string | JavaString, string> {
  public notNullMarshall(input: JavaString, ctx: MarshallingContext): string {
    return input.get();
  }

  public notNullUnmarshall(input: string | JavaString, ctx: UnmarshallingContext): string {
    if (instanceOfString(input)) {
      return input;
    }

    return input.get();
  }
}
