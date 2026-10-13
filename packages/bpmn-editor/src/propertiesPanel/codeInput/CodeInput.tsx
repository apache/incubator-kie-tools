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
import { MenuToggle } from "@patternfly/react-core/dist/js/components/MenuToggle";
import { Select, SelectList, SelectOption } from "@patternfly/react-core/dist/js/components/Select";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import "./CodeInput.css";
import { useBpmnEditorI18n } from "../../i18n";
import { ScriptLanguage } from "../scriptFormats";

export function CodeInput({
  label,
  availableLanguages,
  currentLanguage,
  value,
  onChange,
  onLanguageChange,
}: {
  label: string;
  availableLanguages: string[];
  /** Currently selected language. Defaults to the first of `languages`. */
  currentLanguage?: string;
  value: string | undefined;
  onChange: (e: React.FormEvent, newCode: string) => void;
  /** When provided, a dropdown to select the language is shown instead of a static label. */
  onLanguageChange?: (newLanguage: ScriptLanguage) => void;
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const [isLanguageDropdownOpen, setIsLanguageDropdownOpen] = React.useState(false);

  const selectedLanguage = currentLanguage ?? availableLanguages[0];

  const hasLanguageSelector = !!onLanguageChange && availableLanguages.length > 1;
  const languageSelectId = `${label}-language-select`;

  return (
    <>
      <FormGroup label={`${label}`}>
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
        {!hasLanguageSelector && (
          <div style={{ display: "flex", justifyContent: "flex-end", marginTop: "4px" }}>
            <Label isCompact={true}>{selectedLanguage}</Label>
          </div>
        )}
      </FormGroup>
      {hasLanguageSelector && (
        <FormGroup label={i18n.propertiesPanel.language} fieldId={languageSelectId}>
          <Select
            id={languageSelectId}
            isOpen={isLanguageDropdownOpen}
            selected={selectedLanguage}
            onOpenChange={setIsLanguageDropdownOpen}
            onSelect={(_event, selection) => {
              onLanguageChange(selection as ScriptLanguage);
              setIsLanguageDropdownOpen(false);
            }}
            popperProps={{ appendTo: document.body, width: "trigger" }}
            toggle={(toggleRef: React.Ref<any>) => (
              <MenuToggle
                ref={toggleRef}
                isFullWidth={true}
                isExpanded={isLanguageDropdownOpen}
                isDisabled={isReadOnly}
                onClick={() => setIsLanguageDropdownOpen((open) => !open)}
              >
                {selectedLanguage}
              </MenuToggle>
            )}
          >
            <SelectList>
              {availableLanguages.map((l) => (
                <SelectOption key={l} value={l}>
                  {l}
                </SelectOption>
              ))}
            </SelectList>
          </Select>
        </FormGroup>
      )}
    </>
  );
}
