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
import { useState, useEffect, useCallback, useMemo } from "react";
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
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea/TextArea";
import { FormSelect } from "@patternfly/react-core/dist/js/components/FormSelect/FormSelect";
import { FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect/FormSelectOption";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { Form } from "@patternfly/react-core/dist/js/components/Form/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput/TextInput";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form/FormSection";
import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import { BellIcon } from "@patternfly/react-icons/dist/js/icons/bell-icon";
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { EditIcon } from "@patternfly/react-icons/dist/js/icons/edit-icon";
import { USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING } from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { DataMapping, getDataMapping, setDataMappingForElement } from "../../mutations/_dataMapping";
import { ActionGroup } from "@patternfly/react-core/dist/js/components/Form";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "../../mutations/addOrGetItemDefinitions";
import "./Notifications.css";
import { useBpmnEditorI18n } from "../../i18n";

type Notification = {
  from: string;
  tousers: string;
  togroups: string;
  toemails: string;
  replyTo: string;
  subject: string;
  body: string;
  expiresAt: string;
  type:
    | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY
    | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY;
};

const typeOptions = [
  {
    value: USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY,
    label: "Not Started",
  },
  {
    value: USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY,
    label: "Not Completed",
  },
];

export function NotificationsFormSection({
  element,
}: {
  element: Normalized<BPMN20__tUserTask> & { __$$element: "userTask" };
}) {
  const { i18n, locale } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const [showNotificationsModal, setShowNotificationsModal] = useState(false);

  const count = useMemo(
    () =>
      getDataMapping(element)
        .inputDataMapping.filter((dataMapping) => dataMapping.isExpression)
        .filter(
          (d) =>
            d.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY ||
            d.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY
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
            icon={<BellIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
            title={i18n.propertiesPanel.notificationsProperties.notifications + sectionLabel}
            toogleSectionExpanded={() => setShowNotificationsModal(true)}
            action={
              <Button
                title={i18n.propertiesPanel.manage}
                variant={ButtonVariant.plain}
                isDisabled={isReadOnly}
                onClick={() => setShowNotificationsModal(true)}
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
        title={i18n.propertiesPanel.notificationsProperties.notifications}
        className={"kie-bpmn-editor--notifications--modal"}
        aria-labelledby={"Notifications"}
        variant={ModalVariant.large}
        isOpen={showNotificationsModal}
        onClose={() => setShowNotificationsModal(false)}
      >
        <div style={{ height: "100%" }}>
          <Notifications element={element} setShowNotificationsModal={setShowNotificationsModal} />
        </div>
      </Modal>
    </>
  );
}

export function Notifications({
  element,
  setShowNotificationsModal,
}: {
  element: Normalized<BPMN20__tUserTask> & { __$$element: "userTask" };
  setShowNotificationsModal: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);

  const addNotification = useCallback(() => {
    setNotifications([
      ...notifications,
      {
        from: "",
        tousers: "",
        togroups: "",
        toemails: "",
        replyTo: "",
        subject: "",
        body: "",
        expiresAt: "",
        type: USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY,
      },
    ]);
  }, [notifications]);

  const removeNotification = useCallback(
    (index: number) => {
      setNotifications(notifications.filter((_, i) => i !== index));
    },
    [notifications]
  );

  const handleInputChange = useCallback((index: number, propertyName: keyof Notification, value: string | number) => {
    setNotifications((prevNotifications) => {
      const updatedNotifications = [...prevNotifications];
      updatedNotifications[index] = { ...updatedNotifications[index], [propertyName]: value };
      return updatedNotifications;
    });
  }, []);

  //populates intermediary `notifications` state from the model
  useEffect(() => {
    if (!element) {
      return;
    }

    const { inputDataMapping } = getDataMapping(element);

    setNotifications(
      inputDataMapping
        .filter((dataMapping) => dataMapping.isExpression)
        .filter(
          (s) =>
            s.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY ||
            s.name === USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY
        )
        .flatMap((dataMapping) => {
          const notificationText = dataMapping.value;
          const fromMatches = [...notificationText.matchAll(/from:([^|]*)/g)];
          const bodyMatches = [...notificationText.matchAll(/body:([^@\]]*)/g)];
          const subjectMatches = [...notificationText.matchAll(/subject:([^|]*)/g)];
          const toEmailsMatches = [...notificationText.matchAll(/toemails:([^|]*)/g)];
          const replyToMatches = [...notificationText.matchAll(/replyTo:([^|]*)/g)];
          const usersMatches = [...notificationText.matchAll(/tousers:([^|]*)/g)];
          const groupsMatches = [...notificationText.matchAll(/togroups:([^|]*)/g)];
          const expiresAtMatches = [...notificationText.matchAll(/\]@\[([^\]]*)/g)];

          const from = fromMatches.map((match) => match[1]);
          const tousers = usersMatches.map((match) => match[1]);
          const togroups = groupsMatches.map((match) => match[1]);
          const toemails = toEmailsMatches.map((match) => match[1]);
          const replyTo = replyToMatches.map((match) => match[1]);
          const subject = subjectMatches.map((match) => match[1]);
          const body = bodyMatches.map((match) => match[1]);
          const expiresAt = expiresAtMatches.map((match) => match[1]);

          const notifications = [];
          for (let i = 0; i < expiresAt.length; i++) {
            notifications.push({
              from: from[i] || "",
              tousers: tousers[i] || "",
              togroups: togroups[i] || "",
              toemails: toemails[i] || "",
              replyTo: replyTo[i] || "",
              subject: subject[i] || "",
              body: body[i] || "",
              expiresAt: expiresAt[i] || "",
              type:
                dataMapping.name ?? USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY,
            });
          }
          return notifications;
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
            const { inputDataMapping, outputDataMapping } = getDataMapping(e);

            updateInputDataMapping(
              inputDataMapping,
              notifications,
              USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY
            );
            updateInputDataMapping(
              inputDataMapping,
              notifications,
              USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY
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

      setShowNotificationsModal(false);
    },

    [bpmnEditorStoreApi, element, notifications, setShowNotificationsModal]
  );

  return (
    <>
      <Form onSubmit={handleSubmit} style={{ gridRowGap: 0 }}>
        {(notifications.length > 0 && (
          <>
            <Grid md={12} style={{ alignItems: "center", padding: "0 8px", columnGap: "12px" }}>
              <GridItem span={2}>
                <div>
                  <b>{i18n.propertiesPanel.type}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.expiresAt}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.from}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.toUser}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.toGroup}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.toEmail}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.replyTo}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.subject}</b>
                </div>
              </GridItem>
              <GridItem span={2}>
                <div>
                  <b>{i18n.propertiesPanel.notificationsProperties.body}</b>
                </div>
              </GridItem>
              <GridItem span={1} style={{ textAlign: "right" }}>
                <Button variant={ButtonVariant.plain} style={{ paddingLeft: 0 }} onClick={addNotification}>
                  <PlusCircleIcon color="var(--pf-c-button--m-primary--BackgroundColor)" />
                </Button>
              </GridItem>
            </Grid>
            {notifications.map((entry, i) => (
              <div key={i} style={{ padding: "0 8px" }}>
                <Grid
                  md={12}
                  style={{ alignItems: "center", columnGap: "12px", padding: "8px" }}
                  className={"kie-bpmn-editor--properties-panel--notification-entry"}
                  onMouseEnter={() => setHoveredIndex(i)}
                  onMouseLeave={() => setHoveredIndex(undefined)}
                >
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
                      aria-label={"expires at"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.expiresPlaceholder}
                      isRequired={true}
                      value={entry.expiresAt}
                      onChange={(e, value) => handleInputChange(i, "expiresAt", value)}
                    />
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      aria-label={"from"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.fromPlaceholder}
                      value={entry.from}
                      onChange={(e, value) => handleInputChange(i, "from", value)}
                    />
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      aria-label={"to users"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.toUserPlaceholder}
                      value={entry.tousers}
                      onChange={(e, value) => handleInputChange(i, "tousers", value)}
                    />
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      aria-label={"to groups"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.toGroupPlaceholder}
                      value={entry.togroups}
                      onChange={(e, value) => handleInputChange(i, "togroups", value)}
                    />
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      aria-label={"to emails"}
                      type="email"
                      placeholder={i18n.propertiesPanel.notificationsProperties.toEmailPlaceholder}
                      value={entry.toemails}
                      onChange={(e, value) => handleInputChange(i, "toemails", value)}
                    />
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      aria-label={"reply to"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.replyPlaceholder}
                      value={entry.replyTo}
                      onChange={(e, value) => handleInputChange(i, "replyTo", value)}
                    />
                  </GridItem>
                  <GridItem span={1}>
                    <TextInput
                      aria-label={"subject"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.subjectPlaceholder}
                      value={entry.subject}
                      onChange={(e, value) => handleInputChange(i, "subject", value)}
                    />
                  </GridItem>
                  <GridItem span={2}>
                    <TextArea
                      aria-label={"body"}
                      type="text"
                      placeholder={i18n.propertiesPanel.notificationsProperties.bodyPlaceholder}
                      resizeOrientation={"vertical"}
                      value={entry.body}
                      onChange={(e, value) => handleInputChange(i, "body", value)}
                    />
                  </GridItem>
                  <GridItem span={1} style={{ textAlign: "right" }}>
                    {hoveredIndex === i && (
                      <Button
                        tabIndex={9999} // Prevent tab from going to this button
                        variant={ButtonVariant.plain}
                        style={{ paddingLeft: 0 }}
                        onClick={() => removeNotification(i)}
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
          <div className="kie-bpmn-editor--notifications--empty-state">
            <Bullseye>
              <EmptyState>
                <EmptyStateIcon icon={CubesIcon} />
                <Title headingLevel="h4">{i18n.propertiesPanel.notificationsProperties.noNotifications}</Title>
                <EmptyStateBody>{i18n.propertiesPanel.notificationsProperties.emptyNotificationMessage}</EmptyStateBody>
                <br />
                <EmptyStateActions>
                  <Button variant="secondary" onClick={addNotification}>
                    {i18n.propertiesPanel.notificationsProperties.addNotifications}
                  </Button>
                </EmptyStateActions>
              </EmptyState>
            </Bullseye>
          </div>
        )}
        {!isReadOnly && (
          <ActionGroup>
            <Button
              type="submit"
              className="kie-bpmn-editor--properties-panel--notification-submit-save-button"
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
  notifications: Notification[],
  type: Notification["type"]
) {
  const index = inputDataMapping.findIndex((d) => d.name === type);
  const { dataMapping, nonEmpty } = toDataMapping(notifications, type);

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
  notifications: Notification[],
  type: Notification["type"]
): { nonEmpty: boolean; dataMapping: DataMapping } {
  const filteredNotifications = notifications.filter((n) => n.type === type);

  return {
    nonEmpty: filteredNotifications.length > 0,
    dataMapping: {
      dtype: DEFAULT_DATA_TYPES.OBJECT,
      name: type,
      isExpression: true,
      value: filteredNotifications
        .map(
          (n) =>
            `[from:${n.from}|tousers:${n.tousers}|togroups:${n.togroups}|toemails:${n.toemails}|replyTo:${n.replyTo}|subject:${n.subject}|body:${n.body}]@[${n.expiresAt}]`
        )
        .join("^"),
    },
  };
}
