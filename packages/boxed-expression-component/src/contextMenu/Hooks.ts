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
  xPos: string;
  yPos: string;
  isOpen: boolean;
} {
  const { setCurrentlyOpenContextMenu, currentlyOpenContextMenu } = useBoxedExpressionEditor();

  // FIXME: Tiago -> Maybe using window.scrollX/Y is not the best, as other elements might have the scroll added to it.
  const [scroll, setScroll] = useState({ x: 0, y: 0 });

  // FIXME: Tiago -> Need to calculate the context menu length for it to not be hidden by the borders of the screen.
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
      setPosition({ x: e.pageX, y: e.pageY });
      setScroll({ x: window.scrollX, y: window.scrollY });
      setCurrentlyOpenContextMenu(id);
    },
    [id, setCurrentlyOpenContextMenu]
  );

  useEffect(() => {
    function onScroll(e: UIEvent) {
      if (currentlyOpenContextMenu === id) {
        setScroll({ x: window.scrollX, y: window.scrollY });
      }
    }

    window.addEventListener("scroll", onScroll);

    return () => {
      window.removeEventListener("scroll", onScroll);
    };
  }, [currentlyOpenContextMenu, id]);

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
    xPos: `${position.x - scroll.x + 1}px`, // Leave some margin for clicking without moving the mouse.
    yPos: `${position.y - scroll.y + 1}px`, // Leave some margin for clicking without moving the mouse.
    isOpen,
  };
}
