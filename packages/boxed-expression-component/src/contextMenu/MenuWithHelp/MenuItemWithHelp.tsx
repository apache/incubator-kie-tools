import * as React from "react";
import "./MenuWithHelp.css";
import { MenuItem } from "@patternfly/react-core/dist/js/components/Menu/MenuItem";
import { MenuItemAction } from "@patternfly/react-core/dist/js/components/Menu/MenuItemAction";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";

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
