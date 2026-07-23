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

import { Page, Locator } from "@playwright/test";

interface Position {
  x: number;
  y: number;
}

export class Resizing {
  constructor(public page: Page) {}

  public async resizeCell(target: Locator, from: Position = { x: 0, y: 0 }, to: Position = { x: 0, y: 0 }) {
    await target.hover();
    const handle = target.getByTestId("kie-tools--bee--resizer-handle");
    await handle.hover();

    const box = await handle.boundingBox();
    if (!box) {
      throw new Error("Cannot resize: resizer handle has no bounding box.");
    }

    // Discrete steps instead of `handle.dragTo(handle, ...)`: react-resizable only commits the new
    // width on intermediate "mousemove" events, which a single source-to-target jump doesn't fire.
    await this.page.mouse.move(box.x + from.x, box.y + from.y);
    await this.page.mouse.down();
    await this.page.mouse.move(box.x + to.x, box.y + to.y, { steps: 20 });
    await this.page.mouse.up();
  }

  public async reset(target: Locator) {
    await target.hover();
    await target.getByTestId("kie-tools--bee--resizer-handle").dblclick();

    // Resizer.tsx's onDoubleClick commits the reset width inside a setTimeout(..., 10); give it
    // a moment before callers read layout values right after calling this method.
    await this.page.waitForTimeout(150);
  }
}
