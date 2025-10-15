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
import { CodeEditorProps } from "@patternfly/react-code-editor";

export const Language = {
  json: "json",
};

export const CodeEditor = ({ isReadOnly, height, language, code, onChange }: CodeEditorProps) => {
  return (
    <textarea
      data-testid="code-editor-textarea"
      value={code}
      readOnly={isReadOnly}
      onChange={(e) => onChange?.(e.target.value, e as any)}
      style={{ height }}
      data-language={language}
    />
  );
};
