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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { generateUuid } from "../api";
import { useBoxedExpressionEditor } from "../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../keysUtils";

export function useCustomContextMenuHandler(domEventTargetRef: React.RefObject<HTMLDivElement | null>): {
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
      if (!isOpen) {
        return;
      }

      setCurrentlyOpenContextMenu(undefined);
    },
    [isOpen, setCurrentlyOpenContextMenu]
  );

  const show = useCallback(
    (e: MouseEvent) => {
      e.preventDefault();
      e.stopPropagation();
      setPosition({
        x:
          e.pageX +
          (scrollableParentRef.current?.offsetLeft ?? 0) -
          (scrollableParentRef.current?.getBoundingClientRect().left ?? 0),
        y:
          e.pageY +
          (scrollableParentRef.current?.offsetTop ?? 0) -
          (scrollableParentRef.current?.getBoundingClientRect().top ?? 0),
      });
      setScroll({
        x: scrollableParentRef.current?.scrollLeft ? 0 : window.scrollX,
        y: scrollableParentRef.current?.scrollTop ? 0 : window.scrollY,
      });
      setCurrentlyOpenContextMenu(id);
    },
    [id, scrollableParentRef, setCurrentlyOpenContextMenu]
  );

  useEffect(() => {
    function onScroll(e: UIEvent) {
      if (currentlyOpenContextMenu === id) {
        setScroll({
          x: (e.target as HTMLElement).scrollLeft ? 0 : window.scrollX,
          y: (e.target as HTMLElement).scrollTop ? 0 : window.scrollY,
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
        setCurrentlyOpenContextMenu(undefined);
      }
    }

    document.addEventListener("keydown", handleEscPressed);
    return () => {
      document.removeEventListener("keydown", handleEscPressed);
    };
  }, [setCurrentlyOpenContextMenu]);

  useEffect(() => {
    const elem = domEventTargetRef.current;

    if (currentlyOpenContextMenu && isOpen) {
      document.addEventListener("click", hide);
      document.addEventListener("contextmenu", hide);
      elem?.addEventListener("contextmenu", show);
      return () => {
        elem?.removeEventListener("contextmenu", show);
        document.removeEventListener("contextmenu", hide);
        document.removeEventListener("click", hide);
      };
    }

    elem?.addEventListener("contextmenu", show);
    return () => {
      elem?.removeEventListener("contextmenu", show);
    };
  }, [domEventTargetRef, hide, currentlyOpenContextMenu, isOpen, show]);

  return {
    xPos: position.x - scroll.x + 1, // Leave some margin for clicking without moving the mouse.
    yPos: position.y - scroll.y + 1, // Leave some margin for clicking without moving the mouse.
    isOpen,
  };
}
