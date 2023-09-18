import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { DmnModel, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { CardTitle, Card, CardHeader, CardBody, CardFooter } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery, GalleryItem } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { addIncludedModel } from "../mutations/addIncludedModel";
import { deleteIncludedModel } from "../mutations/deleteIncludedModel";
import { SPEC } from "../Spec";
import { useDmnEditorDependencies } from "./DmnEditorDependenciesContext";
import { dirname, basename } from "path";
import { DmnDependency } from "../DmnEditor";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditor } from "../DmnEditorContext";

export function IncludedModels() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const imports = useDmnEditorStore((s) => s.dmn.model.definitions.import ?? []);

  const { includedModelsContextDescription } = useDmnEditor();
  const { importsByNamespace } = useDmnEditorDerivedStore();
  const { dependenciesByNamespace, onRequestModelsAvailableToInclude, onRequestModelByPath } =
    useDmnEditorDependencies();

  const [isModalOpen, setModalOpen] = useState(false);
  const [isModelSelectOpen, setModelSelectOpen] = useState(false);
  const [selectedPath, setSelectedPath] = useState<string | undefined>(undefined);
  const [modelAlias, setModelAlias] = useState("");

  const [selectedModel, setSelectedModel] = useState<DmnModel | undefined>(undefined);
  // FIXME: Tiago --> Use `useCancellableEffect`
  useEffect(() => {
    if (!selectedPath) {
      return;
    }

    // FIXME: Tiago --> Handle `onRequestModelByPath` not being available.
    onRequestModelByPath?.(selectedPath).then((m) => {
      if (m) {
        setSelectedModel(m);
      } else {
        // FIXME: Tiago --> Handle error.
      }
    });
  }, [onRequestModelByPath, selectedPath]);

  const openModal = useCallback(() => {
    setModalOpen(true);
  }, []);

  const cancel = useCallback(() => {
    setModalOpen(false);
    setModelSelectOpen(false);
    setSelectedPath(undefined);
    setModelAlias("");
  }, []);

  const remove = useCallback(
    (index: number) => {
      dmnEditorStoreApi.setState((state) => {
        deleteIncludedModel({ definitions: state.dmn.model.definitions, index });
      });
    },
    [dmnEditorStoreApi]
  );

  const add = useCallback(() => {
    const valid = true; // FIXME: Tiago --> Do additional checks here like unicity etc
    if (!selectedModel || !valid || !SPEC.IMPORT.name.isValid(modelAlias)) {
      return;
    }

    setModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      addIncludedModel({
        definitions: state.dmn.model.definitions,
        includedModel: {
          xmlns: selectedModel.definitions["@_xmlns"]!,
          namespace: selectedModel.definitions["@_namespace"]!,
          alias: modelAlias,
        },
      });
    });

    cancel();
  }, [cancel, dmnEditorStoreApi, selectedModel, modelAlias]);

  // FIXME: Tiago --> Use `useCancellableEffect`
  const [modelPaths, setModelPaths] = useState<string[]>([]);
  useEffect(() => {
    // FIXME: Tiago --> Handle `onRequestModelsAvailableToInclude` not being available.
    onRequestModelsAvailableToInclude?.().then((m) => {
      setModelPaths(m);
    });
  }, [isModelSelectOpen, onRequestModelsAvailableToInclude]);

  const dependenciesByPath = useMemo(
    () =>
      Object.values(dependenciesByNamespace).reduce((acc, d) => acc.set(d!.path, d!), new Map<string, DmnDependency>()),
    [dependenciesByNamespace]
  );

  const modelPathsNotYetIncluded = useMemo(
    () =>
      modelPaths.filter((path) => {
        // If dependency does not exist, or there's no existing import with this
        // namespace, it can be listed as available for including.
        const dependency = dependenciesByPath.get(path);
        return !dependency || !importsByNamespace.get(dependency.model.definitions["@_namespace"]);
      }),
    [dependenciesByPath, importsByNamespace, modelPaths]
  );

  return (
    <>
      <Modal
        isOpen={isModalOpen}
        onClose={() => setModalOpen(false)}
        title={"Include model"}
        variant={ModalVariant.large}
        actions={[
          <Button key="confirm" variant="primary" onClick={add}>
            Include model
          </Button>,
          <Button key="cancel" variant="link" onClick={cancel}>
            Cancel
          </Button>,
        ]}
      >
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
                {modelPathsNotYetIncluded.map((path) => (
                  <SelectOption key={path} description={dirname(path)} value={path}>
                    {basename(path)}
                  </SelectOption>
                ))}
              </SelectGroup>
              <Divider key="divider" />
            </Select>
          </FormGroup>
          <FormGroup label={"Alias"} isRequired={true}>
            <TextInput
              placeholder={"Enter an alias..."}
              aria-label={"Alias"}
              value={modelAlias}
              onChange={setModelAlias}
            />
          </FormGroup>
          <br />
        </Form>
      </Modal>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.p}>
            Included models are externally defined models that have been added to this DMN file. Included DMN models
            have their decision requirements diagram (DRD) or decision requirements graph (DRG) components available in
            this DMN file. Included PMML models can be invoked through DMN Boxed Functions, usually inside Business
            Knowledge Model nodes (BKMs).
          </Text>
        </TextContent>
      </PageSection>
      {imports.length > 0 && (
        <PageSection>
          <Button onClick={openModal} variant={ButtonVariant.primary}>
            Include model
          </Button>
          <br />
          <br />
          <Divider inset={{ default: "insetMd" }} />
          <br />
          <Gallery hasGutter={true}>
            {imports.map((i, index) => {
              const dependency = dependenciesByNamespace[i["@_namespace"]];
              return (
                <GalleryItem key={i["@_name"]}>
                  <Card>
                    <CardHeader>
                      <CardTitle>{i["@_name"]}</CardTitle>
                    </CardHeader>
                    <CardBody>{i["@_namespace"]}</CardBody>
                    <CardFooter>
                      {dependency?.path ?? "WARNING: Path couldn't be determined."}
                      <br />
                      <br />
                      <Button variant={ButtonVariant.link} onClick={() => remove(index)} style={{ padding: 0 }}>
                        Remove
                      </Button>
                    </CardFooter>
                  </Card>
                </GalleryItem>
              );
            })}
          </Gallery>
        </PageSection>
      )}
      {imports.length <= 0 && (
        <PageSection>
          <EmptyState>
            <EmptyStateIcon icon={PlusCircleIcon} />
            <Title headingLevel="h4" size="lg">
              No external models have been included.
            </Title>
            <EmptyStateBody>{`Select "Include model" to start.`}</EmptyStateBody>
            <Button onClick={openModal} variant={ButtonVariant.primary}>
              Include model
            </Button>
          </EmptyState>
        </PageSection>
      )}
    </>
  );
}
