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

import { Page } from "@playwright/test";
import { Diagram } from "./diagram";
import { DefaultNodeName, Nodes, NodeType } from "./nodes";

export class Palette {
  constructor(public page: Page, public diagram: Diagram, public nodes: Nodes) {}

  public async dragNewNode(args: { type: NodeType; targetPosition: { x: number; y: number }; thenRenameTo?: string }) {
    const { nodeTitle, nodeName } = this.getNewNodeProperties(args.type);

    await this.page
      .getByTitle(nodeTitle, { exact: true })
      .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
    await this.nodes.waitForNodeToBeFocused({ name: nodeName });
    if (args.thenRenameTo) {
      await this.nodes.rename({ current: nodeName, new: args.thenRenameTo });
    }
  }

  private getNewNodeProperties(type: NodeType) {
    switch (type) {
      case NodeType.INPUT_DATA:
        return { nodeTitle: "Input Data", nodeName: DefaultNodeName.INPUT_DATA };
      case NodeType.DECISION:
        return { nodeTitle: "Decision", nodeName: DefaultNodeName.DECISION };
      case NodeType.KNOWLEDGE_SOURCE:
        return { nodeTitle: "Knowledge Source", nodeName: DefaultNodeName.KNOWLEDGE_SOURCE };
      case NodeType.BKM:
        return { nodeTitle: "Business Knowledge Model", nodeName: DefaultNodeName.BKM };
      case NodeType.DECISION_SERVICE:
        return { nodeTitle: "Decision Service", nodeName: DefaultNodeName.DECISION_SERVICE };
      case NodeType.GROUP:
        return { nodeTitle: "Group", nodeName: DefaultNodeName.GROUP };
      case NodeType.TEXT_ANNOTATION:
        return { nodeTitle: "Text Annotation", nodeName: DefaultNodeName.TEXT_ANNOTATION };
    }
  }
}
