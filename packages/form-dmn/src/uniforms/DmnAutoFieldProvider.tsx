/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { AutoField, AutoFields } from "uniforms-patternfly";
import { DmnFeelContextField } from "./DmnFeelContextField";
import * as React from "react";
import { FEEL_CONTEXT } from "./DmnFormJsonSchemaBridge";

export function DmnAutoFieldProvider() {
  return (
    <AutoField.componentDetectorContext.Provider
      value={(props, uniforms) => {
        if (props.field?.["x-dmn-type"] === FEEL_CONTEXT) {
          return DmnFeelContextField;
        }
        return AutoField.defaultComponentDetector(props, uniforms);
      }}
    >
      <AutoFields />
    </AutoField.componentDetectorContext.Provider>
  );
}
