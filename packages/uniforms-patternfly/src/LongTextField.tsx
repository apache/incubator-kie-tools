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
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";

export type LongTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    inputRef?: React.RefObject<HTMLTextAreaElement>;
    onChange: (value: string, event: React.ChangeEvent<HTMLTextAreaElement>) => void;
    value?: string;
    prefix?: string;
  }
>;

function LongTextField({
  disabled,
  id,
  inputRef,
  label,
  name,
  onChange,
  placeholder,
  value,
  ...props
}: LongTextFieldProps) {
  return (
    <div data-testid={"long-text-field"} {...filterDOMProps(props)}>
      {label && <label htmlFor={id}>{label}</label>}
      <TextArea
        id={id}
        disabled={disabled}
        name={name}
        aria-label={name}
        onChange={(value, event) => onChange(event.target.value)}
        placeholder={placeholder}
        ref={inputRef}
        value={value ?? ""}
      />
    </div>
  );
}

export default connectField(LongTextField);
