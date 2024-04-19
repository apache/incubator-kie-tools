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

export function GlobalDiagramProperties() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const [isGlobalSectionExpanded, setGlobalSectionExpanded] = useState<boolean>(true);
  const [isIdNamespaceSectionExpanded, setIdNamespaceSectionExpanded] = useState<boolean>(true);

  const dmnEditorStoreApi = useDmnEditorStoreApi();

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
            title={"Global properties"}
            action={
              <Button
                title={"Close"}
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
              <FormGroup label="Name">
                <InlineFeelNameInput
                  enableAutoFocusing={false}
                  isPlain={false}
                  id={thisDmn.model.definitions["@_id"]!}
                  name={thisDmn.model.definitions["@_name"]}
                  isReadonly={false}
                  shouldCommitOnBlur={true}
                  className={"pf-c-form-control"}
                  onRenamed={(newName) => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_name"] = newName;
                    });
                  }}
                  allUniqueNames={() => new Map()} // Right now, there's no way to know what are the unique names of all DMNs in the scope. So we let any name go.
                />
              </FormGroup>
              <FormGroup label="Description">
                <TextArea
                  aria-label={"Description"}
                  type={"text"}
                  isDisabled={false}
                  style={{ resize: "vertical", minHeight: "40px" }}
                  rows={6}
                  placeholder={"Enter a description..."}
                  value={thisDmn.model.definitions.description?.__$$text}
                  onChange={(newDescription) =>
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions.description = { __$$text: newDescription };
                    })
                  }
                />
              </FormGroup>

              <FormGroup label="Expression language">
                <TextInput
                  aria-label={"Expression language"}
                  type={"text"}
                  isDisabled={false}
                  placeholder={"Enter an expression language..."}
                  value={thisDmn.model.definitions["@_expressionLanguage"]}
                  onChange={(newExprLang) =>
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
            title={"ID & Namespace"}
            action={
              <Button
                title={"Re-generate ID & Namespace"}
                variant={ButtonVariant.plain}
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
              <FormGroup label="ID">
                <ClipboardCopy
                  placeholder="Enter a diagram ID..."
                  isReadOnly={false}
                  hoverTip="Copy"
                  clickTip="Copied"
                  onChange={(newId) => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_id"] = `${newId}`;
                    });
                  }}
                >
                  {thisDmn.model.definitions["@_id"]}
                </ClipboardCopy>
              </FormGroup>

              <FormGroup label="Namespace">
                <ClipboardCopy
                  placeholder="Enter a diagram Namespace..."
                  isReadOnly={false}
                  hoverTip="Copy"
                  clickTip="Copied"
                  onChange={(newNamespace) => {
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
            onClick={() => {
              setRegenerateIdConfirmationModal(false);
              dmnEditorStoreApi.setState((state) => {
                state.dmn.model.definitions["@_id"] = generateUuid();
                state.dmn.model.definitions["@_namespace"] = `https://kie.apache.org/dmn/${generateUuid()}`;
              });
            }}
          >
            Yes, re-generate ID and Namespace
          </Button>,
          <Button key="cancel" variant="link" onClick={() => setRegenerateIdConfirmationModal(false)}>
            Cancel
          </Button>,
        ]}
      >
        Re-generating the ID and Namespace of a DMN file will potentially break other DMN files that depend on it.
        <br />
        <br />
        Are you sure you want to continue?
      </Modal>
    </Form>
  );
}
