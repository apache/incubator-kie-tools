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
import { useState, useMemo, useEffect, useCallback } from "react";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import {
  EmptyState,
  EmptyStateActions,
  EmptyStateBody,
  EmptyStateIcon,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { BPMN20__tUserTask } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { FormSelect } from "@patternfly/react-core/dist/js/components/FormSelect/FormSelect";
import { FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect/FormSelectOption";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { Form } from "@patternfly/react-core/dist/js/components/Form/Form";
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { EditIcon } from "@patternfly/react-icons/dist/js/icons/edit-icon";
import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form/FormSection";
import { RedoIcon } from "@patternfly/react-icons/dist/js/icons/redo-icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING } from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { ActionGroup } from "@patternfly/react-core/dist/js/components/Form";
import { DataMapping, getDataMapping, setDataMappingForElement } from "../../mutations/_dataMapping";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "../../mutations/addOrGetItemDefinitions";
import "./Reassignments.css";
import { useBpmnEditorI18n } from "../../i18n";

const MIN_PERIOD_AMOUNT = 1;
const DEFAULT_PERIOD_UNIT = "m"; // Minutes

type Reassignment = {
  users: string;
  groups: string;
  type:
    | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN
    | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN;
  period: number;
  periodUnit: string;
};

const typeOptions = [
  {
    value: USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN,
    label: "Not Started",
  },
  {
    value: USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN,
    label: "Not Completed",
  },
];

const periodUnits = [
  { value: "m", label: "minutes" },
  { value: "H", label: "hours" },
  { value: "D", label: "days" },
  { value: "M", label: "months" },
  { value: "Y", label: "years" },
];

// [users:tiago,bento|groups:g1,g2]@[7M]]
// [users:a|groups:ga]@[10H]
// [users:tiago,bento|groups:g1,g2]@[7M]^[users:|groups:]@[0H]]

export function ReassignmentsFormSection({
  element,
}: {
  element: Normalized<BPMN20__tUserTask> & { __$$element: "userTask" };
}) {
  const { i18n, locale } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const [showReassignmentsModal, setShowReassignmentsModal] = useState(false);

  const count = useMemo(
    () =>
      getDataMapping(element)
        .inputDataMapping.filter((dataMapping) => dataMapping.isExpression)
        .filter(
          (d) =>
            d.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN ||
            d.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN
        )
        .filter((d) => d.isExpression)
        .reduce((acc, dataMapping) => acc + dataMapping.value.split("^").length, 0),
    [element]
  );

  const sectionLabel = useMemo(() => {
    if (count > 0) {
      return ` (${count})`;
    } else {
      return "";
    }
  }, [count]);

  return (
    <>
      <FormSection
        style={{ gap: "4px" }}
        title={
          <SectionHeader
            expands={"modal"}
            icon={<RedoIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
            title={i18n.propertiesPanel.reassignments + sectionLabel}
            toogleSectionExpanded={() => setShowReassignmentsModal(true)}
            action={
              <Button
                title={i18n.propertiesPanel.manage}
                variant={ButtonVariant.plain}
                isDisabled={isReadOnly}
                onClick={() => setShowReassignmentsModal(true)}
                style={{ paddingBottom: 0, paddingTop: 0 }}
              >
                {isReadOnly ? <EyeIcon /> : <EditIcon />}
              </Button>
            }
            locale={locale}
          />
        }
      />
      <Modal
        title={i18n.propertiesPanel.reassignments}
        className={"kie-bpmn-editor--reassignments--modal"}
        aria-labelledby={"Reassignments"}
        variant={ModalVariant.large}
        isOpen={showReassignmentsModal}
        onClose={() => setShowReassignmentsModal(false)}
      >
        <div style={{ height: "100%" }}>
          <Reassignments element={element} setShowReassignmentsModal={setShowReassignmentsModal} />
        </div>
      </Modal>
    </>
  );
}

export function Reassignments({
  element,
  setShowReassignmentsModal,
}: {
  element: Normalized<BPMN20__tUserTask> & { __$$element: "userTask" };
  setShowReassignmentsModal: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const [reassignments, setReassignments] = useState<Reassignment[]>([]);
  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);

  const addReassignment = useCallback(() => {
    setReassignments([
      ...reassignments,
      {
        users: "",
        groups: "",
        type: USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN,
        period: 0,
        periodUnit: DEFAULT_PERIOD_UNIT,
      },
    ]);
  }, [reassignments]);

  const removeReassignment = useCallback(
    (index: number) => {
      setReassignments(reassignments.filter((_, i) => i !== index));
    },
    [reassignments]
  );

  const handleInputChange = useCallback((index: number, propertyName: keyof Reassignment, value: string | number) => {
    setReassignments((prevReassignments) => {
      const updatedReassignments = [...prevReassignments];
      updatedReassignments[index] = { ...updatedReassignments[index], [propertyName]: value };
      return updatedReassignments;
    });
  }, []);

  //populates intermediary `reassignments` state from the model
  useEffect(() => {
    if (!element) {
      return;
    }

    const { inputDataMapping } = getDataMapping(element);

    setReassignments(
      inputDataMapping
        .filter((dataMapping) => dataMapping.isExpression)
        .filter(
          (s) =>
            s.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN ||
            s.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN
        )
        .flatMap((dataMapping) => {
          const reassignmentText = dataMapping.value;
          const usersMatches = [...reassignmentText.matchAll(/users:([^|]*)/g)];
          const groupsMatches = [...reassignmentText.matchAll(/groups:([^\]]*)/g)];
          const periodMatches = [...reassignmentText.matchAll(/(\d+)([mHDMY])/g)];

          const users = usersMatches.map((match) => match[1]);
          const groups = groupsMatches.map((match) => match[1]);
          const periods = periodMatches.map((match) => parseInt(match[1]));
          const periodUnits = periodMatches.map((match) => match[2]);

          const reassignments = [];
          for (let i = 0; i < users.length; i++) {
            reassignments.push({
              users: users[i] || "",
              groups: groups[i] || "",
              period: periods[i] || MIN_PERIOD_AMOUNT,
              periodUnit: periodUnits[i] || DEFAULT_PERIOD_UNIT,
              type:
                dataMapping.name ??
                USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN,
            });
          }
          return reassignments;
        })
    );
  }, [element]);

  const handleSubmit = useCallback(
    (event) => {
      event.preventDefault();
      if (!event.target.checkValidity()) {
        event.target.reportValidity();
        return;
      }

      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });

        // Make sure Object data type is available
        addOrGetItemDefinitions({ definitions: s.bpmn.model.definitions, dataType: DEFAULT_DATA_TYPES.OBJECT });

        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
            const { inputDataMapping, outputDataMapping } = getDataMapping(element);

            updateInputDataMapping(
              inputDataMapping,
              reassignments,
              USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN
            );

            updateInputDataMapping(
              inputDataMapping,
              reassignments,
              USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN
            );

            setDataMappingForElement({
              definitions: s.bpmn.model.definitions,
              elementId: e["@_id"],
              element: e.__$$element,
              inputDataMapping,
              outputDataMapping,
            });
          }
        });
      });

      setShowReassignmentsModal(false);
    },
    [bpmnEditorStoreApi, element, reassignments, setShowReassignmentsModal]
  );

  return (
    <>
      <Form onSubmit={handleSubmit} style={{ gridRowGap: 0 }}>
        {(reassignments.length > 0 && (
          <>
            <Grid md={12} style={{ alignItems: "center", padding: "0 8px", columnGap: "12px" }}>
              <GridItem span={3}>
                <div>
                  <b>{i18n.propertiesPanel.users}</b>
                </div>
              </GridItem>
              <GridItem span={3}>
                <div>
                  <b>{i18n.propertiesPanel.groups}</b>
                </div>
              </GridItem>
              <GridItem span={2}>
                <div>
                  <b>{i18n.propertiesPanel.type}</b>
                </div>
              </GridItem>
              <GridItem span={3}>
                <div>
                  <b>{i18n.propertiesPanel.period}</b>
                </div>
              </GridItem>
              <GridItem span={1} style={{ textAlign: "right" }}>
                <Button variant={ButtonVariant.plain} style={{ paddingLeft: 0 }} onClick={addReassignment}>
                  <PlusCircleIcon color="var(--pf-c-button--m-primary--BackgroundColor)" />
                </Button>
              </GridItem>
            </Grid>

            {reassignments.map((entry, i) => (
              <div key={i} style={{ padding: "0 8px" }}>
                <Grid
                  md={12}
                  style={{ alignItems: "center", columnGap: "12px", padding: "8px" }}
                  className={"kie-bpmn-editor--properties-panel--reassignment-entry"}
                  onMouseEnter={() => setHoveredIndex(i)}
                  onMouseLeave={() => setHoveredIndex(undefined)}
                >
                  <GridItem span={3}>
                    <TextInput
                      aria-label={"users"}
                      autoFocus={true}
                      type="text"
                      placeholder={i18n.propertiesPanel.usersPlaceholder}
                      value={entry.users}
                      onChange={(e, value) => handleInputChange(i, "users", value)}
                    />
                  </GridItem>
                  <GridItem span={3}>
                    <TextInput
                      aria-label={"groups"}
                      type="text"
                      placeholder={i18n.propertiesPanel.groupsPlaceholder}
                      value={entry.groups}
                      onChange={(e, value) => handleInputChange(i, "groups", value)}
                    />
                  </GridItem>
                  <GridItem span={2}>
                    <FormSelect
                      aria-label={"type"}
                      type={"text"}
                      value={entry.type}
                      onChange={(e, value) => handleInputChange(i, "type", value)}
                    >
                      {typeOptions.map((option) => (
                        <FormSelectOption key={option.label} label={option.label} value={option.value} />
                      ))}
                    </FormSelect>
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      min={MIN_PERIOD_AMOUNT}
                      aria-label={"period"}
                      type="number"
                      required
                      placeholder={i18n.propertiesPanel.periodPlaceholder}
                      value={entry.period}
                      onChange={(e, value) => handleInputChange(i, "period", value)}
                    />
                  </GridItem>
                  <GridItem span={2}>
                    <FormSelect
                      aria-label={"period unit"}
                      type={"text"}
                      value={entry.periodUnit}
                      onChange={(e, value) => handleInputChange(i, "periodUnit", value)}
                    >
                      {periodUnits.map((option) => (
                        <FormSelectOption key={option.label} label={option.label} value={option.value} />
                      ))}
                    </FormSelect>
                  </GridItem>
                  <GridItem span={1} style={{ textAlign: "right" }}>
                    {hoveredIndex === i && (
                      <Button
                        tabIndex={9999} // Prevent tab from going to this button
                        variant={ButtonVariant.plain}
                        style={{ paddingLeft: 0 }}
                        onClick={() => removeReassignment(i)}
                      >
                        <TimesIcon />
                      </Button>
                    )}
                  </GridItem>
                </Grid>
              </div>
            ))}
          </>
        )) || (
          <div className="kie-bpmn-editor--reassignments--empty-state">
            <Bullseye>
              <EmptyState>
                <EmptyStateIcon icon={CubesIcon} />
                <Title headingLevel="h4">{i18n.propertiesPanel.noReassignMents}</Title>
                <EmptyStateBody>{i18n.propertiesPanel.emptyReassignmentMessage}</EmptyStateBody>
                <br />
                <EmptyStateActions>
                  <Button variant="secondary" onClick={addReassignment}>
                    {i18n.propertiesPanel.addReassignment}
                  </Button>
                </EmptyStateActions>
              </EmptyState>
            </Bullseye>
          </div>
        )}
        {!isReadOnly && (
          <ActionGroup>
            <Button
              variant="primary"
              type="submit"
              className="kie-bpmn-editor--properties-panel--reassignment-submit-save-button"
              onMouseUp={(e) => e.currentTarget.blur()}
            >
              {i18n.propertiesPanel.save}
            </Button>
          </ActionGroup>
        )}
      </Form>
    </>
  );
}

function updateInputDataMapping(
  inputDataMapping: DataMapping[],
  reassignments: Reassignment[],
  type: Reassignment["type"]
) {
  const index = inputDataMapping.findIndex((d) => d.name === type);
  const { dataMapping, nonEmpty } = toDataMapping(reassignments, type);

  if (nonEmpty) {
    if (index > -1) {
      inputDataMapping[index] = dataMapping;
    } else {
      inputDataMapping.push(dataMapping);
    }
  } else if (index > -1) {
    inputDataMapping.splice(index, 1);
  }
}

function toDataMapping(
  reassignments: Reassignment[],
  type: Reassignment["type"]
): { nonEmpty: boolean; dataMapping: DataMapping } {
  const filteredReassignments = reassignments.filter((r) => r.type === type);

  return {
    nonEmpty: filteredReassignments.length > 0,
    dataMapping: {
      dtype: DEFAULT_DATA_TYPES.OBJECT,
      name: type,
      isExpression: true,
      value: filteredReassignments
        .map((r) => `[users:${r.users}|groups:${r.groups}]@[${r.period}${r.periodUnit}]`)
        .join("^"),
    },
  };
}
