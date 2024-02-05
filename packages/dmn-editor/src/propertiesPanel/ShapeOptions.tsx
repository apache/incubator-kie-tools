/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useState, useMemo, useCallback, useEffect } from "react";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { DC__Bounds, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { State } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { addOrGetDrd } from "../mutations/addOrGetDrd";
import { ArrowsAltVIcon } from "@patternfly/react-icons/dist/js/icons/arrows-alt-v-icon";
import { ArrowsAltHIcon } from "@patternfly/react-icons/dist/js/icons/arrows-alt-h-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import UndoAltIcon from "@patternfly/react-icons/dist/js/icons/undo-alt-icon";
import { ColorPicker } from "./ColorPicker";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import "./ShapeOptions.css";

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
  const dmnShapesByHref = useDmnEditorStore((s) => s.computed(s).indexes().dmnShapesByHref);

  const shapes = useMemo(() => nodeIds.map((nodeId) => dmnShapesByHref.get(nodeId)), [dmnShapesByHref, nodeIds]);
  // it only edits the first selected node
  const shapesBound = useMemo(() => shapes[0]?.["dc:Bounds"], [shapes]);
  const shapesStyle = useMemo(() => shapes.map((shape) => shape?.["di:Style"]), [shapes]);

  const boundWidth = useMemo(() => +(shapesBound?.["@_width"]?.toFixed(2) ?? ""), [shapesBound]);
  const boundHeight = useMemo(() => +(shapesBound?.["@_height"]?.toFixed(2) ?? ""), [shapesBound]);
  const boundPositionX = useMemo(() => +(shapesBound?.["@_x"]?.toFixed(2) ?? ""), [shapesBound]);
  const boundPositionY = useMemo(() => +(shapesBound?.["@_y"]?.toFixed(2) ?? ""), [shapesBound]);

  const fillColor = useMemo(() => {
    const b = (shapesStyle[0]?.["dmndi:FillColor"]?.["@_blue"] ?? 255).toString(16);
    const g = (shapesStyle[0]?.["dmndi:FillColor"]?.["@_green"] ?? 255).toString(16);
    const r = (shapesStyle[0]?.["dmndi:FillColor"]?.["@_red"] ?? 255).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapesStyle]);
  const strokeColor = useMemo(() => {
    const b = (shapesStyle[0]?.["dmndi:StrokeColor"]?.["@_blue"] ?? 0).toString(16);
    const g = (shapesStyle[0]?.["dmndi:StrokeColor"]?.["@_green"] ?? 0).toString(16);
    const r = (shapesStyle[0]?.["dmndi:StrokeColor"]?.["@_red"] ?? 0).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapesStyle]);

  const editNodeBound = useCallback(
    (callback: (bound?: DC__Bounds, state?: State) => void) => {
      dmnEditorStoreApi.setState((state) => {
        const { diagramElements } = addOrGetDrd({
          definitions: state.dmn.model.definitions,
          drdIndex: state.diagram.drdIndex,
        });
        const shape = diagramElements?.[shapes[0]?.index ?? 0] as DMNDI15__DMNShape | undefined;
        callback(shape?.["dc:Bounds"], state);
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
    (callback: (shape: DMNDI15__DMNShape[], state?: State) => void) => {
      dmnEditorStoreApi.setState((state) => {
        const { diagramElements } = addOrGetDrd({
          definitions: state.dmn.model.definitions,
          drdIndex: state.diagram.drdIndex,
        });
        const _shapes = shapes.map((shape) => diagramElements[shape?.index ?? 0]);
        _shapes.forEach((_shape, i, _shapes) => {
          _shapes[i]["di:Style"] ??= { __$$element: "dmndi:DMNStyle" };
        });
        callback(_shapes, state);
      });
    },
    [dmnEditorStoreApi, shapes]
  );

  const [temporaryStrokeColor, setTemporaryStrokeColor] = useState<string>("000000");
  const onChangeStrokeColor = useCallback(
    (newColor: string) => {
      setTemporaryStrokeColor(newColor.replace("#", ""));
      editShapeStyle((shapes, state) => {
        state!.diagram.isEditingStyle = true;
      });
    },
    [editShapeStyle]
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      const red = parseInt(temporaryStrokeColor.slice(0, 2), 16);
      const green = parseInt(temporaryStrokeColor.slice(2, 4), 16);
      const blue = parseInt(temporaryStrokeColor.slice(4, 6), 16);
      editShapeStyle((shapes, state) => {
        shapes.forEach((shape) => {
          if (
            red !== shape?.["di:Style"]?.["dmndi:StrokeColor"]?.["@_red"] &&
            green !== shape?.["di:Style"]?.["dmndi:StrokeColor"]?.["@_green"] &&
            blue !== shape?.["di:Style"]?.["dmndi:StrokeColor"]?.["@_blue"]
          ) {
            state!.diagram.isEditingStyle = false;
            shape!["di:Style"]!["dmndi:StrokeColor"] ??= { "@_blue": 0, "@_green": 0, "@_red": 0 };
            shape!["di:Style"]!["dmndi:StrokeColor"]["@_red"] = red;
            shape!["di:Style"]!["dmndi:StrokeColor"]["@_green"] = green;
            shape!["di:Style"]!["dmndi:StrokeColor"]["@_blue"] = blue;
          }
        });
      });
    }, 0);
    return () => {
      clearTimeout(timeout);
    };
  }, [editShapeStyle, temporaryStrokeColor]);

  const [temporaryFillColor, setTemporaryFillColor] = useState<string>("ffffff");
  const onChangeFillColor = useCallback(
    (newColor: string) => {
      setTemporaryFillColor(newColor.replace("#", ""));
      editShapeStyle((shapes, state) => {
        state!.diagram.isEditingStyle = true;
      });
    },
    [editShapeStyle]
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      const red = parseInt(temporaryFillColor.slice(0, 2), 16);
      const green = parseInt(temporaryFillColor.slice(2, 4), 16);
      const blue = parseInt(temporaryFillColor.slice(4, 6), 16);
      editShapeStyle((shapes, state) => {
        shapes.forEach((shape) => {
          if (
            red !== shape?.["di:Style"]?.["dmndi:FillColor"]?.["@_red"] &&
            green !== shape?.["di:Style"]?.["dmndi:FillColor"]?.["@_green"] &&
            blue !== shape?.["di:Style"]?.["dmndi:FillColor"]?.["@_blue"]
          ) {
            state!.diagram.isEditingStyle = false;
            shape!["di:Style"]!["dmndi:FillColor"] ??= { "@_blue": 255, "@_green": 255, "@_red": 255 };
            shape!["di:Style"]!["dmndi:FillColor"]["@_red"] = red;
            shape!["di:Style"]!["dmndi:FillColor"]["@_green"] = green;
            shape!["di:Style"]!["dmndi:FillColor"]["@_blue"] = blue;
          }
        });
      });
    }, 0);
    return () => {
      clearTimeout(timeout);
    };
  }, [editShapeStyle, temporaryFillColor]);

  const onReset = useCallback(() => {
    setTemporaryStrokeColor("000000");
    setTemporaryFillColor("ffffff");
  }, []);

  const strokeColorPickerRef = React.useRef<HTMLInputElement>(null) as React.MutableRefObject<HTMLInputElement>;
  const fillColorPickerRef = React.useRef<HTMLInputElement>(null) as React.MutableRefObject<HTMLInputElement>;

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
          <FormGroup label={"Style"}>
            <ToggleGroup>
              <Tooltip content={"Fill color"}>
                <ToggleGroupItem
                  className={"kie-dmn-editor--shape-options-toggle-button"}
                  text={
                    <ColorPicker
                      icon={
                        <div
                          style={{
                            backgroundColor: fillColor,
                            width: "20px",
                            height: "20px",
                            border: "dashed 1px black",
                            marginBottom: "-6px",
                          }}
                        />
                      }
                      color={fillColor}
                      onChange={(newColor) => onChangeFillColor(newColor)}
                      colorPickerRef={fillColorPickerRef}
                    />
                  }
                  key={"fill-color"}
                  buttonId={"shape-style-toggle-group-fill-color"}
                  onClick={() => {
                    fillColorPickerRef.current?.click();
                  }}
                />
              </Tooltip>

              <Tooltip content="Stroke color">
                <ToggleGroupItem
                  className={"kie-dmn-editor--shape-options-toggle-button"}
                  text={
                    <ColorPicker
                      colorDisplay={
                        <div
                          style={{
                            backgroundColor: "transparent",
                            width: "20px",
                            height: "20px",
                            border: "solid 4px",
                            borderColor: strokeColor,
                            marginBottom: "-6px",
                          }}
                        />
                      }
                      color={strokeColor}
                      onChange={(newColor) => onChangeStrokeColor(newColor)}
                      colorPickerRef={strokeColorPickerRef}
                    />
                  }
                  key={"stroke-color"}
                  buttonId={"shape-style-toggle-group-stroke-color"}
                  onClick={() => {
                    strokeColorPickerRef.current?.click();
                  }}
                />
              </Tooltip>

              <Tooltip content={"Width"}>
                <ToggleGroupItem
                  text={
                    <div
                      style={{
                        display: "flex",
                        flexDirection: "row",
                        alignItems: "center",
                        justifyContent: "center",
                        columnGap: "5px",
                      }}
                    >
                      <TextInput
                        aria-label={"Width"}
                        type={"number"}
                        isDisabled={isDimensioningEnabled ? false : true}
                        value={isDimensioningEnabled ? boundWidth : undefined}
                        placeholder={isDimensioningEnabled ? "Enter a value..." : undefined}
                        onChange={onChangeWidth}
                        style={{ maxWidth: "80px", minWidth: "60px", border: "none", backgroundColor: "transparent" }}
                      />
                      <div>
                        <ArrowsAltHIcon aria-label={"Width"} />
                      </div>
                    </div>
                  }
                  key={"bound-width"}
                  buttonId={"shape-style-toggle-group-bound-width"}
                />
              </Tooltip>

              <Tooltip content={"Height"}>
                <ToggleGroupItem
                  text={
                    <div
                      style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}
                    >
                      <TextInput
                        aria-label={"Height"}
                        type={"number"}
                        isDisabled={isDimensioningEnabled ? false : true}
                        value={isDimensioningEnabled ? boundHeight : undefined}
                        placeholder={isDimensioningEnabled ? "Enter a value..." : undefined}
                        onChange={onChangeHeight}
                        style={{ maxWidth: "80px", minWidth: "60px", border: "none", backgroundColor: "transparent" }}
                      />
                      <div>
                        <ArrowsAltVIcon aria-label={"Height"} />
                      </div>
                    </div>
                  }
                  key={"bound-height"}
                  buttonId={"shape-style-toggle-group-bound-height"}
                />
              </Tooltip>
              <Tooltip content={"Reset"}>
                <ToggleGroupItem
                  onClick={onReset}
                  className={"kie-dmn-editor--shape-options-toggle-button"}
                  text={<UndoAltIcon />}
                  key={"reset"}
                  buttonId={"shape-style-toggle-group-reset"}
                />
              </Tooltip>
            </ToggleGroup>
          </FormGroup>
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
