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

import "./Draggable.css";
import * as React from "react";
import { useState, useCallback, useContext, useMemo, useLayoutEffect } from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import GripVerticalIcon from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";

export interface DraggableStateContext {
  source: number;
  dest: number;
  dragging: boolean;
  origin: number;
  leftOrigin: boolean;
}

export interface DraggableDispatchContext {
  onDragStart: (index: number) => void;
  onDragOver: (e: React.DragEvent<HTMLDivElement>, index: number) => void;
  onDragEnd: (index: number) => void;
  onDragEnter: (index: number) => void;
  onDragLeave: (index: number) => void;
}

export const DraggableStateContext = React.createContext<DraggableStateContext>({} as any);
export const DraggableDispatchContext = React.createContext<DraggableDispatchContext>({} as any);
export const DraggableItemContext = React.createContext<{ hovered: boolean }>({} as any);

export function useDraggableStateContext() {
  return useContext(DraggableStateContext);
}

export function useDraggableDispatchContext() {
  return useContext(DraggableDispatchContext);
}

export function useDraggableItemContext() {
  return useContext(DraggableItemContext);
}

export type DraggableReorderFunction = (source: number, dest: number) => void;

export function DragAndDrop({
  reorder,
  onDragEnd,
  values,
  draggableItem,
  isDisabled,
}: {
  reorder: DraggableReorderFunction;
  onDragEnd?: (source: number, dest: number) => void;
  values?: any[];
  draggableItem?: (value: any, index: number) => React.ReactNode;
  isDisabled: boolean;
}) {
  const [source, setSource] = useState<number>(-1);
  const [dest, setDest] = useState<number>(-1);
  const [dragging, setDragging] = useState<boolean>(false);
  const [origin, setOrigin] = useState<number>(-1);
  const [leftOrigin, setLeftOrigin] = useState<boolean>(false);
  const [valuesCopy, setValuesCopy] = useState(values ?? []);
  const [valuesKeys, setValuesKeys] = useState((values ?? [])?.map((_) => generateUuid()));

  useLayoutEffect(() => {
    setValuesCopy((prev) => {
      if (values?.length !== prev.length) {
        setValuesKeys((values ?? [])?.map((_) => generateUuid()));
      }
      return values ?? [];
    });
  }, [values, isDisabled]);

  const onInternalDragStart = useCallback(
    (index: number) => {
      if (isDisabled) {
        return;
      }
      setDragging(true);
      setSource(index);
      setOrigin(index);
      setLeftOrigin(false);
    },
    [isDisabled]
  );

  const onInternalDragOver = useCallback(
    (e: React.DragEvent<HTMLDivElement>, index: number) => {
      if (isDisabled) {
        return;
      }
      e.preventDefault();
      setDest(index);
    },
    [isDisabled]
  );

  const onInternalDragEnd = useCallback(
    (index: number) => {
      if (isDisabled) {
        return;
      }
      onDragEnd?.(origin, dest);
      setDragging(false);
      setSource(-1);
      setDest(-1);
      setOrigin(-1);
      setLeftOrigin(false);
    },
    [dest, onDragEnd, origin, isDisabled]
  );

  const onInternalReorder = useCallback(
    (source: number, dest: number) => {
      if (isDisabled) {
        return;
      }
      setValuesCopy((prev) => {
        const reordenedValues = [...prev];
        const [removedValue] = reordenedValues.splice(source, 1);
        reordenedValues.splice(dest, 0, removedValue);
        return reordenedValues;
      });
      setValuesKeys((prev) => {
        const reordenedKeys = [...prev];
        const [removedKeys] = reordenedKeys.splice(source, 1);
        reordenedKeys.splice(dest, 0, removedKeys);
        return reordenedKeys;
      });
    },
    [isDisabled]
  );

  const onInternalDragEnter = useCallback(
    (index: number) => {
      if (isDisabled) {
        return;
      }
      if (index === dest && index !== source) {
        reorder(source, dest);
        onInternalReorder(source, dest);
        setSource(dest);
        setDest(source);
      }
    },
    [dest, reorder, source, onInternalReorder, isDisabled]
  );

  const onInternalDragLeave = useCallback(
    (index: number) => {
      if (isDisabled) {
        return;
      }
      if (!leftOrigin && index !== source) {
        setLeftOrigin(true);
      }
    },
    [leftOrigin, source, isDisabled]
  );

  return (
    <DraggableStateContext.Provider
      value={{
        source,
        dest,
        dragging,
        origin,
        leftOrigin,
      }}
    >
      <DraggableDispatchContext.Provider
        value={{
          onDragStart: onInternalDragStart,
          onDragOver: onInternalDragOver,
          onDragEnd: onInternalDragEnd,
          onDragEnter: onInternalDragEnter,
          onDragLeave: onInternalDragLeave,
        }}
      >
        {valuesCopy?.map((value, index) => <div key={valuesKeys[index]}>{draggableItem?.(value, index)}</div>)}
      </DraggableDispatchContext.Provider>
    </DraggableStateContext.Provider>
  );
}

export function Draggable(props: {
  index: number;
  children: React.ReactNode;
  style?: React.CSSProperties;
  handlerStyle?: React.CSSProperties;
  rowStyle?: React.CSSProperties;
  rowClassName?: string;
  childrenStyle?: React.CSSProperties;
  childrenClassName?: string;
  itemStyle?: React.CSSProperties;
  itemClassName?: string;
  isDisabled: boolean;
}) {
  const { source, dragging, leftOrigin } = useDraggableStateContext();
  const { onDragStart, onDragOver, onDragEnd, onDragEnter, onDragLeave } = useDraggableDispatchContext();
  const [hoveredItem, setHoveredItem] = useState<number>(-1);
  const [draggable, setDraggable] = useState(false);
  const hovered = useMemo(() => hoveredItem === props.index, [hoveredItem, props.index]);
  const isDragging = useMemo(() => props.index === source && leftOrigin, [leftOrigin, props.index, source]);

  const rowClassName = useMemo(() => {
    let className = "kie-dmn-editor--draggable-row";

    if (hovered) {
      className += " kie-dmn-editor--draggable-row-hovered";
    }

    if (isDragging) {
      className += " kie-dmn-editor--draggable-row-is-dragging";
    }

    return className;
  }, [hovered, isDragging]);

  return (
    <div
      style={props.style}
      className={`${rowClassName} ${props.rowClassName ? props.rowClassName : ""}`}
      draggable={dragging || draggable}
      onDragStart={() => onDragStart(props.index)}
      onDragOver={(e) => onDragOver(e, props.index)}
      onDragEnd={() => {
        onDragEnd(props.index);
        setHoveredItem(-1);
      }}
      onDragLeave={() => onDragLeave(props.index)}
      onDragEnter={() => onDragEnter(props.index)}
      onPointerEnter={() => setHoveredItem(props.index)}
      onPointerLeave={() => setHoveredItem(-1)}
      onPointerOver={() => setHoveredItem(props.index)}
      data-testid={`kie-tools--dmn-editor--draggable-row-${props.index}`}
    >
      {!props.isDisabled ? (
        <div data-testid={"kie-tools--dmn-editor--draggable-icon"}>
          <Icon
            className={"kie-dmn-editor--draggable-icon"}
            onPointerEnter={() => setDraggable(true)}
            onPointerLeave={() => setDraggable(false)}
            style={props.handlerStyle}
          >
            <GripVerticalIcon
              className={
                hovered ? "kie-dmn-editor--draggable-icon-handler-hovered" : "kie-dmn-editor--draggable-icon-handler"
              }
            />
          </Icon>
        </div>
      ) : (
        <div style={{ width: "36px" }}></div>
      )}
      <div
        style={props.childrenStyle}
        className={`kie-dmn-editor--draggable-children ${props.childrenClassName ? props.childrenClassName : ""}`}
        data-testid={"kie-tools--dmn-editor--draggable-children"}
      >
        <DraggableItemContext.Provider value={{ hovered }}>{props.children}</DraggableItemContext.Provider>
      </div>
    </div>
  );
}
