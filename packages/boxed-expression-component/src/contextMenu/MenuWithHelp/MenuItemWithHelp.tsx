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

import * as React from "react";
import { MenuItem } from "@patternfly/react-core/dist/js/components/Menu/MenuItem";
import { MenuItemAction } from "@patternfly/react-core/dist/js/components/Menu/MenuItemAction";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import "./MenuWithHelp.css";

export interface MenuItemWithHelpProps {
  menuItemKey: string;
  menuItemCustomText?: string;
  menuItemHelp: string;
  menuItemIcon?: any;
  menuItemIconStyle?: any;
  setVisibleHelp: (description: string) => void;
  visibleHelp: string;
}

export function MenuItemWithHelp({
  menuItemKey,
  menuItemCustomText,
  menuItemHelp,
  menuItemIcon,
  menuItemIconStyle,
  setVisibleHelp,
  visibleHelp,
}: MenuItemWithHelpProps) {
  const [isHovered, setIsHovered] = React.useState<boolean>();

  return (
    <MenuItem
      className="menu-item-with-help"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      actions={
        (isHovered || visibleHelp === menuItemHelp) && (
          <MenuItemAction
            onClick={() => setVisibleHelp(menuItemHelp)}
            icon={<HelpIcon aria-hidden />}
            actionId={menuItemKey + "-help"}
            aria-label={menuItemKey + "-help"}
          />
        )
      }
      description={visibleHelp === menuItemHelp ? menuItemHelp : ""}
      key={menuItemKey}
      itemId={menuItemKey}
      icon={
        menuItemIcon && (
          <div style={menuItemIconStyle ?? undefined}>
            <>{menuItemIcon}</>
          </div>
        )
      }
    >
      {menuItemCustomText ?? menuItemKey}
    </MenuItem>
  );
}
