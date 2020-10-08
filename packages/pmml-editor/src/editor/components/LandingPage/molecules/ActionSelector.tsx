/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { useState } from "react";
import { Dropdown, DropdownItem, DropdownPosition, DropdownToggle } from "@patternfly/react-core";

export const ActionSelector = () => {
  const [isOpen, setOpen] = useState(false);

  const onToggle = (_isOpen: boolean) => {
    setOpen(_isOpen);
  };
  const onSelect = (event: React.SyntheticEvent<HTMLDivElement>) => {
    setOpen(!isOpen);
    onFocus();
  };
  const onFocus = () => {
    document.getElementById("action-selector-id")?.focus();
  };

  const dropdownItems = [
    <DropdownItem key="createModel" isDisabled={true}>
      Create model
    </DropdownItem>,
    <DropdownItem key="viewDataDictionary" isDisabled={true}>
      View Data Dictionary
    </DropdownItem>
  ];

  return (
    <Dropdown
      data-testid="action-selector"
      onSelect={onSelect}
      toggle={
        <DropdownToggle id="action-selector-id" data-testid="action-selector__toggle" onToggle={onToggle}>
          Actions
        </DropdownToggle>
      }
      isOpen={isOpen}
      dropdownItems={dropdownItems}
      position={DropdownPosition.right}
    />
  );
};
