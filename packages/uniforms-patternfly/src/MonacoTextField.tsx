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

import * as React from "react";
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
// import * as Monaco from "@kie-tools-core/monaco-editor";
import * as Monaco from "monaco-editor";
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";

export type MonacoTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    inputRef?: React.RefObject<HTMLTextAreaElement>;
    onChange: (value: string, event: React.ChangeEvent<HTMLTextAreaElement>) => void;
    value?: string;
    prefix?: string;
  }
>;

function MonacoTextField({
  disabled,
  id,
  inputRef,
  label,
  name,
  onChange,
  placeholder,
  value,
  ...props
}: MonacoTextFieldProps) {
  const container = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const editor = Monaco.editor.create(container.current!, {
      value: `{"name":"John", "age":30, "car":null}`,
      language: "json",
    });
    editor.onDidChangeModelContent(() => {
      const newValue = editor.getValue();
      onChange?.(newValue, {} as React.ChangeEvent<HTMLTextAreaElement>);
    });
  }, []);

  return (
    <div data-testid={"monaco-field"} {...filterDOMProps(props)}>
      {label && (
        <label>
          <b>{label}</b>
        </label>
      )}
      <div id={id} aria-label={name} style={{ height: "250px" }} ref={container} />
    </div>
  );
}

export default connectField(MonacoTextField);
