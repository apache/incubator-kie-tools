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
import { useCallback, useState, useMemo } from "react";
import { CodeEditor, Language } from "@patternfly/react-code-editor/dist/esm";
import { connectField, HTMLFieldProps } from "uniforms";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";

export type CodeEditorTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    helperText?: string;
    label?: string;
    language?: Language;
    onChange: (value?: any) => void;
    prefix?: string;
    value?: any;
  }
>;

function CodeEditorTextField({
  disabled,
  height,
  helperText,
  language = Language.json,
  name,
  value,
  ...props
}: CodeEditorTextFieldProps) {
  const [isInvalid, setIsInvalid] = useState<string | boolean>(false);

  const onChange = useCallback(
    (val: string) => {
      if (language === Language.json) {
        try {
          const stringifiedValue = JSON.parse(val);
          props.onChange(stringifiedValue);
          setIsInvalid(false);
        } catch (error) {
          setIsInvalid("Invalid JSON syntax");
        }
      } else {
        props.onChange(val);
      }
    },
    [language, props]
  );

  const code = useMemo(() => (language === Language.json ? JSON.stringify(value) : value), [value, language]);

  const renderCodeEditor = useMemo(
    () => (
      <CodeEditor
        code={code}
        height={height ? `${height}` : "200px"}
        isReadOnly={disabled}
        language={language}
        onChange={onChange}
      />
      // eslint-disable-next-line react-hooks/exhaustive-deps -- this is needed to prevent CodeEditor to be rendered if `code` or `onChange` are updated.
    ),
    [language, disabled, height]
  );

  return wrapField(
    { ...props, help: helperText },
    <>
      {renderCodeEditor}
      {isInvalid && (
        <div
          style={{
            fontSize: "0.875rem",
            color: "#c9190b",
            marginTop: "0.25rem",
          }}
        >
          {isInvalid}
        </div>
      )}
    </>
  );
}

export default connectField(CodeEditorTextField);
