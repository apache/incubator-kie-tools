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

export class Diagram {
  constructor(public page: Page) {}

  public get() {
    return this.page.getByTestId("kie-dmn-editor--diagram-container");
  }

  // Hover on the top border;
  public async hoverNode(args: { name: string }) {
    const node = this.page.getByTitle(args.name, { exact: true });
    const nodeBoundingBox = await node.boundingBox();
    await node.hover({ position: { x: (nodeBoundingBox?.width ?? 0) / 2, y: 0 } });
  }

  // Click on the top border;
  public async selectNode(args: { name: string }) {
    const node = this.page.getByTitle(args.name, { exact: true });
    const nodeBoundingBox = await node.boundingBox();
    await node.click({ position: { x: (nodeBoundingBox?.width ?? 0) / 2, y: 0 } });
  }
}
