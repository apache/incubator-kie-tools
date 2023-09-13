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
    const handle = target.getByTestId("resizer-handle");
    await handle.dragTo(handle, {
      force: true,
      sourcePosition: from,
      targetPosition: to,
    });
  }

  public async reset(target: Locator) {
    await target.hover();
    await target.getByTestId("resizer-handle").dblclick();
  }
}
