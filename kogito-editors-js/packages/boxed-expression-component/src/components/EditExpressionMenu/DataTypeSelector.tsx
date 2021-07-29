/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useState } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as _ from "lodash";
import { DataType } from "../../api";

export interface DataTypeSelectorProps {
  /** The pre-selected data type */
  selectedDataType: DataType;
  /** On DataType selection callback */
  onDataTypeChange: (dataType: DataType) => void;
  /** By default the menu will be appended inline, but it is possible to append on the parent or on other elements */
  menuAppendTo?: HTMLElement | "inline" | (() => HTMLElement) | "parent";
}

export const DataTypeSelector: React.FunctionComponent<DataTypeSelectorProps> = ({
  selectedDataType,
  onDataTypeChange,
  menuAppendTo,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [dataTypeSelectOpen, setDataTypeSelectOpen] = useState(false);

  const onDataTypeSelect = useCallback(
    (event, selection) => {
      setDataTypeSelectOpen(false);
      onDataTypeChange(selection);
    },
    [onDataTypeChange]
  );

  const getDataTypes = useCallback(() => {
    return _.map(Object.values(DataType), (key) => (
      <SelectOption key={key} value={key} data-ouia-component-id={key}>
        {key}
      </SelectOption>
    ));
  }, []);

  const onDataTypeFilter = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      let input: RegExp;
      try {
        input = new RegExp(e.target.value, "i");
      } catch (exception) {
        return getDataTypes();
      }
      return e.target.value !== "" ? getDataTypes().filter((child) => input.test(child.props.value)) : getDataTypes();
    },
    [getDataTypes]
  );

  const onDataTypeSelectToggle = useCallback((isOpen) => setDataTypeSelectOpen(isOpen), []);

  return (
    <Select
      menuAppendTo={menuAppendTo}
      ouiaId="edit-expression-data-type"
      variant={SelectVariant.typeahead}
      typeAheadAriaLabel={i18n.choose}
      onToggle={onDataTypeSelectToggle}
      onSelect={onDataTypeSelect}
      onFilter={onDataTypeFilter}
      isOpen={dataTypeSelectOpen}
      selections={selectedDataType}
      hasInlineFilter
      inlineFilterPlaceholderText={i18n.choose}
    >
      {getDataTypes()}
    </Select>
  );
};
