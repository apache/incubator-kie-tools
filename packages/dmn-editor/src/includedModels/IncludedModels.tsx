import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { CardTitle, Card, CardHeader, CardBody, CardFooter } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { addImport } from "../mutations/addImport";
import { deleteImport } from "../mutations/deleteImport";
import { SPEC } from "../Spec";
import { useOtherDmns } from "./DmnEditorDependenciesContext";
import { dirname, basename } from "path";
import { OtherDmn } from "../DmnEditor";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditor } from "../DmnEditorContext";
import { DmnModel } from "@kie-tools/dmn-marshaller";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { InlineFeelNameInput, OnInlineFeelNameRenamed } from "../feel/InlineFeelNameInput";
import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { renameImport } from "../mutations/renameImport";

export function IncludedModels() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsImports = useDmnEditorStore((s) => s.dmn.model.definitions.import ?? []);

  const { includedModelsContextDescription, includedModelsContextName } = useDmnEditor();
  const { importsByNamespace, allFeelVariableUniqueNames } = useDmnEditorDerivedStore();
  const { otherDmnsByNamespace, onRequestModelsAvailableToInclude, onRequestOtherDmnByPath } = useOtherDmns();

  const [isModalOpen, setModalOpen] = useState(false);
  const [isModelSelectOpen, setModelSelectOpen] = useState(false);
  const [selectedPath, setSelectedPath] = useState<string | undefined>(undefined);
  const [importName, setImportName] = useState("");

  const [selectedModel, setSelectedModel] = useState<DmnModel | undefined>(undefined);
  // FIXME: Tiago --> Use `useCancellableEffect`
  useEffect(() => {
    if (!selectedPath) {
      return;
    }

    // FIXME: Tiago --> Handle `onRequestOtherDmnByPath` not being available.
    onRequestOtherDmnByPath?.(selectedPath).then((m) => {
      if (m) {
        setSelectedModel(m);
      } else {
        // FIXME: Tiago --> Handle error.
      }
    });
  }, [onRequestOtherDmnByPath, selectedPath]);

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
    if (!selectedModel || !SPEC.IMPORT.name.isValid(generateUuid(), importName, allFeelVariableUniqueNames)) {
      return;
    }

    setModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      addImport({
        definitions: state.dmn.model.definitions,
        includedModel: {
          xmlns: selectedModel.definitions["@_xmlns"]!, // FIXME: Tiago --> This is not always true, we can have a DMN file that doesn't even have a "xmlns" property... We should be able to figure it out based on the type of file.
          namespace: selectedModel.definitions["@_namespace"]!,
          name: importName,
        },
      });
    });

    cancel();
  }, [selectedModel, importName, allFeelVariableUniqueNames, dmnEditorStoreApi, cancel]);

  // FIXME: Tiago --> Use `useCancellableEffect`
  const [modelPaths, setModelPaths] = useState<string[] | undefined>(undefined);
  useEffect(() => {
    // FIXME: Tiago --> Handle `onRequestModelsAvailableToInclude` not being available.
    onRequestModelsAvailableToInclude?.().then((m) => {
      setModelPaths(m);
    });
  }, [isModelSelectOpen, onRequestModelsAvailableToInclude]);

  const otherDmnsByPath = useMemo(
    () => Object.values(otherDmnsByNamespace).reduce((acc, d) => acc.set(d!.path, d!), new Map<string, OtherDmn>()),
    [otherDmnsByNamespace]
  );

  const modelPathsNotYetIncluded = useMemo(
    () =>
      modelPaths &&
      modelPaths.filter((path) => {
        // If otherDmn does not exist, or there's no existing import with this
        // namespace, it can be listed as available for including.
        const otherDmn = otherDmnsByPath.get(path);
        return !otherDmn || !importsByNamespace.get(otherDmn.model.definitions["@_namespace"]);
      }),
    [otherDmnsByPath, importsByNamespace, modelPaths]
  );

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
                {includedModelsContextDescription}
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
                        {modelPathsNotYetIncluded
                          .filter((s) => s.endsWith(".dmn"))
                          .map((path) => (
                            <SelectOption key={path} description={dirname(path)} value={path}>
                              {basename(path)}
                            </SelectOption>
                          ))}
                      </SelectGroup>
                      <Divider key="divider" />
                      <SelectGroup label={"PMML"} key={"DMN"}>
                        {modelPathsNotYetIncluded
                          .filter((s) => s.endsWith(".pmml"))
                          .map((path) => (
                            <SelectOption key={path} description={dirname(path)} value={path}>
                              {basename(path)}
                            </SelectOption>
                          ))}
                        <SelectOption key={"none-pmml"} description={""} value={""} isDisabled={true}>
                          <i>None</i>
                        </SelectOption>
                      </SelectGroup>
                    </Select>
                  </FormGroup>
                  <FormGroup label={"Name"}>
                    <InlineFeelNameInput
                      validate={SPEC.IMPORT.name.isValid}
                      placeholder={"<Default>"}
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
            )) || <>{`All models available in '${includedModelsContextName}' are already included.`}</>}
          </>
        )) || <>Loading...</>}
      </Modal>
      {thisDmnsImports.length > 0 && (
        <PageSection>
          <Button onClick={openModal} variant={ButtonVariant.primary}>
            Include model
          </Button>
          <br />
          <br />
          <Divider inset={{ default: "insetMd" }} />
          <br />
          <Gallery hasGutter={true}>
            {thisDmnsImports.flatMap((i, index) => {
              const otherDmn = otherDmnsByNamespace[i["@_namespace"]];
              return !otherDmn ? [] : <IncludedModelCard i={i} index={index} otherDmn={otherDmn} />;
            })}
          </Gallery>
        </PageSection>
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

function IncludedModelCard({ i, index, otherDmn }: { i: DMN15__tImport; otherDmn: OtherDmn; index: number }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();

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

  return (
    <Card key={i["@_name"]} isCompact={false}>
      <CardHeader>
        <CardTitle>
          <InlineFeelNameInput
            placeholder={"<Default>"}
            isPlain={true}
            allUniqueNames={allFeelVariableUniqueNames}
            id={i["@_id"]!}
            name={i["@_name"]}
            isReadonly={false}
            shouldCommitOnBlur={true}
            onRenamed={rename}
            validate={SPEC.IMPORT.name.isValid}
          />
        </CardTitle>
      </CardHeader>
      <CardBody>
        {`${otherDmn.model.definitions["@_name"]}`}
        <br />
        <small>
          <i>{otherDmn.path ?? "WARNING: Path couldn't be determined."}</i>
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
