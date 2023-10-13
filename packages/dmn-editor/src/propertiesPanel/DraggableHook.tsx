import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import GripVerticalIcon from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";
import * as React from "react";
import { useState, useCallback, useEffect, useContext } from "react";

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

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "row",
        borderWidth: "1px",
        borderStyle: "dashed",
        borderRadius: "5px",
        borderColor: props.index === dest ? "rgb(0, 107, 164)" : dragging ? "gray" : "transparent",
        backgroundColor:
          props.index === dest ? "rgb(0 107 164 / 10%)" : dragging ? "rgb(128 128 128 / 10%)" : "transparent",
      }}
    >
      <Icon
        style={{
          marginTop: "4px",
          marginRight: "8px",
        }}
        draggable={true}
        onDragStart={() => onDragStart(props.index)}
        onDragEnter={() => onDragEnter(props.index)}
        onDragEnd={() => onDragEnd(props.index)}
      >
        <GripVerticalIcon style={{ color: "#8080809e" }} />
      </Icon>
      <div style={{ flexGrow: 1 }}>{props.children}</div>
    </div>
  );
}
