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

import React, { useMemo } from "react";
import {
  Dropdown,
  DropdownProps,
  DropdownItem,
  DropdownSeparator,
} from "@patternfly/react-core/dist/js/components/Dropdown";

import { useWindowSizeRelationToBreakpoint, Breakpoint, RelationToBreakpoint } from "./hooks";
import { ResponsiveDropdownContext } from "./ResponsiveDropdownContext";
import { ResponsiveDropdownModal } from "./ResponsiveDropdownModal";

export interface ResponsiveDropdownProps extends DropdownProps {
  /** Array of nodes that will be rendered in the dropdown Menu list */
  dropdownItems?: React.ReactNode[];
  /** Breakpoint from which the dropdown should turn into a Modal */
  switchingBreakpoint?: Breakpoint;
  /** Function callback to close the dropdown */
  onClose?: () => void;
  /** Dropdown/Modal title */
  title: string;
}

export function ResponsiveDropdown(props: ResponsiveDropdownProps) {
  const isModal = useWindowSizeRelationToBreakpoint(props.switchingBreakpoint || "sm") === RelationToBreakpoint.Below;

  return (
    <ResponsiveDropdownContext.Provider value={{ isModal }}>
      {isModal ? (
        <>
          {props.toggle}
          <ResponsiveDropdownModal isOpen={props.isOpen} onClose={() => props.onClose?.()} title={props.title}>
            <Dropdown {...props} isOpen={true} isFullHeight={true} toggle={<></>} style={{ width: "100%" }} />
          </ResponsiveDropdownModal>
        </>
      ) : (
        <Dropdown
          {...props}
          title={undefined}
          dropdownItems={[
            <DropdownItem isDisabled key="responsive-dropdown-title">
              {props.title}
            </DropdownItem>,
            <DropdownSeparator key="responsive-dropdown-separator" />,
            props.dropdownItems,
          ]}
        />
      )}
    </ResponsiveDropdownContext.Provider>
  );
}
