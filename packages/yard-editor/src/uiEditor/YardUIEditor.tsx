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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useBoxedExpressionEditorI18n } from "../i18n";
import "./YardUIEditor.css";
import {
  Arrow,
  Canvas,
  createEdgeFromNodes,
  detectCircular,
  Edge,
  EdgeData,
  hasLink,
  Label,
  Node,
  NodeData,
  NodeProps,
  Port,
  Remove,
} from "reaflow";
import { YardModel } from "../model";
import { ReactZoomPanPinchRef, TransformComponent, TransformWrapper } from "react-zoom-pan-pinch";

interface Props {
  yardData: YardModel | undefined;
  isReadOnly: boolean;
}

const NODE_FONT_WIDTH: number = 9;
const NODE_FONT_HEIGHT: number = 24;
const NODE_MIN_WIDTH: number = 60;
const NODE_MIN_HEIGHT: number = 44;
const NODE_VERTICAL_SPACING: number = 20;

export const YardUIEditor = ({ yardData, isReadOnly }: Props) => {
  const [nodes, setNodes] = useState<NodeData[]>([]);
  const [edges, setEdges] = useState<EdgeData[]>([]);
  const [diagramHeight, setDiagramHeight] = useState<number>(0);
  const [activeTabIndex, setActiveTabIndex] = useState(0);

  const transformComponentRef = useRef<ReactZoomPanPinchRef | null>(null);

  const { i18n } = useBoxedExpressionEditorI18n();

  const handleTabClick = useCallback((_event, tabIndex) => setActiveTabIndex(tabIndex), []);

  const createNode = useCallback((id: number, text: string) => {
    let textLines = text.split("\n");
    textLines = textLines.length > 1 ? textLines.slice(0, -1) : textLines;
    const textLength = textLines.reduce((largest: string, current: string) => {
      if (current.length > largest.length) {
        return current;
      }
      return largest;
    }, "").length;

    return {
      id: id.toString(),
      data: {
        text: text,
      },
      width: NODE_MIN_WIDTH + NODE_FONT_WIDTH * textLength,
      height: NODE_MIN_HEIGHT + NODE_FONT_HEIGHT * (textLines.length - 1),
    };
  }, []);

  const createEdge = useCallback((fromId: number, toId: number) => {
    return {
      id: fromId.toString() + "-" + toId.toString(),
      from: fromId.toString(),
      to: toId.toString(),
    };
  }, []);

  const setupDiagram = useCallback(() => {
    try {
      let nodesData: NodeData[] = [];
      let edgesData: EdgeData[] = [];
      let nodeId: number = 0;
      let height: number = 0;

      if (yardData?.elements) {
        yardData.elements.map((element) => {
          if (!element.logic?.type || !element.name) {
            nodesData = [];
            edgesData = [];
            return;
          }

          const elementNodeId = ++nodeId;
          nodesData.push(createNode(elementNodeId, element.name));

          if (element.logic.type === "LiteralExpression") {
            const expressionNode = createNode(++nodeId, element.logic.expression ?? "");
            nodesData.push(expressionNode);
            edgesData.push(createEdge(elementNodeId, nodeId));
            height += NODE_VERTICAL_SPACING + expressionNode.height;
          } else if (element.logic.type === "DecisionTable") {
            element.logic.rules?.map((rule) => {
              let inputText = "";
              let outputText = "";
              let ruleIndex = 0;

              element.logic.inputs?.map((input) => {
                if (!Array.isArray(rule)) {
                  inputText += input + ": " + (rule?.when ? rule.when[ruleIndex++] : "") + "\n";
                } else {
                  inputText += input + ": " + rule[ruleIndex++] + "\n";
                }
              });

              const inputsNode = createNode(++nodeId, inputText);
              nodesData.push(inputsNode);
              edgesData.push(createEdge(elementNodeId, nodeId));

              if (element.logic.outputComponents) {
                if (!Array.isArray(rule)) {
                  outputText += element.logic.outputComponents[0] + ": " + (rule?.then ?? "") + "\n";
                } else {
                  element.logic.outputComponents.map((output) => {
                    outputText += output + ": " + rule[ruleIndex++] + "\n";
                  });
                }
              } else {
                if (!Array.isArray(rule)) {
                  outputText += (rule?.then ?? "") + "\n";
                } else {
                  while (ruleIndex < rule.length) {
                    outputText += rule[ruleIndex++] + "\n";
                  }
                }
              }

              const outputsNode = createNode(++nodeId, outputText);
              nodesData.push(outputsNode);
              edgesData.push(createEdge(nodeId - 1, nodeId));

              height += NODE_VERTICAL_SPACING + Math.max(inputsNode.height, outputsNode.height);
            });
          }
        });

        setNodes(nodesData);
        setEdges(edgesData);
        setDiagramHeight(height);
      }
    } catch (e) {
      console.debug("Error during building diagram phase of yard model" + e.toString());
    }
  }, [yardData, setNodes, setEdges]);

  const EmptyStep = ({
    emptyStateBodyText,
    emptyStateTitleText,
  }: {
    emptyStateBodyText: string;
    emptyStateTitleText: string;
  }) => {
    return (
      <EmptyState>
        <EmptyStateIcon icon={CubesIcon} />
        <Title headingLevel={"h6"} size={"md"}>
          {emptyStateTitleText}
        </Title>
        <EmptyStateBody>{emptyStateBodyText}</EmptyStateBody>
      </EmptyState>
    );
  };

  useEffect(() => {
    setupDiagram();
  }, [yardData, setupDiagram]);

  useEffect(() => {
    if (activeTabIndex === 2) {
      transformComponentRef.current?.centerView();
    }
  }, [activeTabIndex]);

  const diagram = useMemo(
    () => (
      <Canvas
        maxWidth={Math.max(4000, diagramHeight / 5)}
        maxHeight={diagramHeight + 2000}
        fit={true}
        direction={"RIGHT"}
        nodes={nodes}
        edges={edges}
        arrow={<Arrow />}
        node={(node: NodeProps) => (
          <Node
            {...node}
            dragType="node"
            remove={<Remove />}
            port={<Port />}
            label={<Label />}
            style={{ fill: "white" }}
          >
            {(event) => (
              <foreignObject height={event.height} width={event.width} x={0} y={0} key={new Date().getTime()}>
                <div
                  style={{
                    padding: 10,
                    textAlign: "center",
                  }}
                >
                  <label style={{ whiteSpace: "pre" }}>{event.node.data.text}</label>
                </div>
              </foreignObject>
            )}
          </Node>
        )}
        edge={<Edge />}
        onNodeLink={(_event, from, to) => {
          const newEdges = edges.filter((e) => e.to !== from.id);
          setEdges([...newEdges, createEdgeFromNodes(to, from)]);
        }}
        onNodeLinkCheck={(_event, from: NodeData, to: NodeData) => {
          if (from.id === to.id) {
            return false;
          }
          if (hasLink(edges, to, from)) {
            return false;
          }
          if (detectCircular(nodes, edges, to, from)) {
            return false;
          }
          return true;
        }}
      />
    ),
    [nodes, edges]
  );

  return (
    <div className={"yard-ui-editor"}>
      <Tabs
        activeKey={activeTabIndex}
        aria-label="yard menu tabs"
        isBox={false}
        onSelect={handleTabClick}
        ouiaId={"yard-ui-tabs"}
      >
        <Tab eventKey={0} title={<TabTitleText>{i18n.generalTab.tabTitle}</TabTitleText>}>
          <div className={"general-body"}>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.name}
            </Title>
            <TextInput
              id={"name-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.name ? yardData.name : ""}
              ouiaId={"yard-name-input"}
            ></TextInput>
            <div className={"separator"}></div>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.kind}
            </Title>
            <TextInput
              id={"kind-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.kind ? yardData.kind : ""}
              ouiaId={"yard-type-input"}
            ></TextInput>
            <div className={"separator"}></div>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.expressionLang}
            </Title>
            <TextInput
              id={"expression-lang-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.expressionLang ? yardData.expressionLang : ""}
              ouiaId={"yard-expr-lang-version-input"}
            ></TextInput>
            <div className={"separator"}></div>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.specVersion}
            </Title>
            <TextInput
              id={"specVersion-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.specVersion ? yardData.specVersion : ""}
              ouiaId={"yard-spec-version-input"}
            ></TextInput>
          </div>
        </Tab>
        <Tab
          eventKey={1}
          title={<TabTitleText>{i18n.decisionInputsTab.tabTitle}</TabTitleText>}
          ouiaId={"decision-inputs-tab"}
        >
          <div className={"decision-input-body"}>
            {yardData?.inputs && yardData?.inputs.length > 0 ? (
              yardData.inputs.map((input, index) => {
                return (
                  <div key={index}>
                    <Title headingLevel="h6" size={TitleSizes.md}>
                      {i18n.decisionInputsTab.name}
                    </Title>
                    <TextInput
                      id={"expression-lang-text-input"}
                      isReadOnly={isReadOnly}
                      value={input?.name ? input.name : ""}
                      ouiaId={"decison-input-name"}
                    ></TextInput>
                    <div className={"separator"}></div>
                    <Title headingLevel="h6" size={TitleSizes.md}>
                      {i18n.decisionInputsTab.type}
                    </Title>
                    <TextInput
                      id={"expression-lang-text-input"}
                      isReadOnly={isReadOnly}
                      value={input?.type ? input.type : ""}
                      ouiaId={"decison-input-type"}
                    ></TextInput>
                    <Divider />
                  </div>
                );
              })
            ) : (
              <EmptyStep
                emptyStateTitleText={i18n.decisionInputsTab.emptyStateTitle}
                emptyStateBodyText={i18n.decisionInputsTab.emptyStateBody}
              />
            )}
          </div>
        </Tab>
        <Tab
          eventKey={2}
          title={<TabTitleText>{i18n.decisionElementsTab.tabTitle}</TabTitleText>}
          ouiaId={"decision-elements-tab"}
        >
          <div
            className={"decision-element-body"}
            style={{ maxWidth: "100%", maxHeight: "100%" }}
            data-ouia-component-id={"decision-diagram-body"}
          >
            {yardData?.elements && yardData?.elements.length > 0 && nodes.length > 0 ? (
              <TransformWrapper centerOnInit={true} initialScale={0.7} minScale={0.3} ref={transformComponentRef}>
                <TransformComponent>{diagram}</TransformComponent>
              </TransformWrapper>
            ) : (
              <EmptyStep
                emptyStateTitleText={i18n.decisionElementsTab.emptyStateTitle}
                emptyStateBodyText={i18n.decisionElementsTab.emptyStateBody}
              />
            )}
          </div>
        </Tab>
      </Tabs>
    </div>
  );
};
