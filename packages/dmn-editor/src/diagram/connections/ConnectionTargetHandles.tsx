import * as React from "react";
import { useMemo } from "react";
import * as RF from "reactflow";

export function ConnectionTargetHandles(props: { isTargeted: boolean }) {
  const style: React.CSSProperties = useMemo(
    () => ({
      opacity: props.isTargeted ? 1 : 0,
    }),
    [props.isTargeted]
  );

  return (
    <>
      {/* Left - For some reason, left needs to be first for snapping to work well. */}
      <RF.Handle
        id="target-left"
        className={"kie-dmn-editor--nswe-target-handle left"}
        style={{ ...style }}
        type={"target"}
        position={RF.Position.Left}
      />

      <RF.Handle
        id="source-left"
        className={"kie-dmn-editor--nswe-target-handle left"}
        style={{ ...style }}
        isConnectableEnd={false}
        type={"source"}
        position={RF.Position.Left}
      />

      {/* North */}
      <RF.Handle
        id="target-north"
        className={"kie-dmn-editor--nswe-target-handle top"}
        style={{ ...style }}
        type={"target"}
        position={RF.Position.Top}
      />

      <RF.Handle
        id="source-north"
        className={"kie-dmn-editor--nswe-target-handle top"}
        style={{ ...style }}
        isConnectableEnd={false}
        type={"source"}
        position={RF.Position.Top}
      />

      {/* Right */}
      <RF.Handle
        id="target-right"
        className={"kie-dmn-editor--nswe-target-handle right"}
        style={{ ...style }}
        type={"target"}
        position={RF.Position.Right}
      />
      <RF.Handle
        id="source-right"
        className={"kie-dmn-editor--nswe-target-handle right"}
        style={{ ...style }}
        isConnectableEnd={false}
        type={"source"}
        position={RF.Position.Right}
      />

      {/* South */}
      <RF.Handle
        id="target-south"
        className={"kie-dmn-editor--nswe-target-handle bottom"}
        style={{ ...style }}
        type={"target"}
        position={RF.Position.Bottom}
      />

      <RF.Handle
        id="source-south"
        className={"kie-dmn-editor--nswe-target-handle bottom"}
        style={{ ...style }}
        isConnectableEnd={false}
        type={"source"}
        position={RF.Position.Bottom}
      />

      {/* Center */}
      <RF.Handle
        id="target-center"
        className={"kie-dmn-editor--nswe-target-handle center"}
        style={{ ...style }}
        type={"target"}
        position={RF.Position.Top}
      />

      <RF.Handle
        id="source-center"
        className={"kie-dmn-editor--nswe-target-handle center"}
        style={{ ...style }}
        isConnectableEnd={false}
        type={"source"}
        position={RF.Position.Top}
      />

      {/* All */}
      {/* <RF.Handle
        id="target-all"
        className={"kie-dmn-editor--nswe-target-handle all"}
        style={{ ...style }}
        type={"target"}
        position={RF.Position.Top}
      />

      <RF.Handle
        id="source-all"
        className={"kie-dmn-editor--nswe-target-handle all"}
        style={{ ...style }}
        isConnectableEnd={false}
        type={"source"}
        position={RF.Position.Top}
      /> */}
    </>
  );
}
