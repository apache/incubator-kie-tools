import * as React from "react";

import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import {
  Form,
  FormFieldGroupExpandable,
  FormFieldGroupHeader,
  FormGroup,
} from "@patternfly/react-core/dist/js/components/Form";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { useCallback, useState } from "react";

import { DataSourceIcon } from "@patternfly/react-icons/dist/js/icons/data-source-icon";
import { PencilAltIcon } from "@patternfly/react-icons/dist/js/icons/pencil-alt-icon";
import "./PropertiesPanel.css";

export type NodeType =
  | "inputData"
  | "decision"
  | "bkm"
  | "decisionService"
  | "knowledgeSource"
  | "textAnnotation"
  | "boxedExpression_dtInput"
  | "boxedExpression_dtOutput"
  | "boxedExpression_relationColumn"
  | "boxedExpression_literal"
  | "boxedExpression_contextEntryInfo"
  | "group";

export function SingleNodeProperties({
  dmn,
  setDmn,
  type,
  nodeId,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  type: NodeType;
  nodeId: string;
}) {
  const [isOpen, setOpen] = useState(false);
  const onToggleDataTypeSelect = useCallback((isOpen: boolean) => {
    setOpen(isOpen);
  }, []);
  const [dataType, setDataType] = useState("Any");

  return (
    <>
      <Form>
        <FormFieldGroupExpandable
          isExpanded={true}
          header={
            <FormFieldGroupHeader
              titleText={{
                text: (
                  <TextContent>
                    <Text component={TextVariants.h4}>
                      <PficonTemplateIcon />
                      &nbsp;&nbsp;
                      {(() => {
                        switch (type) {
                          case "inputData":
                            return <>Input</>;
                          case "decision":
                            return <>Decision</>;
                          case "bkm":
                            return <>Business Knowledge Model</>;
                          case "decisionService":
                            return <>Decision service</>;
                          case "knowledgeSource":
                            return <>Knowledge source</>;
                          case "textAnnotation":
                            return <>Text annotation</>;
                          case "group":
                            return <>Group</>;
                          case "boxedExpression_dtInput":
                            return <>Decision table - Input</>;
                          case "boxedExpression_dtOutput":
                            return <>Decision table - Output</>;
                          case "boxedExpression_relationColumn":
                            return <>Relation - Column</>;
                          case "boxedExpression_literal":
                            return <>Literal</>;
                          case "boxedExpression_contextEntryInfo":
                            return <>Context - Entry info</>;
                          default:
                            throw new Error("");
                        }
                      })()}
                    </Text>
                  </TextContent>
                ),
                id: "properties-panel-shape-options",
              }}
            />
          }
        >
          <FormGroup label="Name">
            <TextInput
              aria-label={"Name"}
              type={"text"}
              isDisabled={false}
              value={""}
              placeholder={"Enter a name..."}
            />
          </FormGroup>
          <FormGroup label="Data type">
            <Select
              variant={SelectVariant.typeahead}
              typeAheadAriaLabel="<Undefined>"
              onToggle={onToggleDataTypeSelect}
              onClear={() => setDataType("")}
              onSelect={(e, v) => setDataType(v as string)}
              selections={dataType}
              isOpen={isOpen}
              aria-labelledby={"Data types selector"}
              placeholderText="<Undefined>"
              isGrouped={true}
              isCreatable={true}
            >
              <SelectGroup label="Built-in" key="built-in">
                <SelectOption key={"Any"} value="Any" />
              </SelectGroup>
              <Divider key="divider" />
              <SelectGroup label="Custom" key="custom">
                <SelectOption key={"tPerson"} value="tPerson" />
              </SelectGroup>
            </Select>
          </FormGroup>
          <FormGroup label="Description">
            <TextArea
              aria-label={"Description"}
              type={"text"}
              isDisabled={false}
              value={""}
              placeholder={"Enter a description..."}
              style={{ resize: "vertical", minHeight: "40px" }}
              rows={6}
            />
          </FormGroup>
          <FormGroup label="ID">
            <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
              {nodeId}
            </ClipboardCopy>
          </FormGroup>
        </FormFieldGroupExpandable>

        <StyleOptions />
        <ShapeOptions />
      </Form>
    </>
  );
}

export function StyleOptions() {
  return (
    <FormFieldGroupExpandable
      isExpanded={false}
      header={
        <FormFieldGroupHeader
          titleText={{
            text: (
              <TextContent>
                <Text component={TextVariants.h4}>
                  <PencilAltIcon />
                  &nbsp;&nbsp;Style
                </Text>
              </TextContent>
            ),
            id: "properties-panel-shape-options",
          }}
        />
      }
    >
      <FormGroup label="Border color">
        <TextInput
          aria-label={"Border color"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a color..."}
        />
      </FormGroup>{" "}
      <FormGroup label="Font">
        <TextInput
          aria-label={"Font"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a font name..."}
        />
      </FormGroup>
      <FormGroup label="Font size">
        <TextInput
          aria-label={"Font size"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a font size..."}
        />
      </FormGroup>
      <FormGroup label="Text color">
        <TextInput
          aria-label={"Text color"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a color..."}
        />
      </FormGroup>
      <FormGroup label="Fill color">
        <TextInput
          aria-label={"Fill color"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a color..."}
        />
      </FormGroup>
    </FormFieldGroupExpandable>
  );
}

export function ShapeOptions() {
  return (
    <FormFieldGroupExpandable
      isExpanded={false}
      header={
        <FormFieldGroupHeader
          titleText={{
            text: (
              <TextContent>
                <Text component={TextVariants.h4}>
                  <CubeIcon />
                  &nbsp;&nbsp;Shape
                </Text>
              </TextContent>
            ),
            id: "properties-panel-shape-options",
          }}
        />
      }
    >
      <Grid hasGutter={true}>
        <GridItem span={6}>
          <FormGroup label="Width">
            <TextInput
              aria-label={"Width"}
              type={"text"}
              isDisabled={false}
              value={""}
              placeholder={"Enter a value..."}
            />
          </FormGroup>
        </GridItem>
        <GridItem span={6}>
          <FormGroup label="Height">
            <TextInput
              aria-label={"Height"}
              type={"text"}
              isDisabled={false}
              value={""}
              placeholder={"Enter a value..."}
            />
          </FormGroup>
        </GridItem>
      </Grid>
      <Grid hasGutter={true}>
        <GridItem span={6}>
          <FormGroup label="X">
            <TextInput aria-label={"X"} type={"text"} isDisabled={false} value={""} placeholder={"Enter a value..."} />
          </FormGroup>
        </GridItem>
        <GridItem span={6}>
          <FormGroup label="Y">
            <TextInput aria-label={"Y"} type={"text"} isDisabled={false} value={""} placeholder={"Enter a value..."} />
          </FormGroup>
        </GridItem>
      </Grid>
    </FormFieldGroupExpandable>
  );
}

export function PropertiesPanel({
  dmn,
  setDmn,
  selectedNodes,
  onClose,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  selectedNodes: string[];
  onClose: () => void;
}) {
  return (
    <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"500px"}>
      <DrawerHead>
        {selectedNodes.length === 1 && (
          <>
            <SingleNodeProperties dmn={dmn} setDmn={setDmn} type={"inputData"} nodeId={selectedNodes[0]} />
          </>
        )}
        {selectedNodes.length > 1 && (
          <>
            <Form>
              <StyleOptions />
            </Form>
          </>
        )}
        {selectedNodes.length <= 0 && (
          <Form>
            <FormFieldGroupExpandable
              isExpanded={true}
              header={
                <FormFieldGroupHeader
                  titleText={{
                    text: (
                      <TextContent>
                        <Text component={TextVariants.h4}>
                          <DataSourceIcon />
                          &nbsp;&nbsp;Global properties
                        </Text>
                      </TextContent>
                    ),
                    id: "properties-panel-shape-options",
                  }}
                />
              }
            >
              <FormGroup label="Name">
                <TextInput
                  aria-label={"Name"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["@_name"]}
                  placeholder={"Enter a name..."}
                />
              </FormGroup>
              <FormGroup label="Description">
                <TextArea
                  aria-label={"Description"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["description"]}
                  placeholder={"Enter a description..."}
                  style={{ resize: "vertical", minHeight: "40px" }}
                  rows={6}
                />
              </FormGroup>

              <br />
              <br />

              <FormGroup label="Namespace">
                <TextInput
                  aria-label={"Namespace"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["@_namespace"]}
                  placeholder={"Enter a namespace..."}
                />
              </FormGroup>
              <FormGroup label="Expression language">
                <TextInput
                  aria-label={"Expression language"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["@_expressionLanguage"]}
                  placeholder={"Enter an expression language..."}
                />
              </FormGroup>
              <FormGroup label="ID">
                <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
                  {dmn.definitions["@_id"]}
                </ClipboardCopy>
              </FormGroup>
            </FormFieldGroupExpandable>
          </Form>
        )}
        <DrawerActions>
          <DrawerCloseButton onClick={onClose} />
        </DrawerActions>
      </DrawerHead>
    </DrawerPanelContent>
  );
}
