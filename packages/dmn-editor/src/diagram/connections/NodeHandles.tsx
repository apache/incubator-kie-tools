import * as React from "react";
import { useMemo } from "react";
import * as RF from "reactflow";

export function NodeHandles(props: { isTargeted: boolean }) {
  const style: React.CSSProperties = useMemo(
    () => ({
      opacity: props.isTargeted ? 1 : 0,
    }),
    [props.isTargeted]
  );

  const sources = useMemo(
    () => (
      <>
        <RF.Handle
          id="source-left"
          className={"kie-dmn-editor--node-handle left"}
          style={{ ...style }}
          isConnectableEnd={false}
          type={"source"}
          position={RF.Position.Left}
        />
        <RF.Handle
          id="source-top"
          className={"kie-dmn-editor--node-handle top"}
          style={{ ...style }}
          isConnectableEnd={false}
          type={"source"}
          position={RF.Position.Top}
        />
        <RF.Handle
          id="source-right"
          className={"kie-dmn-editor--node-handle right"}
          style={{ ...style }}
          isConnectableEnd={false}
          type={"source"}
          position={RF.Position.Right}
        />
        <RF.Handle
          id="source-bottom"
          className={"kie-dmn-editor--node-handle bottom"}
          style={{ ...style }}
          isConnectableEnd={false}
          type={"source"}
          position={RF.Position.Bottom}
        />
        <RF.Handle
          id="source-center"
          className={"kie-dmn-editor--node-handle center"}
          style={{ ...style }}
          isConnectableEnd={false}
          type={"source"}
          position={RF.Position.Top}
        />
      </>
    ),
    [style]
  );

  const targets = useMemo(
    () => (
      <>
        <RF.Handle
          id="target-left"
          className={"kie-dmn-editor--node-handle left"}
          style={{ ...style }}
          isConnectableEnd={props.isTargeted}
          type={"target"}
          position={RF.Position.Left}
        />
        <RF.Handle
          id="target-top"
          className={"kie-dmn-editor--node-handle top"}
          style={{ ...style }}
          isConnectableEnd={props.isTargeted}
          type={"target"}
          position={RF.Position.Top}
        />
        <RF.Handle
          id="target-right"
          className={"kie-dmn-editor--node-handle right"}
          style={{ ...style }}
          isConnectableEnd={props.isTargeted}
          type={"target"}
          position={RF.Position.Right}
        />
        <RF.Handle
          id="target-bottom"
          className={"kie-dmn-editor--node-handle bottom"}
          style={{ ...style }}
          isConnectableEnd={props.isTargeted}
          type={"target"}
          position={RF.Position.Bottom}
        />
        <RF.Handle
          id="target-center"
          className={"kie-dmn-editor--node-handle center"}
          style={{ ...style }}
          isConnectableEnd={props.isTargeted}
          type={"target"}
          position={RF.Position.Top}
        />
      </>
    ),
    [props.isTargeted, style]
  );

  return (
    <>
      {targets}
      {sources}
    </>
  );
}
