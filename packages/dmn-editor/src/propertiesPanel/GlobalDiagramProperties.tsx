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
import { DataSourceIcon } from "@patternfly/react-icons/dist/js/icons/data-source-icon";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useState, useRef, useCallback } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { EXPRESSION_LANGUAGES_LATEST } from "@kie-tools/dmn-marshaller";

export function GlobalDiagramProperties() {
  const [isExpressionLanguageSelectOpen, setExpressionLanguageSelectOpen] = useState(false);
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const [isGlobalSectionExpanded, setGlobalSectionExpanded] = useState<boolean>(true);
  const [isIdNamespaceSectionExpanded, setIdNamespaceSectionExpanded] = useState<boolean>(true);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const settings = useSettings();
  const customLanguages = useDmnEditorStore((s) => s.expressionLanguages.customLanguages);

  const [regenerateIdConfirmationModal, setRegenerateIdConfirmationModal] = useState(false);

  const toggleRef = useRef<HTMLButtonElement>(null);

  const expressionLanguage = thisDmn.model.definitions["@_expressionLanguage"];

  //to keep the list updated when user checks later
  const allLanguages = [
    ...EXPRESSION_LANGUAGES_LATEST,
    ...customLanguages,
    ...(expressionLanguage &&
    !EXPRESSION_LANGUAGES_LATEST.includes(expressionLanguage) &&
    !customLanguages.includes(expressionLanguage)
      ? [expressionLanguage]
      : []),
  ];

  const onCreateOption = useCallback(
    (val: string) => {
      if (val && !EXPRESSION_LANGUAGES_LATEST.includes(val)) {
        dmnEditorStoreApi.setState((state) => {
          state.expressionLanguages.customLanguages.push(val);
        });
      }
    },
    [dmnEditorStoreApi]
  );

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
              <FormGroup label="Description">
                <TextArea
                  aria-label={"Description"}
                  type={"text"}
                  isDisabled={settings.isReadOnly}
                  style={{ resize: "vertical", minHeight: "40px" }}
                  rows={6}
                  placeholder={"Enter a description..."}
                  value={thisDmn.model.definitions.description?.__$$text}
                  onChange={(_event, newDescription) =>
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions.description = { __$$text: newDescription };
                    })
                  }
                />
              </FormGroup>

              <FormGroup label="Expression language">
                <Select
                  toggleRef={toggleRef}
                  variant={SelectVariant.typeahead}
                  aria-label={"Expression language"}
                  isOpen={isExpressionLanguageSelectOpen}
                  onSelect={(e, val) => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dmn.model.definitions["@_expressionLanguage"] = val as string;
                    });
                  }}
                  isCreatable
                  onCreateOption={onCreateOption}
                  onToggle={(event, isExpanded) => setExpressionLanguageSelectOpen(isExpanded)}
                  isDisabled={settings.isReadOnly}
                  selections={thisDmn.model.definitions["@_expressionLanguage"]}
                  placeholderText={"Enter an expression language..."}
                >
                  {allLanguages?.map((EXPRESSION_LANGUAGES: string) => (
                    <SelectOption key={EXPRESSION_LANGUAGES} value={EXPRESSION_LANGUAGES}>
                      {EXPRESSION_LANGUAGES}
                    </SelectOption>
                  ))}
                </Select>
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
              <FormGroup label="ID">
                <ClipboardCopy
                  placeholder="Enter a diagram ID..."
                  isReadOnly={settings.isReadOnly}
                  hoverTip="Copy"
                  clickTip="Copied"
                  onChange={(_event, newId) => {
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
                  isReadOnly={settings.isReadOnly}
                  hoverTip="Copy"
                  clickTip="Copied"
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
