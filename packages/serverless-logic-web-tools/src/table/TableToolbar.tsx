/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import {
  Dropdown,
  DropdownItem,
  DropdownToggle,
  DropdownToggleCheckbox,
  KebabToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { SearchInput } from "@patternfly/react-core/dist/js/components/SearchInput";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { TablePagination, TablePaginationProps } from "./TablePagination";

export type TableToolbarProps = TablePaginationProps & {
  onDeleteActionButtonClick: () => void;
  onToggleAllElements: (checked: boolean) => void;
  searchValue: string;
  selectedElementsCount: number;
  setSearchValue: React.Dispatch<React.SetStateAction<string>>;
  additionalComponents?: React.ReactNode;
};

export function TableToolbar(props: TableToolbarProps) {
  const {
    itemCount: itemCount,
    onDeleteActionButtonClick: onDeleteActionButtonClick,
    selectedElementsCount,
    searchValue,
    setSearchValue,
    onToggleAllElements,
    page,
    perPage,
    perPageOptions,
    setPage,
    setPerPage,
    additionalComponents,
  } = props;
  const [isBulkDropDownOpen, setIsBulkDropDownOpen] = useState(false);
  const [isActionDropdownOpen, setIsActionDropdownOpen] = React.useState(false);

  const isBulkCheckBoxChecked = useMemo(
    () => (itemCount === selectedElementsCount ? true : selectedElementsCount === 0 ? false : null),
    [itemCount, selectedElementsCount]
  );

  const onBulkDropDownSelect = useCallback(() => setIsBulkDropDownOpen(false), []);

  const onBulkDropDownToggle = useCallback((isOpen: boolean) => setIsBulkDropDownOpen(isOpen), []);

  const onActionDropdownToggle = useCallback(() => {
    setIsActionDropdownOpen(!isActionDropdownOpen);
  }, [isActionDropdownOpen]);

  const actionDropdownItems = useMemo(() => {
    return [
      <DropdownItem
        key={"delete-dropdown-item"}
        isDisabled={!selectedElementsCount}
        onClick={onDeleteActionButtonClick}
        ouiaId={"delete-action-button"}
        aria-label="Open confirm delete modal"
      >
        Delete
      </DropdownItem>,
    ];
  }, [selectedElementsCount, onDeleteActionButtonClick]);

  const bulkDropDownItems = useMemo(
    () => [
      <DropdownItem onClick={() => onToggleAllElements(false)} key="none" aria-label="Select none">
        Select none (0)
      </DropdownItem>,
      <DropdownItem onClick={() => onToggleAllElements(true)} key="all" aria-label="Select All">
        Select all({itemCount})
      </DropdownItem>,
    ],
    [itemCount, onToggleAllElements]
  );

  const onSearchChange = useCallback(
    (value: string) => {
      setSearchValue(value);
    },
    [setSearchValue]
  );

  return (
    <Toolbar>
      <ToolbarContent style={{ paddingLeft: "10px", paddingRight: "10px" }}>
        <ToolbarItem alignment={{ default: "alignLeft" }}>
          <Dropdown
            onSelect={onBulkDropDownSelect}
            toggle={
              <DropdownToggle
                splitButtonItems={[
                  <DropdownToggleCheckbox
                    onChange={(checked) => onToggleAllElements(checked)}
                    isChecked={isBulkCheckBoxChecked}
                    id="split-button-text-checkbox"
                    key="bulk-check-box"
                    aria-label="Select all"
                  >
                    {selectedElementsCount ? `${selectedElementsCount} selected` : ""}
                  </DropdownToggleCheckbox>,
                ]}
                onToggle={onBulkDropDownToggle}
                id="toggle-split-button-text"
              />
            }
            isOpen={isBulkDropDownOpen}
            dropdownItems={bulkDropDownItems}
            aria-label="Bulk selection dropdown"
          />
        </ToolbarItem>
        <ToolbarItem variant="search-filter">
          <SearchInput
            placeholder="Filter by name"
            value={searchValue}
            onChange={(_event, value) => onSearchChange(value)}
            onClear={() => onSearchChange("")}
          />
        </ToolbarItem>
        <ToolbarItem>
          <Dropdown
            onSelect={onActionDropdownToggle}
            toggle={<KebabToggle id="toggle-kebab" onToggle={onActionDropdownToggle} />}
            isOpen={isActionDropdownOpen}
            isPlain
            dropdownItems={actionDropdownItems}
          />
        </ToolbarItem>
        {additionalComponents}
        <ToolbarItem variant="pagination">
          <TablePagination
            itemCount={itemCount}
            page={page}
            perPage={perPage}
            perPageOptions={perPageOptions}
            setPage={setPage}
            setPerPage={setPerPage}
            variant="top"
            isCompact
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );
}
