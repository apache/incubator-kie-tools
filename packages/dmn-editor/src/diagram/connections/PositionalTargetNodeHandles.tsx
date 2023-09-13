import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import * as RF from "reactflow";

export enum TargetHandleId {
  TargetLeft = "target-left",
  TargetTop = "target-top",
  TargetRight = "target-right",
  TargetBottom = "target-bottom",
  TargetCenter = "target-center",
}

export function PositionalTargetNodeHandles(props: { isTargeted: boolean; nodeId: string }) {
  const connectionHandleType = RF.useStore((state) => state.connectionHandleType);

  const areTargetsConnectable = props.isTargeted && connectionHandleType !== "target";
  const areSourcesConnectable = props.isTargeted && connectionHandleType === "target";

  const targetsStyle: React.CSSProperties = useMemo(
    () => (areTargetsConnectable ? {} : { opacity: 0, pointerEvents: "none" }),
    [areTargetsConnectable]
  );

  const sourcesStyle: React.CSSProperties = useMemo(
    () => (areSourcesConnectable ? {} : { opacity: 0, pointerEvents: "none" }),
    [areSourcesConnectable]
  );

  return (
    <>
      <>
        <RF.Handle
          id={TargetHandleId.TargetLeft}
          className={"kie-dmn-editor--node-handle left"}
          style={{ ...targetsStyle }}
          isConnectableEnd={areTargetsConnectable}
          type={"target"}
          position={RF.Position.Left}
        />
        <RF.Handle
          id={TargetHandleId.TargetTop}
          className={"kie-dmn-editor--node-handle top"}
          style={{ ...targetsStyle }}
          isConnectableEnd={areTargetsConnectable}
          type={"target"}
          position={RF.Position.Top}
        />
        <RF.Handle
          id={TargetHandleId.TargetRight}
          className={"kie-dmn-editor--node-handle right"}
          style={{ ...targetsStyle }}
          isConnectableEnd={areTargetsConnectable}
          type={"target"}
          position={RF.Position.Right}
        />
        <RF.Handle
          id={TargetHandleId.TargetBottom}
          className={"kie-dmn-editor--node-handle bottom"}
          style={{ ...targetsStyle }}
          isConnectableEnd={areTargetsConnectable}
          type={"target"}
          position={RF.Position.Bottom}
        />
        <RF.Handle
          id={TargetHandleId.TargetCenter}
          className={"kie-dmn-editor--node-handle center"}
          style={{ ...targetsStyle }}
          isConnectableEnd={areTargetsConnectable}
          type={"target"}
          position={RF.Position.Top}
        />
      </>
      {/*  */}
      <>
        <RF.Handle
          id={"source-left"}
          className={"kie-dmn-editor--node-handle left"}
          style={{ ...sourcesStyle }}
          isConnectableStart={areSourcesConnectable}
          type={"source"}
          position={RF.Position.Left}
        />
        <RF.Handle
          id={"source-top"}
          className={"kie-dmn-editor--node-handle top"}
          style={{ ...sourcesStyle }}
          isConnectableStart={areSourcesConnectable}
          type={"source"}
          position={RF.Position.Top}
        />
        <RF.Handle
          id={"source-right"}
          className={"kie-dmn-editor--node-handle right"}
          style={{ ...sourcesStyle }}
          isConnectableStart={areSourcesConnectable}
          type={"source"}
          position={RF.Position.Right}
        />
        <RF.Handle
          id={"source-bottom"}
          className={"kie-dmn-editor--node-handle bottom"}
          style={{ ...sourcesStyle }}
          isConnectableStart={areSourcesConnectable}
          type={"source"}
          position={RF.Position.Bottom}
        />
        <RF.Handle
          id={"source-center"}
          className={"kie-dmn-editor--node-handle center"}
          style={{ ...sourcesStyle }}
          isConnectableStart={areSourcesConnectable}
          type={"source"}
          position={RF.Position.Top}
        />
      </>
    </>
  );
}
