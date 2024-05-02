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
import { useMemo, useState, useCallback, useRef, useEffect } from "react";
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
  const [isEditing, setEditing] = useState(false);

  const onFeelBlur = useCallback(
    (valueOnBlur: string) => {
      if (value !== valueOnBlur) {
        onSave?.(valueOnBlur.trim());
      }
      setEditing(false);
    },
    [onSave, value]
  );

  const onFeelChange = useCallback((_, content, preview) => {
    setPreview(preview);
  }, []);

  const onPreviewChanged = useCallback((newPreview: string) => setPreview(newPreview), []);

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

  useEffect(() => {
    setEditingValue((prev) => (isEditing ? prev : value));
  }, [isEditing, value]);

  const onKeyDown = useCallback(
    (e) => {
      // When inside FEEL Input, all keyboard events should be kept inside it.
      // Exceptions to this strategy are handled on `onFeelKeyDown`.
      if (isEditing) {
        e.stopPropagation();
      }

      // This is used to start editing a cell without being in edit mode.
      if (!isEditing) {
        setEditing(true);
      }
    },
    [isEditing]
  );

  return (
    // FeelInput doens't react to `onFeelChange` updates
    // making it necessary to add a key to force a re-render;
    <div style={{ display: "flex", flexDirection: "column", width: "100%" }} onKeyDown={onKeyDown}>
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
          value={isReadonly || !isEditing ? value : editingValue}
          onChange={onFeelChange}
          onBlur={onFeelBlur}
          onPreviewChanged={onPreviewChanged}
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
