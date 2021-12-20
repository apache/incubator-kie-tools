/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { Popover } from "@patternfly/react-core";
import "./PopoverMenu.css";

export interface PopoverMenuProps {
  /** Optional children element to be considered for triggering the popover */
  children?: React.ReactElement;
  /** Title of the popover menu */
  title: string;
  /** A function which returns the HTMLElement where the popover's arrow should be placed */
  arrowPlacement?: () => HTMLElement;
  /** The content of the popover itself */
  body: React.ReactNode;
  /** The node where to append the popover content */
  appendTo?: HTMLElement | ((ref?: HTMLElement) => HTMLElement);
  /** Additional classname to be used for the popover */
  className?: string;
  /** True to have width automatically computed */
  hasAutoWidth?: boolean;
  /** Popover min width */
  minWidth?: string;
}

export const PopoverMenu: React.FunctionComponent<PopoverMenuProps> = ({
  children,
  arrowPlacement,
  body,
  title,
  appendTo,
  className,
  hasAutoWidth,
  minWidth,
}: PopoverMenuProps) => {
  return (
    <Popover
      data-ouia-component-id="expression-popover-menu"
      className={`popover-menu-selector${className ? " " + className : ""}`}
      hasAutoWidth={hasAutoWidth}
      minWidth={minWidth}
      position="bottom"
      distance={0}
      id="menu-selector"
      reference={arrowPlacement}
      appendTo={appendTo}
      headerContent={
        <div className="selector-menu-title" data-ouia-component-id="expression-popover-menu-title">
          {title}
        </div>
      }
      bodyContent={body}
    >
      {children}
    </Popover>
  );
};
