import { DC__Bounds, DC__Point, DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

// export const SNAP_GRID = { x: 1, y: 1 };
export const SNAP_GRID = { x: 20, y: 20 };

export const MIN_SIZE_FOR_NODES = {
  width: SNAP_GRID.x * 8,
  height: SNAP_GRID.y * 4,
};

export function snapShapePosition(shape: DMNDI13__DMNShape) {
  return {
    x: Math.floor((shape["dc:Bounds"]?.["@_x"] ?? 0) / SNAP_GRID.x) * SNAP_GRID.x,
    y: Math.floor((shape["dc:Bounds"]?.["@_y"] ?? 0) / SNAP_GRID.y) * SNAP_GRID.y,
  };
}

export function offsetShapePosition(shape: DMNDI13__DMNShape, offset: { x: number; y: number }): DMNDI13__DMNShape {
  if (!shape["dc:Bounds"]) {
    return shape;
  }

  return {
    ...shape,
    "dc:Bounds": {
      ...shape["dc:Bounds"],
      "@_x": offset.x + shape["dc:Bounds"]["@_x"],
      "@_y": offset.y + shape["dc:Bounds"]["@_y"],
    },
  };
}

export function snapShapeDimensions(shape: DMNDI13__DMNShape) {
  return {
    width: Math.max(
      Math.floor((shape["dc:Bounds"]?.["@_width"] ?? 0) / SNAP_GRID.x) * SNAP_GRID.x,
      MIN_SIZE_FOR_NODES.width
    ),
    height: Math.max(
      Math.floor((shape["dc:Bounds"]?.["@_height"] ?? 0) / SNAP_GRID.y) * SNAP_GRID.y,
      MIN_SIZE_FOR_NODES.height
    ),
  };
}

export function snapBounds(bounds: DC__Bounds | undefined): DC__Bounds {
  return {
    "@_x": Math.floor((bounds?.["@_x"] ?? 0) / SNAP_GRID.x) * SNAP_GRID.x,
    "@_y": Math.floor((bounds?.["@_y"] ?? 0) / SNAP_GRID.y) * SNAP_GRID.y,
    "@_width": Math.max(Math.floor((bounds?.["@_width"] ?? 0) / SNAP_GRID.x) * SNAP_GRID.x, MIN_SIZE_FOR_NODES.width),
    "@_height": Math.max(
      Math.floor((bounds?.["@_height"] ?? 0) / SNAP_GRID.y) * SNAP_GRID.y,
      MIN_SIZE_FOR_NODES.height
    ),
  };
}

export function snapPoint(bounds: DC__Point, method: "floor" | "ceil" = "floor"): DC__Point {
  return {
    "@_x": Math[method]((bounds?.["@_x"] ?? 0) / SNAP_GRID.x) * SNAP_GRID.x,
    "@_y": Math[method]((bounds?.["@_y"] ?? 0) / SNAP_GRID.y) * SNAP_GRID.y,
  };
}
