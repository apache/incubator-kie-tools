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

import { DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { NumberInput } from "@patternfly/react-core/dist/js/components/NumberInput";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { PencilAltIcon } from "@patternfly/react-icons/dist/js/icons/pencil-alt-icon";
import { UndoAltIcon } from "@patternfly/react-icons/dist/js/icons/undo-alt-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useDmnEditor } from "../DmnEditorContext";
import { addOrGetDrd } from "../mutations/addOrGetDrd";
import { useInViewSelect } from "../responsiveness/useInViewSelect";
import { State } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { ColorPicker } from "./ColorPicker";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import "./FontOptions.css";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

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

const DEFAULT_FONT_COLOR = { "@_blue": 0, "@_green": 0, "@_red": 0 };
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
  const settings = useSettings();

  const shapeStyles = useDmnEditorStore((s) =>
    nodeIds.map((nodeId) => s.computed(s).indexedDrd().dmnShapesByHref.get(nodeId)?.["di:Style"])
  );

  const fontFamily = useMemo(() => shapeStyles[0]?.["@_fontFamily"], [shapeStyles]);
  const isFontBold = useMemo(() => shapeStyles[0]?.["@_fontBold"] ?? false, [shapeStyles]);
  const isFontItalic = useMemo(() => shapeStyles[0]?.["@_fontItalic"] ?? false, [shapeStyles]);
  const isFontUnderline = useMemo(() => shapeStyles[0]?.["@_fontUnderline"], [shapeStyles]);
  const isFontStrikeThrough = useMemo(() => shapeStyles[0]?.["@_fontStrikeThrough"] ?? false, [shapeStyles]);
  const fontSize = useMemo(() => shapeStyles[0]?.["@_fontSize"] ?? DEFAULT_FONT_SIZE, [shapeStyles]);
  const fontColor = useMemo(() => {
    const b = (shapeStyles[0]?.["dmndi:FontColor"]?.["@_blue"] ?? DEFAULT_FONT_COLOR["@_blue"]).toString(16);
    const g = (shapeStyles[0]?.["dmndi:FontColor"]?.["@_green"] ?? DEFAULT_FONT_COLOR["@_green"]).toString(16);
    const r = (shapeStyles[0]?.["dmndi:FontColor"]?.["@_red"] ?? DEFAULT_FONT_COLOR["@_red"]).toString(16);
    return `#${r.length === 1 ? "0" + r : r}${g.length === 1 ? "0" + g : g}${b.length === 1 ? "0" + b : b}`;
  }, [shapeStyles]);

  const [isStyleSectionExpanded, setStyleSectionExpanded] = useState<boolean>(startExpanded);

  const setShapeStyles = useCallback(
    (callback: (shape: Normalized<DMNDI15__DMNShape>[], state: State) => void) => {
      dmnEditorStoreApi.setState((s) => {
        const { diagramElements } = addOrGetDrd({
          definitions: s.dmn.model.definitions,
          drdIndex: s.computed(s).getDrdIndex(),
        });

        const shapes = nodeIds.map((nodeId) => {
          const shape = s.computed(s).indexedDrd().dmnShapesByHref.get(nodeId);
          if (!shape) {
            throw new Error(`DMN Shape for '${nodeId}' does not exist.`);
          }

          return diagramElements[shape.index];
        });

        for (const shape of shapes) {
          shape["di:Style"] ??= { "@_id": generateUuid(), __$$element: "dmndi:DMNStyle" };
        }

        callback(shapes, s);
      });
    },
    [dmnEditorStoreApi, nodeIds]
  );

  const { dmnEditorRootElementRef } = useDmnEditor();
  const toggleRef = React.useRef<HTMLButtonElement>(null);
  const inViewTimezoneSelect = useInViewSelect(dmnEditorRootElementRef, toggleRef);
  const [isFontFamilySelectOpen, setFontFamilySelectOpen] = useState(false);

  const onSelectFont = useCallback(
    (e, value, isPlaceholder) => {
      if (isPlaceholder) {
        setShapeStyles((shapes) => {
          shapes.forEach((shape, i, shapes) => {
            shape["di:Style"]!["@_fontFamily"] ??= undefined;
          });
        });
      } else {
        setShapeStyles((shapes) => {
          shapes.forEach((shape, i, shapes) => {
            shape["di:Style"]!["@_fontFamily"] = value;
          });
        });
      }
    },
    [setShapeStyles]
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
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontSize"] = validateFontSize(
          (shape!["di:Style"]?.["@_fontSize"] ?? DEFAULT_FONT_SIZE) - 1
        );
      });
    });
  }, [setShapeStyles, validateFontSize]);

  const onChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>) => {
      setShapeStyles((shapes) => {
        shapes.forEach((shape) => {
          shape["di:Style"]!["@_fontSize"] = +(event.target as HTMLInputElement).value;
        });
      });
    },
    [setShapeStyles]
  );

  const onPlus = useCallback(() => {
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontSize"] = validateFontSize(
          (shape!["di:Style"]?.["@_fontSize"] ?? DEFAULT_FONT_SIZE) + 1
        );
      });
    });
  }, [setShapeStyles, validateFontSize]);

  const onChangeBold = useCallback(() => {
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontBold"] = !shape?.["di:Style"]?.["@_fontBold"] ?? true;
      });
    });
  }, [setShapeStyles]);

  const onChangeItalic = useCallback(() => {
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontItalic"] = !shape?.["di:Style"]?.["@_fontItalic"] ?? true;
      });
    });
  }, [setShapeStyles]);

  const onChangeUnderline = useCallback(() => {
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontUnderline"] = !shape?.["di:Style"]?.["@_fontUnderline"] ?? true;
      });
    });
  }, [setShapeStyles]);

  const onChangeStrikeThrough = useCallback(() => {
    setShapeStyles((shapes) => {
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontStrikeThrough"] = !shape?.["di:Style"]?.["@_fontStrikeThrough"] ?? true;
      });
    });
  }, [setShapeStyles]);

  const colorPickerRef = React.useRef<HTMLInputElement>(null) as React.MutableRefObject<HTMLInputElement>;

  const [temporaryFontColor, setTemporaryFontColor] = useState<string | undefined>();
  const onChangeColor = useCallback(
    (newColor: string) => {
      setTemporaryFontColor(newColor.replace("#", ""));
      setShapeStyles((shapes, state) => {
        state.diagram.isEditingStyle = true;
      });
    },
    [setShapeStyles]
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (!temporaryFontColor) {
        return;
      }

      setTemporaryFontColor(undefined);

      setShapeStyles((shapes, state) => {
        shapes.forEach((shape) => {
          state.diagram.isEditingStyle = false;
          shape["di:Style"]!["dmndi:FontColor"] ??= { ...DEFAULT_FONT_COLOR };
          shape["di:Style"]!["dmndi:FontColor"]["@_red"] = parseInt(temporaryFontColor.slice(0, 2), 16);
          shape["di:Style"]!["dmndi:FontColor"]["@_green"] = parseInt(temporaryFontColor.slice(2, 4), 16);
          shape["di:Style"]!["dmndi:FontColor"]["@_blue"] = parseInt(temporaryFontColor.slice(4, 6), 16);
        });
      });
    }, 0);

    return () => {
      clearTimeout(timeout);
    };
  }, [setShapeStyles, temporaryFontColor]);

  const onReset = useCallback(() => {
    setShapeStyles((shapes, state) => {
      state.diagram.isEditingStyle = false;
      shapes.forEach((shape) => {
        shape["di:Style"]!["@_fontBold"] = undefined;
        shape["di:Style"]!["@_fontItalic"] = undefined;
        shape["di:Style"]!["@_fontUnderline"] = undefined;
        shape["di:Style"]!["@_fontStrikeThrough"] = undefined;
        shape["di:Style"]!["@_fontSize"] = undefined;
        shape["di:Style"]!["@_fontFamily"] = undefined;
        shape["di:Style"]!["dmndi:FontColor"] = { ...DEFAULT_FONT_COLOR };
      });
    });
  }, [setShapeStyles]);

  return (
    <>
      <PropertiesPanelHeader
        icon={
          <Icon isInline size="md" style={{ marginTop: "10px" }}>
            {" "}
            <PencilAltIcon />
          </Icon>
        }
        expands={true}
        fixed={false}
        isSectionExpanded={isStyleSectionExpanded} // TODO LUIZ: isStyleSectionExpanded
        toogleSectionExpanded={() => setStyleSectionExpanded((prev) => !prev)}
        title={"Font"}
        action={
          <Button
            variant={ButtonVariant.plain}
            isDisabled={settings.isReadOnly}
            onClick={() => onReset()}
            style={{ paddingBottom: 0, paddingTop: 0 }}
            title={"Reset font"}
          >
            <UndoAltIcon />
          </Button>
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
                isDisabled={settings.isReadOnly}
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
                  isDisabled={settings.isReadOnly}
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
                  isDisabled={settings.isReadOnly}
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
                  isDisabled={settings.isReadOnly}
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
                  isDisabled={settings.isReadOnly}
                  aria-label={"Toggle font strike through"}
                  buttonId={FontStyleToggleOptions.STRIKE_THROUGH}
                  isSelected={isFontStrikeThrough}
                  onChange={onChangeStrikeThrough}
                />
                <ToggleGroupItem
                  key={FontStyleToggleOptions.FONT_COLOR}
                  className={"kie-dmn-editor--font-options-toggle-group-item-color-picker"}
                  aria-label={"Font color"}
                  buttonId={FontStyleToggleOptions.FONT_COLOR}
                  onClick={() => colorPickerRef.current?.click()}
                  text={
                    <ColorPicker
                      name={"font"}
                      icon={<p>A</p>}
                      colorPickerRef={colorPickerRef}
                      color={fontColor}
                      onChange={onChangeColor}
                      isDisabled={settings.isReadOnly}
                    />
                  }
                  isDisabled={settings.isReadOnly}
                />
              </ToggleGroup>
            </div>
            <br />
            <div data-testid={"kie-tools--dmn-editor--properties-panel-node-font-style"}>
              <Select
                toggleRef={toggleRef}
                variant={SelectVariant.single}
                aria-label={"Select font style"}
                isOpen={isFontFamilySelectOpen}
                onSelect={onSelectFont}
                onToggle={() => setFontFamilySelectOpen((prev) => !prev)}
                selections={fontFamily ?? ""}
                isDisabled={settings.isReadOnly}
                maxHeight={inViewTimezoneSelect.maxHeight}
                direction={inViewTimezoneSelect.direction}
              >
                {WEBSAFE_FONTS_LIST.map((fontName, index) => (
                  <SelectOption key={index} value={fontName} style={{ fontFamily: fontName }} />
                ))}
              </Select>
            </div>
          </div>
        </FormSection>
      )}
    </>
  );
}
