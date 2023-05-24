/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { defaultDmnRunnerAutoFieldValue } from "@kie-tools/dmn-runner/dist/uniforms";
import { Context, GuaranteedProps } from "uniforms/esm";
import UnitablesListField from "./UnitablesListField";
import UnitablesNestField from "./UnitablesNestField";

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
  return defaultDmnRunnerAutoFieldValue(props, uniforms);
}
