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
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { CardTitle, Card, CardHeader, CardBody, CardFooter } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery, GalleryItem } from "@patternfly/react-core/dist/js/layouts/Gallery";

type ExternalModel = {
  label: string;
  path: string;
  namespace: string;
  type: string;
};

export function IncludedModels() {
  const models = useMemo<{ dmn: ExternalModel[]; pmml: ExternalModel[] }>(() => {
    return {
      dmn: [
        { type: "dmn", label: "Other DMN", path: "src/main/resources/other.dmn", namespace: "NS1" },
        { type: "dmn", label: "Anther DMN", path: "src/main/resources/another.dmn", namespace: "NS2" },
      ],
      pmml: [{ type: "pmml", label: "Some PMML", path: "src/main/resources/some.pmml", namespace: "NS3" }],
    };
  }, []);

  const { dmn } = useDmnEditorStore();
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const imports = useMemo(() => dmn.model.definitions.import ?? [], [dmn.model.definitions.import]);

  const [isModalOpen, setModalOpen] = useState(false);
  const [isModelSelectOpen, setModelSelectOpen] = useState(false);
  const [model, setModel] = useState<ExternalModel | undefined>(undefined);
  const [name, setName] = useState("");

  const openModal = useCallback(() => {
    setModalOpen(true);
  }, []);

  const cancel = useCallback(() => {
    setModalOpen(false);
    setModelSelectOpen(false);
    setModel(undefined);
    setName("");
  }, []);

  const includeModel = useCallback(() => {
    const valid = true; // FIXME: Tiago --> Write validation for name and model path.
    if (!model || !valid) {
      return;
    }

    setModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      state.dmn.model.definitions.import ??= [];
      state.dmn.model.definitions.import.push({
        "@_id": generateUuid(),
        "@_name": name,
        "@_importType": model.type,
        "@_namespace": model.namespace,
      });
    });

    cancel();
  }, [cancel, dmnEditorStoreApi, model, name]);

  return (
    <>
      <Modal
        isOpen={isModalOpen}
        onClose={() => setModalOpen(false)}
        title={"Include model"}
        variant={ModalVariant.large}
        actions={[
          <Button key="confirm" variant="primary" onClick={includeModel}>
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
              onClear={() => setModel(undefined)}
              onSelect={(e, v) => {
                setModel(v as any);
                setModelSelectOpen(false);
              }}
              selections={model}
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
          <FormGroup label={"Name"} isRequired={true}>
            <TextInput placeholder={"Enter a name..."} aria-label={"Name"} value={name} onChange={setName} />
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
            {imports.map((imp, index) => (
              <>
                <GalleryItem>
                  <Card>
                    <CardHeader>
                      <CardTitle>{imp["@_name"]}</CardTitle>
                    </CardHeader>
                    <CardBody>{imp["@_namespace"]}</CardBody>
                    <CardFooter>{imp["@_locationURI"]}</CardFooter>
                  </Card>
                </GalleryItem>
              </>
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
