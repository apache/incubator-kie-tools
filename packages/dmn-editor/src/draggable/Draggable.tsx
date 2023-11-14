import "./Draggable.css";
import * as React from "react";
import { useState, useCallback, useContext, useMemo, useRef } from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import GripVerticalIcon from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";

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

export function DraggableContextProvider({
  children,
  reorder,
  onDragEnd,
}: React.PropsWithChildren<{
  reorder: (source: number, dest: number) => void;
  onDragEnd?: (source: number, dest: number) => void;
}>) {
  const [source, setSource] = useState<number>(-1);
  const [dest, setDest] = useState<number>(-1);
  const [dragging, setDragging] = useState<boolean>(false);
  const [origin, setOrigin] = useState<number>(-1);
  const [leftOrigin, setLeftOrigin] = useState<boolean>(false);

  const onInternalDragStart = useCallback((index: number) => {
    setDragging(true);
    setSource(index);
    setOrigin(index);
    setLeftOrigin(false);
  }, []);

  const onInternalDragOver = useCallback((e: React.DragEvent<HTMLDivElement>, index: number) => {
    e.preventDefault();
    setDest(index);
  }, []);

  const onInternalDragEnd = useCallback(
    (index: number) => {
      onDragEnd?.(origin, dest);
      setDragging(false);
      setSource(-1);
      setDest(-1);
      setOrigin(-1);
      setLeftOrigin(false);
    },
    [dest, onDragEnd, origin]
  );

  const onInternalDragEnter = useCallback(
    (index: number) => {
      if (index === dest && index !== source) {
        reorder(source, dest);
        setSource(dest);
        setDest(source);
      }
    },
    [dest, reorder, source]
  );

  const onInternalDragLeave = useCallback(
    (index: number) => {
      if (!leftOrigin && index !== source) {
        setLeftOrigin(true);
      }
    },
    [leftOrigin, source]
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
        {children}
      </DraggableDispatchContext.Provider>
    </DraggableStateContext.Provider>
  );
}

export function Draggable(props: {
  index: number;
  children: React.ReactNode;
  style?: React.CSSProperties;
  handlerStyle?: React.CSSProperties;
  childrenStyle?: React.CSSProperties;
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
      className={rowClassName}
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
    >
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
      <div className={"kie-dmn-editor--draggable-children"} style={props.childrenStyle}>
        <DraggableItemContext.Provider value={{ hovered }}>{props.children}</DraggableItemContext.Provider>
      </div>
    </div>
  );
}
