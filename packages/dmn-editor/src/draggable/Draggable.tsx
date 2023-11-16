import "./Draggable.css";
import * as React from "react";
import { useState, useCallback, useContext, useMemo, useRef } from "react";
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

export function DraggableContextProvider({
  children,
  reorder,
  onDragEnd,
  draggableListStyle,
  values,
  itemComponent,
}: React.PropsWithChildren<{
  reorder: DraggableReorderFunction;
  onDragEnd?: (source: number, dest: number) => void;
  draggableListStyle?: React.CSSProperties;
  values?: any[];
  itemComponent?: (value: any, index: number) => React.ReactNode;
}>) {
  const [source, setSource] = useState<number>(-1);
  const [dest, setDest] = useState<number>(-1);
  const [dragging, setDragging] = useState<boolean>(false);
  const [origin, setOrigin] = useState<number>(-1);
  const [leftOrigin, setLeftOrigin] = useState<boolean>(false);
  const [valuesCopy, setValuesCopy] = useState(values ?? []);
  const [valuesKeys, setValuesKeys] = useState((values ?? [])?.map((_) => generateUuid()));

  React.useLayoutEffect(() => {
    setValuesCopy(values ?? []);
    setValuesKeys((values ?? [])?.map((_) => generateUuid()));
  }, [values]);

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

  const onInternalReorder = useCallback((source: number, dest: number) => {
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
  }, []);

  const onInternalDragEnter = useCallback(
    (index: number) => {
      if (index === dest && index !== source) {
        reorder(source, dest);
        onInternalReorder(source, dest);
        setSource(dest);
        setDest(source);
      }
    },
    [dest, reorder, source, onInternalReorder]
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
        <ul style={draggableListStyle}>
          {valuesCopy?.map((value, index) => (
            <React.Fragment key={valuesKeys[index]}>{itemComponent?.(value, index)}</React.Fragment>
          ))}
          {children}
        </ul>
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
      <div
        style={props.childrenStyle}
        className={`kie-dmn-editor--draggable-children ${props.childrenClassName ? props.childrenClassName : ""}`}
      >
        <DraggableItemContext.Provider value={{ hovered }}>
          <li style={props.itemStyle} className={props.itemClassName}>
            {props.children}
          </li>
        </DraggableItemContext.Provider>
      </div>
    </div>
  );
}
