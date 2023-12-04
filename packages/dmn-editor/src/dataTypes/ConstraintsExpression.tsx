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
import { useMemo, useState, useCallback } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import "./ConstraintsExpression.css";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import InfoIcon from "@patternfly/react-icons/dist/js/icons/info-icon";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { TypeHelper } from "./Constraints";

export function ConstraintsExpression({
  isReadonly,
  value,
  onSave,
}: {
  isReadonly: boolean;
  value?: string;
  savedValue?: string;
  type: DmnBuiltInDataType;
  typeHelper?: TypeHelper;
  onSave?: (value?: string) => void;
  isDisabled?: boolean;
}) {
  const [preview, setPreview] = useState(value ?? "");
  const [editingValue, setEditingValue] = useState(value);
  const onFeelChange = useCallback(
    (_, content, preview) => {
      onSave?.(content.trim());
      setPreview(preview);
    },
    [onSave]
  );

  const monacoOptions = useMemo(
    () => ({
      fixedOverflowWidgets: true,
      lineNumbers: "off",
      fontSize: 16,
      renderLineHighlight: "none",
      lineDecorationsWidth: 1,
      automaticLayout: true,
      "semanticHighlighting.enabled": true,
    }),
    []
  );

  return (
    <div style={{ display: "flex", flexDirection: "column", width: "100%" }}>
      {isReadonly && (
        <Title size={"md"} headingLevel="h5" style={{ paddingBottom: "10px" }}>
          Equivalent FEEL expression:
        </Title>
      )}

      <div
        style={
          !isReadonly
            ? { flexGrow: 1, flexShrink: 0, border: "solid 1px lightgray", borderRadius: "4px" }
            : { flexGrow: 1, flexShrink: 0, height: "22px" }
        }
      >
        {isReadonly &&
          (value ? (
            <span className="editable-cell-value pf-u-text-break-word" dangerouslySetInnerHTML={{ __html: preview }} />
          ) : (
            <p style={{ fontStyle: "italic" }}>{`<None>`}</p>
          ))}
        <FeelInput
          value={isReadonly ? value : editingValue}
          onChange={onFeelChange}
          onPreviewChanged={setPreview}
          enabled={!isReadonly}
          options={monacoOptions as any}
        />
      </div>
      <HelperText>
        {!isReadonly && (
          <HelperTextItem variant="indeterminate" icon={<InfoIcon />}>
            Check the{" "}
            <a target={"_blank"} href={"https://kiegroup.github.io/dmn-feel-handbook/#feel-values"}>
              FEEL handbook
            </a>{" "}
            to help you on creating your expressions.
          </HelperTextItem>
        )}
      </HelperText>
    </div>
  );
}
