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
import { useCallback, useState, useImperativeHandle, useMemo, useEffect } from "react";
import { Popover, PopoverPosition } from "@patternfly/react-core";
import "./PopoverMenu.css";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../../keysUtils";
import { generateUuid } from "../../api";

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
   * True to show the popover programmatically.
   */
  isVisible?: boolean | null;
  /**
   * Lifecycle function invoked when the popover has fully transitioned out, called when the user click outside the popover.
   */
  onHide?: () => void;
  /**
   * Lifecycle function invoked when the popover has fully transitioned out, called when the user press "Esc" key.
   */
  onCancel?: (event?: MouseEvent | KeyboardEvent) => void;
  /**
   * Lifecycle function invoked when the popover has fully transitioned in.
   */
  onShown?: () => void;
  position?: PopoverPosition;
}

export interface PopoverMenuRef {
  /**
   * set the visibility of the popover
   */
  setIsVisible: (isVisible: boolean) => void;
}

export const PopoverMenu = React.forwardRef(
  (
    {
      children,
      arrowPlacement,
      body,
      title,
      position,
      appendTo,
      className,
      hasAutoWidth,
      minWidth,
      onHide = () => {},
      onCancel = () => {},
      onShown = () => {},
    }: PopoverMenuProps,
    ref
  ) => {
    const { currentlyOpenContextMenu, setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();
    const [isPopoverVisible, setIsPopoverVisible] = useState(false);
    const id = useMemo(() => generateUuid(), []);

    useEffect(() => {
      setIsPopoverVisible(currentlyOpenContextMenu == id);
    }, [id, currentlyOpenContextMenu]);

    const onPopoverShown = useCallback(() => {
      setCurrentlyOpenContextMenu(id);
      onShown();
    }, [setCurrentlyOpenContextMenu, id, onShown]);

    const shouldOpen = useCallback((showFunction?: () => void) => {
      showFunction?.();
    }, []);

    const shouldClose = useCallback(
      (_tip, hideFunction?: () => void, event?: MouseEvent | KeyboardEvent) => {
        // if the esc key has been pressed with a Select component open
        if ((event?.target as Element).closest(".pf-c-select__menu")) {
          return;
        }

        if (event instanceof KeyboardEvent && NavigationKeysUtils.isEscape(event?.key)) {
          onCancel(event);
        } else {
          onHide();
        }

        hideFunction?.();
      },
      [onCancel, onHide]
    );

    useImperativeHandle(
      ref,
      (): PopoverMenuRef => ({
        setIsVisible: (isVisible: boolean) => {
          setCurrentlyOpenContextMenu(isVisible ? id : undefined);
        },
      })
    );

    return (
      <Popover
        data-ouia-component-id="expression-popover-menu"
        className={`popover-menu-selector${className ? " " + className : ""}`}
        hasAutoWidth={hasAutoWidth}
        minWidth={minWidth}
        position={position ?? PopoverPosition.bottom}
        distance={0}
        id="menu-selector"
        reference={arrowPlacement}
        appendTo={appendTo}
        onShown={onPopoverShown}
        headerContent={
          <div className="selector-menu-title" data-ouia-component-id="expression-popover-menu-title">
            {title}
          </div>
        }
        bodyContent={body}
        isVisible={isPopoverVisible}
        shouldClose={shouldClose}
        shouldOpen={shouldOpen}
      >
        {children}
      </Popover>
    );
  }
);
