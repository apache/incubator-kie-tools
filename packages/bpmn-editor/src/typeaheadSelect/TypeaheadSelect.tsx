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

import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { MenuToggle, MenuToggleElement } from "@patternfly/react-core/dist/js/components/MenuToggle";
import { Select, SelectList, SelectOption, SelectOptionProps } from "@patternfly/react-core/dist/js/components/Select";
import {
  TextInputGroup,
  TextInputGroupMain,
  TextInputGroupUtilities,
} from "@patternfly/react-core/dist/js/components/TextInputGroup";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import "./TypeaheadSelect.css";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { useBpmnEditorI18n } from "../i18n";

export type TypeaheadSelectOption = SelectOptionProps & { customLabel?: string | React.ReactElement };

const hasRenderableOptionLabel = (option: TypeaheadSelectOption): boolean => {
  if (option.customLabel !== null && option.customLabel !== undefined) {
    return true;
  }
  const { children } = option;
  if (children === null || children === undefined || typeof children === "boolean") {
    return false;
  }
  // React elements and arrays are objects — we can't inspect their text, but they're renderable
  return typeof children === "object" || String(children).trim().length > 0;
};

// Handles undefined and non-string values (e.g. numbers) safely
const getSelectOptionId = (value: string | number | undefined) =>
  `select-typeahead-${String(value ?? "").replaceAll(" ", "-")}`;

const POPPER_PROPS = { appendTo: document.body };

// Based on https://v5-archive.patternfly.org/components/menus/select/#typeahead-with-create-option
export function TypeaheadSelect({
  id,
  options,
  onCreateNewOption,
  createNewOptionLabel,
  selected,
  isMultiple,
  setSelected,
  isDisabled,
  emptyStateText,
  showCreateOptionWhen,
  placeholder,
  closeAfterSelect,
}: {
  id: string;
  isDisabled?: boolean;
  selected: string | undefined;
  isMultiple: boolean;
  setSelected: (
    newSelected: string | undefined,
    newSelectedLabel: string | undefined,
    args: { triggeredByCreateNewOption: boolean }
  ) => void;
  options: TypeaheadSelectOption[];
  onCreateNewOption?: (newOptionLabel: string) => string;
  createNewOptionLabel?: string;
  emptyStateText?: string;
  showCreateOptionWhen?: "different-than-current" | "always" | "none-match" | "never";
  placeholder?: string;
  closeAfterSelect?: boolean;
}) {
  const { i18n } = useBpmnEditorI18n();
  const [isOpen, setIsOpen] = React.useState(false);
  const [inputValue, setInputValue] = React.useState("");
  const [filterValue, setFilterValue] = React.useState<string>("");
  const [focusedItemIndex, setFocusedItemIndex] = React.useState<number | null>(null);
  const [activeItemId, setActiveItemId] = React.useState<string | null>(null);
  const textInputRef = React.useRef<HTMLInputElement>(null);

  const CREATE_NEW = useMemo(() => generateUuid(), []);

  React.useEffect(() => {
    const optionText = options.find((option) => option.value === selected)?.children;
    setInputValue(typeof optionText === "string" ? optionText : "");
    setFilterValue("");
  }, [options, selected]);

  const showCreateOption = useMemo(
    () =>
      showCreateOptionWhen === "never"
        ? false
        : showCreateOptionWhen === "different-than-current"
          ? filterValue && filterValue !== (selected || "")
          : showCreateOptionWhen === "always"
            ? true
            : // none-match
              !options.some((option) => String(option.children) === filterValue) && filterValue,
    [filterValue, options, selected, showCreateOptionWhen]
  );

  const renderableOptions = useMemo(() => options.filter(hasRenderableOptionLabel), [options]);

  const selectOptions = useMemo(() => {
    let filtered: TypeaheadSelectOption[] = renderableOptions;

    if (filterValue) {
      filtered = renderableOptions.filter((menuItem) =>
        String(menuItem.children).toLowerCase().includes(filterValue.toLowerCase())
      );
    }

    if (showCreateOption) {
      filtered = [
        {
          children: <i style={{ color: "#0067cc" }}>{`${createNewOptionLabel} "${filterValue}"`}</i>,
          value: CREATE_NEW,
        },
        ...filtered,
      ];
    }

    return filtered;
  }, [CREATE_NEW, createNewOptionLabel, filterValue, renderableOptions, showCreateOption]);

  const selectedOptionExists = useMemo(
    () => renderableOptions.some((option) => option.value === selected),
    [renderableOptions, selected]
  );

  const safeSelected = useMemo(() => (selectedOptionExists ? selected : undefined), [selected, selectedOptionExists]);

  const setActiveAndFocusedItem = useCallback(
    (itemIndex: number) => {
      setFocusedItemIndex(itemIndex);
      const focusedItem = selectOptions[itemIndex];
      setActiveItemId(getSelectOptionId(focusedItem.value));
    },
    [selectOptions]
  );

  const resetActiveAndFocusedItem = useCallback(() => {
    setFocusedItemIndex(null);
    setActiveItemId(null);
  }, []);

  const closeMenu = useCallback(() => {
    if (closeAfterSelect ?? true) {
      setIsOpen(false);
      resetActiveAndFocusedItem();
    }
  }, [closeAfterSelect, resetActiveAndFocusedItem]);

  const onInputClick = useCallback(() => {
    if (!isOpen) {
      setIsOpen(true);
    } else if (!inputValue) {
      closeMenu();
    }
  }, [closeMenu, inputValue, isOpen]);

  const onSelect = useCallback(
    (_event: React.MouseEvent<Element, MouseEvent> | undefined, value: string | number | undefined) => {
      if (value) {
        if (value === CREATE_NEW) {
          const newOptionLabel = filterValue;
          const newValue = onCreateNewOption?.(newOptionLabel);
          setSelected(newValue, newOptionLabel, { triggeredByCreateNewOption: true });

          if (isMultiple) {
            setInputValue("");
            // PF's window click listener sees the CREATE_NEW <li> as detached after
            // React's synchronous re-render and calls onOpenChange(false). Re-open in
            // the next macrotask so the dropdown stays visible after adding a custom tag.
            setTimeout(() => {
              setIsOpen(true);
              setFilterValue("");
            }, 0);
            return;
          } else {
            setInputValue(newOptionLabel);
            closeMenu();
          }
        } else if (isMultiple) {
          const optionText = selectOptions.find((option) => option.value === value)?.children;
          setSelected(String(value), String(optionText), { triggeredByCreateNewOption: false });
        } else {
          const optionText = selectOptions.find((option) => option.value === value)?.children;
          setInputValue(typeof optionText === "string" ? optionText : "");
          setSelected(String(value), String(optionText), { triggeredByCreateNewOption: false });
          closeMenu();
        }
        setFilterValue("");
      }
    },
    [CREATE_NEW, closeMenu, filterValue, isMultiple, onCreateNewOption, selectOptions, setSelected]
  );

  const onTextInputChange = useCallback(
    (_event: React.FormEvent<HTMLInputElement>, value: string) => {
      setInputValue(value);
      setFilterValue(value);

      if (!isOpen && value) {
        setIsOpen(true);
      }

      resetActiveAndFocusedItem();
      if (selectOptions.length > 0) {
        setActiveAndFocusedItem(0);
      }
    },
    [isOpen, resetActiveAndFocusedItem, selectOptions, setActiveAndFocusedItem]
  );

  const handleMenuArrowKeys = useCallback(
    (key: string) => {
      let indexToFocus = 0;

      if (!isOpen) {
        setIsOpen(true);
      }

      if (selectOptions.every((option) => option.isDisabled)) {
        return;
      }

      if (key === "ArrowUp") {
        // When no index is set or at the first index, focus to the last, otherwise decrement focus index
        if (focusedItemIndex === null || focusedItemIndex === 0) {
          indexToFocus = selectOptions.length - 1;
        } else {
          indexToFocus = focusedItemIndex - 1;
        }

        // Skip disabled options
        while (selectOptions[indexToFocus].isDisabled) {
          indexToFocus--;
          if (indexToFocus === -1) {
            indexToFocus = selectOptions.length - 1;
          }
        }
      }

      if (key === "ArrowDown") {
        // When no index is set or at the last index, focus to the first, otherwise increment focus index
        if (focusedItemIndex === null || focusedItemIndex === selectOptions.length - 1) {
          indexToFocus = 0;
        } else {
          indexToFocus = focusedItemIndex + 1;
        }

        // Skip disabled options
        while (selectOptions[indexToFocus].isDisabled) {
          indexToFocus++;
          if (indexToFocus === selectOptions.length) {
            indexToFocus = 0;
          }
        }
      }

      setActiveAndFocusedItem(indexToFocus);
    },
    [focusedItemIndex, isOpen, selectOptions, setActiveAndFocusedItem]
  );

  const onInputKeyDown = useCallback(
    (event: React.KeyboardEvent<HTMLInputElement>) => {
      const focusedItem = focusedItemIndex !== null ? selectOptions[focusedItemIndex] : null;

      switch (event.key) {
        case "Enter":
          if (isOpen && focusedItem && !focusedItem.isAriaDisabled) {
            event.stopPropagation();
            event.preventDefault();
            onSelect(undefined, focusedItem.value as string);
          }

          if (!isOpen) {
            setIsOpen(true);
          }

          break;
        case "Escape":
          if (isOpen) {
            event.stopPropagation();
            event.preventDefault();

            setIsOpen(false);
            const optionText = options.find((option) => option.value === selected)?.children;
            setInputValue(typeof optionText === "string" ? optionText : "");
            setFilterValue("");
          }
          break;
        case "ArrowUp":
        case "ArrowDown":
          event.preventDefault();
          handleMenuArrowKeys(event.key);
          break;
      }
    },
    [focusedItemIndex, handleMenuArrowKeys, isOpen, onSelect, options, selectOptions, selected]
  );

  const onToggleClick = useCallback(() => {
    setIsOpen((prev) => !prev);
    textInputRef.current?.focus();
  }, []);

  const onClearButtonClick = useCallback(() => {
    setSelected(undefined, undefined, { triggeredByCreateNewOption: false });
    setInputValue("");
    setFilterValue("");
    resetActiveAndFocusedItem();
    textInputRef?.current?.focus();
  }, [resetActiveAndFocusedItem, setSelected]);

  const onBlur = useCallback(() => {
    const optionText = options.find((option) => option.value === selected)?.children;
    setInputValue(typeof optionText === "string" ? optionText : "");
    setFilterValue("");
  }, [options, selected]);

  const onOpenChange = useCallback(
    (nextOpen: boolean) => {
      setIsOpen(nextOpen);
      if (!nextOpen) {
        resetActiveAndFocusedItem();
      }
    },
    [resetActiveAndFocusedItem]
  );

  const onSelectListMouseDown = useCallback((e: React.MouseEvent) => e.preventDefault(), []);

  const toggle = useCallback(
    (toggleRef: React.Ref<MenuToggleElement>) => (
      <MenuToggle
        ref={toggleRef}
        variant={"typeahead"}
        aria-label={"Typeahead creatable menu toggle"}
        onClick={onToggleClick}
        isExpanded={isOpen}
        isDisabled={isDisabled}
        isFullWidth={true}
      >
        <TextInputGroup isPlain>
          <TextInputGroupMain
            value={inputValue}
            onClick={onInputClick}
            onChange={onTextInputChange}
            onKeyDown={onInputKeyDown}
            onBlur={onBlur}
            id={"create-typeahead-select-input"}
            autoComplete={"off"}
            innerRef={textInputRef}
            placeholder={placeholder ?? i18n.propertiesPanel.undefined}
            {...(activeItemId && { "aria-activedescendant": activeItemId })}
            role={"combobox"}
            isExpanded={isOpen}
            aria-controls={"select-create-typeahead-listbox"}
            style={{ flexWrap: "nowrap" }}
          >
            {isMultiple && <Label>{options.filter((s) => s.isSelected).length}</Label>}
          </TextInputGroupMain>
          <TextInputGroupUtilities {...(!inputValue ? { style: { display: "none" } } : {})}>
            <Button variant="plain" onClick={onClearButtonClick} aria-label="Clear input value">
              <TimesIcon aria-hidden />
            </Button>
          </TextInputGroupUtilities>
        </TextInputGroup>
      </MenuToggle>
    ),
    [
      activeItemId,
      i18n.propertiesPanel.undefined,
      inputValue,
      isDisabled,
      isMultiple,
      isOpen,
      onBlur,
      onClearButtonClick,
      onInputClick,
      onInputKeyDown,
      onTextInputChange,
      onToggleClick,
      options,
      placeholder,
    ]
  );

  return (
    <Select
      id={id}
      isOpen={isOpen}
      selected={safeSelected}
      onSelect={onSelect}
      className={"kie-bpmn-editor--typeahead-selector"}
      onOpenChange={onOpenChange}
      popperProps={POPPER_PROPS}
      toggle={toggle}
      shouldFocusFirstItemOnOpen={false}
    >
      <SelectList id="select-create-typeahead-listbox" onMouseDown={onSelectListMouseDown}>
        {selectOptions.length <= 0 && (
          <>
            {(!filterValue && (
              <div style={{ padding: "16px" }}>
                <i>{emptyStateText ?? i18n.propertiesPanel.empty}</i>
              </div>
            )) || (
              <div style={{ padding: "16px" }}>
                <i>{i18n.propertiesPanel.noMatches}</i>
              </div>
            )}
          </>
        )}
        {selectOptions.map((_option, index) => {
          const { children, customLabel, ...option } = { ..._option };
          return (
            <SelectOption
              key={String(option.value ?? index)}
              isFocused={focusedItemIndex === index}
              className={option.className}
              id={getSelectOptionId(option.value)}
              {...option}
              ref={null}
            >
              {customLabel ?? children}
            </SelectOption>
          );
        })}
      </SelectList>
    </Select>
  );
}
