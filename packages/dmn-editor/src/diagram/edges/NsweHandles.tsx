import * as React from "react";
import * as RF from "reactflow";
import { useMemo } from "react";

export function NsweHandles(props: { isTargeted: boolean }) {
  const style: React.CSSProperties = useMemo(
    () => ({
      opacity: props.isTargeted ? 1 : 0,
      width: "14px",
      height: "14px",
      background: props.isTargeted ? "lightblue" : undefined,
      pointerEvents: "none",
    }),
    [props.isTargeted]
  );

  return (
    <>
      {/* North */}
      <RF.Handle
        id="target-north"
        style={{ ...style, top: "-6px", left: "calc(50%)" }}
        type={"target"}
        position={RF.Position.Top}
      />

      <RF.Handle
        id="source-north"
        style={{ ...style, top: "-6px", left: "calc(50%)" }}
        type={"source"}
        position={RF.Position.Top}
      />

      {/* Right */}
      <RF.Handle
        id="target-right"
        style={{ ...style, top: "calc(50%)", right: "-6px" }}
        type={"target"}
        position={RF.Position.Right}
      />
      <RF.Handle
        id="source-right"
        style={{ ...style, top: "calc(50%)", right: "-6px" }}
        type={"source"}
        position={RF.Position.Right}
      />

      {/* South */}
      <RF.Handle
        id="target-south"
        style={{ ...style, bottom: "-6px", left: "calc(50%)" }}
        type={"target"}
        position={RF.Position.Bottom}
      />

      <RF.Handle
        id="source-south"
        style={{ ...style, bottom: "-6px", left: "calc(50%)" }}
        type={"source"}
        position={RF.Position.Bottom}
      />

      {/* Left */}
      <RF.Handle
        id="source-left"
        style={{ ...style, top: "calc(50%)", left: "-6px" }}
        type={"source"}
        position={RF.Position.Left}
      />

      <RF.Handle
        id="target-left"
        style={{ ...style, top: "calc(50%)", left: "-6px" }}
        type={"target"}
        position={RF.Position.Left}
      />

      {/* Center */}
      <RF.Handle
        id="target-center"
        style={{ ...style, top: "calc(50% - 6px)", left: "calc(50%)" }}
        type={"target"}
        position={RF.Position.Top}
      />

      <RF.Handle
        id="source-center"
        style={{ ...style, top: "calc(50% - 6px)", left: "calc(50%)" }}
        type={"source"}
        position={RF.Position.Top}
      />
    </>
  );
}
