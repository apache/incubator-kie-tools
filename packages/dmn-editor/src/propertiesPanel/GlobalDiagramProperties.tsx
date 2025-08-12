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
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { Form, FormSection, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DataSourceIcon } from "@patternfly/react-icons/dist/js/icons/data-source-icon";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useDmnEditorI18n } from "../i18n";

export function GlobalDiagramProperties() {
  const { i18n } = useDmnEditorI18n();
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const [isGlobalSectionExpanded, setGlobalSectionExpanded] = useState<boolean>(true);
  const [isIdNamespaceSectionExpanded, setIdNamespaceSectionExpanded] = useState<boolean>(true);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const settings = useSettings();

  const [regenerateIdConfirmationModal, setRegenerateIdConfirmationModal] = useState(false);

  return (
    <Form>
      <FormSection
        title={
          <PropertiesPanelHeader
            expands={true}
            fixed={true}
            isSectionExpanded={isGlobalSectionExpanded}
            toogleSectionExpanded={() => setGlobalSectionExpanded((prev) => !prev)}
            icon={<DataSourceIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
            title={i18n.propertiesPanel.globalProperties}
            action={
              <Button
                title={i18n.close}
                variant={ButtonVariant.plain}
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.diagram.propertiesPanel.isOpen = false;
                  });
                }}
              >
                <TimesIcon />
              </Button>
            }
          />
        }
      >
        {isGlobalSectionExpanded && (
          <>
            <FormSection style={{ paddingLeft: "20px" }}>
              <FormGroup label={i18n.name}>
                <InlineFeelNameInput
                  enableAutoFocusing={false}
                  isPlain={false}
                  id={thisDmn.model.definitions["@_id"]!}
                  name={thisDmn.model.definitions["@_name"]}
                  isReadOnly={settings.isReadOnly}
                  shouldCommitOnBlur={true}
                  className={"pf-v5-c-form-control"}
                  onRenamed={(newName) => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_name"] = newName;
                    });
                  }}
                  allUniqueNames={() => new Map()} // Right now, there's no way to know what are the unique names of all DMNs in the scope. So we let any name go.
                />
              </FormGroup>
              <FormGroup label={i18n.propertiesPanel.description}>
                <TextArea
                  aria-label={"Description"}
                  type={"text"}
                  isDisabled={settings.isReadOnly}
                  style={{ resize: "vertical", minHeight: "40px" }}
                  rows={6}
                  placeholder={i18n.propertiesPanel.descriptionPlaceholder}
                  value={thisDmn.model.definitions.description?.__$$text}
                  onChange={(_event, newDescription) =>
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions.description = { __$$text: newDescription };
                    })
                  }
                />
              </FormGroup>

              <FormGroup label={i18n.propertiesPanel.expressionLanguage}>
                <TextInput
                  aria-label={"Expression language"}
                  type={"text"}
                  isDisabled={settings.isReadOnly}
                  placeholder={i18n.propertiesPanel.expressionLanguagePlaceholder}
                  value={thisDmn.model.definitions["@_expressionLanguage"]}
                  onChange={(_event, newExprLang) =>
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_expressionLanguage"] = newExprLang;
                    })
                  }
                />
              </FormGroup>
            </FormSection>
          </>
        )}
      </FormSection>

      <FormSection
        style={{ paddingTop: "20px" }}
        title={
          <PropertiesPanelHeader
            expands={true}
            isSectionExpanded={isIdNamespaceSectionExpanded}
            toogleSectionExpanded={() => setIdNamespaceSectionExpanded((prev) => !prev)}
            title={i18n.propertiesPanel.idAndNamespace}
            action={
              <Button
                title={i18n.propertiesPanel.regenerateIdNamespace}
                variant={ButtonVariant.plain}
                isDisabled={settings.isReadOnly}
                onClick={() => setRegenerateIdConfirmationModal(true)}
                style={{ paddingBottom: 0, paddingTop: 0 }}
              >
                <SyncAltIcon />
              </Button>
            }
          />
        }
      >
        {isIdNamespaceSectionExpanded && (
          <>
            <FormSection style={{ paddingLeft: "20px", marginTop: 0 }}>
              <FormGroup label={i18n.propertiesPanel.id}>
                <ClipboardCopy
                  placeholder={i18n.propertiesPanel.diagramIdPlaceholder}
                  isReadOnly={settings.isReadOnly}
                  hoverTip={i18n.propertiesPanel.copy}
                  clickTip={i18n.propertiesPanel.copied}
                  onChange={(_event, newId) => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_id"] = `${newId}`;
                    });
                  }}
                >
                  {thisDmn.model.definitions["@_id"]}
                </ClipboardCopy>
              </FormGroup>

              <FormGroup label={i18n.propertiesPanel.namespace}>
                <ClipboardCopy
                  placeholder={i18n.propertiesPanel.namespacePlaceholder}
                  isReadOnly={settings.isReadOnly}
                  hoverTip={i18n.propertiesPanel.copy}
                  clickTip={i18n.propertiesPanel.copied}
                  onChange={(_event, newNamespace) => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_namespace"] = `${newNamespace}`;
                    });
                  }}
                >
                  {thisDmn.model.definitions["@_namespace"]}
                </ClipboardCopy>
              </FormGroup>
            </FormSection>
          </>
        )}
      </FormSection>
      <Modal
        aria-labelledby={"Regenerate ID & Namespace"}
        variant={ModalVariant.small}
        isOpen={regenerateIdConfirmationModal}
        onClose={() => setRegenerateIdConfirmationModal(false)}
        actions={[
          <Button
            key="confirm"
            variant={ButtonVariant.primary}
            isDisabled={settings.isReadOnly}
            onClick={() => {
              setRegenerateIdConfirmationModal(false);
              dmnEditorStoreApi.setState((state) => {
                state.dmn.model.definitions["@_id"] = generateUuid();
                state.dmn.model.definitions["@_namespace"] = `https://kie.apache.org/dmn/${generateUuid()}`;
              });
            }}
          >
            {i18n.propertiesPanel.yesRegenerateIdNamespace}
          </Button>,
          <Button key="cancel" variant="link" onClick={() => setRegenerateIdConfirmationModal(false)}>
            {i18n.cancel}
          </Button>,
        ]}
      >
        {i18n.propertiesPanel.regeneratingIdNamespaceMessage}
        <br />
        <br />
        {i18n.propertiesPanel.sureToContinue}
      </Modal>
    </Form>
  );
}
