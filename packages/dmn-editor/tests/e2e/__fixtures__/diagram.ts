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

export const DEFAULT_DRD_NAME = "Default DRD";
export const UNNAMED_DRD_NAME = "Unnamed DRD";

export class Diagram {
  constructor(public page: Page) {}

  public get() {
    return this.page.getByTestId("kie-dmn-editor--diagram-container");
  }

  public async dblclick(position: { x: number; y: number }) {
    const offsetX = 100;
    const offsetY = 100;

    return this.get().dblclick({ position: { x: position.x + offsetX, y: position.y + offsetY } });
  }

  public async resetFocus() {
    return this.get().click({ position: { x: 0, y: 0 } });
  }

  public async select(args: { startPosition: { x: number; y: number }; endPosition: { x: number; y: number } }) {
    await this.page.mouse.move(args.startPosition.x, args.startPosition.y);
    await this.page.mouse.down();
    await this.page.mouse.move(args.endPosition.x, args.endPosition.y);
    await this.page.mouse.up();
  }
}
