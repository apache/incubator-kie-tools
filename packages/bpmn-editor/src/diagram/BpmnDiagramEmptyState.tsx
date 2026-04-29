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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ProcessAutomationIcon } from "@patternfly/react-icons/dist/js/icons/process-automation-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { MousePointerIcon } from "@patternfly/react-icons/dist/js/icons/mouse-pointer-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useState, useCallback, useMemo } from "react";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { useBpmnEditorI18n } from "../i18n";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { isProcessIdValid, getProcessIdErrorMessage } from "../validation/processIdValidation";
import { initializeProcess } from "../mutations/initializeProcess";

export function BpmnDiagramEmptyState({
  setShowEmptyState,
}: {
  setShowEmptyState: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const currentProcessId = useBpmnEditorStore(
    (s) => s.bpmn.model.definitions.rootElement?.find((e) => e.__$$element === "process")?.["@_id"]
  );
  const hasValidProcessId = useMemo(() => isProcessIdValid(currentProcessId), [currentProcessId]);

  const hasEverHadValidProcessId = useBpmnEditorStore((s) => s.diagram.hasEverHadValidProcessId);

  const [tempProcessId, setTempProcessId] = useState(currentProcessId ?? "");

  const onChangeProcessId = useCallback(() => {
    if (!isProcessIdValid(tempProcessId)) {
      return;
    }

    bpmnEditorStoreApi.setState((state) => {
      initializeProcess({
        definitions: state.bpmn.model.definitions,
        processId: tempProcessId,
      });
      state.diagram.hasEverHadValidProcessId = true;
    });
  }, [tempProcessId, bpmnEditorStoreApi]);

  // If process has valid ID OR had one before, show original simple empty state
  if (hasValidProcessId || hasEverHadValidProcessId) {
    return (
      <Bullseye
        style={{
          position: "absolute",
          width: "100%",
          pointerEvents: "none",
          zIndex: 1,
          height: "auto",
          marginTop: "120px",
        }}
      >
        <div className={"kie-bpmn-editor--diagram-empty-state"}>
          <Button
            title={i18n.propertiesPanel.close}
            style={{
              position: "absolute",
              top: "8px",
              right: 0,
            }}
            variant={ButtonVariant.plain}
            icon={<TimesIcon />}
            onClick={() => setShowEmptyState(false)}
          />
          <EmptyState>
            <EmptyStateIcon icon={MousePointerIcon} />
            <Title size={"md"} headingLevel={"h4"}>
              {i18n.bpmnDiagramEmptyState.emptyBpmnTitle}
            </Title>
            <EmptyStateBody>{i18n.bpmnDiagramEmptyState.startByDraggingNodes}</EmptyStateBody>
          </EmptyState>
        </div>
      </Bullseye>
    );
  }
  // Show process ID input form for new processes without ID
  return (
    <Bullseye
      style={{
        position: "absolute",
        width: "100%",
        pointerEvents: "none",
        zIndex: 1,
        height: "auto",
        marginTop: "120px",
      }}
    >
      <div className={"kie-bpmn-editor--diagram-empty-state"}>
        <EmptyState>
          <EmptyStateIcon icon={ProcessAutomationIcon} />
          <Title size={"lg"} headingLevel={"h2"}>
            {i18n.bpmnDiagramEmptyState.createProcessTitle}
          </Title>
          <EmptyStateBody>
            <TextContent style={{ marginBottom: "24px" }}>
              <Text component={TextVariants.p}>{i18n.bpmnDiagramEmptyState.createProcessDescription}</Text>
            </TextContent>
            <Form style={{ textAlign: "left" }}>
              <FormGroup label={i18n.bpmnDiagramEmptyState.processIdLabel} isRequired fieldId="process-id-input">
                <TextInput
                  id="process-id-input"
                  type="text"
                  value={tempProcessId}
                  onChange={(e, value) => setTempProcessId(value)}
                  validated={
                    tempProcessId.length > 0 ? (isProcessIdValid(tempProcessId) ? "success" : "error") : "default"
                  }
                  placeholder={i18n.bpmnDiagramEmptyState.processIdPlaceholder}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      e.preventDefault();
                      if (isProcessIdValid(tempProcessId)) {
                        onChangeProcessId();
                      }
                    }
                  }}
                  aria-describedby="process-id-helper"
                />
                {tempProcessId.length > 0 && !isProcessIdValid(tempProcessId) && (
                  <FormHelperText>
                    <HelperText>
                      <HelperTextItem variant="error" icon={<InfoCircleIcon />} id="process-id-helper">
                        {getProcessIdErrorMessage(tempProcessId, i18n)}
                      </HelperTextItem>
                    </HelperText>
                  </FormHelperText>
                )}
              </FormGroup>
            </Form>
          </EmptyStateBody>
          <EmptyStateActions style={{ marginTop: "24px" }}>
            <Button
              variant={ButtonVariant.primary}
              onClick={onChangeProcessId}
              isDisabled={!isProcessIdValid(tempProcessId)}
            >
              {i18n.bpmnDiagramEmptyState.startModeling}
            </Button>
          </EmptyStateActions>
        </EmptyState>
      </div>
    </Bullseye>
  );
}
