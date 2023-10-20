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

const DraggableContext = React.createContext<DraggableContext>({} as any);

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
  const { dest, dragging, onDragStart, onDragEnter, onDragEnd } = useDraggableContext();

  const destClassName = useMemo(
    () => (props.index === dest ? "kie-dmn-editor--draggable-row-dest" : ""),
    [dest, props.index]
  );

  const draggingClassName = useMemo(() => (dragging ? "kie-dmn-editor--draggable-row-dragging" : ""), [dragging]);

  return (
    <div className={`kie-dmn-editor--draggable-row ${destClassName} ${draggingClassName}`}>
      <Icon
        className={"kie-dmn-editor--draggable-icon"}
        draggable={true}
        onDragStart={() => onDragStart(props.index)}
        onDragEnter={() => onDragEnter(props.index)}
        onDragEnd={() => onDragEnd(props.index)}
      >
        <GripVerticalIcon />
      </Icon>
      <div className={"kie-dmn-editor--draggable-children"}>{props.children}</div>
    </div>
  );
}