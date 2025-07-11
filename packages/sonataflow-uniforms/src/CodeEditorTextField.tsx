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
import { useCallback, useMemo, useState } from "react";
import { CodeEditor, Language } from "@patternfly/react-code-editor";
import { connectField, HTMLFieldProps } from "uniforms";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";

export type CodeEditorTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    helperText?: string;
    label?: string;
    language?: Language;
    onChange: (value?: string) => void;
    prefix?: string;
    value?: string;
  }
>;

function CodeEditorTextField({
  disabled,
  height,
  helperText,
  language = Language.json,
  name,
  value = "",
  ...props
}: CodeEditorTextFieldProps) {
  const [hiddenValue, setHiddenValue] = useState(value ?? "");

  const isInvalid = useMemo(() => {
    if (!value.trim()) {
      return true;
    }

    if (language === Language.json) {
      try {
        JSON.parse(value);
        return true;
      } catch (error) {
        return "Invalid JSON syntax";
      }
    }

    return false;
  }, [value, language]);

  const onChange = useCallback(
    (val: string) => {
      setHiddenValue(val);
      props.onChange(val);
    },
    [props]
  );

  const stringifiedValue = useMemo(() => {
    if (value && typeof value === "object") {
      const isEmptyObj = !Object.keys(value).length;
      return isEmptyObj ? "" : JSON.stringify(value);
    }
    return value;
  }, [value]);

  return wrapField(
    { ...props, help: helperText },
    <>
      <CodeEditor
        code={stringifiedValue ?? ""}
        height={height ? `${height}` : "200px"}
        isReadOnly={disabled}
        language={language}
        onChange={onChange}
      />
      <input type="hidden" name={name} value={hiddenValue} data-testid={"code-editor-hidden-field"} />
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
