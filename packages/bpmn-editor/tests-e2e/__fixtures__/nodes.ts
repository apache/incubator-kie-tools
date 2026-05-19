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

import { expect, Locator, Page } from "@playwright/test";
import { Diagram } from "./diagram";
import { EdgeType } from "./edges";

export enum NodeType {
  START_EVENT = "node_startEvent",
  INTERMEDIATE_CATCH_EVENT = "node_intermediateCatchEvent",
  INTERMEDIATE_THROW_EVENT = "node_intermediateThrowEvent",
  END_EVENT = "node_endEvent",
  TASK = "node_task",
  CALL_ACTIVITY = "node_callActivity",
  SUB_PROCESS = "node_subProcess",
  GATEWAY = "node_gateway",
  DATA_OBJECT = "node_dataObject",
  TEXT_ANNOTATION = "node_textAnnotation",
  GROUP = "node_group",
  LANE = "node_lane",
}

export enum DefaultNodeName {
  START_EVENT = "", // Events use ID as label, not a name
  INTERMEDIATE_CATCH_EVENT = "", // Events use ID as label, not a name
  INTERMEDIATE_THROW_EVENT = "", // Events use ID as label, not a name
  END_EVENT = "", // Events use ID as label, not a name
  TASK = "New Task",
  CALL_ACTIVITY = "New Call Activity",
  SUB_PROCESS = "New Sub-process",
  GATEWAY = "", // Gateways use ID as label when no name is set
  DATA_OBJECT = "New Data Object",
  TEXT_ANNOTATION = "", // Text annotations use their text content
  GROUP = "", // Groups use ID as label
  LANE = "New Lane",
}

export enum NodePosition {
  BOTTOM,
  CENTER,
  LEFT,
  RIGHT,
  TOP,
  TOP_PADDING,
}

export class Nodes {
  constructor(
    public page: Page,
    public diagram: Diagram,
    public browserName: string
  ) {}

  public get(args: { name: string }) {
    return this.page.getByTestId(/kie-tools--bpmn-editor--node-/).and(this.page.getByLabel(args.name, { exact: true }));
  }

  public async getId(args: { name: string }): Promise<string> {
    return (await this.get({ name: args.name }).getAttribute("data-nodehref")) ?? "";
  }

  public getByType(type: NodeType) {
    const nodeTypeMap: Record<NodeType, RegExp> = {
      [NodeType.START_EVENT]: /^kie-tools--bpmn-editor--node-start-event-/,
      [NodeType.INTERMEDIATE_CATCH_EVENT]: /^kie-tools--bpmn-editor--node-intermediate-catch-event-/,
      [NodeType.INTERMEDIATE_THROW_EVENT]: /^kie-tools--bpmn-editor--node-intermediate-throw-event-/,
      [NodeType.END_EVENT]: /^kie-tools--bpmn-editor--node-end-event-/,
      [NodeType.TASK]: /^kie-tools--bpmn-editor--node-task-/,
      [NodeType.CALL_ACTIVITY]: /^kie-tools--bpmn-editor--node-task-/, // Call activities are tasks
      [NodeType.SUB_PROCESS]: /^kie-tools--bpmn-editor--node-sub-process-/,
      [NodeType.GATEWAY]: /^kie-tools--bpmn-editor--node-gateway-/,
      [NodeType.DATA_OBJECT]: /^kie-tools--bpmn-editor--node-data-object-/,
      [NodeType.TEXT_ANNOTATION]: /^kie-tools--bpmn-editor--node-text-annotation-/,
      [NodeType.GROUP]: /^kie-tools--bpmn-editor--node-group-/,
      [NodeType.LANE]: /^kie-tools--bpmn-editor--node-lane-/,
    };
    return this.page.getByTestId(nodeTypeMap[type]);
  }

  public async getIdByType(type: NodeType): Promise<string> {
    const node = this.getByType(type).first();
    return (await node.getAttribute("data-nodehref")) ?? "";
  }

  public async delete(args: { name: string }) {
    await this.select({ name: args.name, position: NodePosition.TOP_PADDING });
    await this.diagram.get().press("Delete");
  }

  public async deleteMultiple(args: { names: string[] }) {
    await this.selectMultiple({ names: args.names, position: NodePosition.TOP_PADDING });
    await this.diagram.get().press("Delete");
  }

  public async dragNewConnectedEdge(args: { type: EdgeType; from: string; to: string; position?: NodePosition }) {
    const fromIsId = args.from.startsWith("_");
    const toIsId = args.to.startsWith("_");

    const from = fromIsId ? this.getById({ id: args.from }) : this.get({ name: args.from });
    const to = toIsId ? this.getById({ id: args.to }) : this.get({ name: args.to });

    await from.scrollIntoViewIfNeeded();
    await to.scrollIntoViewIfNeeded();

    if (fromIsId) {
      await this.selectById({ id: args.from, position: NodePosition.TOP });
    } else {
      await this.select({ name: args.from, position: NodePosition.TOP });
    }

    const targetPosition =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node: to, position: args.position })
        : undefined;

    await from.getByTitle(this.getAddEdgeTitle(args.type)).dragTo(to, {
      targetPosition,
      force: true,
      noWaitAfter: true,
    });
  }

  public async dragNewConnectedNode(args: {
    type: NodeType;
    from: string;
    targetPosition: { x: number; y: number };
    thenRenameTo?: string;
  }) {
    const isId = args.from.startsWith("_");

    let node: Locator;
    if (isId) {
      await this.selectById({ id: args.from, position: NodePosition.TOP });
      node = this.getById({ id: args.from });
    } else {
      await this.select({ name: args.from, position: NodePosition.TOP });
      node = this.get({ name: args.from });
    }

    const testId = await node.getAttribute("data-testid");
    const isGateway = testId?.startsWith("kie-tools--bpmn-editor--node-gateway-") ?? false;
    if (isGateway) {
      const box = await node.boundingBox();
      if (box) {
        await this.page.mouse.move(box.x + box.width - 10, box.y + box.height / 2);
      }
    }

    const { addNodeTitle, nodeName } = this.getNewConnectedNodeProperties(args.type);

    await node.getByTitle(addNodeTitle).dragTo(this.diagram.get(), { targetPosition: args.targetPosition });

    if (args.thenRenameTo) {
      await this.rename({ current: nodeName, new: args.thenRenameTo });
    }
  }

  public getById(args: { id: string }) {
    const escapedId = args.id.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    return this.page.getByTestId(new RegExp(`^kie-tools--bpmn-editor--node-.+-${escapedId}$`));
  }

  public async selectById(args: { id: string; position?: NodePosition }) {
    const node = this.getById({ id: args.id });
    const coordinates =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
        : undefined;
    await node.click({ position: coordinates, force: true });

    const testId = await node.getAttribute("data-testid");
    const isGateway = testId?.startsWith("kie-tools--bpmn-editor--node-gateway-") ?? false;

    if (!isGateway) {
      await this.waitForNodeToBeFocused({ id: args.id });
    }
  }

  public async renameByLocator(args: {
    nodeLocator: ReturnType<Page["locator"]>;
    newName: string;
    needsSelection?: boolean;
  }) {
    const needsSelection = args.needsSelection ?? true;

    const textbox = args.nodeLocator.getByRole("textbox").first();
    if (await textbox.isVisible()) {
      await textbox.fill(args.newName);
      await this.diagram.get().press("Enter");
      return;
    }

    await this.page.keyboard.press("Enter");
    await this.page.keyboard.type(args.newName);
    await this.diagram.resetFocus();
  }

  public async rename(args: { current: string; new: string }) {
    const node = this.get({ name: args.current });

    await this.renameByLocator({
      nodeLocator: node,
      newName: args.new,
      needsSelection: true,
    });
  }

  public async resize(args: { nodeName: string; xOffset: number; yOffset: number }) {
    const node = this.get({ name: args.nodeName });
    await this.select({ name: args.nodeName, position: NodePosition.CENTER });

    const nodeBox = await node.boundingBox();
    if (nodeBox) {
      const handleX = nodeBox.x + nodeBox.width;
      const handleY = nodeBox.y + nodeBox.height / 2;

      await this.page.mouse.move(handleX, handleY);
      await this.page.mouse.down();
      await this.page.mouse.move(handleX + args.xOffset, handleY + args.yOffset);
      await this.page.mouse.up();
    }
  }

  public async select(args: { name: string; position?: NodePosition }) {
    const node = this.get({ name: args.name });

    const position =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
        : undefined;

    await node.click({ position, force: true });
  }

  public async selectMultiple(args: { names: string[]; position?: NodePosition }) {
    if (this.browserName === "webkit") {
      await this.page.keyboard.down("Meta");
    } else {
      await this.page.keyboard.down("Control");
    }

    for (const name of args.names) {
      const node = this.get({ name });

      const position =
        args.position !== undefined
          ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
          : undefined;

      await node.click({ position, force: true });
    }

    if (this.browserName === "webkit") {
      await this.page.keyboard.up("Meta");
    } else {
      await this.page.keyboard.up("Control");
    }
  }

  public async waitForNodeToBeFocused(args: { name?: string; id?: string }) {
    if (args.id) {
      await this.page.waitForFunction((id) => {
        const element = document.querySelector(`[data-nodehref="${id}"]`);
        return element?.getAttribute("data-selected") === "true";
      }, args.id);
      return;
    }

    if (args.name) {
      await this.page.waitForFunction((name) => {
        const element = document.querySelector(`[data-nodelabel="${name}"]`);
        return element?.getAttribute("data-selected") === "true";
      }, args.name);
    }
  }

  public async morphNode(args: { nodeLocator: Locator; targetMorphType: string; exact?: boolean }): Promise<void> {
    const exact = args.exact ?? false;

    const box = await args.nodeLocator.boundingBox();
    expect(box).not.toBeNull();

    await this.page.mouse.move(box!.x + box!.width / 2, box!.y + box!.height / 2);

    const morphingToggle = args.nodeLocator.getByRole("button", { name: /morph/i });
    const toggleCount = await morphingToggle.count();
    const isToggleVisible = toggleCount > 0 ? await morphingToggle.isVisible() : false;

    if (!isToggleVisible) {
      return;
    }

    await morphingToggle.click({ force: true });

    const morphingPanel = this.page.getByTestId("kie-tools--bpmn-editor--morphing-panel");
    const morphingOption = morphingPanel.getByTitle(args.targetMorphType, { exact });
    const optionCount = await morphingOption.count();
    const isOptionVisible = optionCount > 0 ? await morphingOption.isVisible() : false;

    if (!isOptionVisible) {
      await this.diagram.resetFocus();
      return;
    }

    await morphingOption.click({ force: true });
  }

  public async openMorphingPanel(args: { nodeLocator: Locator }): Promise<void> {
    const box = await args.nodeLocator.boundingBox();
    expect(box).not.toBeNull();

    await this.page.mouse.move(box!.x + box!.width / 2, box!.y + box!.height / 2);

    const morphingToggle = args.nodeLocator.getByRole("button", { name: /morph/i });
    await expect(morphingToggle).toBeVisible();
    await morphingToggle.click({ force: true });
  }

  private async getPositionalNodeHandleCoordinates(args: {
    node: Locator;
    position: NodePosition;
  }): Promise<{ x: number; y: number }> {
    const box = await args.node.boundingBox();
    expect(box).not.toBeNull();

    switch (args.position) {
      case NodePosition.TOP:
        return { x: box!.width / 2, y: 5 };
      case NodePosition.BOTTOM:
        return { x: box!.width / 2, y: box!.height - 5 };
      case NodePosition.LEFT:
        return { x: 5, y: box!.height / 2 };
      case NodePosition.RIGHT:
        return { x: box!.width - 5, y: box!.height / 2 };
      case NodePosition.CENTER:
        return { x: box!.width / 2, y: box!.height / 2 };
      case NodePosition.TOP_PADDING:
        return { x: box!.width / 2, y: 15 };
    }
  }

  private getAddEdgeTitle(type: EdgeType): string {
    switch (type) {
      case EdgeType.SEQUENCE_FLOW:
        return "Add Sequence Flow";
      case EdgeType.ASSOCIATION:
        return "Add Association";
    }
  }

  private getNewConnectedNodeProperties(type: NodeType) {
    switch (type) {
      case NodeType.START_EVENT:
        return { addNodeTitle: "Add Start Event", nodeName: DefaultNodeName.START_EVENT };
      case NodeType.INTERMEDIATE_CATCH_EVENT:
        return { addNodeTitle: "Add Intermediate Catch Event", nodeName: DefaultNodeName.INTERMEDIATE_CATCH_EVENT };
      case NodeType.INTERMEDIATE_THROW_EVENT:
        return { addNodeTitle: "Add Intermediate Throw Event", nodeName: DefaultNodeName.INTERMEDIATE_THROW_EVENT };
      case NodeType.END_EVENT:
        return { addNodeTitle: "Add End Event", nodeName: DefaultNodeName.END_EVENT };
      case NodeType.TASK:
        return { addNodeTitle: "Add Task", nodeName: DefaultNodeName.TASK };
      case NodeType.CALL_ACTIVITY:
        return { addNodeTitle: "Add Call Activity", nodeName: DefaultNodeName.CALL_ACTIVITY };
      case NodeType.SUB_PROCESS:
        return { addNodeTitle: "Add Sub-process", nodeName: DefaultNodeName.SUB_PROCESS };
      case NodeType.GATEWAY:
        return { addNodeTitle: "Add Gateway", nodeName: DefaultNodeName.GATEWAY };
      case NodeType.DATA_OBJECT:
        return { addNodeTitle: "Add Data Object", nodeName: DefaultNodeName.DATA_OBJECT };
      case NodeType.TEXT_ANNOTATION:
        return { addNodeTitle: "Add Text Annotation", nodeName: DefaultNodeName.TEXT_ANNOTATION };
      case NodeType.GROUP:
        return { addNodeTitle: "Add Group", nodeName: DefaultNodeName.GROUP };
      case NodeType.LANE:
        return { addNodeTitle: "Add Lane", nodeName: DefaultNodeName.LANE };
    }
  }
}
