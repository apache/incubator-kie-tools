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
import { useBoxedExpressionEditor } from "../components/BoxedExpressionEditor/BoxedExpressionEditorContext";

export function useCustomContextMenuHandler(domEventTarget: HTMLDivElement | Document = document): {
  ref: React.RefObject<HTMLDivElement>;
  xPos: string;
  yPos: string;
  isOpen: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  targetElement?: EventTarget;
} {
  const { setContextMenuOpen } = useBoxedExpressionEditor();
  const containerRef = useRef<HTMLDivElement>(null);

  const [xPos, setXPos] = useState("0px");
  const [yPos, setYPos] = useState("0px");
  const [isOpen, setOpen] = useState(false);

  const eventTarget = useRef<EventTarget>();

  const hide = useCallback(
    (e: MouseEvent) => {
      e.preventDefault();

      if (!isOpen) {
        return;
      }

      setOpen(false);
      setContextMenuOpen(false);
    },
    [isOpen, setContextMenuOpen]
  );

  const show = useCallback(
    (e: MouseEvent) => {
      if (containerRef.current && containerRef.current === e.target) {
        e.preventDefault();
        eventTarget.current = e.target;
        setXPos(`${e.pageX}px`);
        setYPos(`${e.pageY}px`);
        setOpen(true);
        setContextMenuOpen(true);
      }
    },
    [setXPos, setYPos, setContextMenuOpen]
  );

  useEffect(() => {
    document.addEventListener("click", hide);
    domEventTarget.addEventListener("contextmenu", hide);
    domEventTarget.addEventListener("contextmenu", show);
    return () => {
      document.removeEventListener("click", hide);
      domEventTarget.removeEventListener("contextmenu", hide);
      domEventTarget.removeEventListener("contextmenu", show);
    };
  });

  return {
    ref: containerRef,
    xPos,
    yPos,
    isOpen,
    setOpen,
    targetElement: eventTarget.current,
  };
}
