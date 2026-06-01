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

import { Page, Locator, expect } from "@playwright/test";

interface Position {
  x: number;
  y: number;
}

export class Resizing {
  constructor(public page: Page) {}

  public async resizeCell(target: Locator, from: Position = { x: 0, y: 0 }, to: Position = { x: 0, y: 0 }) {
    await target.hover();
    const handle = target.getByTestId("kie-tools--bee--resizer-handle");
    const box = await handle.boundingBox();
    if (!box) {
      throw new Error("resizer handle not found");
    }
    const startX = box.x + box.width / 2 + from.x;
    const startY = box.y + box.height / 2 + from.y;
    await this.page.mouse.move(startX, startY);
    await this.page.mouse.down();
    await this.page.mouse.move(startX + (to.x - from.x) / 2, startY + (to.y - from.y) / 2, { steps: 5 });
    await this.page.mouse.move(startX + (to.x - from.x), startY + (to.y - from.y), { steps: 10 });
    await this.page.mouse.up();
  }

  public async resizeCellManually(page: Page, target: Locator, relativePosition: { to: Position }) {
    await target.hover();
    const handle = target.getByTestId("kie-tools--bee--resizer-handle");
    await handle.hover({ position: { x: 1, y: 1 }, force: true });
    const box = await handle.boundingBox();
    if (!box) {
      throw new Error("Could not find the resizer handle to resize the cell.");
    }

    const widthOf = async () => Math.round((await target.boundingBox())?.width ?? 0);
    const widthBeforeResize = await widthOf();

    await page.mouse.move(box.x + 1, box.y + 1);
    await page.mouse.down({ button: "left" });
    const { x: endX, y: endY } = relativePosition.to;
    let previousWidth = -1;
    await expect
      .poll(
        async () => {
          await page.mouse.move(endX - 1, endY);
          await page.mouse.move(endX, endY);
          const width = await widthOf();
          const settled = width === previousWidth && width !== widthBeforeResize;
          previousWidth = width;
          return settled;
        },
        { timeout: 5000 }
      )
      .toBe(true);

    await page.mouse.up({ button: "left" });
  }

  public async reset(target: Locator) {
    await target.hover({ position: { x: 1, y: 1 } });
    await target.getByTestId("kie-tools--bee--resizer-handle").dblclick();
  }

  public async resetManually(page: Page, target: Locator) {
    await target.hover({ position: { x: 1, y: 1 } });
    await target.getByTestId("kie-tools--bee--resizer-handle").hover({ position: { x: 1, y: 1 }, force: true });
    await page.mouse.down({ button: "left", clickCount: 2 });
    await page.mouse.up();
  }
}
