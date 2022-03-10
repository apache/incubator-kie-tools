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
import { useCallback, useState } from "react";
import { Popover } from "@patternfly/react-core";
import "./PopoverMenu.css";
import { useBoxedExpression } from "../../context";

/**
 * Check if the key pressed is Esc Key.
 *
 * @param key the key from the event
 * @return true if yes, false otherwise
 */
const isEscKey = (key = ""): boolean => /^esc.*/i.test(key);

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
  /**
   * Lifecycle function invoked when the popover has fully transitioned out, called when the user click outside the popover.
   */
  onHide?: () => void;
  /**
   * Lifecycle function invoked when the popover has fully transitioned out, called when the user press "Esc" key.
   */
  onCancel?: (event?: MouseEvent | KeyboardEvent) => void;
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
  onHide = () => {},
  onCancel = () => {},
}: PopoverMenuProps) => {
  const { setIsContextMenuOpen } = useBoxedExpression();
  const [isVisible] = useState(false);

  const onHidden = useCallback(() => {
    setIsContextMenuOpen(false);
  }, [setIsContextMenuOpen]);

  const onShown = useCallback(() => {
    setIsContextMenuOpen(true);
  }, [setIsContextMenuOpen]);

  const shouldOpen = useCallback((showFunction?: () => void) => {
    showFunction?.();
  }, []);

  const shouldClose = useCallback(
    (_tip, hideFunction?: () => void, event?: MouseEvent | KeyboardEvent) => {
      // if the esc key has been pressed with a Select component open
      if ((event?.target as Element).closest(".pf-c-select__menu")) {
        return;
      }

      if (event instanceof KeyboardEvent && isEscKey(event?.key)) {
        onCancel(event);
      } else {
        onHide();
      }

      hideFunction?.();
    },
    [onCancel, onHide]
  );

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
      onHidden={onHidden}
      onShown={onShown}
      headerContent={
        <div className="selector-menu-title" data-ouia-component-id="expression-popover-menu-title">
          {title}
        </div>
      }
      bodyContent={body}
      isVisible={isVisible}
      shouldClose={shouldClose}
      shouldOpen={shouldOpen}
    >
      {children}
    </Popover>
  );
};
