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

export function useCustomContextMenuHandler(domEventTargetRef: React.RefObject<HTMLDivElement | null>): {
  xPos: string;
  yPos: string;
  isOpen: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
} {
  const { setContextMenuOpen } = useBoxedExpressionEditor();

  const [xPos, setXPos] = useState("0px");
  const [yPos, setYPos] = useState("0px");
  const [isOpen, setOpen] = useState(false);

  const hide = useCallback(
    (e: MouseEvent) => {
      console.info("custom hide");
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
      console.info(`custom show: ${e.pageX}, ${e.pageY}`);
      e.preventDefault();
      setXPos(`${e.pageX}px`);
      setYPos(`${e.pageY}px`);
      setOpen(true);
      setContextMenuOpen(true);
    },
    [setXPos, setYPos, setContextMenuOpen]
  );

  useEffect(() => {
    document.addEventListener("click", hide);
    // domEventTarget.addEventListener("contextmenu", hide);
    const elem = domEventTargetRef.current;
    elem?.addEventListener("contextmenu", show);
    return () => {
      elem?.removeEventListener("contextmenu", show);
      // domEventTarget.removeEventListener("contextmenu", hide);
      document.removeEventListener("click", hide);
    };
  });

  return {
    xPos,
    yPos,
    isOpen,
    setOpen,
  };
}
