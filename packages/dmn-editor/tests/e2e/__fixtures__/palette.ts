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
    switch (args.type) {
      case NodeType.INPUT_DATA:
        await this.page
          .getByTitle("Input Data", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });

        if (args.thenRenameTo) {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.INPUT_DATA });
          await this.nodes.rename({ current: DefaultNodeName.INPUT_DATA, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.INPUT_DATA });
        }
        break;
      case NodeType.DECISION:
        await this.page
          .getByTitle("Decision", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.nodes.rename({ current: DefaultNodeName.DECISION, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.DECISION });
        }
        break;
      case NodeType.KNOWLEDGE_SOURCE:
        await this.page
          .getByTitle("Knowledge Source", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.nodes.rename({ current: DefaultNodeName.KNOWLEDGE_SOURCE, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
        }
        break;
      case NodeType.BKM:
        await this.page
          .getByTitle("Business Knowledge Model", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.nodes.rename({ current: DefaultNodeName.BKM, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.BKM });
        }
        break;
      case NodeType.DECISION_SERVICE:
        await this.page
          .getByTitle("Decision Service", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.nodes.rename({ current: DefaultNodeName.DECISION_SERVICE, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.DECISION_SERVICE });
        }
        break;
      case NodeType.GROUP:
        await this.page
          .getByTitle("Group", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.nodes.rename({ current: DefaultNodeName.GROUP, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.GROUP });
        }
        break;
      case NodeType.TEXT_ANNOTATION:
        await this.page
          .getByTitle("Text Annotation", { exact: true })
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.nodes.rename({ current: DefaultNodeName.TEXT_ANNOTATION, new: args.thenRenameTo });
        } else {
          await this.nodes.waitForNodeToBeFocused({ name: DefaultNodeName.TEXT_ANNOTATION });
        }
        break;
    }
  }
}
