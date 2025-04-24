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

export class DrgNodes {
  constructor(
    public diagram: Diagram,
    public page: Page
  ) {}

  public async toggle() {
    await this.page.getByTitle("DRG Nodes").click();
  }

  public popover() {
    return this.page.getByTestId("kie-tools--dmn-editor--palette-nodes-popover");
  }

  public async dragNode(args: { name: string; targetPosition: { x: number; y: number } }) {
    // This short delay prevents issues where the diagram container gets moved outside the viewport during drag operations.
    await new Promise((resolve) => setTimeout(resolve, 500));

    await this.popover()
      .getByText(args.name, { exact: true })
      .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
  }
}
