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
import { DmnBuiltInDataType, DmnDataType } from "../../api";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export interface DataTypeSelectorProps {
  /** The pre-selected data type */
  value: DmnBuiltInDataType;
  /** On DataType selection callback */
  onChange: (dataType: DmnBuiltInDataType) => void;
  /** By default the menu will be appended inline, but it is possible to append on the parent or on other elements */
  /** Callback for toggle select behavior */
  onToggle?: (isOpen: boolean) => void;
  /** event fired when the user press a key */
  onKeyDown?: (e: React.KeyboardEvent) => void;
  menuAppendTo?: HTMLElement | "inline" | (() => HTMLElement) | "parent";
}

export const DataTypeSelector: React.FunctionComponent<DataTypeSelectorProps> = ({
  value,
  onChange,
  menuAppendTo,
  onToggle = () => {},
  onKeyDown = () => {},
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { dataTypes } = useBoxedExpressionEditor();

  const [isOpen, setOpen] = useState(false);

  const selectContainerRef = useRef<HTMLDivElement>(null);

  const onSelect = useCallback(
    (event, selection) => {
      /* this setTimeout keeps the context menu open after type selection changes. Without this Popover component thinks there has been a click outside the context menu, after DataTypeSelector has changed. This because the Select component has been removed from the html*/
      setTimeout(() => setOpen(false), 0);

      onChange(selection);

      // Because Select leave the focus to the detached btn, give back the focus to the selectWrapperRef
      (selectContainerRef.current?.querySelector("button") as HTMLInputElement)?.focus();
    },
    [onChange]
  );

  const buildSelectGroup = useCallback(
    (group: "builtIn" | "custom", options: DmnDataType[]) => {
      return (
        <SelectGroup label={i18n.dataTypeDropDown[group]} key={group}>
          {options.map(({ name }) => (
            <SelectOption key={name} value={name} data-ouia-component-id={name} />
          ))}
        </SelectGroup>
      );
    },
    [i18n]
  );

  const buildSelectGroups = useCallback(() => {
    const [customDataTypes, builtInDataTypes] = _.chain(dataTypes).partition("isCustom").value();
    if (_.isEmpty(customDataTypes)) {
      return [buildSelectGroup("builtIn", builtInDataTypes)];
    }

    return [
      buildSelectGroup("builtIn", builtInDataTypes),
      <Divider key="divider" />,
      buildSelectGroup("custom", customDataTypes),
    ];
  }, [buildSelectGroup, dataTypes]);

  // FIXME: Tiago -> This is not good.
  // We should not filter based a JSX Element value, but rather based on the data alone.
  const onFilter = useCallback(
    (_, textInput: string) => {
      if (textInput === "") {
        return buildSelectGroups();
      }

      return buildSelectGroups().reduce((acc, group) => {
        const filteredGroup = React.cloneElement(group, {
          children: group.props?.children?.filter((item: React.ReactElement) =>
            item.props.value.toLowerCase().includes(textInput.toLowerCase())
          ),
        });

        if (filteredGroup.props?.children?.length > 0) {
          acc.push(filteredGroup);
        }

        return acc;
      }, [] as JSX.Element[]);
    },
    [buildSelectGroups]
  );

  const onSelectToggle = useCallback(
    (isOpen) => {
      setOpen(isOpen);
      onToggle(isOpen);
    },
    [onToggle]
  );

  return (
    <div ref={selectContainerRef} tabIndex={-1} onKeyDown={onKeyDown}>
      <Select
        menuAppendTo={menuAppendTo}
        ouiaId="edit-expression-data-type"
        variant={SelectVariant.single}
        typeAheadAriaLabel={i18n.choose}
        onToggle={onSelectToggle}
        onSelect={onSelect}
        onFilter={onFilter}
        isOpen={isOpen}
        selections={value}
        isGrouped
        hasInlineFilter
        inlineFilterPlaceholderText={i18n.choose}
        maxHeight={500}
      >
        {buildSelectGroups()}
      </Select>
    </div>
  );
};
