/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { Dropdown, DropdownToggle, DropdownItem } from "@patternfly/react-core/deprecated";
import { TextContent, Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import React, { useState, useEffect, useMemo, useCallback } from "react";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import ProcessDetailsErrorModal from "../ProcessDetailsErrorModal/ProcessDetailsErrorModal";
import "../styles.css";
import { TriggerableNode } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ProcessDetailsChannelApi } from "../../../api";

interface ProcessDetailsNodeTriggerProps {
  processInstanceData: ProcessInstance;
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>;
}

const BANNED_NODE_TYPES = ["BoundaryEventNode", "EndNode", "Join", "StartNode", "Split"];

const ProcessDetailsNodeTrigger: React.FC<ProcessDetailsNodeTriggerProps> = ({ processInstanceData, channelApi }) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [selectedNode, setSelectedNode] = useState<TriggerableNode>();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>("");
  const [titleType, setTitleType] = useState<string>("");
  const [modalContent, setModalContent] = useState<string>("");
  const [isError, setIsError] = useState<boolean>(false);
  const [nodes, setNodes] = useState<TriggerableNode[]>([]);

  useEffect(() => {
    (async () => {
      await channelApi.requests
        .processDetails__getTriggerableNodes(processInstanceData)
        .then((nodeInstances: TriggerableNode[]) => {
          setIsError(false);
          setNodes(nodeInstances);
        })
        .catch((error) => {
          setIsError(true);
          setModalTitle("Node trigger Error");
          setModalContent(`Retrieval of nodes failed with error: ${error.message}`);
          setTitleType("failure");
          setIsModalOpen(true);
        });
    })();
  }, [channelApi.requests, processInstanceData]);

  const handleModalToggle = (): void => {
    setIsModalOpen(!isModalOpen);
  };

  const onSelect = (event: React.SyntheticEvent<HTMLDivElement>): void => {
    setIsOpen(!isOpen);
    getSelectedNode(event.currentTarget.id);
  };

  const onToggle = (isDropDownOpen: boolean): void => {
    setIsOpen(isDropDownOpen);
  };

  const getSelectedNode = useCallback(
    (selectedNodeId: string): void => {
      setSelectedNode(nodes.find((node: TriggerableNode) => node.id === selectedNodeId));
    },
    [nodes]
  );

  const nodeDropDownOptions = useMemo(() => {
    return nodes
      .filter((node) => {
        return node.type !== null && !BANNED_NODE_TYPES.includes(node.type);
      })
      .map((node) => (
        <DropdownItem key={node.id} id={node.id}>
          {node.name}
        </DropdownItem>
      ));
  }, [nodes]);

  const onTriggerClick = (): void => {
    setModalTitle("Node trigger process");
    channelApi.requests
      .processDetails__handleNodeTrigger(processInstanceData, selectedNode!)
      .then(() => {
        setTitleType("success");
        setModalContent(`The node ${selectedNode?.name} was triggered successfully`);
      })
      .catch((error) => {
        setTitleType("failure");
        setModalContent(`The node ${selectedNode?.name} trigger failed. ErrorMessage : ${error.message}`);
      })
      .finally(() => {
        setIsModalOpen(true);
      });
  };

  const errorModalAction: JSX.Element[] = [
    <Button key="confirm-selection" variant="primary" onClick={handleModalToggle}>
      OK
    </Button>,
  ];
  return (
    <>
      <ProcessDetailsErrorModal
        errorString={modalContent}
        errorModalOpen={isModalOpen}
        errorModalAction={errorModalAction}
        handleErrorModal={handleModalToggle}
        label="Node Trigger Error"
        title={setTitle(titleType, modalTitle)}
      />
      {!isError ? (
        <Card className="node-trigger" style={{ height: "100%" }}>
          <CardHeader>
            <Title headingLevel="h3" size="xl">
              Node Trigger
            </Title>
          </CardHeader>
          <CardHeader>
            <div>Select a node from the process nodes list and click Trigger to launch it manually.</div>
          </CardHeader>
          <CardBody>
            <div>
              <Dropdown
                direction="up"
                onSelect={onSelect}
                toggle={
                  <DropdownToggle
                    id="toggle-id"
                    data-testid="toggle-id"
                    onToggle={(_event, isDropDownOpen: boolean) => onToggle(isDropDownOpen)}
                    toggleIndicator={CaretDownIcon}
                  >
                    {selectedNode ? selectedNode.name : "select a node"}
                  </DropdownToggle>
                }
                isOpen={isOpen}
                dropdownItems={nodeDropDownOptions}
              />
            </div>
            {selectedNode && (
              <>
                <div className="pf-v5-u-mt-md">
                  <Flex direction={{ default: "column" }}>
                    <FlexItem>
                      <TextContent>
                        {" "}
                        <Split hasGutter>
                          <SplitItem>
                            <Text component={TextVariants.h6}>{"Node name : "}</Text>
                          </SplitItem>
                          <SplitItem>
                            <Text component={TextVariants.p}>{selectedNode.name}</Text>
                          </SplitItem>
                        </Split>
                      </TextContent>
                    </FlexItem>
                    <FlexItem>
                      <TextContent>
                        {" "}
                        <Split hasGutter>
                          <SplitItem>
                            <Text component={TextVariants.h6}>{"Node type : "}</Text>
                          </SplitItem>
                          <SplitItem>
                            <Text component={TextVariants.p}>{selectedNode.type}</Text>
                          </SplitItem>
                        </Split>
                      </TextContent>
                    </FlexItem>
                    <FlexItem>
                      <TextContent>
                        {" "}
                        <Split hasGutter>
                          <SplitItem>
                            <Text component={TextVariants.h6}>{"Node id : "}</Text>
                          </SplitItem>
                          <SplitItem>
                            <Text component={TextVariants.p}>{selectedNode.id}</Text>
                          </SplitItem>
                        </Split>
                      </TextContent>
                    </FlexItem>
                  </Flex>
                </div>
              </>
            )}
            <div className="pf-v5-u-mt-md">
              <Button
                variant="secondary"
                onClick={onTriggerClick}
                id="trigger"
                data-testid="trigger"
                isDisabled={!selectedNode}
              >
                Trigger
              </Button>
            </div>
          </CardBody>
        </Card>
      ) : (
        <></>
      )}
    </>
  );
};

export default ProcessDetailsNodeTrigger;
