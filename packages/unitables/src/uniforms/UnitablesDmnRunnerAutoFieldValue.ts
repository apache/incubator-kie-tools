/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { defaultDmnRunnerAutoFieldValue } from "@kie-tools/dmn-runner/dist/uniforms";
import { Context, GuaranteedProps } from "uniforms/esm";
import UnitablesListField from "./UnitablesListField";
import UnitablesNestField from "./UnitablesNestField";
import UnitablesNotSupportedField from "./UnitablesNotSupportedField";
import { RECURSION_KEYWORD } from "@kie-tools/dmn-runner/dist/constants";

export function unitablesDmnRunnerAutoFieldValue(
  props: GuaranteedProps<unknown>,
  uniforms: Context<Record<string, unknown>>
) {
  if (props.field?.type === "array") {
    return UnitablesListField;
  }
  if (props.field?.type === "object") {
    return UnitablesNestField;
  }
  if (props.field?.[`${RECURSION_KEYWORD}`]) {
    return UnitablesNotSupportedField;
  }
  return defaultDmnRunnerAutoFieldValue(props, uniforms);
}
