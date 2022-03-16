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

import * as React from "react";
import { Ref, useCallback, useMemo } from "react";
import { TextInput, TextInputProps } from "@patternfly/react-core/dist/js/components/TextInput";
import { connectField } from "uniforms";

type DmnFeelContextComponentProps = {
  id: string;
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: string) => void;
  value?: string;
  disabled: boolean;
  error?: boolean;
  errorMessage?: string;
  field?: { format: string };
} & Omit<TextInputProps, "isDisabled">;

function DmnFeelContext(props: DmnFeelContextComponentProps) {
  const stringifiedValue = useMemo(() => {
    if (props.value && typeof props.value === "object") {
      return JSON.stringify(props.value);
    }
    return props.value;
  }, [props.value]);

  const onChange = useCallback(
    (value: string, event: React.FormEvent<HTMLInputElement>) => {
      try {
        const parsedObject = JSON.parse((event.target as any).value);
        props.onChange(parsedObject);
      } catch (err) {
        props.onChange((event.target as any).value);
      }
    },
    [props.onChange]
  );

  return (
    <>
      <TextInput
        name={props.name}
        isDisabled={props.disabled}
        validated={props.error ? "error" : "default"}
        onChange={onChange}
        placeholder={props.placeholder}
        ref={props.inputRef}
        type={props.type ?? "text"}
        value={stringifiedValue ?? ""}
      />
    </>
  );
}

export const DmnFeelContextField = connectField(DmnFeelContext);
