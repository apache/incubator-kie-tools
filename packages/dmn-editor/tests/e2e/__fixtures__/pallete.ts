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
import { Node, NodeType } from "./node";

export class Pallete {
  constructor(public page: Page, public diagram: Diagram, public node: Node) {
    this.page = page;
  }

  public async dragNewNode(args: { type: NodeType; targetPosition: { x: number; y: number } }) {
    switch (args.type) {
      case NodeType.INPUT_DATA:
        await this.page
          .getByTitle("Input Data", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "New Input Data" });
      case NodeType.DECISION:
        await this.page
          .getByTitle("Decision", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "New Decision" });
      case NodeType.KNOWLEDGE_SOURCE:
        await this.page
          .getByTitle("Knowledge Source", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "New Knowledge Source" });
      case NodeType.BKM:
        await this.page
          .getByTitle("Business Knowledge Model", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "New Business Knowledge Model" });
      case NodeType.DECISION_SERVICE:
        await this.page
          .getByTitle("Decision Service", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "New Decision Service" });
      case NodeType.GROUP:
        await this.page
          .getByTitle("Group", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "New Group" });
      case NodeType.TEXT_ANNOTATION:
        await this.page
          .getByTitle("Text Annotation", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        return this.node.getId({ name: "Text Annotation" });
    }
  }
}
