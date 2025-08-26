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
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import { useState, useRef } from "react";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { EXPRESSION_LANGUAGES_LATEST } from "@kie-tools/dmn-marshaller";

export function ExpressionLangaugeSelect({
  OnClear,
  onSelect,
  allLanguages,
  selections,
}: {
  OnClear: () => void;
  onSelect?: (event: React.ChangeEvent<HTMLInputElement>, value: string) => void;
  allLanguages: string[];
  selections: string;
}) {
  const [isExpressionLanguageSelectOpen, setExpressionLanguageSelectOpen] = useState(false);
  const [customLanguages, setCustomLanguages] = useState<string[]>(EXPRESSION_LANGUAGES_LATEST);

  const settings = useSettings();
  const toggleRef = useRef<HTMLButtonElement>(null);

  return (
    <Select
      toggleRef={toggleRef}
      variant={SelectVariant.typeahead}
      aria-label={"Expression language"}
      isOpen={isExpressionLanguageSelectOpen}
      onSelect={onSelect}
      onClear={OnClear}
      isCreatable
      onCreateOption={(val) => {
        if (val && !customLanguages.includes(val)) {
          setCustomLanguages((prev) => [...prev, val]);
        }
      }}
      onToggle={(event, isExpanded) => setExpressionLanguageSelectOpen(isExpanded)}
      isDisabled={settings.isReadOnly}
      selections={selections}
      placeholderText={"Enter an expression language..."}
    >
      {allLanguages?.map((language: string) => (
        <SelectOption key={language} value={language}>
          {language}
        </SelectOption>
      ))}
    </Select>
  );
}
