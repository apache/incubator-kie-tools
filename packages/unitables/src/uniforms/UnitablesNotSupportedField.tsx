/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { HTMLFieldProps } from "uniforms";
import { connectField, filterDOMProps } from "uniforms/esm";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";

export type UnitablesNotSupportedFieldProps = HTMLFieldProps<
  object,
  HTMLDivElement,
  { recursion: boolean; recursionRef: string }
>;

function UnitablesNotSupportedField({ recursion, recursionRef, ...props }: UnitablesNotSupportedFieldProps) {
  return wrapField(
    props as any,
    <div style={{ display: "flex" }} {...filterDOMProps(props)}>
      <div
        aria-label="field type not supported"
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          fontSize: "14px",
          backgroundColor: "rgb(240, 240, 240)",
          width: "100%",
        }}
      >
        Recursive structures are not supported yet
      </div>
    </div>
  );
}

export default connectField(UnitablesNotSupportedField);
