import "./Draggable.css";
import * as React from "react";
import { useState, useCallback, useEffect, useContext, useMemo } from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import GripVerticalIcon from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";

export interface DraggableContext {
  source: number;
  dest: number;
  dragging: boolean;
  dragged: number;
  onDragStart: (index: number) => void;
  onDragOver: (index: number) => void;
  onDragEnd: (index: number) => void;
  onDragEnter: (index: number) => void;
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
  const [dragged, setDragged] = useState(-1);
  const [dragging, setDragging] = useState<boolean>(false);

  const onDragStart = useCallback((index: number) => {
    console.log("onDragStart");
    setDragging(true);
    setSource(index);
    setDragged(index);
  }, []);

  const onDragOver = useCallback((index: number) => {
    setDest(index);
  }, []);

  const onDragEnd = useCallback((index: number) => {
    console.log("onDragEnd");
    setDragging(false);
    setSource(-1);
    setDest(-1);
    setDragged(-1);
  }, []);

  const onDragEnter = useCallback(
    (index: number) => {
      console.log("onDragEnter");

      if (index === dest && index !== source) {
        reorder(source, dest);
        setSource(dest);
        setDest(source);
      }
    },
    [dest, reorder, source]
  );

  return (
    <DraggableContext.Provider
      value={{
        source,
        dest,
        dragging,
        dragged,
        onDragStart,
        onDragOver,
        onDragEnd,
        onDragEnter,
      }}
    >
      {children}
    </DraggableContext.Provider>
  );
}

export function Draggable(props: React.PropsWithChildren<{ index: number }>) {
  const { source, dragged, dragging, onDragStart, onDragOver, onDragEnd, onDragEnter } = useDraggableContext();
  const [draggable, setDraggable] = useState(false);
  const [hover, setHover] = useState(false);

  const rowClassName = useMemo(() => {
    let className = "kie-dmn-editor--draggable-row";

    if (hover) {
      className += " kie-dmn-editor--draggable-row-hovered";
    }

    // if (props.index === source) {
    //   className += " kie-dmn-editor--draggable-row-aaa";
    // }
    return className;
  }, [hover]);

  return (
    <div
      className={rowClassName}
      draggable={dragging || draggable}
      onDragStart={() => onDragStart(props.index)}
      onDragOver={() => onDragOver(props.index)}
      onDragEnd={() => {
        onDragEnd(props.index);
        setHover(false);
      }}
      onDragEnter={() => onDragEnter(props.index)}
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
