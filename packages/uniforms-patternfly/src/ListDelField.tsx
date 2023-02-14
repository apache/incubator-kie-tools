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
import { Button, ButtonProps } from "@patternfly/react-core/dist/js/components/Button";
import { MinusCircleIcon } from "@patternfly/react-icons/dist/js/icons/minus-circle-icon";
import { connectField, FieldProps, filterDOMProps, joinName, useField } from "uniforms";

export type ListDelFieldProps = FieldProps<unknown, ButtonProps, { icon?: ReactNode }>;

function ListDelField({ name, disabled, ...props }: ListDelFieldProps) {
  const nameParts = joinName(null, name);
  const nameIndex = +nameParts[nameParts.length - 1];
  const parentName = joinName(nameParts.slice(0, -1));
  const parent = useField<{ minCount?: number }, unknown[]>(parentName, {}, { absoluteName: true })[0];

  const limitNotReached = !disabled && !(parent.minCount! >= parent.value!.length);

  return (
    <Button
      data-testid={"list-del-field"}
      disabled={!limitNotReached || disabled}
      variant="plain"
      style={{ paddingLeft: "0", paddingRight: "0" }}
      onClick={() => {
        const value = parent.value!.slice();
        value.splice(nameIndex, 1);
        !disabled && limitNotReached && parent.onChange(value);
      }}
      {...filterDOMProps(props)}
    >
      <MinusCircleIcon color="#cc0000" />
    </Button>
  );
}

export default connectField<ListDelFieldProps>(ListDelField, {
  initialValue: false,
  kind: "leaf",
});
