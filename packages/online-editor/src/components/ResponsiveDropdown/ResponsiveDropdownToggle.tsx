import React, { useEffect } from "react";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownSeparator,
  DropdownToggle,
  DropdownPosition,
  DropdownDirection,
  DropdownProps,
  DropdownToggleProps,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { ResponsiveDropdownContext } from "./ResponsiveDropdownContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

export interface ResponsiveDropdownToggleProps extends DropdownToggleProps {
  /** Indicates where menu will be aligned horizontally */
  switchingBreakpoint?: "sm" | "md" | "lg" | "xl" | "2xl";
}

export function ResponsiveDropdownToggle(args: ResponsiveDropdownToggleProps) {
  return (
    <ResponsiveDropdownContext.Consumer>
      {({ isModal }) => {
        if (!isModal) {
          return <DropdownToggle {...args} />;
        } else {
          return (
            <Button
              variant={ButtonVariant.plain}
              onClick={() => args.onToggle?.(args.isOpen || false)}
              className={args.className}
            >
              {args.children}
            </Button>
          );
        }
      }}
    </ResponsiveDropdownContext.Consumer>
  );
}
