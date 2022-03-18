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

import React, { useCallback, useEffect, useRef, useState } from "react";

export function useContextMenuHandler(domEventTarget: HTMLDivElement | Document = document): {
  contextMenuRef: React.RefObject<HTMLDivElement>;
  contextMenuXPos: string;
  contextMenuYPos: string;
  contextMenuVisibility: boolean;
  setContextMenuVisibility: (value: ((prevState: boolean) => boolean) | boolean) => void;
  targetElement?: EventTarget;
} {
  const wrapperRef = useRef<HTMLDivElement>(null);

  const [xPos, setXPos] = useState("0px");
  const [yPos, setYPos] = useState("0px");
  const [contextMenuVisible, setContextMenuVisible] = useState(false);
  const eventTarget = useRef<EventTarget>();

  const hideContextMenu = useCallback(() => {
    contextMenuVisible && setContextMenuVisible(false);
  }, [contextMenuVisible]);

  const showContextMenu = useCallback(
    (event: MouseEvent) => {
      if (wrapperRef.current && wrapperRef.current === event.target) {
        event.preventDefault();
        eventTarget.current = event.target;
        setXPos(`${event.pageX}px`);
        setYPos(`${event.pageY}px`);
        setContextMenuVisible(true);
      }
    },
    [setXPos, setYPos]
  );

  useEffect(() => {
    document.addEventListener("click", hideContextMenu);
    domEventTarget.addEventListener("contextmenu", hideContextMenu);
    domEventTarget.addEventListener("contextmenu", showContextMenu);
    return () => {
      document.removeEventListener("click", hideContextMenu);
      domEventTarget.removeEventListener("contextmenu", hideContextMenu);
      domEventTarget.removeEventListener("contextmenu", showContextMenu);
    };
  });

  return {
    contextMenuRef: wrapperRef,
    contextMenuXPos: xPos,
    contextMenuYPos: yPos,
    contextMenuVisibility: contextMenuVisible,
    setContextMenuVisibility: setContextMenuVisible,
    targetElement: eventTarget.current,
  };
}
