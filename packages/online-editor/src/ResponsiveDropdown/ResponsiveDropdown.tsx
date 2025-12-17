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

import React from "react";
import { Dropdown, DropdownProps } from "@patternfly/react-core/deprecated";

import { useWindowSizeRelationToBreakpoint } from "./hooks";
import { ResponsiveDropdownContext } from "./ResponsiveDropdownContext";
import { ResponsiveDropdownModal } from "./ResponsiveDropdownModal";
import { Breakpoint, RelationToBreakpoint } from "../responsiveBreakpoints/ResponsiveBreakpoints";

export interface ResponsiveDropdownProps extends DropdownProps {
  /** Array of nodes that will be rendered in the dropdown Menu list */
  dropdownItems?: React.ReactNode[];
  /** Breakpoint from which the dropdown should turn into a Modal */
  switchingBreakpoint?: Breakpoint;
  /** Function callback to close the dropdown */
  onClose?: () => void;
  /** Dropdown/Modal title */
  title?: string;
}

export function ResponsiveDropdown(props: React.PropsWithChildren<ResponsiveDropdownProps>) {
  const isModal = useWindowSizeRelationToBreakpoint(props.switchingBreakpoint || "sm") === RelationToBreakpoint.Below;

  return (
    <ResponsiveDropdownContext.Provider value={{ isModal }}>
      {isModal ? (
        <>
          {props.toggle}
          <ResponsiveDropdownModal isOpen={props.isOpen} onClose={props.onClose} title={props.title}>
            <Dropdown {...props} isOpen={true} isFullHeight={true} toggle={<></>} style={{ width: "100%" }} />
          </ResponsiveDropdownModal>
        </>
      ) : (
        <Dropdown {...props} menuAppendTo={props.menuAppendTo ?? document.body} />
      )}
    </ResponsiveDropdownContext.Provider>
  );
}
