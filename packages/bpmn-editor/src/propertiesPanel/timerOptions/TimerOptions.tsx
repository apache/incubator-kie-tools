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
import { useMemo, useState } from "react";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Select, SelectOption } from "@patternfly/react-core/dist/js/components/Select";
import { MenuToggle } from "@patternfly/react-core/dist/js/components/MenuToggle";
import { Normalized } from "../../normalization/normalize";
import {
  BPMN20__tFormalExpression,
  BPMN20__tProcess,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import "./TimerOptions.css";
import { useBpmnEditorI18n } from "../../i18n";

export type WithTimer =
  | undefined
  | Normalized<
      ElementFilter<
        Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
        "startEvent" | "intermediateCatchEvent" | "intermediateThrowEvent" | "endEvent" | "boundaryEvent"
      >
    >;

export function TimerOptions({ element }: { element: WithTimer }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const [selectedOption, setSelectedOption] = useState<string | undefined>(undefined);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  // const [selectedDate, setSelectedDate] = useState<string>("");

  const handleOptionChange = (value: string) => {
    setSelectedOption(value);
    bpmnEditorStoreApi.setState((s) => {
      const { process } = addOrGetProcessAndDiagramElements({
        definitions: s.bpmn.model.definitions,
      });
      visitFlowElementsAndArtifacts(process, ({ element: e }) => {
        if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
          let timerEventDefinition = e.eventDefinition?.find((event) => event.__$$element === "timerEventDefinition");
          if (!timerEventDefinition) {
            timerEventDefinition = {
              "@_id": generateUuid(),
              __$$element: "timerEventDefinition",
            };
            e.eventDefinition = e.eventDefinition || [];
            e.eventDefinition.push(timerEventDefinition);
          }
          timerEventDefinition.timeCycle = undefined;
          timerEventDefinition.timeDate = undefined;
          timerEventDefinition.timeDuration = undefined;
        }
      });
    });
  };

  const handleDropdownToggle = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  // const handleDateChange = (event: React.FormEvent<HTMLInputElement>, value: string, date?: Date) => {
  //   setSelectedDate(value);
  // };
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  // "language" is not part of the BPMN spec, but supported/required by the Kogito engine.
  const timerEventTimerType = useMemo(
    () =>
      (
        element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeCycle as
          | BPMN20__tFormalExpression
          | undefined
      )?.["@_language"] || "iso",
    [element?.eventDefinition]
  );

  return (
    <FormGroup label={i18n.propertiesPanel.timerOptions} fieldId="timer-options">
      <div className="radio-group">
        <Radio
          id="fire-once"
          name="timer-options"
          label={i18n.propertiesPanel.fireOnceAfterDuration}
          isChecked={
            selectedOption === "fire-once" ||
            !!element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")
              ?.timeDuration?.__$$text
          }
          onChange={() => handleOptionChange("fire-once")}
          isDisabled={isReadOnly}
        />
        {(selectedOption === "fire-once" ||
          !!element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeDuration
            ?.__$$text) && (
          <TextInput
            id="fire-once-input"
            value={
              element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")
                ?.timeDuration?.__$$text ?? ""
            }
            onChange={(e, newTimeDuration) =>
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({
                  definitions: s.bpmn.model.definitions,
                });
                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
                    let timerEventDefinition = e.eventDefinition?.find(
                      (event) => event.__$$element === "timerEventDefinition"
                    );
                    timerEventDefinition ??= {
                      "@_id": generateUuid(),
                      __$$element: "timerEventDefinition",
                    };
                    timerEventDefinition.timeDuration ??= {
                      "@_id": generateUuid(),
                      __$$text: newTimeDuration || "",
                    };
                    timerEventDefinition.timeDuration.__$$text = newTimeDuration;
                  }
                });
              })
            }
            isDisabled={isReadOnly}
            type="text"
            placeholder={i18n.propertiesPanel.durationPlaceholder}
            className="timer-input"
          />
        )}
      </div>

      <div className="radio-group">
        <Radio
          id="fire-multiple"
          name="timer-options"
          label={i18n.propertiesPanel.fireMultipleTimes}
          isChecked={
            selectedOption === "fire-multiple" ||
            element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeCycle !==
              undefined
          }
          onChange={() => handleOptionChange("fire-multiple")}
          isDisabled={isReadOnly}
        />
        {(selectedOption === "fire-multiple" ||
          element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeCycle !==
            undefined) && (
          <div className="timer-options-multiple">
            <div className="dropdown-group">
              <Select
                toggle={(toggleRef) => (
                  <MenuToggle
                    style={{ width: "30%" }}
                    ref={toggleRef}
                    onClick={handleDropdownToggle}
                    isExpanded={isDropdownOpen}
                    isDisabled={isReadOnly}
                  >
                    {timerEventTimerType}
                  </MenuToggle>
                )}
                id="iso-cron-select"
                isOpen={isDropdownOpen}
                onSelect={(event, selection) => {
                  const lang = selection === "cron" ? selection : undefined;
                  bpmnEditorStoreApi.setState((s) => {
                    const { process } = addOrGetProcessAndDiagramElements({
                      definitions: s.bpmn.model.definitions,
                    });
                    visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                      if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
                        const timerEventDefinition = e.eventDefinition?.find(
                          (event) => event.__$$element === "timerEventDefinition"
                        );
                        if (timerEventDefinition) {
                          timerEventDefinition.timeCycle ??= {
                            "@_id": generateUuid(),
                            __$$text: "",
                          };
                          // "language" is not part of the BPMN spec, but supported/required by the Kogito engine.
                          (timerEventDefinition.timeCycle as BPMN20__tFormalExpression)["@_language"] = lang;
                        }
                      }
                    });
                  });
                  setIsDropdownOpen(false);
                }}
                selected={timerEventTimerType}
                className="iso-cron-select"
              >
                <SelectOption value="iso">{i18n.propertiesPanel.iso}</SelectOption>
                <SelectOption value="cron">{i18n.propertiesPanel.cron}</SelectOption>
              </Select>
              <TextInput
                id="fire-multiple-input"
                value={
                  element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")
                    ?.timeCycle?.__$$text ?? ""
                }
                onChange={(e, newTimeCycle) =>
                  bpmnEditorStoreApi.setState((s) => {
                    const { process } = addOrGetProcessAndDiagramElements({
                      definitions: s.bpmn.model.definitions,
                    });
                    visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                      if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
                        let timerEventDefinition = e.eventDefinition?.find(
                          (event) => event.__$$element === "timerEventDefinition"
                        );
                        timerEventDefinition ??= {
                          "@_id": generateUuid(),
                          __$$element: "timerEventDefinition",
                        };
                        timerEventDefinition.timeCycle ??= {
                          "@_id": generateUuid(),
                          __$$text: newTimeCycle || "",
                        };
                        timerEventDefinition.timeCycle.__$$text = newTimeCycle;
                      }
                    });
                  })
                }
                isDisabled={isReadOnly}
                type="text"
                placeholder={i18n.propertiesPanel.timePlaceholder}
                className="timer-input"
              />
            </div>
          </div>
        )}
      </div>

      <div className="radio-group">
        <Radio
          id="fire-specific-date"
          name="timer-options"
          label={i18n.propertiesPanel.fireAtspecificDate}
          isChecked={
            selectedOption === "fire-specific-date" ||
            !!element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeDate
              ?.__$$text
          }
          onChange={() => handleOptionChange("fire-specific-date")}
          isDisabled={isReadOnly}
        />
        {(selectedOption === "fire-specific-date" ||
          !!element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeDate
            ?.__$$text) && (
          <div className="timer-options-specific-date">
            <TextInput
              id="specific-date-input"
              value={
                element?.eventDefinition?.find((eventDef) => eventDef.__$$element === "timerEventDefinition")?.timeDate
                  ?.__$$text ?? ""
              }
              onChange={(e, newTimeDate) =>
                bpmnEditorStoreApi.setState((s) => {
                  const { process } = addOrGetProcessAndDiagramElements({
                    definitions: s.bpmn.model.definitions,
                  });
                  visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                    if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
                      let timerEventDefinition = e.eventDefinition?.find(
                        (event) => event.__$$element === "timerEventDefinition"
                      );
                      timerEventDefinition ??= {
                        "@_id": generateUuid(),
                        __$$element: "timerEventDefinition",
                      };
                      timerEventDefinition.timeDate ??= {
                        "@_id": generateUuid(),
                        __$$text: newTimeDate || "",
                      };
                      timerEventDefinition.timeDate.__$$text = newTimeDate;
                    }
                  });
                })
              }
              isDisabled={isReadOnly}
              type="text"
              placeholder={i18n.propertiesPanel.fireAtspecificDatePlaceholder}
              className="timer-input"
            />
            {/* <div className="datepicker-group">
              <DatePicker
                value={selectedDate}
                onChange={handleDateChange}
                inputProps={{
                  isDisabled: false,
                  placeholder: "Select a date",
                  "aria-label": "Date picker input",
                }}
              />
            </div> */}
          </div>
        )}
      </div>
    </FormGroup>
  );
}
