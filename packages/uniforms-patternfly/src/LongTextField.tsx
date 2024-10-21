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

import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import * as React from "react";
import { connectField, HTMLFieldProps } from "uniforms";
import wrapField from "./wrapField";

export type LongTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    inputRef?: React.RefObject<HTMLTextAreaElement>;
    onChange: (value: string, event: React.ChangeEvent<HTMLTextAreaElement>) => void;
    value?: string;
    prefix?: string;
    autoResize?: boolean;
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
  rows,
  autoResize,
  ...props
}: LongTextFieldProps) {
  return wrapField(
    { id, label, name, value, ...props },
    <TextArea
      id={id}
      disabled={disabled}
      name={name}
      data-testid={"long-text-field"}
      aria-label={name}
      onChange={(event, value) => onChange(event.target.value)}
      placeholder={placeholder}
      ref={inputRef}
      resizeOrientation="vertical"
      value={value ?? ""}
      rows={rows}
      autoResize={autoResize}
    />
  );
}

export default connectField(LongTextField);
