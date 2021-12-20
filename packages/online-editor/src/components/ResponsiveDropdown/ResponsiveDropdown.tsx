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

import React, { useEffect, useState } from "react";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownSeparator,
  DropdownToggle,
  DropdownPosition,
  DropdownDirection,
  DropdownProps,
} from "@patternfly/react-core/dist/js/components/Dropdown";

import { useIsBelowBreakpoint, Breakpoint } from "./hooks";
import { ResponsiveDropdownContext } from "./ResponsiveDropdownContext";
import { ResponsiveDropdownToggle } from "./ResponsiveDropdownToggle";

export interface ResponsiveDropdownProps extends DropdownProps {
  /** Classes applied to root element of dropdown */
  className?: string;
  /** Array of nodes that will be rendered in the dropdown Menu list */
  dropdownItems?: React.ReactNode[];
  /** Flag to indicate if dropdown is opened */
  isOpen: boolean;
  /** Flag indicating that the dropdown should expand to full height */
  isFullHeight?: boolean;
  /** Breakpoint from which the dropdown should turn into a Modal */
  switchingBreakpoint?: Breakpoint;
}

export function ResponsiveDropdown(args: ResponsiveDropdownProps) {
  const isModal = useIsBelowBreakpoint(args.switchingBreakpoint || "sm");

  return (
    <ResponsiveDropdownContext.Provider value={{ isModal }}>
      {isModal ? <>{args.toggle}</> : <Dropdown {...args} />}
    </ResponsiveDropdownContext.Provider>
  );
}
