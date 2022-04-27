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

import { Divider, Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useState, useRef, useEffect } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as _ from "lodash";
import { DataType, DataTypeProps } from "../../api";
import { useBoxedExpression } from "../../context";

export interface DataTypeSelectorProps {
  /** The pre-selected data type */
  selectedDataType: DataType;
  /** On DataType selection callback */
  onDataTypeChange: (dataType: DataType) => void;
  /** By default the menu will be appended inline, but it is possible to append on the parent or on other elements */
  /** Callback for toggle select behavior */
  onToggle?: (isOpen: boolean) => void;
  /** event fired when the user press a key */
  onKeyDown?: (e: React.KeyboardEvent) => void;
  menuAppendTo?: HTMLElement | "inline" | (() => HTMLElement) | "parent";
}

export const DataTypeSelector: React.FunctionComponent<DataTypeSelectorProps> = ({
  selectedDataType,
  onDataTypeChange,
  menuAppendTo,
  onToggle = () => {},
  onKeyDown = () => {},
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { dataTypes } = useBoxedExpression();

  const [dataTypeSelectIsOpen, setDataTypeSelectIsOpen] = useState(false);

  const selectWrapperRef = useRef<HTMLDivElement>(null);

  const onDataTypeSelect = useCallback(
    (event, selection) => {
      /* this setTimeout keeps the context menu open after type selection changes. Without this Popover component thinks there has been a click outside the context menu, after DataTypeSelector has changed. This because the Select component has been removed from the html*/
      setTimeout(() => setDataTypeSelectIsOpen(false), 0);

      onDataTypeChange(selection);

      // Because Select leave the focus to the detached btn, give back the focus to the selectWrapperRef
      (selectWrapperRef.current?.querySelector("button") as HTMLInputElement)?.focus();
    },
    [onDataTypeChange]
  );

  const buildOptionsByGroup = useCallback(
    (key: "default" | "custom", options: DataTypeProps[]) => {
      return (
        <SelectGroup label={i18n.dataTypeDropDown[key]} key={key}>
          {_.chain(options)
            .map(({ name }) => <SelectOption key={name} value={name} data-ouia-component-id={name} />)
            .value()}
        </SelectGroup>
      );
    },
    [i18n.dataTypeDropDown]
  );

  const getDataTypes = useCallback(() => {
    const [customDataTypes, defaultDataTypes] = _.chain(dataTypes).partition("isCustom").value();
    const dataTypeGroups = [buildOptionsByGroup("default", defaultDataTypes)];
    if (!_.isEmpty(customDataTypes)) {
      dataTypeGroups.push(<Divider key="divider" />);
      dataTypeGroups.push(buildOptionsByGroup("custom", customDataTypes));
    }
    return dataTypeGroups;
  }, [buildOptionsByGroup, dataTypes]);

  const onFilteringDataTypes = useCallback(
    (_, textInput: string) => {
      if (textInput === "") {
        return getDataTypes();
      } else {
        return getDataTypes().reduce((groups: JSX.Element[], group: JSX.Element) => {
          const filteredGroup = React.cloneElement(group, {
            children: group.props?.children?.filter((item: React.ReactElement) => {
              return item.props.value.toLowerCase().includes(textInput.toLowerCase());
            }),
          });
          if (filteredGroup && filteredGroup.props?.children?.length > 0) {
            groups.push(filteredGroup);
          }
          return groups;
        }, []);
      }
    },
    [getDataTypes]
  );

  const onDataTypeSelectToggle = useCallback(
    (isOpen) => {
      setDataTypeSelectIsOpen(isOpen);
      onToggle(isOpen);
    },
    [onToggle]
  );

  return (
    <div ref={selectWrapperRef} tabIndex={-1} onKeyDown={onKeyDown}>
      <Select
        menuAppendTo={menuAppendTo}
        ouiaId="edit-expression-data-type"
        variant={SelectVariant.single}
        typeAheadAriaLabel={i18n.choose}
        onToggle={onDataTypeSelectToggle}
        onSelect={onDataTypeSelect}
        onFilter={onFilteringDataTypes}
        isOpen={dataTypeSelectIsOpen}
        selections={selectedDataType}
        isGrouped
        hasInlineFilter
        inlineFilterPlaceholderText={i18n.choose}
        maxHeight={500}
      >
        {getDataTypes()}
      </Select>
    </div>
  );
};
