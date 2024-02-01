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

const DEFAULT_FILL_COLOR = { "@_blue": 255, "@_green": 255, "@_red": 255 };
const DEFAULT_STROKE_COLOR = { "@_blue": 0, "@_green": 0, "@_red": 0 };

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
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const shapes = useDmnEditorStore((s) => nodeIds.map((nodeId) => s.computed(s).indexes().dmnShapesByHref.get(nodeId)));
  const shapeStyles = useMemo(() => shapes.map((shape) => shape?.["di:Style"]), [shapes]);

  // For when a single node is selected.
  const shapeBound = useMemo(() => shapes[0]?.["dc:Bounds"], [shapes]);
  const boundWidth = useMemo(() => +(shapeBound?.["@_width"]?.toFixed(2) ?? ""), [shapeBound]);
  const boundHeight = useMemo(() => +(shapeBound?.["@_height"]?.toFixed(2) ?? ""), [shapeBound]);
  const boundPositionX = useMemo(() => +(shapeBound?.["@_x"]?.toFixed(2) ?? ""), [shapeBound]);
  const boundPositionY = useMemo(() => +(shapeBound?.["@_y"]?.toFixed(2) ?? ""), [shapeBound]);

  const fillColor = useMemo(() => {
    const b = (shapeStyles[0]?.["dmndi:FillColor"]?.["@_blue"] ?? DEFAULT_FILL_COLOR["@_red"]).toString(16);
    const g = (shapeStyles[0]?.["dmndi:FillColor"]?.["@_green"] ?? DEFAULT_FILL_COLOR["@_green"]).toString(16);
    const r = (shapeStyles[0]?.["dmndi:FillColor"]?.["@_red"] ?? DEFAULT_FILL_COLOR["@_blue"]).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapeStyles]);

  const strokeColor = useMemo(() => {
    const b = (shapeStyles[0]?.["dmndi:StrokeColor"]?.["@_blue"] ?? DEFAULT_STROKE_COLOR["@_red"]).toString(16);
    const g = (shapeStyles[0]?.["dmndi:StrokeColor"]?.["@_green"] ?? DEFAULT_STROKE_COLOR["@_green"]).toString(16);
    const r = (shapeStyles[0]?.["dmndi:StrokeColor"]?.["@_red"] ?? DEFAULT_STROKE_COLOR["@_blue"]).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapeStyles]);

  const [isShapeSectionExpanded, setShapeSectionExpanded] = useState<boolean>(startExpanded);

  const setBounds = useCallback(
    (callback: (bounds: DC__Bounds, state: State) => void) => {
      dmnEditorStoreApi.setState((s) => {
        const { diagramElements } = addOrGetDrd({ definitions: s.dmn.model.definitions, drdIndex: s.diagram.drdIndex });

        const index = nodeIds.map((nodeId) => s.computed(s).indexes().dmnShapesByHref.get(nodeId))[0]?.index ?? -1;
        if (index < 0) {
          throw new Error(`DMN Shape for '${nodeIds[0]}' does not exist.`);
        }

        const shape = diagramElements?.[index];

        if (shape.__$$element !== "dmndi:DMNShape") {
          throw new Error(`DMN Element with index ${index} is not a DMNShape.`);
        }

        shape["dc:Bounds"] ??= { "@_height": 0, "@_width": 0, "@_x": 0, "@_y": 0 };

        callback(shape["dc:Bounds"], s);
      });
    },
    [dmnEditorStoreApi, nodeIds]
  );

  const onChangeWidth = useCallback(
    (newWidth: string) => {
      setBounds((bounds) => {
        bounds["@_width"] = +parseFloat(newWidth).toFixed(2);
      });
    },
    [setBounds]
  );

  const onChangeHeight = useCallback(
    (newHeight: string) => {
      setBounds((bounds) => {
        bounds["@_height"] = +parseFloat(newHeight).toFixed(2);
      });
    },
    [setBounds]
  );

  const onChangePositionX = useCallback(
    (newX: string) => {
      setBounds((bounds) => {
        bounds["@_x"] = +parseFloat(newX).toFixed(2);
      });
    },
    [setBounds]
  );

  const onChangePositionY = useCallback(
    (newY: string) => {
      setBounds((bounds) => {
        bounds["@_y"] = +parseFloat(newY).toFixed(2);
      });
    },
    [setBounds]
  );

  const setShapeStyles = useCallback(
    (callback: (shape: DMNDI15__DMNShape[], state: State) => void) => {
      dmnEditorStoreApi.setState((s) => {
        const { diagramElements } = addOrGetDrd({ definitions: s.dmn.model.definitions, drdIndex: s.diagram.drdIndex });

        const shapes = nodeIds.map((nodeId) => {
          const shape = s.computed(s).indexes().dmnShapesByHref.get(nodeId);
          if (!shape) {
            throw new Error(`DMN Shape for '${nodeId}' does not exist.`);
          }

          return diagramElements[shape.index];
        });

        let i = 0;
        for (const shape of shapes) {
          if (shape.__$$element !== "dmndi:DMNShape") {
            throw new Error(`DMN Element with index ${i++} is not a DMNShape.`);
          }

          shape["di:Style"] ??= { __$$element: "dmndi:DMNStyle" };
        }

        callback(shapes, s);
      });
    },
    [dmnEditorStoreApi, nodeIds]
  );

  const [temporaryStrokeColor, setTemporaryStrokeColor] = useState<string | undefined>();
  const onChangeStrokeColor = useCallback(
    (newColor: string) => {
      setTemporaryStrokeColor(newColor.replace("#", ""));
      setShapeStyles((shapes, state) => {
        state.diagram.isEditingStyle = true;
      });
    },
    [setShapeStyles]
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (!temporaryStrokeColor) {
        return;
      }

      setTemporaryStrokeColor(undefined);

      setShapeStyles((shapes, state) => {
        shapes.forEach((shape) => {
          state.diagram.isEditingStyle = false;
          shape!["di:Style"]!["dmndi:StrokeColor"] ??= DEFAULT_STROKE_COLOR;
          shape!["di:Style"]!["dmndi:StrokeColor"]["@_red"] = parseInt(temporaryStrokeColor.slice(0, 2), 16);
          shape!["di:Style"]!["dmndi:StrokeColor"]["@_green"] = parseInt(temporaryStrokeColor.slice(2, 4), 16);
          shape!["di:Style"]!["dmndi:StrokeColor"]["@_blue"] = parseInt(temporaryStrokeColor.slice(4, 6), 16);
        });
      });
    }, 0);

    return () => {
      clearTimeout(timeout);
    };
  }, [setShapeStyles, temporaryStrokeColor]);

  const [temporaryFillColor, setTemporaryFillColor] = useState<string | undefined>();
  const onChangeFillColor = useCallback(
    (newColor: string) => {
      setTemporaryFillColor(newColor.replace("#", ""));
      setShapeStyles((shapes, state) => {
        state.diagram.isEditingStyle = true;
      });
    },
    [setShapeStyles]
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (!temporaryFillColor) {
        return;
      }

      setTemporaryFillColor(undefined);

      setShapeStyles((shapes, state) => {
        shapes.forEach((shape) => {
          state.diagram.isEditingStyle = false;
          shape!["di:Style"]!["dmndi:FillColor"] ??= DEFAULT_FILL_COLOR;
          shape!["di:Style"]!["dmndi:FillColor"]["@_red"] = parseInt(temporaryFillColor.slice(0, 2), 16);
          shape!["di:Style"]!["dmndi:FillColor"]["@_green"] = parseInt(temporaryFillColor.slice(2, 4), 16);
          shape!["di:Style"]!["dmndi:FillColor"]["@_blue"] = parseInt(temporaryFillColor.slice(4, 6), 16);
        });
      });
    }, 0);

    return () => {
      clearTimeout(timeout);
    };
  }, [setShapeStyles, temporaryFillColor]);

  const onReset = useCallback(() => {
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape!["di:Style"]!["dmndi:FillColor"] ??= DEFAULT_FILL_COLOR;
        shape!["di:Style"]!["dmndi:FillColor"]["@_red"] = DEFAULT_FILL_COLOR["@_red"];
        shape!["di:Style"]!["dmndi:FillColor"]["@_green"] = DEFAULT_FILL_COLOR["@_green"];
        shape!["di:Style"]!["dmndi:FillColor"]["@_blue"] = DEFAULT_FILL_COLOR["@_blue"];

        shape!["di:Style"]!["dmndi:StrokeColor"] ??= DEFAULT_STROKE_COLOR;
        shape!["di:Style"]!["dmndi:StrokeColor"]["@_red"] = DEFAULT_STROKE_COLOR["@_red"];
        shape!["di:Style"]!["dmndi:StrokeColor"]["@_green"] = DEFAULT_STROKE_COLOR["@_green"];
        shape!["di:Style"]!["dmndi:StrokeColor"]["@_blue"] = DEFAULT_STROKE_COLOR["@_blue"];
      });
    });
  }, [setShapeStyles]);

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
