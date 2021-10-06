import * as React from "react";
import { useState } from "react";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";

export function DeleteDropdownWithConfirmation(props: { onDelete: () => void; item: React.ReactNode }) {
  const [isDeleteDropdownOpen, setDeleteDropdownOpen] = useState(false);
  return (
    <Dropdown
      onClick={(e) => e.stopPropagation()}
      className={"kogito-tooling--masthead-hoverable"}
      onSelect={() => setDeleteDropdownOpen(false)}
      isOpen={isDeleteDropdownOpen}
      isPlain={true}
      position={DropdownPosition.right}
      toggle={
        <DropdownToggle toggleIndicator={null} onToggle={setDeleteDropdownOpen} onClick={(e) => e.stopPropagation()}>
          <TrashIcon />
        </DropdownToggle>
      }
      dropdownItems={[
        <DropdownGroup label={"Are you sure?"} key="confirm-delete">
          <DropdownItem onClick={props.onDelete}>{props.item}</DropdownItem>
        </DropdownGroup>,
      ]}
    />
  );
}
