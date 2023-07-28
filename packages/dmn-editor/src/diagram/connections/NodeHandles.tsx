import * as React from "react";
import { useMemo } from "react";
import * as RF from "reactflow";

export enum TargetHandleId {
  TargetLeft = "target-left",
  TargetTop = "target-top",
  TargetRight = "target-right",
  TargetBottom = "target-bottom",
  TargetCenter = "target-center",
}

export function NodeHandles(props: { isTargeted: boolean }) {
  const style: React.CSSProperties = useMemo(
    () => ({
      opacity: props.isTargeted ? 1 : 0,
    }),
    [props.isTargeted]
  );

  return (
    <>
      <RF.Handle
        id={TargetHandleId.TargetLeft}
        className={"kie-dmn-editor--node-handle left"}
        style={{ ...style }}
        isConnectableEnd={props.isTargeted}
        type={"target"}
        position={RF.Position.Left}
      />
      <RF.Handle
        id={TargetHandleId.TargetTop}
        className={"kie-dmn-editor--node-handle top"}
        style={{ ...style }}
        isConnectableEnd={props.isTargeted}
        type={"target"}
        position={RF.Position.Top}
      />
      <RF.Handle
        id={TargetHandleId.TargetRight}
        className={"kie-dmn-editor--node-handle right"}
        style={{ ...style }}
        isConnectableEnd={props.isTargeted}
        type={"target"}
        position={RF.Position.Right}
      />
      <RF.Handle
        id={TargetHandleId.TargetBottom}
        className={"kie-dmn-editor--node-handle bottom"}
        style={{ ...style }}
        isConnectableEnd={props.isTargeted}
        type={"target"}
        position={RF.Position.Bottom}
      />
      <RF.Handle
        id={TargetHandleId.TargetCenter}
        className={"kie-dmn-editor--node-handle center"}
        style={{ ...style }}
        isConnectableEnd={props.isTargeted}
        type={"target"}
        position={RF.Position.Top}
      />
    </>
  );
}
