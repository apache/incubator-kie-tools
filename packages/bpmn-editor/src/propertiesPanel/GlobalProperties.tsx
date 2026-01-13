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

import {
  deleteBpmn20Drools10MetaDataEntry,
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Form, FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { DataSourceIcon } from "@patternfly/react-icons/dist/js/icons/data-source-icon";
import { ImportIcon } from "@patternfly/react-icons/dist/js/icons/import-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { TagIcon } from "@patternfly/react-icons/dist/js/icons/tag-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useState } from "react";
import { addOrGetProcessAndDiagramElements } from "../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { Imports } from "./imports/Imports";
import { Metadata } from "./metadata/Metadata";
import { SlaDueDateInput } from "./slaDueDate/SlaDueDateInput";
import { useBpmnEditorI18n } from "../i18n";

export function GlobalProperties() {
  const { i18n, locale } = useBpmnEditorI18n();
  const thisBpmn = useBpmnEditorStore((s) => s.bpmn);
  const settings = useBpmnEditorStore((s) => s.settings);

  const process = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.find((s) => s.__$$element === "process")
  );

  const importsCount = process?.extensionElements?.["drools:import"]?.length ?? 0;
  const metadataEntriesCount = process?.extensionElements?.["drools:metaData"]?.length ?? 0;

  const [isGlobalSectionExpanded, setGlobalSectionExpanded] = useState<boolean>(true);
  const [isImportsSectionExpanded, setImportsSectionExpanded] = useState<boolean>(false);
  const [isMetadataSectionExpanded, setMetadataSectionExpanded] = useState<boolean>(false);
  const [isIdNamespaceSectionExpanded, setIdNamespaceSectionExpanded] = useState<boolean>(false);
  const [isMiscSectionExpanded, setMiscSectionExpanded] = useState<boolean>(false);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const [showRegenerateIdConfirmationModal, setShowRegenerateIdConfirmationModal] = useState(false);
  const [showCorrelationsModal, setShowCorrelationsModal] = useState(false);

  const closeCorrelationsModal = React.useCallback(() => {
    setShowCorrelationsModal(false);
  }, []);

  return (
    <>
      <Form>
        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isGlobalSectionExpanded}
              toogleSectionExpanded={() => setGlobalSectionExpanded((prev) => !prev)}
              icon={<DataSourceIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
              title={i18n.propertiesPanel.process}
              action={
                <Button
                  title={i18n.propertiesPanel.close}
                  variant={ButtonVariant.plain}
                  onClick={() => {
                    bpmnEditorStoreApi.setState((state) => {
                      state.propertiesPanel.isOpen = false;
                    });
                  }}
                >
                  <TimesIcon />
                </Button>
              }
              locale={locale}
            />
          }
        >
          {isGlobalSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px" }}>
                <FormGroup label={i18n.propertiesPanel.name}>
                  <TextInput
                    aria-label={"Name"}
                    type={"text"}
                    isDisabled={settings.isReadOnly}
                    placeholder={i18n.propertiesPanel.enterNamePlaceholder}
                    value={process?.["@_name"] ?? ""}
                    onChange={(e, newName) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (newName) {
                          process["@_name"] = newName;
                        } else {
                          delete process["@_name"];
                        }
                      })
                    }
                  />
                </FormGroup>
                <FormGroup label={i18n.propertiesPanel.documentation}>
                  <TextArea
                    aria-label={"Documentation"}
                    type={"text"}
                    isDisabled={settings.isReadOnly}
                    style={{ resize: "vertical", minHeight: "40px" }}
                    rows={3}
                    placeholder={i18n.propertiesPanel.documentationPlaceholder}
                    value={process?.documentation?.[0].__$$text ?? ""}
                    onChange={(e, newDocumentation) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (newDocumentation) {
                          process.documentation ??= [];
                          process.documentation[0] = {
                            "@_id": generateUuid(),
                            __$$text: newDocumentation,
                          };
                        } else {
                          delete process.documentation;
                        }
                      })
                    }
                  />
                </FormGroup>

                <Divider inset={{ default: "insetXs" }} />

                <FormGroup
                  fieldId="kie-bpmn-editor--global-properties-panel--adhoc"
                  // helperText={"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."} // FIXME: Tiago -> Description
                >
                  <Checkbox
                    label={i18n.propertiesPanel.adhoc}
                    id="kie-bpmn-editor--global-properties-panel--adhoc"
                    name="is-adhoc"
                    aria-label="Adhoc"
                    isChecked={process?.["@_drools:adHoc"] ?? false}
                    onChange={(e, checked) => {
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        process["@_drools:adHoc"] = checked;
                      });
                    }}
                  />
                </FormGroup>

                <SlaDueDateInput element={process} />
              </FormSection>
            </>
          )}
        </FormSection>

        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isImportsSectionExpanded}
              toogleSectionExpanded={() => setImportsSectionExpanded((prev) => !prev)}
              icon={<ImportIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
              title={i18n.propertiesPanel.imports + (importsCount > 0 ? ` (${importsCount})` : "")}
              locale={locale}
            />
          }
        >
          {isImportsSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px", gap: 0 }}>
                <Imports p={process} />
              </FormSection>
            </>
          )}
        </FormSection>

        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isMetadataSectionExpanded}
              toogleSectionExpanded={() => setMetadataSectionExpanded((prev) => !prev)}
              icon={<ColumnsIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
              title={i18n.propertiesPanel.metadata + (metadataEntriesCount > 0 ? ` (${metadataEntriesCount})` : "")}
              locale={locale}
            />
          }
        >
          {isMetadataSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px", gap: 0 }}>
                <Metadata obj={process} />
              </FormSection>
            </>
          )}
        </FormSection>

        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isIdNamespaceSectionExpanded}
              toogleSectionExpanded={() => setIdNamespaceSectionExpanded((prev) => !prev)}
              icon={<TagIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
              title={i18n.propertiesPanel.idNamespace}
              action={
                <Button
                  title={i18n.propertiesPanel.regenerateIdNamespace}
                  variant={ButtonVariant.plain}
                  isDisabled={settings.isReadOnly}
                  onClick={() => setShowRegenerateIdConfirmationModal(true)}
                  style={{ paddingBottom: 0, paddingTop: 0 }}
                >
                  <SyncAltIcon />
                </Button>
              }
              locale={locale}
            />
          }
        >
          {isIdNamespaceSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px" }}>
                <FormGroup label={i18n.propertiesPanel.id}>
                  <ClipboardCopy
                    placeholder={i18n.propertiesPanel.idPlaceholder}
                    isReadOnly={settings.isReadOnly}
                    hoverTip={i18n.propertiesPanel.copy}
                    clickTip={i18n.propertiesPanel.copied}
                    onChange={(e, newId) => {
                      bpmnEditorStoreApi.setState((state) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: state.bpmn.model.definitions,
                        });
                        process["@_id"] = `${newId}`;
                      });
                    }}
                  >
                    {process?.["@_id"]}
                  </ClipboardCopy>
                </FormGroup>

                <FormGroup label={i18n.propertiesPanel.namespace}>
                  <ClipboardCopy
                    placeholder={i18n.propertiesPanel.namespacePlaceholder}
                    isReadOnly={settings.isReadOnly}
                    hoverTip={i18n.propertiesPanel.copy}
                    clickTip={i18n.propertiesPanel.copied}
                    onChange={(e, newNamespace) => {
                      bpmnEditorStoreApi.setState((state) => {
                        state.bpmn.model.definitions["@_targetNamespace"] = `${newNamespace}`;
                      });
                    }}
                  >
                    {thisBpmn.model.definitions["@_targetNamespace"]}
                  </ClipboardCopy>
                </FormGroup>
              </FormSection>
            </>
          )}
        </FormSection>

        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isMiscSectionExpanded}
              toogleSectionExpanded={() => setMiscSectionExpanded((prev) => !prev)}
              title={i18n.propertiesPanel.misc}
              locale={locale}
            />
          }
        >
          {isMiscSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px" }}>
                <FormGroup
                  label={i18n.propertiesPanel.expressionLanguage}
                  //   helperText={
                  //     "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
                  //   } // FIXME: Tiago -> Description
                >
                  <TextInput
                    aria-label={"Expression language"}
                    type={"text"}
                    isDisabled={settings.isReadOnly}
                    placeholder={i18n.propertiesPanel.expressionLanguagePlaceholder}
                    value={thisBpmn.model.definitions["@_expressionLanguage"] ?? ""}
                    onChange={(e, newExprLang) =>
                      bpmnEditorStoreApi.setState((state) => {
                        if (newExprLang) {
                          state.bpmn.model.definitions["@_expressionLanguage"] = newExprLang;
                        } else {
                          delete state.bpmn.model.definitions["@_expressionLanguage"];
                        }
                      })
                    }
                  />
                </FormGroup>

                <FormGroup
                  label={i18n.propertiesPanel.type}
                  // helperText={
                  //   "Consectetur adipiscing elit. Lorem ipsum dolor sit amet, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
                  // } // FIXME: Tiago -> Description
                >
                  <ToggleGroup isCompact aria-label="Process type">
                    <ToggleGroupItem
                      text={i18n.propertiesPanel.private}
                      isDisabled={settings.isReadOnly}
                      isSelected={process?.["@_processType"] === "Private"}
                      onChange={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          const { process } = addOrGetProcessAndDiagramElements({
                            definitions: s.bpmn.model.definitions,
                          });
                          process["@_processType"] = "Private";
                        });
                      }}
                    />
                    <ToggleGroupItem
                      text={i18n.propertiesPanel.public}
                      isDisabled={settings.isReadOnly}
                      isSelected={process?.["@_processType"] === "Public"}
                      onChange={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          const { process } = addOrGetProcessAndDiagramElements({
                            definitions: s.bpmn.model.definitions,
                          });
                          process["@_processType"] = "Public";
                        });
                      }}
                    />
                  </ToggleGroup>
                </FormGroup>

                <FormGroup
                  fieldId="kie-bpmn-editor--global-properties-panel--executable"
                  // helperText={"Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."} // FIXME: Tiago -> Description
                >
                  <Checkbox
                    label={i18n.propertiesPanel.executable}
                    id="kie-bpmn-editor--global-properties-panel--executable"
                    name="is-executable"
                    aria-label="Executable"
                    isChecked={process?.["@_isExecutable"] ?? true}
                    onChange={(e, checked) => {
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        process["@_isExecutable"] = checked;
                      });
                    }}
                  />
                </FormGroup>

                <FormGroup
                  label={i18n.propertiesPanel.packageName}
                  // helperText={"Dot-separated, like Java packages."} // FIXME: Tiago -> Description
                >
                  <TextInput
                    aria-label={"Package name"}
                    type={"text"}
                    isDisabled={settings.isReadOnly}
                    placeholder={i18n.propertiesPanel.packageNamePlaceholder}
                    value={process?.["@_drools:packageName"] ?? ""}
                    onChange={(e, newPackageName) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (newPackageName) {
                          process["@_drools:packageName"] = newPackageName;
                        } else {
                          delete process["@_drools:packageName"];
                        }
                      })
                    }
                  />
                </FormGroup>

                <FormGroup
                  label={i18n.propertiesPanel.version}
                  // helperText={"E.g., 0.0.1"} // FIXME: Tiago -> Description
                >
                  <TextInput
                    aria-label={"Version"}
                    type={"text"}
                    isDisabled={settings.isReadOnly}
                    placeholder={i18n.propertiesPanel.versionPlaceholder}
                    value={process?.["@_drools:version"] ?? ""}
                    onChange={(e, newVersion) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (newVersion) {
                          process["@_drools:version"] = newVersion;
                        } else {
                          delete process["@_drools:version"];
                        }
                      })
                    }
                  />
                </FormGroup>

                <FormGroup
                  label={i18n.propertiesPanel.processInstanceDescription}
                  //   helperText={
                  //     "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut."
                  //   } // FIXME: Tiago -> Description
                >
                  <TextArea
                    aria-label={"Process Instance Description"}
                    type={"text"}
                    isDisabled={settings.isReadOnly}
                    style={{ resize: "vertical", minHeight: "40px" }}
                    rows={3}
                    placeholder={i18n.propertiesPanel.processInstanceDescriptionPlaceholder}
                    value={parseBpmn20Drools10MetaData(process).get("customDescription") ?? ""} // FIXME: Tiago
                    onChange={(e, newDescription) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (newDescription) {
                          setBpmn20Drools10MetaData(process, "customDescription", newDescription);
                        } else {
                          deleteBpmn20Drools10MetaDataEntry(process, "customDescription");
                        }
                      })
                    }
                  />
                </FormGroup>
              </FormSection>
            </>
          )}
        </FormSection>

        <br />
        <br />
        <br />

        <Modal
          aria-labelledby={"Regenerate ID & Namespace"}
          variant={ModalVariant.small}
          isOpen={showRegenerateIdConfirmationModal}
          onClose={() => setShowRegenerateIdConfirmationModal(false)}
          actions={[
            <Button
              key="confirm"
              variant={ButtonVariant.primary}
              isDisabled={settings.isReadOnly}
              onClick={() => {
                setShowRegenerateIdConfirmationModal(false);
                bpmnEditorStoreApi.setState((state) => {
                  const { process } = addOrGetProcessAndDiagramElements({ definitions: state.bpmn.model.definitions });
                  process["@_id"] = generateUuid();
                  state.bpmn.model.definitions["@_targetNamespace"] = `https://kie.apache.org/bpmn/${generateUuid()}`;
                });
              }}
            >
              {i18n.propertiesPanel.regenerateId}
            </Button>,
            <Button key="cancel" variant="link" onClick={() => setShowRegenerateIdConfirmationModal(false)}>
              {i18n.propertiesPanel.cancel}
            </Button>,
          ]}
        >
          {i18n.propertiesPanel.regenerateMessage}
          <br />
          <br />
          {i18n.propertiesPanel.continueMessage}
        </Modal>
      </Form>
    </>
  );
}
