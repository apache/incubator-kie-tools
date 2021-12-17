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

import React from "react";
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

import global_breakpoint_xs from "@patternfly/react-tokens/dist/esm/global_breakpoint_xs";
import global_breakpoint_sm from "@patternfly/react-tokens/dist/esm/global_breakpoint_sm";
import global_breakpoint_md from "@patternfly/react-tokens/dist/esm/global_breakpoint_md";
import global_breakpoint_lg from "@patternfly/react-tokens/dist/esm/global_breakpoint_lg";
import global_breakpoint_xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_xl";
import global_breakpoint_2xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_2xl";

const breakpoints = {
  xs: global_breakpoint_xs.value,
  sm: global_breakpoint_sm.value,
  md: global_breakpoint_md.value,
  lg: global_breakpoint_lg.value,
  xl: global_breakpoint_xl.value,
  "2xl": global_breakpoint_2xl.value,
};

export interface ResponsiveDropdownProps extends DropdownProps {
  /** Classes applied to root element of dropdown */
  className?: string;
  /** Array of nodes that will be rendered in the dropdown Menu list */
  dropdownItems?: React.ReactNode[];
  /** Flag to indicate if dropdown is opened */
  isOpen?: boolean;
  /** Flag indicating that the dropdown should expand to full height */
  isFullHeight?: boolean;
  /** Indicates where menu will be aligned horizontally */
  switchingBreakpoint?: "sm" | "md" | "lg" | "xl" | "2xl";
}

export function ResponsiveDropdown(args: ResponsiveDropdownProps) {
  console.log(breakpoints);

  return <div></div>;
}
