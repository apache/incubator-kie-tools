import * as React from "react";
import * as RF from "reactflow";
import { useMemo } from "react";

export function NsweHandles() {
  const style = useMemo(
    () => ({
      opacity: 0,
      margin: "4px",
    }),
    []
  );

  return (
    <>
      <RF.Handle style={style} type="target" position={RF.Position.Top} id="target-north" />
      <RF.Handle style={style} type="target" position={RF.Position.Right} id="target-right" />
      <RF.Handle style={style} type="target" position={RF.Position.Bottom} id="target-south" />
      <RF.Handle style={style} type="target" position={RF.Position.Left} id="target-left" />
      <RF.Handle style={style} type="source" position={RF.Position.Top} id="source-north" />
      <RF.Handle style={style} type="source" position={RF.Position.Right} id="source-right" />
      <RF.Handle style={style} type="source" position={RF.Position.Bottom} id="source-south" />
      <RF.Handle style={style} type="source" position={RF.Position.Left} id="source-left" />
    </>
  );
}
