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
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ns as dmn12ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_2/ts-gen/meta";
import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardHeader, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { basename, dirname, extname } from "path";
import { useCallback, useMemo, useRef, useState } from "react";
import { ExternalModel } from "../DmnEditor";
import { useDmnEditor } from "../DmnEditorContext";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { InlineFeelNameInput, OnInlineFeelNameRenamed } from "../feel/InlineFeelNameInput";
import { addImport } from "../mutations/addImport";
import { deleteImport } from "../mutations/deleteImport";
import { renameImport } from "../mutations/renameImport";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { KIE_UNKNOWN_NAMESPACE } from "../kie/kie";
import { ExternalModelLabel } from "./ExternalModelLabel";
import { useExternalModels } from "./DmnEditorDependenciesContext";
import { allPmmlImportNamespaces, getPmmlNamespace } from "../pmml/pmml";
import { allDmnImportNamespaces } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { getNamespaceOfDmnImport } from "./importNamespaces";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert/Alert";
import { KebabToggle } from "@patternfly/react-core/deprecated";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { useInViewSelect } from "../responsiveness/useInViewSelect";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { State } from "../store/Store";
import "./IncludedModels.css";
import { Popover, PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import { AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useSettings } from "../settings/DmnEditorSettingsContext";

export const EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER = "<Default>";

const namespaceForNewImportsByFileExtension: Record<string, string> = {
  ".dmn": dmn12ns.get("")!, // FIXME: Tiago --> THIS SHOULD BE 1.5, BUT JIT EXECUTOR FAILS WITH IT. KEEPING 1.2 FOR NOW.
  ".pmml": "https://www.dmg.org/PMML-4_4",
};

export function IncludedModels() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsImports = useDmnEditorStore((s) => s.dmn.model.definitions.import ?? []);
  const settings = useSettings();

  const { externalContextDescription, externalContextName, dmnEditorRootElementRef, onRequestToResolvePath } =
    useDmnEditor();
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
  const { externalModelsByNamespace, onRequestExternalModelsAvailableToInclude, onRequestExternalModelByPath } =
    useExternalModels();

  const [isModalOpen, setModalOpen] = useState(false);
  const [isModelSelectOpen, setModelSelectOpen] = useState(false);
  const [selectedPathRelativeToThisDmn, setSelectedPathRelativeToThisDmn] = useState<string | undefined>(undefined);
  const [importName, setImportName] = useState("");

  const [selectedModel, setSelectedModel] = useState<ExternalModel | undefined>(undefined);
  const [selectedModelError, setSelectedModelError] = useState<string | undefined>(undefined);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!selectedPathRelativeToThisDmn) {
          return;
        }

        if (onRequestExternalModelByPath === undefined) {
          return;
        }

        onRequestExternalModelByPath(selectedPathRelativeToThisDmn)
          .then((externalModel) => {
            if (canceled.get()) {
              return;
            }

            if (!externalModel) {
              return;
            }

            setSelectedModelError(undefined);
            setSelectedModel(externalModel);
          })
          .catch((err) => {
            setSelectedModelError(
              `An error occurred when parsing the selected model '${selectedPathRelativeToThisDmn}'. Please double-check it is a non-empty valid model.`
            );
            console.error(err);
            return;
          });
      },
      [onRequestExternalModelByPath, selectedPathRelativeToThisDmn]
    )
  );

  const openModal = useCallback(() => {
    setModalOpen(true);
  }, []);

  const cancel = useCallback(() => {
    setModalOpen(false);
    setModelSelectOpen(false);
    setSelectedPathRelativeToThisDmn(undefined);
    setImportName("");
    setSelectedModelError(undefined);
  }, []);

  const add = useCallback(() => {
    const s = dmnEditorStoreApi.getState();
    if (
      !selectedPathRelativeToThisDmn ||
      !selectedModel ||
      !DMN15_SPEC.IMPORT.name.isValid(generateUuid(), importName, s.computed(s).getAllFeelVariableUniqueNames())
    ) {
      return;
    }

    const xmlns = namespaceForNewImportsByFileExtension[extname(selectedPathRelativeToThisDmn)];
    if (!xmlns) {
      throw new Error(`Can't import model with an unsupported file extension: '${selectedPathRelativeToThisDmn}'.`);
    }

    const namespace =
      selectedModel.type === "dmn"
        ? selectedModel.model.definitions["@_namespace"]!
        : selectedModel.type === "pmml"
          ? getPmmlNamespace({
              normalizedPosixPathRelativeToTheOpenFile: selectedModel.normalizedPosixPathRelativeToTheOpenFile,
            })
          : KIE_UNKNOWN_NAMESPACE;

    setModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      addImport({
        definitions: state.dmn.model.definitions,
        includedModel: {
          xmlns,
          namespace,
          name: importName,
          normalizedPathRelativeToThisDmn: selectedModel.normalizedPosixPathRelativeToTheOpenFile,
        },
      });
    });

    setTimeout(() => {
      setSelectedModelError(undefined);
      setSelectedModel(undefined);
    }, 5000); // Give it time for the `externalModelsByNamespace` object to be reassembled externally.

    cancel();
  }, [dmnEditorStoreApi, selectedPathRelativeToThisDmn, selectedModel, importName, cancel]);

  const [modelPathRelativeToThisDmn, setModelPathsRelativeToThisDmn] = useState<string[] | undefined>(undefined);
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        onRequestExternalModelsAvailableToInclude?.()
          .then((paths) => {
            if (canceled.get()) {
              return;
            }
            setModelPathsRelativeToThisDmn(paths);
          })
          .catch((err) => {
            console.error(err);
            return;
          });
      },
      [onRequestExternalModelsAvailableToInclude]
    )
  );

  const externalModelsByPathsRelativeToThisDmn = useMemo(
    () =>
      Object.entries(externalModelsByNamespace ?? {}).reduce((acc, [namespace, externalModel]) => {
        if (!externalModel) {
          console.warn(`DMN EDITOR: Could not find model with namespace '${namespace}'. Ignoring.`);
          return acc;
        } else {
          return acc.set(externalModel.normalizedPosixPathRelativeToTheOpenFile, externalModel);
        }
      }, new Map<string, ExternalModel>()),
    [externalModelsByNamespace]
  );

  const modelPathsRelativeToThisDmnNotYetIncluded = useMemo(
    () =>
      modelPathRelativeToThisDmn &&
      modelPathRelativeToThisDmn.filter((path) => {
        // If externalModel does not exist, or there's no existing import with this
        // namespace, it can be listed as available for including.
        const externalModel = externalModelsByPathsRelativeToThisDmn.get(path);
        return (
          !externalModel ||
          (externalModel.type === "dmn" && !importsByNamespace.get(externalModel.model.definitions["@_namespace"])) ||
          (externalModel.type === "pmml" &&
            !importsByNamespace.get(
              getPmmlNamespace({
                normalizedPosixPathRelativeToTheOpenFile: externalModel.normalizedPosixPathRelativeToTheOpenFile,
              })
            ))
        );
      }),
    [externalModelsByPathsRelativeToThisDmn, importsByNamespace, modelPathRelativeToThisDmn]
  );

  const pmmlPathsNotYetIncluded = useMemo(
    () => modelPathsRelativeToThisDmnNotYetIncluded?.filter((s) => s.endsWith(".pmml")),
    [modelPathsRelativeToThisDmnNotYetIncluded]
  );
  const dmnPathsNotYetIncluded = useMemo(
    () => modelPathsRelativeToThisDmnNotYetIncluded?.filter((s) => s.endsWith(".dmn")),
    [modelPathsRelativeToThisDmnNotYetIncluded]
  );

  const selectToggleRef = useRef<HTMLButtonElement>(null);
  const inViewSelect = useInViewSelect(dmnEditorRootElementRef, selectToggleRef);

  const getAllUniqueNames = useCallback((s: State) => s.computed(s).getAllFeelVariableUniqueNames(), []);

  return (
    <>
      <Modal
        data-testid={"kie-tools--dmn-editor--included-models-modal"}
        isOpen={isModalOpen}
        onClose={() => cancel()}
        title={"Include model"}
        variant={ModalVariant.large}
        actions={
          (modelPathsRelativeToThisDmnNotYetIncluded?.length ?? 0) > 0 && selectedModelError === undefined
            ? [
                <Button key="confirm" variant="primary" onClick={add}>
                  Include model
                </Button>,
                <Button key="cancel" variant="link" onClick={cancel}>
                  Cancel
                </Button>,
              ]
            : [
                <Button key="cancel" variant="link" onClick={cancel} style={{ paddingLeft: 0 }}>
                  Cancel
                </Button>,
              ]
        }
      >
        {(modelPathsRelativeToThisDmnNotYetIncluded && (
          <>
            {(modelPathsRelativeToThisDmnNotYetIncluded.length > 0 && (
              <>
                <br />
                {externalContextDescription}
                <br />
                <br />
                <Form>
                  <FormGroup label={"Model"} isRequired={true}>
                    <Select
                      toggleRef={selectToggleRef}
                      maxHeight={inViewSelect.maxHeight}
                      direction={inViewSelect.direction}
                      menuAppendTo={document.body}
                      variant={SelectVariant.typeahead}
                      typeAheadAriaLabel={"Select a model to include..."}
                      placeholderText={"Select a model to include..."}
                      onToggle={(_event, val) => setModelSelectOpen(val)}
                      onClear={() => setSelectedPathRelativeToThisDmn(undefined)}
                      onSelect={(e, path) => {
                        if (typeof path !== "string") {
                          throw new Error(`Invalid path for an included model ${JSON.stringify(path)}`);
                        }

                        setSelectedPathRelativeToThisDmn(path);
                        setModelSelectOpen(false);
                      }}
                      selections={selectedPathRelativeToThisDmn}
                      isOpen={isModelSelectOpen}
                      aria-labelledby={"Included model selector"}
                      isGrouped={true}
                    >
                      <SelectGroup label={"DMN"} key={"DMN"}>
                        {((dmnPathsNotYetIncluded?.length ?? 0) > 0 &&
                          dmnPathsNotYetIncluded?.map((p) => {
                            const normalizedPosixPathRelativeToTheWorkspaceRoot = onRequestToResolvePath?.(p) ?? p;
                            return (
                              <SelectOption
                                key={normalizedPosixPathRelativeToTheWorkspaceRoot}
                                description={dirname(normalizedPosixPathRelativeToTheWorkspaceRoot)}
                                value={p}
                              >
                                {basename(normalizedPosixPathRelativeToTheWorkspaceRoot)}
                              </SelectOption>
                            );
                          })) || (
                          <SelectOption key={"none-dmn"} isDisabled={true} description={""} value={""}>
                            <i>None</i>
                          </SelectOption>
                        )}
                      </SelectGroup>
                      <Divider key="divider" />
                      <SelectGroup label={"PMML"} key={"PMML"}>
                        {((pmmlPathsNotYetIncluded?.length ?? 0) > 0 &&
                          pmmlPathsNotYetIncluded?.map((p) => {
                            const normalizedPosixPathRelativeToTheWorkspaceRoot = onRequestToResolvePath?.(p) ?? p;
                            return (
                              <SelectOption
                                key={normalizedPosixPathRelativeToTheWorkspaceRoot}
                                description={dirname(normalizedPosixPathRelativeToTheWorkspaceRoot)}
                                value={p}
                              >
                                {basename(normalizedPosixPathRelativeToTheWorkspaceRoot)}
                              </SelectOption>
                            );
                          })) || (
                          <SelectOption key={"none-pmml"} isDisabled={true} description={""} value={""}>
                            <i>None</i>
                          </SelectOption>
                        )}
                      </SelectGroup>
                    </Select>
                  </FormGroup>
                  <FormGroup label={"Name"}>
                    <InlineFeelNameInput
                      validate={DMN15_SPEC.IMPORT.name.isValid}
                      placeholder={EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER}
                      isPlain={false}
                      id={generateUuid()}
                      name={importName}
                      isReadOnly={false}
                      shouldCommitOnBlur={true}
                      // className={"pf-v5-c-form-control"}
                      onRenamed={setImportName}
                      allUniqueNames={getAllUniqueNames}
                    />
                  </FormGroup>
                  <br />
                  {selectedModelError !== undefined && (
                    <span className={"kie-dmn-editor--selected-model-to-include-error"}>{selectedModelError}</span>
                  )}
                </Form>
              </>
            )) || (
              <>
                {((modelPathRelativeToThisDmn?.length ?? 0) > 0 &&
                  `All models available${
                    externalContextName ? ` in '${externalContextName}' ` : ` `
                  }are already included.`) ||
                  `There's no available models${
                    externalContextName ? ` in '${externalContextName}' ` : ` `
                  }to be included.`}
              </>
            )}
          </>
        )) || <>Loading...</>}
      </Modal>
      {thisDmnsImports.length > 0 && (
        <>
          {/* This padding was necessary because PF4 has a @media query that doesn't run inside iframes, for some reason. */}
          <PageSection style={{ padding: "24px" }}>
            <Button isDisabled={settings.isReadOnly} onClick={openModal} variant={ButtonVariant.primary}>
              Include model
            </Button>
            <br />
            <br />
            <Divider inset={{ default: "insetMd" }} />
            <br />
            <Gallery hasGutter={true} minWidths={{ xl: "calc(25% - 1rem)", md: "calc(33% - 1rem)", sm: "100%" }}>
              {thisDmnsImports.flatMap((dmnImport, index) => {
                const externalModel =
                  externalModelsByNamespace?.[getNamespaceOfDmnImport({ dmnImport: dmnImport })] ??
                  (!isModalOpen && index === thisDmnsImports.length - 1 ? selectedModel : undefined); // Use the selected model to avoid showing the "unknown included model" card.

                return !externalModel ? (
                  <IncludedModelCard
                    key={dmnImport["@_id"]}
                    _import={dmnImport}
                    index={index}
                    externalModel={undefined}
                    isReadOnly={settings.isReadOnly}
                  />
                ) : (
                  <IncludedModelCard
                    key={dmnImport["@_id"]}
                    _import={dmnImport}
                    index={index}
                    externalModel={externalModel}
                    isReadOnly={settings.isReadOnly}
                  />
                );
              })}
            </Gallery>
          </PageSection>
        </>
      )}
      {thisDmnsImports.length <= 0 && (
        <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
          <EmptyState style={{ maxWidth: "1280px" }}>
            <EmptyStateHeader
              titleText="No external models have been included."
              icon={<EmptyStateIcon icon={CubesIcon} />}
              headingLevel={"h4"}
            />
            <EmptyStateBody>
              Included models are externally defined models that have been added to this DMN file. Included DMN models
              have their decision requirements diagram (DRD) or decision requirements graph (DRG) components available
              in this DMN file. Included PMML models can be invoked through DMN Boxed Functions, usually inside Business
              Knowledge Model nodes (BKMs)
            </EmptyStateBody>
            <EmptyStateFooter>
              {!settings.isReadOnly && (
                <Button onClick={openModal} variant={ButtonVariant.primary}>
                  Include model
                </Button>
              )}
            </EmptyStateFooter>
          </EmptyState>
        </Flex>
      )}
    </>
  );
}

function IncludedModelCard({
  _import,
  index,
  externalModel,
  isReadOnly,
}: {
  _import: Normalized<DMN15__tImport>;
  externalModel: ExternalModel | undefined;
  index: number;
  isReadOnly: boolean;
}) {
  const { externalModelsByNamespace } = useExternalModels();
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const { onRequestToJumpToPath, onRequestToResolvePath } = useDmnEditor();

  const remove = useCallback(
    (index: number) => {
      setRemovePopoverOpen(false);
      dmnEditorStoreApi.setState((state) => {
        const externalModelTypesByNamespace = state
          .computed(state)
          .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace);
        deleteImport({
          definitions: state.dmn.model.definitions,
          __readonly_index: index,
          __readonly_externalModelTypesByNamespace: externalModelTypesByNamespace,
          __readonly_externalModelsByNamespace: externalModelsByNamespace,
        });
      });
    },
    [dmnEditorStoreApi, externalModelsByNamespace]
  );

  const rename = useCallback<OnInlineFeelNameRenamed>(
    (newName) => {
      dmnEditorStoreApi.setState((state) => {
        renameImport({
          definitions: state.dmn.model.definitions,
          index,
          newName,
          allTopLevelDataTypesByFeelName: state.computed(state).getDataTypes(externalModelsByNamespace)
            .allTopLevelDataTypesByFeelName,
        });
      });
    },
    [dmnEditorStoreApi, externalModelsByNamespace, index]
  );

  const extension = useMemo(() => {
    if (allDmnImportNamespaces.has(_import["@_importType"])) {
      return "dmn";
    } else if (allPmmlImportNamespaces.has(_import["@_importType"])) {
      return "pmml";
    } else {
      return "Unknwon";
    }
  }, [_import]);

  const title = useMemo(() => {
    if (externalModel === undefined) {
      return "";
    }
    if (externalModel.type === "dmn") {
      return externalModel.model.definitions["@_name"];
    } else if (externalModel.type === "pmml") {
      return "";
    }
  }, [externalModel]);

  const pathDisplayed = useMemo(() => {
    if (externalModel !== undefined) {
      return (
        onRequestToResolvePath?.(externalModel.normalizedPosixPathRelativeToTheOpenFile) ??
        externalModel.normalizedPosixPathRelativeToTheOpenFile
      );
    }
  }, [onRequestToResolvePath, externalModel]);

  const [isRemovePopoverOpen, setRemovePopoverOpen] = useState(false);
  const [isConfirmationPopoverOpen, setConfirmationPopoverOpen] = useState(false);
  const shouldRenderConfirmationMessage = useMemo(
    () => isRemovePopoverOpen && isConfirmationPopoverOpen,
    [isConfirmationPopoverOpen, isRemovePopoverOpen]
  );

  return (
    <Card isCompact={false}>
      <CardHeader
        {...(!isReadOnly && {
          actions: {
            actions: (
              <>
                <Popover
                  bodyContent={
                    shouldRenderConfirmationMessage ? (
                      <Alert
                        isInline
                        variant={AlertVariant.warning}
                        title={"This action have major impact to your model"}
                        actionClose={
                          <AlertActionCloseButton
                            onClose={() => {
                              setRemovePopoverOpen(false);
                              setConfirmationPopoverOpen(false);
                            }}
                          />
                        }
                        actionLinks={
                          <>
                            <AlertActionLink
                              onClick={(ev) => {
                                remove(index);
                                ev.stopPropagation();
                                ev.preventDefault();
                              }}
                              variant={"link"}
                              style={{ color: "var(--pf-v5-global--danger-color--200)", fontWeight: "bold" }}
                            >
                              {`Yes, remove included ${extension.toUpperCase()}`}
                            </AlertActionLink>
                          </>
                        }
                      >
                        {extension === "dmn" && (
                          <>
                            Removing an included DMN will erase all its imported nodes and connected edges from your
                            model. The references to item definitions, Business Knowledge Model functions, and Decision
                            expressions will remain, requiring to be manually removed.
                          </>
                        )}
                        {extension === "pmml" && (
                          <>
                            Removing an included PMML will not erase references on Boxed Function expressions, requiring
                            it to be manually removed.
                          </>
                        )}
                      </Alert>
                    ) : (
                      <Button
                        variant={"plain"}
                        onClick={(ev) => {
                          ev.stopPropagation();
                          ev.preventDefault();
                          if (isReadOnly) {
                            return;
                          }
                          setConfirmationPopoverOpen(true);
                        }}
                      >
                        <TrashIcon />
                        {"  "}
                        Remove
                      </Button>
                    )
                  }
                  hasNoPadding={shouldRenderConfirmationMessage}
                  maxWidth={shouldRenderConfirmationMessage ? "300px" : "150px"}
                  minWidth={shouldRenderConfirmationMessage ? "300px" : "150px"}
                  isVisible={isRemovePopoverOpen}
                  showClose={false}
                  shouldClose={() => {
                    setRemovePopoverOpen(false);
                    setConfirmationPopoverOpen(false);
                  }}
                  position={PopoverPosition.bottom}
                  shouldOpen={() => setRemovePopoverOpen(true)}
                >
                  <Button
                    variant={"plain"}
                    onClick={(ev) => {
                      ev.stopPropagation();
                      ev.preventDefault();
                    }}
                  >
                    <KebabToggle />
                  </Button>
                </Popover>
              </>
            ),
            hasNoOffset: false,
            className: undefined,
          },
        })}
      >
        <CardTitle>
          <InlineFeelNameInput
            placeholder={EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER}
            isPlain={true}
            allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
            id={_import["@_id"]!}
            name={_import["@_name"]}
            isReadOnly={isReadOnly}
            shouldCommitOnBlur={true}
            onRenamed={rename}
            validate={DMN15_SPEC.IMPORT.name.isValid}
          />
          <br />
          <br />
          <ExternalModelLabel extension={extension} />
          <br />
          <br />
        </CardTitle>
      </CardHeader>
      {externalModel ? (
        <CardBody>
          {`${title}`}
          <br />
          <br />
          <small>
            <Button
              variant={ButtonVariant.link}
              style={{ paddingLeft: 0, whiteSpace: "break-spaces", textAlign: "left" }}
              onClick={() => {
                onRequestToJumpToPath?.(externalModel.normalizedPosixPathRelativeToTheOpenFile);
              }}
            >
              <i>{pathDisplayed}</i>
            </Button>
          </small>
        </CardBody>
      ) : (
        // unknown
        <CardBody>
          <Alert title={"External model not found."} isInline={true} variant={AlertVariant.danger}>
            <Divider style={{ marginTop: "16px" }} />
            <br />
            <p>
              <b>Namespace:</b>&nbsp;{_import["@_namespace"]}
            </p>
            <p>
              <b>URI:</b>&nbsp;{_import["@_locationURI"] ?? <i>None</i>}
            </p>
          </Alert>
        </CardBody>
      )}
    </Card>
  );
}
