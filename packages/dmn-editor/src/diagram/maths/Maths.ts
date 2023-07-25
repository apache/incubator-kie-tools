import * as RF from "reactflow";

export type Shape = {
  width: number | undefined | null;
  height: number | undefined | null;
  x: number | undefined | null;
  y: number | undefined | null;
};

// returns the parameters (sx, sy, tx, ty, sourcePos, targetPos) you need to create an edge
export function getDiscretelyAutoPositionedEdgeParamsForRfNodes(source: RF.Node, target: RF.Node) {
  const src = {
    x: source.positionAbsolute?.x,
    y: source.positionAbsolute?.y,
    width: source.width,
    height: source.height,
  };
  const tgt = {
    x: target.positionAbsolute?.x,
    y: target.positionAbsolute?.y,
    width: target.width,
    height: target.height,
  };
  return getDiscretelyAutoPositionedEdgeParams(src, tgt);
}

export function getDiscretelyAutoPositionedEdgeParams(src: Shape, tgt: Shape) {
  const [sx, sy, sourcePos] = getPositionalHandlePosition(src, tgt);
  const [tx, ty, targetPos] = getPositionalHandlePosition(tgt, src);
  return { sx, sy, tx, ty, sourcePos, targetPos };
}
// returns the position (top, right, bottom, or right) passed node compared to
export function getPositionalHandlePosition(a: Shape, b: Shape) {
  const centerA = getCenter(a.x, a.y, a.width, a.height);
  const centerB = getCenter(b.x, b.y, b.width, b.height);

  const horizontalDiff = Math.abs(centerA.x - centerB.x);
  const verticalDiff = Math.abs(centerA.y - centerB.y);

  let position;

  // when the horizontal difference between the nodes is bigger, we use Position.Left or Position.Right for the handle
  if (horizontalDiff > verticalDiff) {
    position = centerA.x > centerB.x ? RF.Position.Left : RF.Position.Right;
  } else {
    // here the vertical difference between the nodes is bigger, so we use Position.Top or Position.Bottom for the handle
    position = centerA.y > centerB.y ? RF.Position.Top : RF.Position.Bottom;
  }

  const [x, y] = getHandleCoordsByPosition(a, position);
  return [x, y, position] as const;
}

export function getCenter(
  x: number | null | undefined,
  y: number | null | undefined,
  width: number | null | undefined,
  height: number | null | undefined
) {
  return {
    x: (x ?? 0) + (width ?? 0) / 2,
    y: (y ?? 0) + (height ?? 0) / 2,
  };
}

function getHandleCoordsByPosition(node: Shape, handlePosition: RF.Position) {
  let handleX = 0;
  let handleY = 0;

  switch (handlePosition) {
    case RF.Position.Left:
      handleX = 0;
      handleY = (node.height ?? 0) / 2;
      break;
    case RF.Position.Right:
      handleX = node.width ?? 0;
      handleY = (node.height ?? 0) / 2;
      break;
    case RF.Position.Top:
      handleX = (node.width ?? 0) / 2;
      handleY = 0;
      break;
    case RF.Position.Bottom:
      handleX = (node.width ?? 0) / 2;
      handleY = node.height ?? 0;
      break;
  }

  return [(node?.x ?? 0) + handleX, (node?.y ?? 0) + handleY];
}
