/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Context, GuaranteedProps } from "uniforms/esm";
import { AutoField } from "@kie-tools/uniforms-patternfly/dist/esm";
import { X_DMN_TYPE } from "@kie-tools/extended-services-api";
import { DmnFeelContextField } from "./DmnFeelContextField";

export function defaultDmnRunnerAutoFieldValue(
  props: GuaranteedProps<unknown>,
  uniforms: Context<Record<string, unknown>>
) {
  if (props.field?.["x-dmn-type"] === X_DMN_TYPE.CONTEXT) {
    return DmnFeelContextField;
  }
  return AutoField.defaultComponentDetector(props, uniforms);
}
