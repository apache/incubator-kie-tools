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
import { useCallback, useEffect, useImperativeHandle, useLayoutEffect, useMemo, useState } from "react";
import { generateUuid } from "../api";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../keysUtils/keyUtils";
import ReactDOM from "react-dom";

const MOUSE_CLICK_OFFSET_IN_PX = {
  x: 8,
  y: 0,
};

export function useCustomContextMenuHandler(
  domEventTargetRef: React.RefObject<HTMLElement | null>,
  triggerOn: "contextmenu" | "click"
): {
  xPos: number;
  yPos: number;
  isOpen: boolean;
} {
  const { setCurrentlyOpenContextMenu, currentlyOpenContextMenu, editorRef, scrollableParentRef } =
    useBoxedExpressionEditor();

  const [scroll, setScroll] = useState({ x: 0, y: 0 });

  const [position, setPosition] = useState({ x: 0, y: 0 });

  const [isOpen, setOpen] = useState(false);

  const id = useMemo(() => generateUuid(), []);

  const hide = useCallback(
    (e: MouseEvent) => {
      e.preventDefault();
      /* In SAFARI only, CTRL + click shortcut used to open the Menu, results in two distinct mouse events: "contextmenu" and “click“, in this order.
         Considering this hide() function is currently bound with both event handlers, the second event (click) will suddenly close the menu. 
         To prevent this, if ctrlKey is actually pressed, the event is ignored. */
      if (!isOpen || e.ctrlKey) {
        return;
      }

      setCurrentlyOpenContextMenu(undefined);
    },
    [isOpen, setCurrentlyOpenContextMenu]
  );

  const show = useCallback(
    (e: MouseEvent) => {
      if (e.currentTarget !== domEventTargetRef.current) {
        return;
      }

      e.preventDefault();
      e.stopPropagation();
      setPosition({
        x: e.pageX + MOUSE_CLICK_OFFSET_IN_PX.x,
        y: e.pageY + MOUSE_CLICK_OFFSET_IN_PX.y,
      });
      setScroll({
        x: scrollableParentRef.current ? scrollableParentRef.current?.scrollLeft : window.scrollX,
        y: scrollableParentRef.current ? scrollableParentRef.current?.scrollTop : window.scrollY,
      });
      setCurrentlyOpenContextMenu(id);
    },
    [domEventTargetRef, id, scrollableParentRef, setCurrentlyOpenContextMenu]
  );

  useEffect(() => {
    function onScroll(e: UIEvent) {
      if (currentlyOpenContextMenu === id) {
        setScroll({
          x: e.target && e.target !== document ? (e.target as HTMLElement).scrollLeft : window.scrollX,
          y: e.target && e.target !== document ? (e.target as HTMLElement).scrollTop : window.scrollY,
        });
      }
    }

    const target = scrollableParentRef.current ?? window;
    target?.addEventListener("scroll", onScroll);

    return () => {
      target?.removeEventListener("scroll", onScroll);
    };
  }, [currentlyOpenContextMenu, editorRef, id, scrollableParentRef]);

  useEffect(() => {
    setOpen(id === currentlyOpenContextMenu);
  }, [id, currentlyOpenContextMenu]);

  useEffect(() => {
    function handleEscPressed(e: KeyboardEvent) {
      if (NavigationKeysUtils.isEsc(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        setCurrentlyOpenContextMenu(undefined);
      }
    }

    window?.addEventListener("keydown", handleEscPressed);
    return () => {
      window?.removeEventListener("keydown", handleEscPressed);
    };
  }, [scrollableParentRef, setCurrentlyOpenContextMenu]);

  useEffect(() => {
    const elem = domEventTargetRef?.current;

    if (currentlyOpenContextMenu && isOpen) {
      document.addEventListener("click", hide);
      document.addEventListener("contextmenu", hide);
      elem?.addEventListener(triggerOn, show);
      return () => {
        elem?.removeEventListener(triggerOn, show);
        document.removeEventListener("contextmenu", hide);
        document.removeEventListener("click", hide);
      };
    }

    elem?.addEventListener(triggerOn, show);
    return () => {
      elem?.removeEventListener(triggerOn, show);
    };
  }, [domEventTargetRef, hide, currentlyOpenContextMenu, isOpen, show, triggerOn]);

  return {
    xPos: position.x - scroll.x + 1, // Leave some margin for clicking without moving the mouse.
    yPos: position.y - scroll.y + 1, // Leave some margin for clicking without moving the mouse.
    isOpen,
  };
}

const PADDING_FROM_BOTTOM_IN_PX = 20;
const MIN_HEIGHT_IN_PX = 400;
const WIDTH_IN_PX = 300;

export interface ContextMenuRef {
  recalculateNiceHeight(scrollHeight?: number): void;
}

export function ContextMenu({
  children,
  domEventTargetRef,
  triggerOn,
  forwardRef,
}: React.PropsWithChildren<{
  forwardRef?: React.RefObject<ContextMenuRef>;
  domEventTargetRef: React.RefObject<HTMLElement>;
  triggerOn: "contextmenu" | "click";
}>) {
  const contextMenuContainerRef = React.createRef<HTMLDivElement>();
  const { scrollableParentRef } = useBoxedExpressionEditor();

  const { xPos, yPos, isOpen } = useCustomContextMenuHandler(domEventTargetRef, triggerOn);
  const [niceY, setNiceY] = useState(Number.MAX_SAFE_INTEGER);

  const recalculateNiceHeight = useCallback(
    (scrollHeight?: number) => {
      if (contextMenuContainerRef.current) {
        contextMenuContainerRef.current.style.maxHeight = ""; // reset it first so scrollHeight is properly calcualted.
      }

      const contextMenuHeight = scrollHeight ?? contextMenuContainerRef?.current?.scrollHeight ?? 0;
      const availableHeight = scrollableParentRef?.current?.getBoundingClientRect().height ?? window.innerHeight;
      const remainingHeight = availableHeight - (scrollableParentRef?.current?.getBoundingClientRect().top ?? 0) - yPos;

      const finalHeight = Math.min(contextMenuHeight, remainingHeight - PADDING_FROM_BOTTOM_IN_PX);
      const minHeight = Math.min(availableHeight, MIN_HEIGHT_IN_PX, contextMenuHeight);

      if (finalHeight < contextMenuHeight && finalHeight <= minHeight) {
        if (contextMenuContainerRef.current) {
          contextMenuContainerRef.current.style.maxHeight = minHeight - PADDING_FROM_BOTTOM_IN_PX + "px";
        }
        setNiceY(Math.max(PADDING_FROM_BOTTOM_IN_PX, availableHeight - minHeight));
      } else {
        if (contextMenuContainerRef.current) {
          contextMenuContainerRef.current.style.maxHeight = finalHeight + "px";
        }
        setNiceY(yPos);
      }
    },
    [contextMenuContainerRef, scrollableParentRef, yPos]
  );

  useImperativeHandle(
    forwardRef,
    () => ({
      recalculateNiceHeight,
    }),
    [recalculateNiceHeight]
  );

  useLayoutEffect(() => {
    recalculateNiceHeight();
  }, [contextMenuContainerRef, recalculateNiceHeight]);

  useEffect(() => {
    function onResize() {
      recalculateNiceHeight();
    }

    const target = scrollableParentRef.current ?? window;
    target?.addEventListener("resize", onResize);
    window.addEventListener("resize", onResize);
    return () => {
      window.removeEventListener("resize", onResize);
      target?.removeEventListener("resize", onResize);
    };
  }, [scrollableParentRef, recalculateNiceHeight]);

  const style: React.CSSProperties = useMemo(
    () => ({
      top: `${niceY}px`,
      left: `${xPos}px`,
      width: `${WIDTH_IN_PX}px`,
    }),
    [xPos, niceY]
  );

  return (
    <>
      {isOpen &&
        ReactDOM.createPortal(
          <div
            className={"context-menu-container"}
            ref={contextMenuContainerRef}
            style={style}
            onMouseDown={(e) => e.stopPropagation()}
            data-testid={"kie-tools--bee--context-menu-container"}
          >
            {children}
          </div>,
          scrollableParentRef.current ?? document.body
        )}
    </>
  );
}
