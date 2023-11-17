import * as React from "react";
import { useState, useMemo, useCallback } from "react";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { DC__Bounds, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditorStoreApi } from "../store/Store";
import { addOrGetDrd } from "../mutations/addOrGetDrd";
import { ArrowsAltVIcon } from "@patternfly/react-icons/dist/js/icons/arrows-alt-v-icon";
import { ArrowsAltHIcon } from "@patternfly/react-icons/dist/js/icons/arrows-alt-h-icon";
import { NODE_MIN_HEIGHT, NODE_MIN_WIDTH } from "../diagram/nodes/DefaultSizes";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import UndoAltIcon from "@patternfly/react-icons/dist/js/icons/undo-alt-icon";

export function ShapeOptions({
  startExpanded,
  nodeIds,
  isDimensioningEnabled,
  isPositioningEnabled,
}: {
  startExpanded: boolean;
  nodeIds: string[];
  isDimensioningEnabled: boolean;
  isPositioningEnabled: boolean;
}) {
  const [isShapeSectionExpanded, setShapeSectionExpanded] = useState<boolean>(startExpanded);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { dmnShapesByHref } = useDmnEditorDerivedStore();

  const shapes = useMemo(() => nodeIds.map((nodeId) => dmnShapesByHref.get(nodeId)), [dmnShapesByHref, nodeIds]);
  // it only edits the first selected node
  const shapesBound = useMemo(() => shapes[0]?.["dc:Bounds"], [shapes]);
  const shapesStyle = useMemo(() => shapes.map((shape) => shape?.["di:Style"]), [shapes]);

  const boundWidth = useMemo(() => +(shapesBound?.["@_width"]?.toFixed(2) ?? ""), [shapesBound]);
  const boundHeight = useMemo(() => +(shapesBound?.["@_height"]?.toFixed(2) ?? ""), [shapesBound]);
  const boundPositionX = useMemo(() => +(shapesBound?.["@_x"]?.toFixed(2) ?? ""), [shapesBound]);
  const boundPositionY = useMemo(() => +(shapesBound?.["@_y"]?.toFixed(2) ?? ""), [shapesBound]);

  const fillColor = useMemo(() => {
    const b = (shapesStyle[0]?.["dmndi:FillColor"]?.["@_blue"] ?? 0).toString(16);
    const g = (shapesStyle[0]?.["dmndi:FillColor"]?.["@_green"] ?? 0).toString(16);
    const r = (shapesStyle[0]?.["dmndi:FillColor"]?.["@_red"] ?? 0).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapesStyle]);
  const strokeColor = useMemo(() => {
    const b = (shapesStyle[0]?.["dmndi:StrokeColor"]?.["@_blue"] ?? 0).toString(16);
    const g = (shapesStyle[0]?.["dmndi:StrokeColor"]?.["@_green"] ?? 0).toString(16);
    const r = (shapesStyle[0]?.["dmndi:StrokeColor"]?.["@_red"] ?? 0).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapesStyle]);

  const editNodeBound = useCallback(
    (callback: (bound?: DC__Bounds) => void) => {
      dmnEditorStoreApi.setState((state) => {
        const { diagramElements } = addOrGetDrd({
          definitions: state.dmn.model.definitions,
          drdIndex: state.diagram.drdIndex,
        });
        const shape = diagramElements?.[shapes[0]?.index ?? 0] as DMNDI15__DMNShape | undefined;
        callback(shape?.["dc:Bounds"]);
      });
    },
    [dmnEditorStoreApi, shapes]
  );

  const onChangeWidth = useCallback(
    (newWidth: string) => {
      editNodeBound((bound) => {
        bound!["@_width"] = +parseFloat(newWidth).toFixed(2);
      });
    },
    [editNodeBound]
  );

  const onChangeHeight = useCallback(
    (newHeight: string) => {
      editNodeBound((bound) => {
        bound!["@_height"] = +parseFloat(newHeight).toFixed(2);
      });
    },
    [editNodeBound]
  );

  const onChangePositionX = useCallback(
    (newX: string) => {
      editNodeBound((bound) => {
        bound!["@_x"] = +parseFloat(newX).toFixed(2);
      });
    },
    [editNodeBound]
  );

  const onChangePositionY = useCallback(
    (newY: string) => {
      editNodeBound((bound) => {
        bound!["@_y"] = +parseFloat(newY).toFixed(2);
      });
    },
    [editNodeBound]
  );

  const editShapeStyle = useCallback(
    (callback: (shape: DMNDI15__DMNShape[]) => void) => {
      dmnEditorStoreApi.setState((state) => {
        const { diagramElements } = addOrGetDrd({
          definitions: state.dmn.model.definitions,
          drdIndex: state.diagram.drdIndex,
        });
        const _shapes = shapes.map((shape) => diagramElements[shape?.index ?? 0]);
        _shapes.forEach((_shape, i, _shapes) => {
          _shapes[i]["di:Style"] ??= { __$$element: "dmndi:DMNStyle" };
        });
        callback(_shapes);
      });
    },
    [dmnEditorStoreApi, shapes]
  );

  const onChangeStrokeColor = useCallback(
    (newColor: string) => {
      const withoutHash = newColor.replace("#", "");
      editShapeStyle((shapes) => {
        shapes.forEach((shape) => {
          shape!["di:Style"]!["dmndi:StrokeColor"] ??= { "@_blue": 0, "@_green": 0, "@_red": 0 };
          shape!["di:Style"]!["dmndi:StrokeColor"]["@_red"] = parseInt(withoutHash.slice(0, 2), 16);
          shape!["di:Style"]!["dmndi:StrokeColor"]["@_green"] = parseInt(withoutHash.slice(2, 4), 16);
          shape!["di:Style"]!["dmndi:StrokeColor"]["@_blue"] = parseInt(withoutHash.slice(4, 6), 16);
        });
      });
    },
    [editShapeStyle]
  );

  const onChangeFillColor = useCallback(
    (newColor: string) => {
      const withoutHash = newColor.replace("#", "");
      editShapeStyle((shapes) => {
        shapes.forEach((shape) => {
          shape!["di:Style"]!["dmndi:FillColor"] ??= { "@_blue": 0, "@_green": 0, "@_red": 0 };
          shape!["di:Style"]!["dmndi:FillColor"]["@_red"] = parseInt(withoutHash.slice(0, 2), 16);
          shape!["di:Style"]!["dmndi:FillColor"]["@_green"] = parseInt(withoutHash.slice(2, 4), 16);
          shape!["di:Style"]!["dmndi:FillColor"]["@_blue"] = parseInt(withoutHash.slice(4, 6), 16);
        });
      });
    },
    [editShapeStyle]
  );

  const onReset = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape!["di:Style"]!["dmndi:StrokeColor"] ??= { "@_blue": 0, "@_green": 0, "@_red": 0 };
        shape!["di:Style"]!["dmndi:StrokeColor"]["@_red"] = 0;
        shape!["di:Style"]!["dmndi:StrokeColor"]["@_green"] = 0;
        shape!["di:Style"]!["dmndi:StrokeColor"]["@_blue"] = 0;
        shape!["di:Style"]!["dmndi:FillColor"] ??= { "@_blue": 0, "@_green": 0, "@_red": 0 };
        shape!["di:Style"]!["dmndi:FillColor"]["@_red"] = 255;
        shape!["di:Style"]!["dmndi:FillColor"]["@_green"] = 255;
        shape!["di:Style"]!["dmndi:FillColor"]["@_blue"] = 255;
      });
    });
  }, [editShapeStyle]);

  return (
    <>
      <PropertiesPanelHeader
        icon={<CubeIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
        expands={true}
        fixed={false}
        isSectionExpanded={isShapeSectionExpanded}
        toogleSectionExpanded={() => setShapeSectionExpanded((prev) => !prev)}
        title={"Shape"}
      />
      {isShapeSectionExpanded && (
        <FormSection style={{ paddingLeft: "20px", marginTop: "0px" }}>
          <FormGroup label={"Color"}>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "auto auto auto auto",
                gridTemplateRows: "auto",
                gridTemplateAreas: `
                'border-color-label border-color-value fill-color-label fill-color-value color-reset'
            `,
                columnGap: "5px",
                alignItems: "center",
              }}
            >
              <div style={{ gridArea: "border-color-label" }}>
                <p>Border</p>
              </div>
              <div style={{ gridArea: "border-color-value" }}>
                <input
                  aria-label={"Border color"}
                  type={"color"}
                  disabled={false}
                  value={strokeColor}
                  onChange={(e) => onChangeStrokeColor(e.currentTarget.value)}
                  style={{ width: "25px", border: "none" }}
                  placeholder={"Enter a color..."}
                />
              </div>

              <div style={{ gridArea: "fill-color-label" }}>
                <p>Fill</p>
              </div>
              <div style={{ gridArea: "fill-color-value" }}>
                <input
                  aria-label={"Fill color"}
                  type={"color"}
                  disabled={false}
                  value={fillColor}
                  onChange={(e) => onChangeFillColor(e.currentTarget.value)}
                  style={{ width: "25px", border: "none" }}
                  placeholder={"Enter a color..."}
                />
              </div>

              <div style={{ gridArea: "color-reset", justifySelf: "flex-end" }}>
                <Tooltip content={"Reset"}>
                  <Button variant={ButtonVariant.plain} onClick={onReset}>
                    <UndoAltIcon />
                  </Button>
                </Tooltip>
              </div>
            </div>
          </FormGroup>

          {isDimensioningEnabled && (
            <FormGroup label={"Dimension"}>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "auto auto auto auto",
                  gridTemplateRows: "auto",
                  gridTemplateAreas: `
                'width-label width-value heigth-label heigth-value'
            `,
                  columnGap: "5px",
                  alignItems: "center",
                }}
              >
                <div style={{ gridArea: "width-value" }}>
                  <TextInput
                    aria-label={"Width"}
                    type={"number"}
                    isDisabled={false}
                    value={boundWidth}
                    onChange={onChangeWidth}
                    placeholder={"Enter a value..."}
                  />
                </div>
                <div style={{ gridArea: "width-label" }}>
                  <Tooltip content={"Width"}>
                    <ArrowsAltHIcon aria-label={"Width"} />
                  </Tooltip>
                </div>

                <div style={{ gridArea: "heigth-value" }}>
                  <TextInput
                    aria-label={"Height"}
                    type={"number"}
                    isDisabled={false}
                    value={boundHeight}
                    onChange={onChangeHeight}
                    placeholder={"Enter a value..."}
                  />
                </div>
                <div style={{ gridArea: "heigth-label" }}>
                  <Tooltip content={"Height"}>
                    <ArrowsAltVIcon aria-label={"Height"} />
                  </Tooltip>
                </div>
              </div>
            </FormGroup>
          )}
          {isPositioningEnabled && (
            <FormGroup label={"Position"}>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "auto auto auto auto",
                  gridTemplateRows: "auto",
                  gridTemplateAreas: `
                'position-x-label position-x-value position-y-label position-y-value'
            `,
                  columnGap: "5px",
                  alignItems: "center",
                }}
              >
                <div style={{ gridArea: "position-x-value" }}>
                  <TextInput
                    aria-label={"X"}
                    type={"number"}
                    isDisabled={false}
                    value={boundPositionX}
                    onChange={onChangePositionX}
                    placeholder={"Enter a value..."}
                  />
                </div>
                <div style={{ gridArea: "position-x-label" }}>
                  <p>X</p>
                </div>

                <div style={{ gridArea: "position-y-value" }}>
                  <TextInput
                    aria-label={"Y"}
                    type={"number"}
                    isDisabled={false}
                    value={boundPositionY}
                    onChange={onChangePositionY}
                    placeholder={"Enter a value..."}
                  />
                </div>
                <div style={{ gridArea: "position-y-label" }}>
                  <p>Y</p>
                </div>
              </div>
            </FormGroup>
          )}
        </FormSection>
      )}
    </>
  );
}
