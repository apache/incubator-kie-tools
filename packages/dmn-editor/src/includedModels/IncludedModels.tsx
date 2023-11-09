import * as React from "react";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardHeader, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { basename, dirname, extname } from "path";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ExternalModel } from "../DmnEditor";
import { useDmnEditor } from "../DmnEditorContext";
import { DMN15_SPEC } from "../Dmn15Spec";
import { InlineFeelNameInput, OnInlineFeelNameRenamed } from "../feel/InlineFeelNameInput";
import { addImport } from "../mutations/addImport";
import { deleteImport } from "../mutations/deleteImport";
import { renameImport } from "../mutations/renameImport";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { KIE_UNKNOWN_NAMESPACE } from "../kie/kie";
import { ExternalModelLabel } from "./ExternalModelLabel";
import { useExternalModels } from "./DmnEditorDependenciesContext";
import { allPmmlImportNamespaces, getPmmlNamespace } from "../pmml/pmml";
import { allDmnImportNamespaces } from "../Dmn15Spec";
import { getNamespaceOfDmnImport } from "./importNamespaces";

const EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER = "<Default>";

const namespaceForNewImportsByFileExtension: Record<string, string> = {
  ".dmn": dmn15ns.get("")!,
  ".pmml": "https://www.dmg.org/PMML-4_4",
};

export function IncludedModels() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const thisDmnsImports = useMemo(() => thisDmn.model.definitions.import ?? [], [thisDmn.model.definitions.import]);

  const { externalContextDescription, externalContextName } = useDmnEditor();
  const { importsByNamespace, allFeelVariableUniqueNames } = useDmnEditorDerivedStore();
  const { externalModelsByNamespace, onRequestExternalModelsAvailableToInclude, onRequestExternalModelByPath } =
    useExternalModels();

  const [isModalOpen, setModalOpen] = useState(false);
  const [isModelSelectOpen, setModelSelectOpen] = useState(false);
  const [selectedPath, setSelectedPath] = useState<string | undefined>(undefined);
  const [importName, setImportName] = useState("");

  const [selectedModel, setSelectedModel] = useState<ExternalModel | undefined>(undefined);
  // FIXME: Tiago --> Use `useCancellableEffect`
  useEffect(() => {
    if (!selectedPath) {
      return;
    }

    // FIXME: Tiago --> Handle `onRequestExternalModelByPath` not being available.
    onRequestExternalModelByPath?.(selectedPath).then((m) => {
      if (m) {
        setSelectedModel(m);
      } else {
        // FIXME: Tiago --> Handle error.
      }
    });
  }, [onRequestExternalModelByPath, selectedPath]);

  const openModal = useCallback(() => {
    setModalOpen(true);
  }, []);

  const cancel = useCallback(() => {
    setModalOpen(false);
    setModelSelectOpen(false);
    setSelectedPath(undefined);
    setImportName("");
  }, []);

  const add = useCallback(() => {
    if (
      !selectedPath ||
      !selectedModel ||
      !DMN15_SPEC.IMPORT.name.isValid(generateUuid(), importName, allFeelVariableUniqueNames)
    ) {
      return;
    }

    const xmlns = namespaceForNewImportsByFileExtension[extname(selectedPath)];
    if (!xmlns) {
      throw new Error(`Can't import model with an unsupported file extension: '${selectedPath}'.`);
    }

    const namespace =
      selectedModel.type === "dmn"
        ? selectedModel.model.definitions["@_namespace"]!
        : selectedModel.type === "pmml"
        ? getPmmlNamespace({ fileRelativePath: selectedModel.relativePath })
        : KIE_UNKNOWN_NAMESPACE;

    setModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      addImport({
        definitions: state.dmn.model.definitions,
        includedModel: {
          xmlns,
          namespace,
          name: importName,
        },
      });
    });

    cancel();
  }, [selectedPath, selectedModel, importName, allFeelVariableUniqueNames, dmnEditorStoreApi, cancel]);

  // FIXME: Tiago --> Use `useCancellableEffect`
  const [modelPaths, setModelPaths] = useState<string[] | undefined>(undefined);
  useEffect(() => {
    // FIXME: Tiago --> Handle `onRequestExternalModelsAvailableToInclude` not being available.
    onRequestExternalModelsAvailableToInclude?.().then((m) => {
      setModelPaths(m);
    });
  }, [isModelSelectOpen, onRequestExternalModelsAvailableToInclude]);

  const externalModelsByPath = useMemo(
    () =>
      Object.entries(externalModelsByNamespace ?? {}).reduce((acc, [namespace, externalModel]) => {
        if (!externalModel) {
          console.warn(`DMN EDITOR: Could not find model with namespace '${namespace}'. Ignoring.`);
          return acc;
        } else {
          return acc.set(externalModel.relativePath, externalModel);
        }
      }, new Map<string, ExternalModel>()),
    [externalModelsByNamespace]
  );

  const modelPathsNotYetIncluded = useMemo(
    () =>
      modelPaths &&
      modelPaths.filter((path) => {
        // If externalModel does not exist, or there's no existing import with this
        // namespace, it can be listed as available for including.
        const externalModel = externalModelsByPath.get(path);
        return (
          !externalModel ||
          (externalModel.type === "dmn" && !importsByNamespace.get(externalModel.model.definitions["@_namespace"])) ||
          (externalModel.type === "pmml" &&
            !importsByNamespace.get(getPmmlNamespace({ fileRelativePath: externalModel.relativePath })))
        );
      }),
    [externalModelsByPath, importsByNamespace, modelPaths]
  );

  const pmmlPathsNotYetIncluded = modelPathsNotYetIncluded?.filter((s) => s.endsWith(".pmml"));
  const dmnPathsNotYetIncluded = modelPathsNotYetIncluded?.filter((s) => s.endsWith(".dmn"));

  return (
    <>
      <Modal
        isOpen={isModalOpen}
        onClose={() => setModalOpen(false)}
        title={"Include model"}
        variant={ModalVariant.large}
        actions={
          (modelPathsNotYetIncluded?.length ?? 0) > 0
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
        {(modelPathsNotYetIncluded && (
          <>
            {(modelPathsNotYetIncluded.length > 0 && (
              <>
                <br />
                {externalContextDescription}
                <br />
                <br />
                <Form>
                  <FormGroup label={"Model"} isRequired={true}>
                    <Select
                      menuAppendTo={document.body} //FIXME: Tiago --> Really? Maybe append to the Editor's container?
                      variant={SelectVariant.typeahead}
                      typeAheadAriaLabel={"Select a model to include..."}
                      placeholderText={"Select a model to include..."}
                      onToggle={setModelSelectOpen}
                      onClear={() => setSelectedPath(undefined)}
                      onSelect={(e, v) => {
                        if (typeof v !== "string") {
                          throw new Error(`Invalid path for an included model ${JSON.stringify(v)}`);
                        }

                        setSelectedPath(v);
                        setModelSelectOpen(false);
                      }}
                      selections={selectedPath}
                      isOpen={isModelSelectOpen}
                      aria-labelledby={"Included model selector"}
                      isGrouped={true}
                    >
                      <SelectGroup label={"DMN"} key={"DMN"}>
                        {((dmnPathsNotYetIncluded?.length ?? 0) > 0 &&
                          dmnPathsNotYetIncluded?.map((path) => (
                            <SelectOption key={path} description={dirname(path)} value={path}>
                              {basename(path)}
                            </SelectOption>
                          ))) || (
                          <SelectOption key={"none-dmn"} isDisabled={true} description={""} value={""}>
                            <i>None</i>
                          </SelectOption>
                        )}
                      </SelectGroup>
                      <Divider key="divider" />
                      <SelectGroup label={"PMML"} key={"PMML"}>
                        {((pmmlPathsNotYetIncluded?.length ?? 0) > 0 &&
                          pmmlPathsNotYetIncluded?.map((path) => (
                            <SelectOption key={path} description={dirname(path)} value={path}>
                              {basename(path)}
                            </SelectOption>
                          ))) || (
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
                      isReadonly={false}
                      shouldCommitOnBlur={true}
                      className={"pf-c-form-control"}
                      onRenamed={setImportName}
                      allUniqueNames={allFeelVariableUniqueNames}
                    />
                  </FormGroup>
                  <br />
                </Form>
              </>
            )) || (
              <>
                {((modelPaths?.length ?? 0) > 0 &&
                  `All models available in '${externalContextName}' are already included.`) ||
                  `There's no available models in '${externalContextName}' to be included.`}
              </>
            )}
          </>
        )) || <>Loading...</>}
      </Modal>
      {thisDmnsImports.length > 0 && (
        <>
          {/* This padding was necessary because PF4 has a @media query that doesn't run inside iframes, for some reason. */}
          <PageSection style={{ padding: "24px" }}>
            <Button onClick={openModal} variant={ButtonVariant.primary}>
              Include model
            </Button>
            <br />
            <br />
            <Divider inset={{ default: "insetMd" }} />
            <br />
            <Gallery hasGutter={true}>
              {thisDmnsImports.flatMap((i, index) => {
                const externalModel = externalModelsByNamespace?.[getNamespaceOfDmnImport({ dmnImport: i })];

                return !externalModel ? (
                  []
                ) : (
                  <IncludedModelCard key={i["@_id"]} _import={i} index={index} externalModel={externalModel} />
                );
              })}
            </Gallery>
          </PageSection>
        </>
      )}
      {thisDmnsImports.length <= 0 && (
        <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
          <EmptyState style={{ maxWidth: "1280px" }}>
            <EmptyStateIcon icon={CubesIcon} />
            <Title size={"lg"} headingLevel={"h4"}>
              No external models have been included.
            </Title>
            <EmptyStateBody>
              Included models are externally defined models that have been added to this DMN file. Included DMN models
              have their decision requirements diagram (DRD) or decision requirements graph (DRG) components available
              in this DMN file. Included PMML models can be invoked through DMN Boxed Functions, usually inside Business
              Knowledge Model nodes (BKMs)
            </EmptyStateBody>
            <Button onClick={openModal} variant={ButtonVariant.primary}>
              Include model
            </Button>
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
}: {
  _import: DMN15__tImport;
  externalModel: ExternalModel;
  index: number;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { onRequestToJumpToPath, onRequestToResolvePath } = useDmnEditor();

  const remove = useCallback(
    (index: number) => {
      dmnEditorStoreApi.setState((state) => {
        deleteImport({ definitions: state.dmn.model.definitions, index });
      });
    },
    [dmnEditorStoreApi]
  );

  const { allFeelVariableUniqueNames, allTopLevelDataTypesByFeelName } = useDmnEditorDerivedStore();

  const rename = useCallback<OnInlineFeelNameRenamed>(
    (newName) => {
      dmnEditorStoreApi.setState((state) => {
        renameImport({ definitions: state.dmn.model.definitions, index, newName, allTopLevelDataTypesByFeelName });
      });
    },
    [allTopLevelDataTypesByFeelName, dmnEditorStoreApi, index]
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
    if (externalModel.type === "dmn") {
      return externalModel.model.definitions["@_name"];
    } else if (externalModel.type === "pmml") {
      return "";
    }
  }, [externalModel.model, externalModel.type]);

  const pathDisplayed = onRequestToResolvePath?.(externalModel.relativePath) ?? externalModel.relativePath;

  return (
    <Card isCompact={false}>
      <CardHeader>
        <CardTitle>
          <InlineFeelNameInput
            placeholder={EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER}
            isPlain={true}
            allUniqueNames={allFeelVariableUniqueNames}
            id={_import["@_id"]!}
            name={_import["@_name"]}
            isReadonly={false}
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
      <CardBody>
        {`${title}`}
        <br />
        <br />
        <small>
          <Button
            variant={ButtonVariant.link}
            style={{ paddingLeft: 0, whiteSpace: "break-spaces", textAlign: "left" }}
            onClick={() => {
              onRequestToJumpToPath?.(externalModel.relativePath);
            }}
          >
            <i>{pathDisplayed}</i>
          </Button>
        </small>
      </CardBody>
      <CardFooter>
        <br />
        <br />
        <Button variant={ButtonVariant.link} onClick={() => remove(index)} style={{ padding: 0 }}>
          Remove
        </Button>
      </CardFooter>
    </Card>
  );
}
