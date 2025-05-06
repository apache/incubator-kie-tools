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
import { useCallback, useState } from "react";
import { CodeEditor, Language } from "@patternfly/react-code-editor/dist/js/components/CodeEditor";
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";

export type CodeEditorTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    onChange: (value?: string) => void;
    value?: string;
    prefix?: string;
    language?: Language;
  }
>;

function CodeEditorTextField({ disabled, height, label, language, name, value, ...props }: CodeEditorTextFieldProps) {
  const [hiddenValue, setHiddenValue] = useState(value ?? "");

  const onChange = useCallback(
    (val: string) => {
      setHiddenValue(val);
      props.onChange(val);
    },
    [props]
  );

  return (
    <div data-testid={"code-editor-field"} {...filterDOMProps(props)}>
      {label && (
        <label>
          <b>{label}</b>
        </label>
      )}
      <CodeEditor
        code={value ?? ""}
        height={height ? `${height}` : "200px"}
        isReadOnly={disabled}
        language={language ?? Language.json}
        onChange={onChange}
      />
      <input type="hidden" name={name} value={hiddenValue} data-testid={"code-editor-hidden-field"} />
    </div>
  );
}

export default connectField(CodeEditorTextField);
