/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useLayoutEffect, useMemo, useState } from "react";
import "./SelectionBox.css";

export interface SelectionBoxProps {
  /** CSS classes of elements that must not trigger the selection box */
  ignoredElements?: string[];

  /** Handler executed when the cursor starts dragging */
  onDragStart?: (startPosition: SelectionStart) => void;

  /** Handler executed when the cursor is drawing the selection box */
  onDragMove?: () => void;

  /** Handler executed when the cursor stops dragging */
  onDragStop?: (endRect: SelectionRect) => void;
}

interface SelectionStart {
  x: number;
  y: number;
}

export interface SelectionRect extends SelectionStart {
  width: number;
  height: number;
}

type SelectionStartState = SelectionStart | null;
type SelectionRectState = SelectionRect | null;
type SelectionStyleState = React.CSSProperties | null;

export const SelectionBox: React.FunctionComponent<SelectionBoxProps> = ({
  ignoredElements,
  onDragStart,
  onDragMove,
  onDragStop,
}: SelectionBoxProps) => {
  const [selectionBoxStyle, setSelectionBoxStyle] = useState<SelectionStyleState>(null);
  const [selectionStart, setSelectionStart] = useState<SelectionStartState>(null);
  const [selectionRect, setSelectionRect] = useState<SelectionRectState>(null);

  useEffect(() => {
    const pxValue = (value: number) => `${value}px`;
    let style = {};
    if (selectionRect) {
      style = {
        width: pxValue(selectionRect.width),
        height: pxValue(selectionRect.height),
        top: pxValue(selectionRect.y),
        left: pxValue(selectionRect.x),
      };
    }
    setSelectionBoxStyle(style);
  }, [selectionRect]);

  const getCoordinate = useCallback((event: MouseEvent | TouchEvent): SelectionStart => {
    if ("touches" in event) {
      return {
        x: event.touches[0].clientX,
        y: event.touches[0].clientY,
      };
    }

    return {
      x: event.clientX,
      y: event.clientY,
    };
  }, []);

  const moveHandler = useCallback(
    (event: MouseEvent | TouchEvent) => {
      if (!selectionStart) {
        return;
      }

      onDragMove?.();

      const mouseCoordinate = getCoordinate(event);
      const x = Math.min(selectionStart.x, mouseCoordinate.x);
      const y = Math.min(selectionStart.y, mouseCoordinate.y);

      const width = Math.abs(mouseCoordinate.x - selectionStart.x);
      const height = Math.abs(mouseCoordinate.y - selectionStart.y);

      setSelectionRect({ x, y, width, height });
    },
    [selectionStart, setSelectionRect, getCoordinate, onDragMove]
  );

  const downHandler = useCallback(
    (event: MouseEvent | TouchEvent) => {
      const targetElement = event.target as Element;
      const isIgnoredTarget = ignoredElements?.some((e) => targetElement.classList.contains(e)) || false;

      if (isIgnoredTarget) {
        return;
      }

      const startPosition = getCoordinate(event);
      setSelectionStart(startPosition);
      onDragStart?.(startPosition);
    },
    [setSelectionStart, ignoredElements, getCoordinate, onDragStart]
  );

  const upHandler = useCallback(() => {
    onDragStop?.(selectionRect!);
    setSelectionStart(null);
    setSelectionRect(null);
  }, [selectionRect, setSelectionStart, setSelectionRect, onDragStop]);

  useLayoutEffect(() => {
    const mouseMoveType = "mousemove";
    const mouseDownType = "mousedown";
    const mouseUpType = "mouseup";
    const touchMoveType = "touchmove";
    const touchStartType = "touchstart";
    const touchEndType = "touchend";

    document.addEventListener(mouseMoveType, moveHandler);
    document.addEventListener(mouseDownType, downHandler);
    document.addEventListener(mouseUpType, upHandler);
    document.addEventListener(touchMoveType, moveHandler);
    document.addEventListener(touchStartType, downHandler);
    document.addEventListener(touchEndType, upHandler);
    return () => {
      document.removeEventListener(mouseMoveType, moveHandler);
      document.removeEventListener(mouseDownType, downHandler);
      document.removeEventListener(mouseUpType, upHandler);
      document.removeEventListener(touchMoveType, moveHandler);
      document.removeEventListener(touchStartType, downHandler);
      document.removeEventListener(touchEndType, upHandler);
    };
  }, [moveHandler, downHandler, upHandler]);

  return useMemo(() => {
    return <div style={{ ...selectionBoxStyle }} className="kie-selection-box"></div>;
  }, [selectionBoxStyle]);
};
