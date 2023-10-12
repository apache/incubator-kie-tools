import * as React from "react";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import {
  Form,
  FormFieldGroupExpandable,
  FormFieldGroupHeader,
  FormGroup,
} from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DataSourceIcon } from "@patternfly/react-icons/dist/js/icons/data-source-icon";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { InputGroup } from "@patternfly/react-core";
import { SyncAltIcon } from "@patternfly/react-icons/dist/esm/icons/sync-alt-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

export function GlobalDiagramProperties() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const [isIdSuffleModalOpen, setIdSuffleModalOpen] = useState(false);

  return (
    <Form>
      <FormFieldGroupExpandable
        isExpanded={true}
        header={
          <FormFieldGroupHeader
            titleText={{
              id: "properties-panel-global-options",
              text: (
                <TextContent>
                  <Text component={TextVariants.h4}>
                    <DataSourceIcon />
                    &nbsp;&nbsp;Global properties
                  </Text>
                </TextContent>
              ),
            }}
          />
        }
      >
        <FormGroup label="Name">
          <InlineFeelNameInput
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
            allUniqueNames={new Map()} // Right now, there's no way to know what are the unique names of all DMNs in the scope. So we let any name go.
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
            value={thisDmn.model.definitions["description"]}
            onChange={(newDescription) =>
              dmnEditorStoreApi.setState((state) => {
                state.dmn.model.definitions.description = newDescription;
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

        <br />
        <br />
      </FormFieldGroupExpandable>

      <FormFieldGroupExpandable
        isExpanded={true}
        header={
          <FormFieldGroupHeader
            titleText={{
              id: "properties-panel-global-options-id-and-namespace",
              text: (
                <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                  <TextContent>
                    <Text component={TextVariants.h4}>ID & Namespace</Text>
                  </TextContent>
                  <Button
                    variant={ButtonVariant.plain}
                    onClick={() => setIdSuffleModalOpen(true)}
                    style={{ paddingBottom: 0, paddingTop: 0 }}
                  >
                    <SyncAltIcon />
                  </Button>
                </Flex>
              ),
            }}
          />
        }
      >
        <FormGroup label="ID">
          <ClipboardCopy
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
      </FormFieldGroupExpandable>
      <Modal
        variant={ModalVariant.small}
        isOpen={isIdSuffleModalOpen}
        onClose={() => setIdSuffleModalOpen(false)}
        actions={[
          <Button
            key="confirm"
            variant={ButtonVariant.primary}
            onClick={() => {
              setIdSuffleModalOpen(false);
              dmnEditorStoreApi.setState((state) => {
                state.dmn.model.definitions["@_id"] = generateUuid();
                state.dmn.model.definitions["@_namespace"] = `https://kie.org/dmn/${generateUuid()}`;
              });
            }}
          >
            Yes, re-generate ID and Namespace
          </Button>,
          <Button key="cancel" variant="link" onClick={() => setIdSuffleModalOpen(false)}>
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
