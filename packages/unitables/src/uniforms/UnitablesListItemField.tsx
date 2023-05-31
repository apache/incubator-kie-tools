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
import { ReactNode } from "react";
import { connectField } from "uniforms";
import { AutoField, ListDelField } from "@kie-tools/uniforms-patternfly/dist/esm";
import { HTMLFieldProps } from "uniforms/esm";

export type UnitablesListItemFieldProps = HTMLFieldProps<
  unknown[],
  HTMLDivElement,
  {
    children?: ReactNode;
    value?: unknown;
    name: string;
    style?: object;
  }
>;

function UnitablesListItemField(props: React.PropsWithChildren<UnitablesListItemFieldProps>) {
  return (
    <div
      data-testid={"unitables-list-item-field"}
      style={{
        display: "flex",
        justifyContent: "space-between",
        width: "100%",
        ...props.style,
      }}
    >
      <div style={{ width: "100%", borderRight: "1px solid var(--pf-global--palette--black-300)" }}>
        {props.children ?? <AutoField label={null} name={""} />}
      </div>
      <div>
        <ListDelField name={""} style={{ minWidth: "60px", maxWidth: "60px" }} />
      </div>
    </div>
  );
}

export default connectField<UnitablesListItemFieldProps>(UnitablesListItemField, {
  initialValue: false,
});
