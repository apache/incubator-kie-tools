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
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { Popover, PopoverPosition, PopoverProps } from "@patternfly/react-core/dist/js/components/Popover";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../../keysUtils/keyUtils";
import { generateUuid } from "../../api";
import "./PopoverMenu.css";

export interface PopoverMenuProps {
  /** Optional children element to be considered for triggering the popover */
  children?: React.ReactElement;
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
  distance?: number;
}

export interface PopoverMenuRef {
  /**
   * set the visibility of the popover
   */
  setIsVisible: (isVisible: boolean) => void;
}

const POPUP_DROP_DOWN_RESERVED_HEIGHT = 200;

const POPUP_DEFAULT_HEIGHT = 200;

export const PopoverMenu = React.forwardRef(
  (
    {
      children,
      arrowPlacement,
      body,
      position,
      distance,
      appendTo,
      className,
      hasAutoWidth,
      minWidth = `var(--pf-v5-c-popover--MinWidth)`,
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

    const shouldOpen: PopoverProps["shouldOpen"] = useCallback((_event, showFunction) => {
      showFunction?.();
    }, []);

    const shouldClose: PopoverProps["shouldClose"] = useCallback(
      (event, hideFunction): void => {
        if (event instanceof KeyboardEvent && NavigationKeysUtils.isEsc(event.key)) {
          onCancel(event);
        } else {
          hideFunction?.();
        }
      },
      [onCancel]
    );

    const onHideCallback: PopoverProps["onHide"] = useCallback(
      (tip): void => {
        // This validation is to prevent this code of being called twice, because if the user clicks outside the
        // Boxed Expression component the onHide() is called again by the Popover which is listen to clicks
        // on the document to close all opened popups.
        if (currentlyOpenContextMenu) {
          onHide();
          setCurrentlyOpenContextMenu(undefined);
        }
      },
      [currentlyOpenContextMenu, onHide, setCurrentlyOpenContextMenu]
    );

    useImperativeHandle(
      ref,
      (): PopoverMenuRef => ({
        setIsVisible: (isVisible: boolean) => {
          setCurrentlyOpenContextMenu(isVisible ? id : undefined);
        },
      })
    );

    const appendElement = useMemo(() => {
      if (appendTo instanceof HTMLElement) {
        return appendTo;
      } else if (appendTo) {
        return appendTo();
      }
    }, [appendTo]);

    const yPos = appendElement?.getBoundingClientRect().top ?? 0;
    const popupPosition = useMemo(() => {
      if (appendElement) {
        const availableHeight = document.documentElement.clientHeight;
        if (POPUP_DEFAULT_HEIGHT + yPos + POPUP_DROP_DOWN_RESERVED_HEIGHT > availableHeight) {
          return PopoverPosition.right;
        }
      }
      return PopoverPosition.bottom;
    }, [appendElement, yPos]);

    return (
      <Popover
        id={"menu-selector"}
        data-ouia-component-id={"expression-popover-menu"}
        data-testid={"kie-tools--bee--expression-popover-menu"}
        className={`popover-menu-selector ${className ?? ""}`}
        hasAutoWidth={hasAutoWidth}
        minWidth={minWidth}
        position={popupPosition}
        distance={distance ?? 0}
        triggerRef={arrowPlacement}
        appendTo={appendTo}
        // Need this 1px to render something and not break it.
        headerContent={<div style={{ height: "1px" }}></div>}
        bodyContent={body}
        isVisible={isPopoverVisible}
        onShown={onPopoverShown}
        onHide={onHideCallback}
        shouldClose={shouldClose}
        shouldOpen={shouldOpen}
        flipBehavior={["bottom-start", "bottom", "bottom-end", "right-start", "left-start", "right-end", "left-end"]}
      >
        {children}
      </Popover>
    );
  }
);
