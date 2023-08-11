import * as React from "react";
import { useCallback, useMemo, useState } from "react";

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
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

export type ExternalModel = {
  label: string;
  path: string;
  model: {
    "@_namespace": string;
    "@_xmlns": string;
  };
};

export function IncludedModels() {
  const models = useMemo<{ dmn: ExternalModel[]; pmml: ExternalModel[] }>(() => {
    return {
      dmn: [
        {
          model: {
            "@_xmlns": "https://www.omg.org/spec/DMN/20211108/MODEL/",
            "@_namespace": "https://kie.org/dmn/_42458D20-6E53-4071-A60D-544A06CEBC9F",
          },
          label: "Other DMN",
          path: "src/main/resources/other.dmn",
        },
        {
          model: {
            "@_xmlns": "https://www.omg.org/spec/DMN/20211108/MODEL/",
            "@_namespace": "https://kie.org/dmn/_4B5EA01B-236F-490B-BB33-57569043B73B",
          },
          label: "Anther DMN",
          path: "src/main/resources/another.dmn",
        },
      ],
      pmml: [
        {
          model: {
            "@_xmlns": "http://www.dmg.org/PMML-4_4",
            "@_namespace": "https://kie.org/pmml/_8A20E3CC-B6E1-40E1-AF0F-AF9A748337A6",
          },
          label: "Some PMML",
          path: "src/main/resources/some.pmml",
        },
      ],
    };
  }, []);

  const { dmn } = useDmnEditorStore();
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const imports = useMemo(() => dmn.model.definitions.import ?? [], [dmn.model.definitions.import]);

  const [isModalOpen, setModalOpen] = useState(false);
  const [isModelSelectOpen, setModelSelectOpen] = useState(false);
  const [selectedModel, setSelectedModel] = useState<ExternalModel | undefined>(undefined);
  const [modelAlias, setModelAlias] = useState("");

  const openModal = useCallback(() => {
    setModalOpen(true);
  }, []);

  const cancel = useCallback(() => {
    setModalOpen(false);
    setModelSelectOpen(false);
    setSelectedModel(undefined);
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
    if (!selectedModel || !valid || !SPEC.namedElement.isValidName(modelAlias)) {
      return;
    }

    setModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      addIncludedModel({
        definitions: state.dmn.model.definitions,
        includedModel: {
          xmlns: selectedModel.model["@_xmlns"],
          namespace: selectedModel.model["@_namespace"],
          alias: modelAlias,
        },
      });
    });

    cancel();
  }, [cancel, dmnEditorStoreApi, selectedModel, modelAlias]);

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
        {`All models (DMN and PMML) from 'Untitled folder' can be included.`}
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
              onClear={() => setSelectedModel(undefined)}
              onSelect={(e, v) => {
                setSelectedModel(v as any);
                setModelSelectOpen(false);
              }}
              selections={selectedModel}
              isOpen={isModelSelectOpen}
              aria-labelledby={"Included model selector"}
              isGrouped={true}
            >
              <SelectGroup label="DMN" key="DMN">
                {models.dmn.map((m) => (
                  <SelectOption key={m.path} description={m.path} value={m}>
                    {m.label}
                  </SelectOption>
                ))}
              </SelectGroup>
              <Divider key="divider" />
              <SelectGroup label="PMML" key="PMML">
                {models.pmml.map((m) => (
                  <SelectOption key={m.path} description={m.path} value={m}>
                    {m.label}
                  </SelectOption>
                ))}
              </SelectGroup>
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
            {imports.map((i, index) => (
              <GalleryItem key={i["@_name"]}>
                <Card>
                  <CardHeader>
                    <CardTitle>{i["@_name"]}</CardTitle>
                  </CardHeader>
                  <CardBody>{i["@_namespace"]}</CardBody>
                  <CardFooter>
                    {`/some/resolved/path`}
                    <br />
                    <br />
                    <Button variant={ButtonVariant.link} onClick={() => remove(index)} style={{ padding: 0 }}>
                      Remove
                    </Button>
                  </CardFooter>
                </Card>
              </GalleryItem>
            ))}
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
