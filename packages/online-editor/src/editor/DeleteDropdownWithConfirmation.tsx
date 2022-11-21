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

import * as React from "react";
import { useState, ReactElement } from "react";
import { DropdownGroup, DropdownItem, DropdownPosition } from "@patternfly/react-core/dist/js/components/Dropdown";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { ResponsiveDropdown } from "../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../ResponsiveDropdown/ResponsiveDropdownToggle";

export function DeleteDropdownWithConfirmation(
  props: {
    onDelete: () => void;
    item: React.ReactNode;
    label?: string | ReactElement;
    isHoverable?: boolean;
  } = { onDelete: () => {}, item: <></>, isHoverable: true }
) {
  const [isDeleteDropdownOpen, setDeleteDropdownOpen] = useState(false);
  return (
    <ResponsiveDropdown
      onClick={(e) => e.stopPropagation()}
      className={props.isHoverable ? "kie-tools--masthead-hoverable" : ""}
      onSelect={() => setDeleteDropdownOpen(false)}
      onClose={() => setDeleteDropdownOpen(false)}
      isOpen={isDeleteDropdownOpen}
      isPlain={true}
      position={DropdownPosition.right}
      toggle={
        <ResponsiveDropdownToggle
          icon={<TrashIcon />}
          toggleIndicator={null}
          onToggle={() => setDeleteDropdownOpen((prev) => !prev)}
          onClick={(e) => e.stopPropagation()}
        >
          {props.label}
        </ResponsiveDropdownToggle>
      }
      dropdownItems={[
        <DropdownGroup label={"Are you sure?"} key="confirm-delete">
          <DropdownItem tabIndex={1} onClick={props.onDelete}>
            {props.item}
          </DropdownItem>
        </DropdownGroup>,
      ]}
    />
  );
}
