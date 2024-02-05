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
import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { PencilAltIcon } from "@patternfly/react-icons/dist/js/icons/pencil-alt-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { State } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { NumberInput } from "@patternfly/react-core/dist/js/components/NumberInput";
import { DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "../mutations/addOrGetDrd";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { Select, SelectVariant, SelectOption } from "@patternfly/react-core/dist/js/components/Select";
import { useInViewSelect } from "../responsiveness/useInViewSelect";
import { useDmnEditor } from "../DmnEditorContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { UndoAltIcon } from "@patternfly/react-icons/dist/js/icons/undo-alt-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ColorPicker } from "./ColorPicker";
import "./FontOptions.css";

// https://www.w3schools.com/cssref/css_websafe_fonts.php
// Array of [name, family]
const WEBSAFE_FONTS_LIST = [
  "Arial",
  "Verdana",
  "Tahoma",
  "Trebuchet MS",
  "Times New Roman",
  "Georgia",
  "Garamond",
  "Courier New",
  "Brush Script MT",
];

const DEFAULT_FONT_SIZE = 16;
const MAX_FONT_SIZE = 72;
const MIN_FONT_SIZE = 0;

enum FontStyleToggleOptions {
  BOLD = "bold",
  ITALIC = "italic",
  UNDERLINE = "underline",
  STRIKE_THROUGH = "strike-through",
  FONT_COLOR = "font-color",
}

export function FontOptions({ startExpanded, nodeIds }: { startExpanded: boolean; nodeIds: string[] }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const [isStyleSectionExpanded, setStyleSectionExpanded] = useState<boolean>(startExpanded);

  const dmnShapesByHref = useDmnEditorStore((s) => s.computed(s).indexes().dmnShapesByHref);
  const shapes = useMemo(() => nodeIds.map((nodeId) => dmnShapesByHref.get(nodeId)), [dmnShapesByHref, nodeIds]);
  const shapesStyle = useMemo(() => shapes.map((shape) => shape?.["di:Style"]), [shapes]);

  const fontFamily = useMemo(() => shapesStyle[0]?.["@_fontFamily"], [shapesStyle]);
  const isFontBold = useMemo(() => shapesStyle[0]?.["@_fontBold"] ?? false, [shapesStyle]);
  const isFontItalic = useMemo(() => shapesStyle[0]?.["@_fontItalic"] ?? false, [shapesStyle]);
  const isFontUnderline = useMemo(() => shapesStyle[0]?.["@_fontUnderline"], [shapesStyle]);
  const isFontStrikeThrough = useMemo(() => shapesStyle[0]?.["@_fontStrikeThrough"] ?? false, [shapesStyle]);
  const fontSize = useMemo(() => shapesStyle[0]?.["@_fontSize"] ?? DEFAULT_FONT_SIZE, [shapesStyle]);
  const fontColor = useMemo(() => {
    const b = (shapesStyle[0]?.["dmndi:FontColor"]?.["@_blue"] ?? 0).toString(16);
    const g = (shapesStyle[0]?.["dmndi:FontColor"]?.["@_green"] ?? 0).toString(16);
    const r = (shapesStyle[0]?.["dmndi:FontColor"]?.["@_red"] ?? 0).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapesStyle]);

  const editShapeStyle = useCallback(
    (callback: (shape: DMNDI15__DMNShape[], state: State) => void) => {
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

  const { dmnEditorRootElementRef } = useDmnEditor();
  const toggleRef = React.useRef<HTMLButtonElement>(null);
  const inViewTimezoneSelect = useInViewSelect(dmnEditorRootElementRef, toggleRef);
  const [isFontFamilySelectOpen, setFontFamilySelectOpen] = useState(false);

  const onSelectFont = useCallback(
    (e, value, isPlaceholder) => {
      if (isPlaceholder) {
        editShapeStyle((shapes) => {
          shapes.forEach((shape, i, shapes) => {
            shape["di:Style"]!["@_fontFamily"] ??= undefined;
          });
        });
        return;
      }
      editShapeStyle((shapes) => {
        shapes.forEach((shape, i, shapes) => {
          shape["di:Style"]!["@_fontFamily"] = value;
        });
      });
    },
    [editShapeStyle]
  );

  const validateFontSize = useCallback((value?: number): number => {
    if (value === undefined) {
      return DEFAULT_FONT_SIZE;
    }
    if (value >= MAX_FONT_SIZE) {
      return MAX_FONT_SIZE;
    }
    if (value <= MIN_FONT_SIZE) {
      return MIN_FONT_SIZE;
    }
    return value;
  }, []);

  const onMinus = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontSize"] = validateFontSize(
          (shape!["di:Style"]?.["@_fontSize"] ?? DEFAULT_FONT_SIZE) - 1
        );
      });
    });
  }, [editShapeStyle, validateFontSize]);

  const onChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>) => {
      editShapeStyle((shapes) => {
        shapes.forEach((shape) => {
          shape["di:Style"]!["@_fontSize"] = +(event.target as HTMLInputElement).value;
        });
      });
    },
    [editShapeStyle]
  );

  const onPlus = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontSize"] = validateFontSize(
          (shape!["di:Style"]?.["@_fontSize"] ?? DEFAULT_FONT_SIZE) + 1
        );
      });
    });
  }, [editShapeStyle, validateFontSize]);

  const onChangeBold = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontBold"] = !shape?.["di:Style"]?.["@_fontBold"] ?? true;
      });
    });
  }, [editShapeStyle]);

  const onChangeItalic = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontItalic"] = !shape?.["di:Style"]?.["@_fontItalic"] ?? true;
      });
    });
  }, [editShapeStyle]);

  const onChangeUnderline = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontUnderline"] = !shape?.["di:Style"]?.["@_fontUnderline"] ?? true;
      });
    });
  }, [editShapeStyle]);

  const onChangeStrikeThrough = useCallback(() => {
    editShapeStyle((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontStrikeThrough"] = !shape?.["di:Style"]?.["@_fontStrikeThrough"] ?? true;
      });
    });
  }, [editShapeStyle]);

  const colorPickerRef = React.useRef<HTMLInputElement>(null) as React.MutableRefObject<HTMLInputElement>;

  const [temporaryFontColor, setTemporaryFontColor] = useState<string>("000000");
  const onChangeColor = useCallback(
    (newColor: string) => {
      setTemporaryFontColor(newColor.replace("#", ""));
      editShapeStyle((shapes, state) => {
        state!.diagram.isEditingStyle = true;
      });
    },
    [editShapeStyle]
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      const red = parseInt(temporaryFontColor.slice(0, 2), 16);
      const green = parseInt(temporaryFontColor.slice(2, 4), 16);
      const blue = parseInt(temporaryFontColor.slice(4, 6), 16);
      editShapeStyle((shapes, state) => {
        shapes.forEach((shape) => {
          if (
            red !== shape?.["di:Style"]?.["dmndi:FontColor"]?.["@_red"] &&
            green !== shape?.["di:Style"]?.["dmndi:FontColor"]?.["@_green"] &&
            blue !== shape?.["di:Style"]?.["dmndi:FontColor"]?.["@_blue"]
          ) {
            state!.diagram.isEditingStyle = false;
            shape!["di:Style"]!["dmndi:FontColor"] ??= { "@_blue": 0, "@_green": 0, "@_red": 0 };
            shape!["di:Style"]!["dmndi:FontColor"]["@_red"] = red;
            shape!["di:Style"]!["dmndi:FontColor"]["@_green"] = green;
            shape!["di:Style"]!["dmndi:FontColor"]["@_blue"] = blue;
          }
        });
      });
    }, 0);
    return () => {
      clearTimeout(timeout);
    };
  }, [editShapeStyle, temporaryFontColor]);

  const onReset = useCallback(() => {
    setTemporaryFontColor("000000");
    editShapeStyle((shapes, state) => {
      state!.diagram.isEditingStyle = false;
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontBold"] = undefined;
        shape["di:Style"]!["@_fontItalic"] = undefined;
        shape["di:Style"]!["@_fontUnderline"] = undefined;
        shape["di:Style"]!["@_fontStrikeThrough"] = undefined;
        shape["di:Style"]!["@_fontSize"] = undefined;
        shape["di:Style"]!["@_fontFamily"] = undefined;
      });
    });
  }, [editShapeStyle]);

  return (
    <>
      <PropertiesPanelHeader
        icon={<PencilAltIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
        expands={true}
        fixed={false}
        isSectionExpanded={isStyleSectionExpanded} // TODO LUIZ: isStyleSectionExpanded
        toogleSectionExpanded={() => setStyleSectionExpanded((prev) => !prev)}
        title={"Font"}
        action={
          <Tooltip content={"Reset"}>
            <Button variant={ButtonVariant.plain} onClick={() => onReset()} style={{ paddingBottom: 0, paddingTop: 0 }}>
              <UndoAltIcon />
            </Button>
          </Tooltip>
        }
      />
      {isStyleSectionExpanded && (
        <FormSection style={{ paddingLeft: "20px", marginTop: "0px" }}>
          <div style={{ display: "flex", flexDirection: "column" }}>
            <div className={"kie-dmn-editor--font-options-toggle-group"}>
              <NumberInput
                aria-label={"Font size"}
                className={"kie-dmn-editor--font-options-toggle-group-item-number-input"}
                value={fontSize}
                isDisabled={false}
                widthChars={2}
                onMinus={onMinus}
                onChange={onChange}
                onPlus={onPlus}
                inputName="Font size"
                inputAriaLabel="Font size"
                minusBtnAriaLabel="minus"
                plusBtnAriaLabel="plus"
              />
              <ToggleGroup areAllGroupsDisabled={false} aria-label="Default with multiple selectable">
                <ToggleGroupItem
                  aria-label={"Toggle font bold"}
                  className={"kie-dmn-editor--font-options-toggle-group-item"}
                  text={
                    <div>
                      <b>B</b>
                    </div>
                  }
                  key={FontStyleToggleOptions.BOLD}
                  buttonId={FontStyleToggleOptions.BOLD}
                  isSelected={isFontBold}
                  onChange={onChangeBold}
                />
                <ToggleGroupItem
                  aria-label={"Toggle font italic"}
                  className={"kie-dmn-editor--font-options-toggle-group-item-italic"}
                  text={
                    <div>
                      <i style={{ fontFamily: "serif" }}>I</i>
                    </div>
                  }
                  key={FontStyleToggleOptions.ITALIC}
                  buttonId={FontStyleToggleOptions.ITALIC}
                  isSelected={isFontItalic}
                  onChange={onChangeItalic}
                />
                <ToggleGroupItem
                  key={FontStyleToggleOptions.UNDERLINE}
                  className={"kie-dmn-editor--font-options-toggle-group-item"}
                  text={
                    <div>
                      <u>U</u>
                    </div>
                  }
                  aria-label={"Toggle font underline"}
                  buttonId={FontStyleToggleOptions.UNDERLINE}
                  isSelected={isFontUnderline}
                  onChange={onChangeUnderline}
                />
                <ToggleGroupItem
                  key={FontStyleToggleOptions.STRIKE_THROUGH}
                  className={"kie-dmn-editor--font-options-toggle-group-item"}
                  text={
                    <div>
                      <p style={{ textDecoration: "line-through" }}>S</p>
                    </div>
                  }
                  aria-label={"Toggle font strike through"}
                  buttonId={FontStyleToggleOptions.STRIKE_THROUGH}
                  isSelected={isFontStrikeThrough}
                  onChange={onChangeStrikeThrough}
                />
                <ToggleGroupItem
                  key={FontStyleToggleOptions.FONT_COLOR}
                  className={"kie-dmn-editor--font-options-toggle-group-item-color-picker"}
                  aria-label={"Toggle font strike through"}
                  buttonId={FontStyleToggleOptions.FONT_COLOR}
                  onClick={() => colorPickerRef.current?.click()}
                  text={
                    <ColorPicker
                      icon={<p>A</p>}
                      colorPickerRef={colorPickerRef}
                      color={fontColor}
                      onChange={onChangeColor}
                    />
                  }
                />
              </ToggleGroup>
            </div>
            <br />
            <Select
              toggleRef={toggleRef}
              variant={SelectVariant.single}
              aria-label={"Select font style"}
              isOpen={isFontFamilySelectOpen}
              onSelect={onSelectFont}
              onToggle={() => setFontFamilySelectOpen((prev) => !prev)}
              selections={fontFamily ?? ""}
              isDisabled={false}
              maxHeight={inViewTimezoneSelect.maxHeight}
              direction={inViewTimezoneSelect.direction}
            >
              {WEBSAFE_FONTS_LIST.map((fontName, index) => (
                <SelectOption key={index} value={fontName} style={{ fontFamily: fontName }} />
              ))}
            </Select>
          </div>
        </FormSection>
      )}
    </>
  );
}
