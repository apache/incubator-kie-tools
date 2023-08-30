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

import BpmnPalette from "./BpmnPalette";
import { By } from "selenium-webdriver";
import Editor from "../Editor";
import Element from "../../Element";
import Locator from "../../Locator";
import SideBar from "../SideBar";

export default class BpmnEditor extends Editor {
  private static readonly CANVAS_LOCATOR: By = By.className("canvas-panel");
  private static readonly PALETTE_LOCATOR: By = By.className("kie-palette");
  private static readonly SIDE_BAR_LOCATOR: By = By.className("collapsed-docks-bar-E");

  public async dragAndDropStartEventToCanvas(): Promise<void> {
    const bpmnPalette: BpmnPalette = await this.getBpmnPalette();
    await bpmnPalette.dragAndDropStartEventToCanvas();
    return await this.clickToCanvas();
  }

  public async isPalettePresent(): Promise<boolean> {
    return await this.tools.by(BpmnEditor.PALETTE_LOCATOR).wait(1000).isPresent();
  }

  private async getBpmnPalette(): Promise<BpmnPalette> {
    const palette: Element = await this.tools.by(BpmnEditor.PALETTE_LOCATOR).getElement();
    return await this.tools.createPageFragment(BpmnPalette, palette);
  }

  private async clickToCanvas(): Promise<void> {
    const canvas: Element = await this.tools.by(BpmnEditor.CANVAS_LOCATOR).getElement();
    return await canvas.click();
  }

  public async getSideBar(): Promise<SideBar> {
    const sideBar: Locator = await this.tools.by(BpmnEditor.SIDE_BAR_LOCATOR);
    await sideBar.wait(1000).untilPresent();
    return await this.tools.createPageFragment(SideBar, await sideBar.getElement());
  }
}
