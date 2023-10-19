import "./Draggable.css";
import * as React from "react";
import { useState, useCallback, useEffect, useContext, useMemo } from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import GripVerticalIcon from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";

export interface DraggableContext {
  source: number;
  dest: number;
  dragging: boolean;
  onDragStart: (index: number) => void;
  onDragEnter: (index: number) => void;
  onDragEnd: (index: number) => void;
}

export const DraggableContext = React.createContext<DraggableContext>({} as any);

export function useDraggableContext() {
  return useContext(DraggableContext);
}

export function DraggableContextProvider({
  children,
  reorder,
}: React.PropsWithChildren<{ reorder: (source: number, dest: number) => void }>) {
  const [source, setSource] = useState<number>(-1);
  const [dest, setDest] = useState<number>(-1);
  const [dragging, setDragging] = useState<boolean>(false);

  const onDragStart = useCallback((index: number) => {
    setDragging(true);
    setSource(index);
  }, []);

  const onDragEnter = useCallback((index: number) => {
    setDest(index);
  }, []);

  const onDragEnd = useCallback((index: number) => {
    setDragging(false);
  }, []);

  useEffect(() => {
    if (dragging === false && source !== -1 && dest !== -1) {
      setSource(-1);
      setDest(-1);

      reorder(source, dest);
    }
  }, [dest, dragging, source, reorder]);

  return (
    <DraggableContext.Provider
      value={{
        source,
        dest,
        dragging,
        onDragStart,
        onDragEnter,
        onDragEnd,
      }}
    >
      {children}
    </DraggableContext.Provider>
  );
}

export function Draggable(props: React.PropsWithChildren<{ index: number }>) {
  const { source, dest, dragging, onDragStart, onDragEnter, onDragEnd } = useDraggableContext();
  const [draggable, setDraggable] = useState(false);
  const [hover, setHover] = useState(false);

  const rowClassName = useMemo(() => {
    let className = "kie-dmn-editor--draggable-row";
    if (props.index === dest) {
      className += " kie-dmn-editor--draggable-row-dest";
    }
    if (hover) {
      className += " kie-dmn-editor--draggable-row-hovered";
    }
    return className;
  }, [dest, props.index, hover]);

  return (
    <div
      className={rowClassName}
      draggable={dragging || draggable}
      onDragStart={() => onDragStart(props.index)}
      onDragEnter={() => onDragEnter(props.index)}
      onDragEnd={() => onDragEnd(props.index)}
      onPointerEnter={() => setHover(true)}
      onPointerLeave={() => setHover(false)}
      onPointerOver={() => setHover(true)}
    >
      {
        <Icon
          className={"kie-dmn-editor--draggable-icon"}
          onPointerEnter={() => setDraggable(true)}
          onPointerLeave={() => setDraggable(false)}
        >
          <GripVerticalIcon
            className={
              props.index === source || hover
                ? "kie-dmn-editor--draggable-icon-svg-hovered"
                : "kie-dmn-editor--draggable-icon-svg"
            }
          />
        </Icon>
      }
      <div className={"kie-dmn-editor--draggable-children"}>{props.children}</div>
    </div>
  );
}
