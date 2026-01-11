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
import { useBpmnEditorStore } from "../../store/StoreContext";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import "./CodeInput.css";
import { useBpmnEditorI18n } from "../../i18n";

export function CodeInput({
  label,
  languages,
  value,
  onChange,
}: {
  label: string;
  languages: string[];
  value: string | undefined;
  onChange: (e: React.FormEvent, newCode: string) => void;
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const languageLabel = React.useMemo(() => {
    return (
      <Label
        isCompact={true}
        style={{
          margin: "16px",
          top: "24px",
          right: "0",
          position: "absolute",
        }}
      >
        {languages[0]}
      </Label>
    );
  }, [languages]);

  return (
    <FormGroup label={`${label}`} style={{ position: "relative" }}>
      <TextArea
        aria-label={`${label}`}
        type={"text"}
        isDisabled={isReadOnly}
        value={value}
        onChange={onChange}
        placeholder={i18n.propertiesPanel.codePlaceholder}
        style={{ resize: "vertical", minHeight: "40px", fontFamily: "monospace" }}
        rows={3}
      />
      {languageLabel}
    </FormGroup>
  );
}
